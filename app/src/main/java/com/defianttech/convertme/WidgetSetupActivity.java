package com.defianttech.convertme;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

/*
 * Copyright (c) 2014-2016 Dmitry Brant
 */
public class WidgetSetupActivity extends AppCompatActivity {
    private static final String TAG = "WidgetSetupActivity";

    UnitCollection[] collections;
    private String[] allCategoryNames;
    private int widgetId = -1;

    private Spinner unitCategorySpinner;
    private Spinner unitFromSpinner;
    private Spinner unitToSpinner;

    private WidgetPrefs prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.widget_setup_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.configure_widget);

        if (!TextUtils.isEmpty(getIntent().getAction())
                && getIntent().getAction().contains(WidgetProvider.CLICK_ACTION_SETTINGS)) {
            widgetId = WidgetProvider.getWidgetId(getIntent().getAction());
        }
        if (widgetId == -1) {
            return;
        }
        Log.d(TAG, "Configuring widget " + widgetId);

        prefs = new WidgetPrefs(this, widgetId);

        collections = UnitCollection.getInstance(this);
        allCategoryNames = UnitCollection.getAllCategoryNames(this);
        SpinnerAdapter categoryAdapter = new ArrayAdapter(this, R.layout.unit_categoryitem, allCategoryNames);

        unitCategorySpinner = (Spinner) findViewById(R.id.unit_category_spinner);
        unitCategorySpinner.setAdapter(categoryAdapter);
        unitCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                prefs.currentCategory = UnitCollection.collectionIndexByName(collections, allCategoryNames[i]);
                if (prefs.currentFromIndex > collections[prefs.currentCategory].length()) {
                    prefs.currentFromIndex = 0;
                }
                if (prefs.currentToIndex > collections[prefs.currentCategory].length()) {
                    prefs.currentToIndex = 0;
                }
                setUnitSpinners(prefs.currentCategory);
                prefs.save(WidgetSetupActivity.this);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        for (int i = 0; i < allCategoryNames.length; i++) {
            if (allCategoryNames[i].equals(collections[prefs.currentCategory].getNames()[0])) {
                unitCategorySpinner.setSelection(i);
            }
        }

        unitFromSpinner = (Spinner) findViewById(R.id.unit_from_spinner);
        unitFromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                prefs.currentFromIndex = i;
                prefs.save(WidgetSetupActivity.this);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        unitToSpinner = (Spinner) findViewById(R.id.unit_to_spinner);
        unitToSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                prefs.currentToIndex = i;
                prefs.save(WidgetSetupActivity.this);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (widgetId == -1) {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

    private void setUnitSpinners(int category) {
        unitFromSpinner.setAdapter(new ArrayAdapter(this, R.layout.unit_categoryitem, collections[category].getItems()));
        unitFromSpinner.setSelection(prefs.currentFromIndex);

        unitToSpinner.setAdapter(new ArrayAdapter(this, R.layout.unit_categoryitem, collections[category].getItems()));
        unitToSpinner.setSelection(prefs.currentToIndex);
    }
}

