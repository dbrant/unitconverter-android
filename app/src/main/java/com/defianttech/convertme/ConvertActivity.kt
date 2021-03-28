package com.defianttech.convertme

import android.content.*
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.view.*
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import androidx.annotation.ColorInt
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.defianttech.convertme.NumberPadView.OnValueChangedListener
import com.defianttech.convertme.databinding.ConvertmeBinding
import java.text.DecimalFormat
import kotlin.math.abs

/*
* Copyright (c) 2014-2019 Dmitry Brant
*/
class ConvertActivity : AppCompatActivity() {
    private lateinit var binding: ConvertmeBinding

    private lateinit var collections: Array<UnitCollection>
    private lateinit var allCategoryNames: Array<String>

    private var currentCategory = UnitCollection.DEFAULT_CATEGORY
    private var currentUnitIndex = UnitCollection.DEFAULT_FROM_INDEX
    private var currentValue = UnitCollection.DEFAULT_VALUE

    private lateinit var categoryMenu: PopupMenu
    private var listAdapter = UnitListAdapter()
    private var actionMode: ActionMode? = null
    private var editModeEnabled = false

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ConvertmeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        resetLists()

        setSupportActionBar(binding.mainToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.toolbarContents.categoryToolbarContainer.setOnClickListener { categoryMenu.show() }
        categoryMenu = PopupMenu(this@ConvertActivity, binding.toolbarContents.categoryToolbarContainer)

        for ((i, name) in allCategoryNames.withIndex()) {
            categoryMenu.menu.add(0, i, 0, name)
        }

        categoryMenu.setOnMenuItemClickListener { item ->
            currentCategory = UnitCollection.collectionIndexByName(collections,
                    allCategoryNames[item.itemId])
            if (currentUnitIndex >= collections[currentCategory].length()) {
                currentUnitIndex = 0
            }
            binding.toolbarContents.categoryText.text = item.title
            listAdapter.notifyDataSetInvalidated()
            true
        }

        binding.unitsList.adapter = listAdapter
        binding.unitsList.onItemClickListener = OnItemClickListener { _, _, position, _ ->
            if (editModeEnabled) {
                collections[currentCategory][position]
                        .isEnabled = !collections[currentCategory][position].isEnabled
            } else {
                currentUnitIndex = position
            }
            listAdapter.notifyDataSetChanged()
        }

        binding.unitsList.onItemLongClickListener = OnItemLongClickListener { _, view, position, _ ->
            if (editModeEnabled) {
                return@OnItemLongClickListener false
            }
            doLongPressMenu(view.findViewById(R.id.unitValue), position)
            true
        }

        binding.numberPad.valueChangedListener = object : OnValueChangedListener {
            override fun onValueChanged(value: String) {
                setValueFromNumberPad(value)
                listAdapter.notifyDataSetChanged()
            }
        }

        restoreSettings()

        for (name in allCategoryNames) {
            if (name == collections[currentCategory].names[0]) {
                binding.toolbarContents.categoryText.text = name
            }
        }

        binding.fabEdit.setOnClickListener { startSupportActionMode(EditUnitsActionModeCallback()) }
    }

