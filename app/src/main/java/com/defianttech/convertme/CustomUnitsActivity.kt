package com.defianttech.convertme

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.defianttech.convertme.databinding.CustomUnitsActivityBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/*
 * Copyright (c) 2022 Dmitry Brant
 */
class CustomUnitsActivity : AppCompatActivity() {
    private lateinit var binding: CustomUnitsActivityBinding
    private lateinit var customUnits: CustomUnits

    private val customUnitsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == ConvertActivity.RESULT_CODE_CUSTOM_UNITS_CHANGED) {
            setResult(ConvertActivity.RESULT_CODE_CUSTOM_UNITS_CHANGED)
            resetList()
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CustomUnitsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setTitle(R.string.custom_units)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.navigationBarColor = getColor(R.color.number_pad_background)
        }

        resetList()
        binding.unitsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.unitsRecyclerView.adapter = RecyclerAdapter()

        binding.addButton.setOnClickListener {
            customUnitsLauncher.launch(Intent(this, CustomUnitsAddActivity::class.java))
        }
    }

    private fun resetList() {
        customUnits = UnitCollection.getCustomUnits(this)
        binding.unitsRecyclerView.adapter?.notifyDataSetChanged()
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

    internal inner class DefaultViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private val nameView: TextView = itemView.findViewById(R.id.unitName)
        private val unitBaseView: TextView = itemView.findViewById(R.id.unitBaseText)
        private val editButton: View = itemView.findViewById(R.id.btnEditUnit)
        private val deleteButton: View = itemView.findViewById(R.id.btnDeleteUnit)

        init {
            editButton.setOnClickListener(this)
            deleteButton.setOnClickListener(this)
        }

        fun bind(position: Int) {
            val unit = customUnits.units[position]
            editButton.tag = position
            deleteButton.tag = position
            nameView.text = unit.name
            val categories = UnitCollection.getInstance(this@CustomUnitsActivity)
            try {
                val baseUnit = categories[unit.categoryId].items.first { it.id == unit.baseUnitId }
                unitBaseView.text = "1 ${unit.name} = ${(1.0 / unit.multiplier)} ${baseUnit.name}\n1 ${baseUnit.name} = ${unit.multiplier} ${unit.name}"
            } catch (e: NoSuchElementException) {
            }
        }

        override fun onClick(v: View?) {
            val unit = customUnits.units[v?.tag as Int]
            if (v == editButton) {

                val intent = Intent(this@CustomUnitsActivity, CustomUnitsAddActivity::class.java)
                        .putExtra(ConvertActivity.INTENT_EXTRA_UNIT_ID, unit.id)
                customUnitsLauncher.launch(intent)

            } else if (v == deleteButton) {
                // check if other custom units depend on this one...
                for (u in customUnits.units) {
                    if (u.baseUnitId == unit.id) {
                        MaterialAlertDialogBuilder(this@CustomUnitsActivity)
                                .setMessage(getString(R.string.delete_unit_dependent, unit.name, u.name))
                                .setPositiveButton(android.R.string.ok, null)
                                .show()
                        return
                    }
                }
                MaterialAlertDialogBuilder(this@CustomUnitsActivity)
                        .setMessage(getString(R.string.delete_unit_confirm, unit.name))
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            UnitCollection.deleteCustomUnit(this@CustomUnitsActivity, unit)
                            resetList()
                            setResult(ConvertActivity.RESULT_CODE_CUSTOM_UNITS_CHANGED)
                        }
                        .show()
            }
        }
    }

    internal inner class RecyclerAdapter : RecyclerView.Adapter<DefaultViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultViewHolder {
            val view = LayoutInflater.from(this@CustomUnitsActivity).inflate(R.layout.custom_unit, null)
            val params = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
            val margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics).toInt()
            params.setMargins(margin, margin, margin, margin)
            view.layoutParams = params
            return DefaultViewHolder(view)
        }

        override fun onBindViewHolder(holder: DefaultViewHolder, position: Int) {
            holder.bind(position)
        }

        override fun getItemCount(): Int {
            return customUnits.units.size
        }
    }
}
