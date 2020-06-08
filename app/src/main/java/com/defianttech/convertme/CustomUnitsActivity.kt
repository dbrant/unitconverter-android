package com.defianttech.convertme

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.custom_units_activity.*

/*
 * Copyright (c) 2020 Dmitry Brant
 */
class CustomUnitsActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_units_activity)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.custom_units)

        add_button.setOnClickListener {
            startActivityForResult(Intent(this, CustomUnitsAddActivity::class.java), ConvertActivity.REQUEST_CODE_CUSTOM_UNITS)
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
}

