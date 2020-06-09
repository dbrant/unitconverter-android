package com.defianttech.convertme

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.custom_units_add_activity.*

/*
 * Copyright (c) 2020 Dmitry Brant
 */
class CustomUnitsAddActivity : AppCompatActivity() {
    private var categories: Array<UnitCollection> = UnitCollection.getInstance(this)
    private var allCategoryNames: Array<String> = UnitCollection.getAllCategoryNames(this)
    private val textWatcher: UnitTextWatcher = UnitTextWatcher()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_units_add_activity)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.add_new_unit)

        val categoryAdapter = ArrayAdapter(this, R.layout.unit_categoryitem, allCategoryNames)

        unit_category_spinner.adapter = categoryAdapter
        unit_category_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, index: Int, l: Long) {
                val currentCategory = UnitCollection.collectionIndexByName(categories, allCategoryNames[index])
                unit_base_spinner.adapter = ArrayAdapter(this@CustomUnitsAddActivity, R.layout.unit_categoryitem, categories[currentCategory].items)

                // find the default base unit in this collection
                val defaultIndex = categories[currentCategory].items.indexOfFirst { unit -> unit.multiplier == 1.0 }
                if (defaultIndex >= 0) {
                    unit_base_spinner.setSelection(defaultIndex)
                }
                updatePreview()
            }
            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }

        unit_base_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, index: Int, l: Long) {
                updatePreview()
            }
            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }

        unit_name_text.addTextChangedListener(textWatcher)
        unit_multiplier_text.addTextChangedListener(textWatcher)

        add_button.setOnClickListener {
            addNewUnit()
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        unit_name_text.removeTextChangedListener(textWatcher)
        unit_multiplier_text.removeTextChangedListener(textWatcher)
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

    private fun updatePreview() {
        val multiplier = unit_multiplier_text.text.toString().toDoubleOrNull()
        val currentCategory = UnitCollection.collectionIndexByName(categories, allCategoryNames[unit_category_spinner.selectedItemPosition])
        val baseUnitId = categories[currentCategory].items[unit_base_spinner.selectedItemPosition].id
        val baseUnit = categories[currentCategory].items.first { u -> u.id == baseUnitId }
        if (unit_name_text.text.isNullOrEmpty() || multiplier == null || multiplier == 0.0 || baseUnit == null) {
            unit_preview_label.visibility = View.GONE
            unit_preview_text.visibility = View.GONE
            return
        }
        unit_preview_label.visibility = View.VISIBLE
        unit_preview_text.visibility = View.VISIBLE
        unit_preview_text.text = "1 " +  unit_name_text.text + " = " + multiplier + " " + baseUnit.name + "\n" +
                "1 " + baseUnit.name + " = " + (1.0 / multiplier) + " " + unit_name_text.text
    }

    private fun addNewUnit() {
        val currentCategory = UnitCollection.collectionIndexByName(categories, allCategoryNames[unit_category_spinner.selectedItemPosition])
        val baseUnitId = categories[currentCategory].items[unit_base_spinner.selectedItemPosition].id
        val multiplier = unit_multiplier_text.text.toString().toDoubleOrNull()
        if (multiplier == null || multiplier == 0.0) {
            unit_multiplier_input.error = getString(R.string.custom_unit_multiplier_invalid)
            return
        }
        if (unit_name_text.text.isNullOrEmpty()) {
            unit_name_input.error = getString(R.string.custom_unit_name_invalid)
            return
        }
        UnitCollection.addCustomUnit(this, currentCategory, baseUnitId, 1.0 / multiplier, unit_name_text.text.toString())
        setResult(ConvertActivity.RESULT_CODE_CUSTOM_UNITS_CHANGED)
        finish()
    }

    private inner class UnitTextWatcher : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            updatePreview()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }
}

