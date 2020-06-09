package com.defianttech.convertme

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.custom_units_add_activity.*
import java.lang.Exception

/*
 * Copyright (c) 2020 Dmitry Brant
 */
class CustomUnitsAddActivity : AppCompatActivity() {
    private var categories: Array<UnitCollection> = UnitCollection.getInstance(this)
    private var allCategoryNames: Array<String> = UnitCollection.getAllCategoryNames(this)

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
            }
            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }

        add_button.setOnClickListener {
            addNewUnit()
        }
    }

    public override fun onResume() {
        super.onResume()
    }

    public override fun onPause() {
        super.onPause()
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

    private fun addNewUnit() {
        val currentCategory = UnitCollection.collectionIndexByName(categories, allCategoryNames[unit_category_spinner.selectedItemPosition])
        val baseUnitId = categories[currentCategory].items[unit_base_spinner.selectedItemPosition].id
        val multiplier = unit_multiplier_text.text.toString().toDoubleOrNull()
        if (multiplier == null) {
            unit_multiplier_input.error = getString(R.string.custom_unit_multiplier_invalid)
            return
        }
        if (unit_name_text.text.isNullOrEmpty()) {
            unit_name_input.error = getString(R.string.custom_unit_name_invalid)
            return
        }
        UnitCollection.addCustomUnit(this, currentCategory, baseUnitId, multiplier, unit_name_text.text.toString())
        setResult(ConvertActivity.RESULT_CODE_CUSTOM_UNITS_CHANGED)
        finish()
    }
}

