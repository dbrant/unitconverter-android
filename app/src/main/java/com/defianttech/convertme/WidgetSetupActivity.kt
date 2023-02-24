package com.defianttech.convertme

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.defianttech.convertme.databinding.WidgetSetupActivityBinding

/*
 * Copyright (c) 2014-2020 Dmitry Brant
 */
class WidgetSetupActivity : AppCompatActivity() {
    private lateinit var binding: WidgetSetupActivityBinding

    private var collections = UnitCollection.getInstance(this)
    private var allCategoryNames = UnitCollection.getAllCategoryNames(this)
    private var widgetId = -1
    private lateinit var prefs: WidgetPrefs

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WidgetSetupActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setTitle(R.string.configure_widget)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.navigationBarColor = getColor(R.color.number_pad_background)
        }

        if (!intent.action.isNullOrEmpty() && intent.action!!.contains(WidgetProvider.CLICK_ACTION_SETTINGS)) {
            widgetId = WidgetProvider.getWidgetId(intent.action!!)
        }
        if (widgetId == -1) {
            return
        }
        Log.d("WidgetSetupActivity", "Configuring widget $widgetId")

        prefs = WidgetPrefs(this, widgetId)

        val categoryAdapter = ArrayAdapter(this, R.layout.unit_categoryitem, allCategoryNames)

        binding.unitCategorySpinner.adapter = categoryAdapter
        binding.unitCategorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                prefs.currentCategory = UnitCollection.collectionIndexByName(collections, allCategoryNames[i])
                if (prefs.currentFromIndex > collections[prefs.currentCategory].length()) {
                    prefs.currentFromIndex = 0
                }
                if (prefs.currentToIndex > collections[prefs.currentCategory].length()) {
                    prefs.currentToIndex = 0
                }
                setUnitSpinners(prefs.currentCategory)
                prefs.save(this@WidgetSetupActivity)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }

        for (i in allCategoryNames.indices) {
            if (allCategoryNames[i] == collections[prefs.currentCategory].names[0]) {
                binding.unitCategorySpinner.setSelection(i)
            }
        }

        binding.unitFromSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                prefs.currentFromIndex = i
                prefs.save(this@WidgetSetupActivity)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }

        binding.unitToSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                prefs.currentToIndex = i
                prefs.save(this@WidgetSetupActivity)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }

        binding.unitIncrementText.setText(prefs.increment.toString())
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
            prefs.increment = java.lang.Float.parseFloat(binding.unitIncrementText.text.toString())
        } catch (e: NumberFormatException) {
            prefs.increment = 1f
        }

        prefs.save(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                return true
            }
        }
        return false
    }

    private fun setUnitSpinners(category: Int) {
        binding.unitFromSpinner.adapter = ArrayAdapter(this, R.layout.unit_categoryitem, collections[category].items)
        binding.unitFromSpinner.setSelection(prefs.currentFromIndex)

        binding.unitToSpinner.adapter = ArrayAdapter(this, R.layout.unit_categoryitem, collections[category].items)
        binding.unitToSpinner.setSelection(prefs.currentToIndex)
    }
}
