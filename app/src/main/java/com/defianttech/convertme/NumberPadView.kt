package com.defianttech.convertme

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.number_pad_view.view.*

/*
 * Copyright (c) 2014-2020 Dmitry Brant
 */
class NumberPadView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : ConstraintLayout(context, attrs, defStyle), View.OnClickListener {

    interface OnValueChangedListener {
        fun onValueChanged(value: String)
    }

    var currentValue = "0"
    var valueChangedListener: OnValueChangedListener? = null

    init {
        View.inflate(context, R.layout.number_pad_view, this)

        btn0.setOnClickListener(this)
        btn1.setOnClickListener(this)
        btn2.setOnClickListener(this)
        btn3.setOnClickListener(this)
        btn4.setOnClickListener(this)
        btn5.setOnClickListener(this)
        btn6.setOnClickListener(this)
        btn7.setOnClickListener(this)
        btn8.setOnClickListener(this)
        btn9.setOnClickListener(this)
        btnNegative.setOnClickListener(this)
        btnDecimal.setOnClickListener(this)

        btnBackspace.setOnClickListener {
            if (currentValue.isNotEmpty()) {
                currentValue = currentValue.substring(0, currentValue.length - 1)
            }
            if (currentValue.isEmpty()) {
                currentValue = "0"
            }
            valueChangedListener!!.onValueChanged(currentValue)
        }
        btnClear.setOnClickListener {
            currentValue = "0"
            valueChangedListener!!.onValueChanged(currentValue)
        }
    }

    private fun appendDigit(str: String) {
        var curVal = currentValue
        if (str == ".") {
            if (!curVal.contains(str)) {
                curVal += "."
            }
        } else if (str == "±") {
            if (curVal.startsWith("-")) {
                curVal = curVal.substring(1, curVal.length)
            } else {
                curVal = "-$curVal"
            }
        } else {
            if (curVal == "0") {
                curVal = str
            } else if (curVal == "-0") {
                curVal = "-$str"
            } else {
                curVal += str
            }
        }
        currentValue = curVal
    }

    override fun onClick(v: View?) {
        val text = (v as TextView).text.toString()
        appendDigit(text)
        valueChangedListener!!.onValueChanged(currentValue)
    }
}