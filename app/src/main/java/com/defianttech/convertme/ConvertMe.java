package com.defianttech.convertme;

import java.text.DecimalFormat;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

/*
 * Copyright (C) 2014-2016 Defiant Technologies, LLC
 */
public class ConvertMe extends AppCompatActivity {
    private static final String TAG = "ConvertMe";
    private static final String PREFS_NAME = "ConvertMePrefs";
    private static final int DEFAULT_CATEGORY = 5; //default to "distance"
    private static final int DEFAULT_INDEX = 2; //default to "centimeter"
    private static final double DEFAULT_VALUE = 1.0;

    private final UnitCollection[] collection = UnitCollection.COLLECTION;

    private int currentCategory = DEFAULT_CATEGORY;
    private int currentUnitIndex = DEFAULT_INDEX;

    private double currentValue = DEFAULT_VALUE;
    private NumberPadView numberPadView;
    private UnitListAdapter listAdapter;
    private ListView unitsList;
    private FloatingActionButton fabEdit;
    private ActionMode actionMode;
    private boolean editModeEnabled;

    private DecimalFormat dfExp = new DecimalFormat("#.#######E0");
    private DecimalFormat dfNoexp = new DecimalFormat("#.#######");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.convertme);

        String[] unitCategories = new String[collection.length];
        for(int i=0; i<collection.length; i++){
        	unitCategories[i] = collection[i].getName();
        }
        SpinnerAdapter categoryAdapter = new ArrayAdapter(this, R.layout.unit_categoryitem, unitCategories);

        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setListNavigationCallbacks(categoryAdapter, new ActionBar.OnNavigationListener() {
                @Override
                public boolean onNavigationItemSelected(int i, long l) {
                    currentCategory = i;
                    if (currentUnitIndex >= collection[currentCategory].length()) {
                        currentUnitIndex = 0;
                    }
                    listAdapter.notifyDataSetInvalidated();
                    return true;
                }
            });
        }

        unitsList = (ListView) findViewById(R.id.unitsList);
        listAdapter = new UnitListAdapter();
        unitsList.setAdapter(listAdapter);
        unitsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (editModeEnabled) {
                    collection[currentCategory].get(position).setEnabled(!collection[currentCategory].get(position).isEnabled());
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
                String resultStr = String.format("%1$s", getConvertedResult(position));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    clipboard.setPrimaryClip(ClipData.newPlainText("", resultStr));
                } else {
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(resultStr);
                }
                Toast.makeText(ConvertMe.this, R.string.menu_clipboard_copied, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        numberPadView = (NumberPadView) findViewById(R.id.numberPad);
        numberPadView.setOnValueChangedListener(new NumberPadView.OnValueChangedListener() {
            @Override
            public void onValueChanged(String value) {
                setValueFromNumberPad(value);
                listAdapter.notifyDataSetChanged();
            }
        });

        restoreSettings();
        getSupportActionBar().setSelectedNavigationItem(currentCategory);

        fabEdit = (FloatingActionButton) findViewById(R.id.fabEdit);
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSupportActionMode(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        actionMode = mode;
                        actionMode.setTitle("Show/hide units");
                        editModeEnabled = true;
                        updateActionModeState();
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
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
                        actionMode = null;
                        editModeEnabled = false;
                        updateActionModeState();
                    }
                });
            }
        });

    }

    @Override
    public void onStop(){
        super.onStop();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("currentCategory", currentCategory);
        editor.putInt("currentUnitIndex", currentUnitIndex);
        editor.putString("currentValue", numberPadView.getCurrentValue());
        editor.commit();
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
        }
        return false;
    }

    private void restoreSettings() {
        try {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            currentCategory = settings.getInt("currentCategory", DEFAULT_CATEGORY);
            if (currentCategory >= collection.length) {
                currentCategory = DEFAULT_CATEGORY;
            }
            currentUnitIndex = settings.getInt("currentUnitIndex", DEFAULT_INDEX);
            if (currentUnitIndex >= collection[currentCategory].length()) {
                currentUnitIndex = 0;
            }
            numberPadView.setCurrentValue(settings.getString("currentValue", "1"));
            setValueFromNumberPad(numberPadView.getCurrentValue());
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
        unitsList.setChoiceMode(editModeEnabled ? AbsListView.CHOICE_MODE_NONE : AbsListView.CHOICE_MODE_SINGLE);
        listAdapter.notifyDataSetChanged();
    }

    private void setValueFromNumberPad(String value) {
        try {
            currentValue = Double.parseDouble(value);
        } catch (NumberFormatException e) {
            currentValue = 0.0;
        }
    }

    private final class UnitListAdapter extends BaseAdapter {
        public UnitListAdapter() {
        }

        @Override
        public int getCount() {
            return collection[currentCategory].length();
        }

        @Override
        public Object getItem(int position) {
            return collection[currentCategory].get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.unit_listitem, parent, false);
            }
            View itemContainer = convertView.findViewById(R.id.unitItemContainer);
            TextView unitName = (TextView) convertView.findViewById(R.id.unitName);
            TextView unitValue = (TextView) convertView.findViewById(R.id.unitValue);
            ImageView chkEnable = (ImageView) convertView.findViewById(R.id.chkSelected);
            unitName.setText(Html.fromHtml(collection[currentCategory].get(position).getName()));


            AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, collection[currentCategory].get(position).isEnabled() ? ViewGroup.LayoutParams.WRAP_CONTENT : 0);
            convertView.setLayoutParams(params);
            //convertView.setVisibility(collection[currentCategory].get(position).isEnabled() ? View.VISIBLE : View.GONE);



            if (position == currentUnitIndex) {
                unitsList.setItemChecked(position, true);
            }

            if (editModeEnabled) {
                itemContainer.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                chkEnable.setImageDrawable(getResources().getDrawable(collection[currentCategory].get(position).isEnabled() ? R.drawable.ic_check_box_black : R.drawable.ic_check_box_outline_blank_black));
            } else {
                itemContainer.setBackgroundDrawable(getResources().getDrawable(R.drawable.selectable_item_background));
            }
            chkEnable.setVisibility(editModeEnabled ? View.VISIBLE : View.GONE);

            double p = getConvertedResult(position);
            String strValue;
            try{
                if((Math.abs(p) > 1e6) || (Math.abs(p) < 1e-6 && Math.abs(p) > 0.0)){
                    strValue = dfExp.format(p);
                }else{
                    strValue = dfNoexp.format(p);
                }
                if(strValue.contains("E")){
                    strValue = strValue.replace("E", " × 10<sup><small>");
                    strValue += "</small></sup>";
                }
                unitValue.setText(Html.fromHtml(strValue));
            }catch(Exception e){
                Log.d(TAG, "Error while rendering unit.", e);
            }
            return convertView;
        }
    }

    private double getConvertedResult(int targetUnitIndex) {
        double result = (currentValue - collection[currentCategory].get(currentUnitIndex).getOffset())
                / collection[currentCategory].get(currentUnitIndex).getMultiplier();
        result *= collection[currentCategory].get(targetUnitIndex).getMultiplier();
        result += collection[currentCategory].get(targetUnitIndex).getOffset();
        return result;
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

