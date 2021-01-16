package com.defianttech.convertme

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter

/*
 * Copyright (c) 2014-2020 Dmitry Brant
 */
class WidgetSetupActivity : AppCompatActivity() {

    private var collections: Array<UnitCollection> = UnitCollection.getInstance(this)
    private var allCategoryNames: Array<String> = UnitCollection.getAllCategoryNames(this)
    private var widgetId = -1
    private var prefs: WidgetPrefs? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.widget_setup_activity)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle(R.string.configure_widget)

        if (!TextUtils.isEmpty(intent.action) && intent.action!!.contains(WidgetProvider.CLICK_ACTION_SETTINGS)) {
            widgetId = WidgetProvider.getWidgetId(intent.action!!)
        }
        if (widgetId == -1) {
            return
        }
        Log.d("WidgetSetupActivity", "Configuring widget $widgetId")

        prefs = WidgetPrefs(this, widgetId)

        val categoryAdapter = ArrayAdapter(this, R.layout.unit_categoryitem, allCategoryNames)

        unit_category_spinner.adapter = categoryAdapter
        unit_category_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                prefs!!.currentCategory = UnitCollection.collectionIndexByName(collections, allCategoryNames[i])
                if (prefs!!.currentFromIndex > collections[prefs!!.currentCategory].length()) {
                    prefs!!.currentFromIndex = 0
                }
                if (prefs!!.currentToIndex > collections[prefs!!.currentCategory].length()) {
                    prefs!!.currentToIndex = 0
                }
                setUnitSpinners(prefs!!.currentCategory)
                prefs!!.save(this@WidgetSetupActivity)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }

        for (i in allCategoryNames.indices) {
            if (allCategoryNames[i] == collections[prefs!!.currentCategory].names[0]) {
                unit_category_spinner.setSelection(i)
            }
        }

        unit_from_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                prefs!!.currentFromIndex = i
                prefs!!.save(this@WidgetSetupActivity)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }

        unit_to_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                prefs!!.currentToIndex = i
                prefs!!.save(this@WidgetSetupActivity)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }

        unit_increment_text.setText(prefs!!.increment.toString())
    }

    public override fun onResume() {
        super.onResume()
        if (widgetId == -1) {
            finish()
        }
    }

    public override fun onPause() {
        super.onPause()
        try {
            prefs!!.increment = java.lang.Float.parseFloat(unit_increment_text.text.toString())
        } catch (e: NumberFormatException) {
            prefs!!.increment = 1f
        }

        prefs!!.save(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return false
    }

    private fun setUnitSpinners(category: Int) {
        unit_from_spinner.adapter = ArrayAdapter(this, R.layout.unit_categoryitem, collections[category].items)
        unit_from_spinner.setSelection(prefs!!.currentFromIndex)

        unit_to_spinner.adapter = ArrayAdapter(this, R.layout.unit_categoryitem, collections[category].items)
        unit_to_spinner.setSelection(prefs!!.currentToIndex)
    }
}

