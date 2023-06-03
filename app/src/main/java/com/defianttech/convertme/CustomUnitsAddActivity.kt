package com.defianttech.convertme

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.defianttech.convertme.databinding.CustomUnitsAddActivityBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/*
 * Copyright (c) 2022 Dmitry Brant
 */
class CustomUnitsAddActivity : AppCompatActivity() {
    private lateinit var binding: CustomUnitsAddActivityBinding
    
    private var categories = UnitCollection.getInstance(this)
    private var allCategoryNames = UnitCollection.getAllCategoryNames(this)
    private val textWatcher = UnitTextWatcher()
    private var editUnit: CustomUnits.CustomUnit? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CustomUnitsAddActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.navigationBarColor = getColor(R.color.number_pad_background)
        }

        val editUnitId = intent.getIntExtra(ConvertActivity.INTENT_EXTRA_UNIT_ID, 0)
        if (editUnitId != 0) {
            editUnit = UnitCollection.getCustomUnits(this).units.first { it.id == editUnitId }
        }

        supportActionBar?.setTitle(if (isEditing()) R.string.edit_unit else R.string.add_new_unit)

        val categoryAdapter = ArrayAdapter(this, R.layout.unit_categoryitem, allCategoryNames)

        binding.unitCategorySpinner.adapter = categoryAdapter
        binding.unitCategorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, index: Int, l: Long) {
                val currentCategory = UnitCollection.collectionIndexByName(categories, allCategoryNames[index])
                binding.unitBaseSpinner.adapter = ArrayAdapter(this@CustomUnitsAddActivity, R.layout.unit_categoryitem, categories[currentCategory].items)

                if (isEditing()) {
                    val defaultIndex = categories[editUnit!!.categoryId].items.indexOfFirst { it.id == editUnit!!.baseUnitId }
                    if (defaultIndex >= 0) {
                        binding.unitBaseSpinner.setSelection(defaultIndex)
                    }
                } else {
                    // find the default base unit in this collection
                    val defaultIndex = categories[currentCategory].items.indexOfFirst { it.multiplier == 1.0 }
                    if (defaultIndex >= 0) {
                        binding.unitBaseSpinner.setSelection(defaultIndex)
                    }
                }
                updatePreview()
            }
            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }

        binding.unitBaseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, index: Int, l: Long) {
                updatePreview()
            }
            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }

        binding.unitNameText.addTextChangedListener(textWatcher)
        binding.unitMultiplierText.addTextChangedListener(textWatcher)

        binding.invertButton.setOnClickListener {
            val multiplier = binding.unitMultiplierText.text.toString().toDoubleOrNull()
            if (multiplier != null) {
                if (multiplier == 0.0) {
                    MaterialAlertDialogBuilder(this@CustomUnitsAddActivity)
                            .setMessage(R.string.nice_try)
                            .setPositiveButton(android.R.string.ok, null)
                            .show()
                } else {
                    binding.unitMultiplierText.setText((1 / multiplier).toString())
                }
            }
        }

        binding.addButton.setText(if (isEditing()) R.string.done_button else R.string.add_button)
        binding.addButton.setOnClickListener {
            if (isEditing()) {
                commitEditUnit()
            } else {
                addNewUnit()
            }
        }

        if (isEditing()) {
            binding.unitCategorySpinner.setSelection(allCategoryNames.indexOf(categories[editUnit!!.categoryId].names[0]))
            binding.unitCategorySpinner.isEnabled = false

            binding.unitNameText.setText(editUnit!!.name)
            binding.unitMultiplierText.setText((1.0 / editUnit!!.multiplier).toString())
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        binding.unitNameText.removeTextChangedListener(textWatcher)
        binding.unitMultiplierText.removeTextChangedListener(textWatcher)
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

    private fun isEditing(): Boolean {
        return editUnit != null
    }

    private fun updatePreview() {
        if (binding.unitCategorySpinner.selectedItemPosition == -1 || binding.unitBaseSpinner.selectedItemPosition == -1) {
            return
        }
        val multiplier = binding.unitMultiplierText.text.toString().toDoubleOrNull() ?: 0.0
        val currentCategory = UnitCollection.collectionIndexByName(categories, allCategoryNames[binding.unitCategorySpinner.selectedItemPosition])
        val baseUnitId = categories[currentCategory].items[binding.unitBaseSpinner.selectedItemPosition].id
        var baseUnit: SingleUnit? = null
        try {
            baseUnit = categories[currentCategory].items.first { it.id == baseUnitId }
        } catch (e: NoSuchElementException) {
        }
        if (binding.unitNameText.text.isNullOrEmpty() || multiplier == 0.0 || baseUnit == null) {
            binding.unitPreviewLabel.visibility = View.GONE
            binding.unitPreviewText.visibility = View.GONE
            return
        }
        binding.unitPreviewLabel.visibility = View.VISIBLE
        binding.unitPreviewText.visibility = View.VISIBLE
        binding.unitPreviewText.text = "1 " +  binding.unitNameText.text + " = " + multiplier + " " + baseUnit.name + "\n" +
                "1 " + baseUnit.name + " = " + (1.0 / multiplier) + " " + binding.unitNameText.text
    }

    private fun addNewUnit() {
        val currentCategory = UnitCollection.collectionIndexByName(categories, allCategoryNames[binding.unitCategorySpinner.selectedItemPosition])
        val baseUnitId = categories[currentCategory].items[binding.unitBaseSpinner.selectedItemPosition].id
        val offset = categories[currentCategory].items[binding.unitBaseSpinner.selectedItemPosition].offset
        val multiplier = binding.unitMultiplierText.text.toString().toDoubleOrNull()
        if (multiplier == null || multiplier == 0.0) {
            binding.unitMultiplierInput.error = getString(R.string.custom_unit_multiplier_invalid)
            return
        }
        if (binding.unitNameText.text.isNullOrEmpty()) {
            binding.unitNameInput.error = getString(R.string.custom_unit_name_invalid)
            return
        }
        UnitCollection.addCustomUnit(this, currentCategory, baseUnitId, 1.0 / multiplier, offset, binding.unitNameText.text.toString())
        setResult(ConvertActivity.RESULT_CODE_CUSTOM_UNITS_CHANGED)
        finish()
    }

    private fun commitEditUnit() {
        val currentCategory = editUnit!!.categoryId
        val baseUnitId = categories[currentCategory].items[binding.unitBaseSpinner.selectedItemPosition].id
        val multiplier = binding.unitMultiplierText.text.toString().toDoubleOrNull()
        if (multiplier == null || multiplier == 0.0) {
            binding.unitMultiplierInput.error = getString(R.string.custom_unit_multiplier_invalid)
            return
        }
        if (binding.unitNameText.text.isNullOrEmpty()) {
            binding.unitNameInput.error = getString(R.string.custom_unit_name_invalid)
            return
        }
        UnitCollection.editCustomUnit(this, editUnit!!.id, baseUnitId, 1.0 / multiplier, binding.unitNameText.text.toString())
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