    public override fun onStop() {
        super.onStop()
        saveSettings()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.menu_about -> {
                showAboutDialog()
                return true
            }
            R.id.menu_custom_units -> {
                startActivityForResult(Intent(this, CustomUnitsActivity::class.java), REQUEST_CODE_CUSTOM_UNITS)
                return true
            }
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CUSTOM_UNITS && resultCode == RESULT_CODE_CUSTOM_UNITS_CHANGED) {
            resetLists()
            restoreSettings()
        }
    }

    private fun resetLists() {
        collections = UnitCollection.getInstance(this)
        allCategoryNames = UnitCollection.getAllCategoryNames(this)
        listAdapter.notifyDataSetChanged()
    }

    private fun saveSettings() {
        val editor = getPrefs(this).edit()
        editor.putInt(KEY_CURRENT_CATEGORY, currentCategory)
        editor.putInt(KEY_CURRENT_UNIT, currentUnitIndex)
        editor.putString(KEY_CURRENT_VALUE, binding.numberPad.currentValue)
        for (col in collections) {
            for (unit in col.items) {
                editor.putBoolean(unit.name, unit.isEnabled)
            }
        }
        editor.apply()
    }

    private fun restoreSettings() {
        try {
            val prefs = getPrefs(this)
            currentCategory = prefs.getInt(KEY_CURRENT_CATEGORY, UnitCollection.DEFAULT_CATEGORY)
            if (currentCategory >= collections.size) {
                currentCategory = UnitCollection.DEFAULT_CATEGORY
            }
            currentUnitIndex = prefs.getInt(KEY_CURRENT_UNIT, UnitCollection.DEFAULT_FROM_INDEX)
            if (currentUnitIndex >= collections[currentCategory].length()) {
                currentUnitIndex = 0
            }
            binding.numberPad.currentValue = prefs.getString(KEY_CURRENT_VALUE, "1")!!
            setValueFromNumberPad(binding.numberPad.currentValue)
            for (col in collections) {
                for (unit in col.items) {
                    unit.isEnabled = prefs.getBoolean(unit.name, true)
                }
            }
        } catch (ex: Exception) {
            //ehh...
        }
    }

    private fun updateActionModeState() {
        binding.numberPad.visibility = if (editModeEnabled) View.GONE else View.VISIBLE
        if (editModeEnabled) {
            binding.fabEdit.hide()
        } else {
            binding.fabEdit.show()
        }
        listAdapter.notifyDataSetInvalidated()
    }

    private fun setValueFromNumberPad(value: String) {
        currentValue = try {
            value.toDouble()
        } catch (e: NumberFormatException) {
            0.0
        }
    }

    private inner class EditUnitsActionModeCallback : ActionMode.Callback {
        @ColorInt
        var statusBarColor = 0
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            actionMode = mode
            actionMode!!.title = getString(R.string.show_hide_units)
            editModeEnabled = true
            updateActionModeState()
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                statusBarColor = window.statusBarColor
                window.statusBarColor = Color.BLACK
            }
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            if (item.itemId == android.R.id.home) {
                mode.finish()
                return true
            }
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.statusBarColor = statusBarColor
            }
            actionMode = null
            editModeEnabled = false
            updateActionModeState()
        }
    }

    private inner class UnitListAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return collections[currentCategory].length()
        }

        override fun getItem(position: Int): Any {
            return collections[currentCategory][position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItemViewType(position: Int): Int {
            return if (editModeEnabled) {
                0
            } else {
                if (collections[currentCategory][position].isEnabled) 0 else 1
            }
        }

        override fun getViewTypeCount(): Int {
            return 2
        }

        override fun getView(position: Int, convView: View?, parent: ViewGroup): View {
            var convertView = convView
            if (editModeEnabled) {
                if (convertView == null) {
                    convertView = layoutInflater.inflate(R.layout.unit_listitem, parent, false)
                }
                val itemContainer = convertView!!.findViewById<View>(R.id.unitItemContainer)
                val unitName = convertView.findViewById<TextView>(R.id.unitName)
                val unitValue = convertView.findViewById<TextView>(R.id.unitValue)
                unitValue.visibility = View.GONE
                val chkEnable = convertView.findViewById<ImageView>(R.id.chkSelected)
                chkEnable.visibility = View.VISIBLE
                unitName.text = Html.fromHtml(collections[currentCategory][position].name)
                if (position == currentUnitIndex) {
                    binding.unitsList.setItemChecked(position, true)
                }
                itemContainer.setBackgroundColor(ContextCompat.getColor(this@ConvertActivity,
                        android.R.color.transparent))
                chkEnable.setImageResource(if (collections[currentCategory][position].isEnabled) R.drawable.ic_check_box_white_24dp else R.drawable.ic_check_box_outline_blank_white_24dp)
            } else {
                if (collections[currentCategory][position].isEnabled) {
                    if (convertView == null) {
                        convertView = layoutInflater.inflate(R.layout.unit_listitem, parent, false)
                    }
                    val itemContainer = convertView!!.findViewById<View>(R.id.unitItemContainer)
                    val unitName = convertView.findViewById<TextView>(R.id.unitName)
                    val unitValue = convertView.findViewById<TextView>(R.id.unitValue)
                    unitValue.visibility = View.VISIBLE
                    val chkEnable = convertView.findViewById<ImageView>(R.id.chkSelected)
                    chkEnable.visibility = View.GONE
                    unitName.text = Html.fromHtml(collections[currentCategory][position].name)
                    if (position == currentUnitIndex) {
                        binding.unitsList.setItemChecked(position, true)
                    }
                    ViewCompat.setBackground(itemContainer,
                            ContextCompat.getDrawable(this@ConvertActivity,
                                    R.drawable.selectable_item_background))
                    val p = UnitCollection.convert(this@ConvertActivity, currentCategory,
                            currentUnitIndex, position, currentValue)
                    unitValue.text = getFormattedValueStr(p)
                } else {
                    if (convertView == null) {
                        val params = AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)
                        convertView = View(this@ConvertActivity)
                        convertView.layoutParams = params
                    }
                }
            }
            return convertView
        }
    }

    private fun doLongPressMenu(parentView: View, position: Int) {
        val menu = PopupMenu(this, parentView, Gravity.END or Gravity.CENTER_HORIZONTAL)
        menu.menuInflater.inflate(R.menu.menu_long_press, menu.menu)
        menu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { menuItem ->
            val resultStr: String
            when (menuItem.itemId) {
                R.id.menu_copy_value -> {
                    resultStr = String.format("%1\$s", getValueStr(UnitCollection.convert(this@ConvertActivity, currentCategory, currentUnitIndex, position, currentValue)))
                    setClipboardText(resultStr)
                    return@OnMenuItemClickListener true
                }
                R.id.menu_copy_row -> {
                    resultStr = String.format("%1\$s %2\$s = %3\$s %4\$s", getValueStr(currentValue),
                            collections[currentCategory][currentUnitIndex].name,
                            getValueStr(UnitCollection.convert(this@ConvertActivity, currentCategory, currentUnitIndex, position, currentValue)),
                            collections[currentCategory][position].name)
                    setClipboardText(resultStr)
                    return@OnMenuItemClickListener true
                }
            }
            false
        })
        menu.show()
    }

    private fun setClipboardText(text: String) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("", text))
        Toast.makeText(this@ConvertActivity, R.string.menu_clipboard_copied, Toast.LENGTH_SHORT).show()
    }

    private fun showAboutDialog() {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.menu_about))
                .setMessage(getString(R.string.about_message))
                .setPositiveButton(R.string.ok, null)
                .create()
                .show()
    }

    companion object {
        private const val TAG = "ConvertActivity"
        private const val PREFS_NAME = "ConvertMePrefs"
        private const val KEY_CURRENT_CATEGORY = "currentCategory"
        private const val KEY_CURRENT_UNIT = "currentUnitIndex"
        private const val KEY_CURRENT_VALUE = "currentValue"
        const val REQUEST_CODE_CUSTOM_UNITS = 100
        const val RESULT_CODE_CUSTOM_UNITS_CHANGED = 1
        const val INTENT_EXTRA_UNIT_ID = "extra_unit_id"
        private val dfExp = DecimalFormat("#.#######E0")
        private val dfNoexp = DecimalFormat("#.#######")

        fun getPrefs(context: Context): SharedPreferences {
            return context.getSharedPreferences(PREFS_NAME, 0)
        }

        fun getFormattedValueStr(value: Double): Spanned {
            var strValue = getValueStr(value)
            try {
                if (strValue.contains("E")) {
                    strValue = strValue.replace("E", " × 10<sup><small>")
                    strValue += "</small></sup>"
                }
            } catch (e: Exception) {
                Log.d(TAG, "Error while rendering unit.", e)
            }
            return Html.fromHtml(strValue)
        }

        private fun getValueStr(value: Double): String {
            return if (abs(value) > 1e6 || abs(value) < 1e-6 && abs(value) > 0.0) {
                dfExp.format(value)
            } else {
                dfNoexp.format(value)
            }
        }
    }
}
