package com.defianttech.convertme;

import java.text.DecimalFormat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

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

    private double lastValue = DEFAULT_VALUE;
    private UnitListAdapter listAdapter;
    private ListView unitsList;

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
                    if (currentUnitIndex >= collection[currentCategory].getItems().length) {
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
                currentUnitIndex = position;
                listAdapter.notifyDataSetChanged();
            }
        });

        //restore settings...
        try {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            currentCategory = settings.getInt("currentCategory", DEFAULT_CATEGORY);
            if (currentCategory >= unitCategories.length) {
                currentCategory = DEFAULT_CATEGORY;
            }
            getSupportActionBar().setSelectedNavigationItem(currentCategory);
            currentUnitIndex = settings.getInt("currentUnitIndex", DEFAULT_INDEX);
            if (currentUnitIndex >= collection[currentCategory].getItems().length) {
                currentUnitIndex = 0;
            }
            lastValue = settings.getFloat("lastValue", (float) DEFAULT_VALUE);
        } catch(Exception ex) {
            //ehh...
        }

        EditText unitValueText = (EditText) findViewById(R.id.unitValueText);
        unitValueText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    lastValue = Double.parseDouble(editable.toString());
                } catch (NumberFormatException e) {
                    lastValue = 0.0;
                }
                listAdapter.notifyDataSetChanged();
            }
        });
        unitValueText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideSoftKeyboard();
                    return true;
                }
                return false;
            }
        });
        unitValueText.setText(Double.toString(lastValue));
    }

    @Override
    public void onStop(){
        super.onStop();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("currentCategory", currentCategory);
        editor.putInt("currentUnitIndex", currentUnitIndex);
        editor.putFloat("lastValue", (float) lastValue);
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
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.menu_about))
                        .setMessage(getString(R.string.about_message))
                        .setPositiveButton(R.string.ok, null)
                        .create()
                        .show();
                return true;
        }
        return false;
    }

    private final class UnitListAdapter extends BaseAdapter {
        public UnitListAdapter() {
        }

        @Override
        public int getCount() {
            return collection[currentCategory].getItems().length;
        }

        @Override
        public Object getItem(int position) {
            return collection[currentCategory].getItems()[position];
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
            unitName.setText(Html.fromHtml(collection[currentCategory].getItems()[position].getName()));

            if (position == currentUnitIndex) {
                unitsList.setItemChecked(position, true);
            }

            double p = (lastValue - collection[currentCategory].getItems()[currentUnitIndex].getOffset())
                    / collection[currentCategory].getItems()[currentUnitIndex].getMultiplier();
            p *= collection[currentCategory].getItems()[position].getMultiplier();
            p += collection[currentCategory].getItems()[position].getOffset();

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

    private void hideSoftKeyboard() {
        InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }
}

