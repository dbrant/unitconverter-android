package com.defianttech.convertme

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.custom_units_activity.*
import kotlinx.android.synthetic.main.custom_units_activity.add_button
import kotlinx.android.synthetic.main.custom_units_activity.toolbar
import kotlinx.android.synthetic.main.custom_units_add_activity.*


/*
 * Copyright (c) 2020 Dmitry Brant
 */
class CustomUnitsActivity : AppCompatActivity() {
    private var customUnits: CustomUnits? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_units_activity)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.custom_units)

        resetList()
        units_recycler_view.layoutManager = LinearLayoutManager(this)
        units_recycler_view.adapter = RecyclerAdapter()

        add_button.setOnClickListener {
            startActivityForResult(Intent(this, CustomUnitsAddActivity::class.java), ConvertActivity.REQUEST_CODE_CUSTOM_UNITS)
        }
    }

    private fun resetList() {
        customUnits = UnitCollection.getCustomUnits(this)
        units_recycler_view.adapter?.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ConvertActivity.REQUEST_CODE_CUSTOM_UNITS && resultCode == ConvertActivity.RESULT_CODE_CUSTOM_UNITS_CHANGED) {
            setResult(ConvertActivity.RESULT_CODE_CUSTOM_UNITS_CHANGED)
            resetList()
        }
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
            val unit = customUnits!!.units[position]
            editButton.tag = position
            deleteButton.tag = position
            nameView.text = unit.name
            val categories = UnitCollection.getInstance(this@CustomUnitsActivity)
            val baseUnit = categories[unit.categoryId].items.first { u -> u.id == unit.baseUnitId }
            if (baseUnit == null) {
                // TODO: add warning
                return
            }
            unitBaseView.text = "1 " +  unit.name + " = " + (1.0 / unit.multiplier) + " " + baseUnit.name + "\n" +
                    "1 " + baseUnit.name + " = " + unit.multiplier + " " + unit.name
        }

        override fun onClick(v: View?) {
            val unit = customUnits!!.units[v?.tag as Int]
            if (v == editButton) {

            } else if (v == deleteButton) {
                // check if other custom units depend on this one...
                for (u in customUnits!!.units) {
                    if (u.baseUnitId == unit.id) {
                        AlertDialog.Builder(this@CustomUnitsActivity)
                                .setMessage(getString(R.string.delete_unit_dependent, unit.name, u.name))
                                .setPositiveButton(android.R.string.ok, null)
                                .create()
                                .show()
                        return
                    }
                }
                AlertDialog.Builder(this@CustomUnitsActivity)
                        .setMessage(getString(R.string.delete_unit_confirm, unit.name))
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(android.R.string.ok) { _: DialogInterface, i: Int ->
                            UnitCollection.deleteCustomUnit(this@CustomUnitsActivity, unit)
                            resetList()
                            setResult(ConvertActivity.RESULT_CODE_CUSTOM_UNITS_CHANGED)
                        }
                        .create()
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
            return customUnits!!.units.size
        }
    }
}

