package com.defianttech.convertme;

import java.text.DecimalFormat;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.Space;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/*
 * Copyright (c) 2014-2017 Dmitry Brant
 */
public class ConvertActivity extends AppCompatActivity {
    private static final String TAG = "ConvertActivity";
    private static final String PREFS_NAME = "ConvertMePrefs";
    private static final String KEY_CURRENT_CATEGORY = "currentCategory";
    private static final String KEY_CURRENT_UNIT = "currentUnitIndex";
    private static final String KEY_CURRENT_VALUE = "currentValue";

    private static DecimalFormat dfExp = new DecimalFormat("#.#######E0");
    private static DecimalFormat dfNoexp = new DecimalFormat("#.#######");

    private UnitCollection[] collections;
    private String[] allCategoryNames;

    private int currentCategory = UnitCollection.DEFAULT_CATEGORY;
    private int currentUnitIndex = UnitCollection.DEFAULT_FROM_INDEX;

    private double currentValue = UnitCollection.DEFAULT_VALUE;

    private TextView categoryText;
    private PopupMenu categoryMenu;

    private NumberPadView numberPadView;
    private UnitListAdapter listAdapter;
    private ListView unitsList;
    private FloatingActionButton fabEdit;
    private ActionMode actionMode;
    private boolean editModeEnabled;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.convertme);

        collections = UnitCollection.getInstance(this);
        allCategoryNames = UnitCollection.getAllCategoryNames(this);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        View categoryContainer = findViewById(R.id.category_toolbar_container);
        categoryContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryMenu.show();
            }
        });

        categoryMenu = new PopupMenu(ConvertActivity.this, categoryContainer);
        int i = 0;
        for (String name : allCategoryNames) {
            categoryMenu.getMenu().add(0, i++, 0, name);
        }
        categoryMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                currentCategory = UnitCollection.collectionIndexByName(collections,
                        allCategoryNames[item.getItemId()]);
                if (currentUnitIndex >= collections[currentCategory].length()) {
                    currentUnitIndex = 0;
                }
                categoryText.setText(item.getTitle());
                listAdapter.notifyDataSetInvalidated();
                return true;
            }
        });

        categoryText = findViewById(R.id.category_text);
        unitsList = findViewById(R.id.unitsList);
        listAdapter = new UnitListAdapter();
        unitsList.setAdapter(listAdapter);
        unitsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (editModeEnabled) {
                    collections[currentCategory].get(position)
                            .setEnabled(!collections[currentCategory].get(position).isEnabled());
                } else {
                    currentUnitIndex = position;
                }
                listAdapter.notifyDataSetChanged();
            }
        });
        unitsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (editModeEnabled) {
                    return false;
                }
                doLongPressMenu(view.findViewById(R.id.unitValue), position);
                return true;
            }
        });

        numberPadView = findViewById(R.id.numberPad);
        numberPadView.setOnValueChangedListener(new NumberPadView.OnValueChangedListener() {
            @Override
            public void onValueChanged(String value) {
                setValueFromNumberPad(value);
                listAdapter.notifyDataSetChanged();
            }
        });

        restoreSettings();

        for (String name : allCategoryNames) {
            if (name.equals(collections[currentCategory].getNames()[0])) {
                categoryText.setText(name);
            }
        }

        fabEdit = findViewById(R.id.fabEdit);
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSupportActionMode(new EditUnitsActionModeCallback());
            }
        });
    }

    @Override
    public void onStop(){
        super.onStop();
        saveSettings();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_about:
                showAboutDialog();
                return true;
            default:
                break;
        }
        return false;
    }

    @NonNull
    public static SharedPreferences getPrefs(@NonNull Context context) {
        return context.getSharedPreferences(ConvertActivity.PREFS_NAME, 0);
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = getPrefs(this).edit();
        editor.putInt(KEY_CURRENT_CATEGORY, currentCategory);
        editor.putInt(KEY_CURRENT_UNIT, currentUnitIndex);
        editor.putString(KEY_CURRENT_VALUE, numberPadView.getCurrentValue());
        for (UnitCollection col : collections) {
            for (SingleUnit unit : col.getItems()) {
                editor.putBoolean(unit.getName(), unit.isEnabled());
            }
        }
        editor.apply();
    }

    private void restoreSettings() {
        try {
            SharedPreferences prefs = getPrefs(this);
            currentCategory = prefs.getInt(KEY_CURRENT_CATEGORY, UnitCollection.DEFAULT_CATEGORY);
            if (currentCategory >= collections.length) {
                currentCategory = UnitCollection.DEFAULT_CATEGORY;
            }
            currentUnitIndex = prefs.getInt(KEY_CURRENT_UNIT, UnitCollection.DEFAULT_FROM_INDEX);
            if (currentUnitIndex >= collections[currentCategory].length()) {
                currentUnitIndex = 0;
            }
            numberPadView.setCurrentValue(prefs.getString(KEY_CURRENT_VALUE, "1"));
            setValueFromNumberPad(numberPadView.getCurrentValue());
            for (UnitCollection col : collections) {
                for (SingleUnit unit : col.getItems()) {
                    unit.setEnabled(prefs.getBoolean(unit.getName(), true));
                }
            }
        } catch(Exception ex) {
            //ehh...
        }
    }

    private void updateActionModeState() {
        numberPadView.setVisibility(editModeEnabled ? View.GONE : View.VISIBLE);
        if (editModeEnabled) {
            fabEdit.hide();
        } else {
            fabEdit.show();
        }
        listAdapter.notifyDataSetInvalidated();
    }

    private void setValueFromNumberPad(@NonNull String value) {
        try {
            currentValue = Double.parseDouble(value);
        } catch (NumberFormatException e) {
            currentValue = 0.0;
        }
    }

    private final class EditUnitsActionModeCallback implements ActionMode.Callback {
        @ColorInt int statusBarColor;

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            actionMode = mode;
            actionMode.setTitle(getString(R.string.show_hide_units));
            editModeEnabled = true;
            updateActionModeState();
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                statusBarColor = getWindow().getStatusBarColor();
                getWindow().setStatusBarColor(Color.BLACK);
            }
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == android.R.id.home) {
                mode.finish();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(statusBarColor);
            }
            actionMode = null;
            editModeEnabled = false;
            updateActionModeState();
        }
    }

    private final class UnitListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return collections[currentCategory].length();
        }

        @Override
        public Object getItem(int position) {
            return collections[currentCategory].get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            if (editModeEnabled) {
                return 0;
            } else {
                return collections[currentCategory].get(position).isEnabled() ? 0 : 1;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (editModeEnabled) {

                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.unit_listitem, parent, false);
                }
                View itemContainer = convertView.findViewById(R.id.unitItemContainer);
                TextView unitName = convertView.findViewById(R.id.unitName);
                TextView unitValue = convertView.findViewById(R.id.unitValue);
                unitValue.setVisibility(View.GONE);
                ImageView chkEnable = convertView.findViewById(R.id.chkSelected);
                chkEnable.setVisibility(View.VISIBLE);
                unitName.setText(Html.fromHtml(collections[currentCategory].get(position).getName()));

                if (position == currentUnitIndex) {
                    unitsList.setItemChecked(position, true);
                }

                itemContainer.setBackgroundColor(ContextCompat.getColor(ConvertActivity.this,
                        android.R.color.transparent));
                chkEnable.setImageResource(collections[currentCategory].get(position).isEnabled()
                        ? R.drawable.ic_check_box_white_24dp
                        : R.drawable.ic_check_box_outline_blank_white_24dp);

            } else {

                if (collections[currentCategory].get(position).isEnabled()) {
                    if (convertView == null) {
                        convertView = getLayoutInflater().inflate(R.layout.unit_listitem, parent, false);
                    }
                    View itemContainer = convertView.findViewById(R.id.unitItemContainer);
                    TextView unitName = convertView.findViewById(R.id.unitName);
                    TextView unitValue = convertView.findViewById(R.id.unitValue);
                    unitValue.setVisibility(View.VISIBLE);
                    ImageView chkEnable = convertView.findViewById(R.id.chkSelected);
                    chkEnable.setVisibility(View.GONE);
                    unitName.setText(Html.fromHtml(collections[currentCategory].get(position).getName()));

                    if (position == currentUnitIndex) {
                        unitsList.setItemChecked(position, true);
                    }

                    ViewCompat.setBackground(itemContainer,
                            ContextCompat.getDrawable(ConvertActivity.this,
                                    R.drawable.selectable_item_background));

                    double p = UnitCollection.convert(ConvertActivity.this, currentCategory,
                            currentUnitIndex, position, currentValue);
                    unitValue.setText(getFormattedValueStr(p));
                } else {
                    if (convertView == null) {
                        AbsListView.LayoutParams params
                                = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
                        convertView = new Space(ConvertActivity.this);
                        convertView.setLayoutParams(params);
                    }
                }
            }
            return convertView;
        }
    }

    public static Spanned getFormattedValueStr(double value) {
        String strValue = getValueStr(value);
        try{
            if(strValue.contains("E")){
                strValue = strValue.replace("E", " × 10<sup><small>");
                strValue += "</small></sup>";
            }
        }catch(Exception e){
            Log.d(TAG, "Error while rendering unit.", e);
        }
        return Html.fromHtml(strValue);
    }

    private static String getValueStr(double val) {
        if((Math.abs(val) > 1e6) || (Math.abs(val) < 1e-6 && Math.abs(val) > 0.0)){
            return dfExp.format(val);
        } else {
            return dfNoexp.format(val);
        }
    }

    private void doLongPressMenu(@NonNull View parentView, final int position) {
        PopupMenu menu = new PopupMenu(this, parentView, Gravity.END | Gravity.CENTER_HORIZONTAL);
        menu.getMenuInflater().inflate(R.menu.menu_long_press, menu.getMenu());
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                String resultStr;
                switch (menuItem.getItemId()) {
                    case R.id.menu_copy_value:
                        resultStr = String.format("%1$s", getValueStr(UnitCollection.convert(ConvertActivity.this, currentCategory, currentUnitIndex, position, currentValue)));
                        setClipboardText(resultStr);
                        return true;
                    case R.id.menu_copy_row:
                        resultStr = String.format("%1$s %2$s = %3$s %4$s", getValueStr(currentValue),
                                collections[currentCategory].get(currentUnitIndex).getName(),
                                getValueStr(UnitCollection.convert(ConvertActivity.this, currentCategory, currentUnitIndex, position, currentValue)),
                                collections[currentCategory].get(position).getName());
                        setClipboardText(resultStr);
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });
        menu.show();
    }

    private void setClipboardText(@NonNull String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (clipboard == null) {
            return;
        }
        clipboard.setPrimaryClip(ClipData.newPlainText("", text));
        Toast.makeText(ConvertActivity.this, R.string.menu_clipboard_copied, Toast.LENGTH_SHORT).show();
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.menu_about))
                .setMessage(getString(R.string.about_message))
                .setPositiveButton(R.string.ok, null)
                .create()
                .show();
    }
}

