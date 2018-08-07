package com.defianttech.convertme

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.number_pad_view.view.*

/*
 * Copyright (c) 2014-2018 Dmitry Brant
 */
class NumberPadView : LinearLayout {

    var currentValue = "0"
    private var valueChangedListener: OnValueChangedListener? = null

    private val numberClickListener = OnClickListener { view ->
        val text = (view as TextView).text.toString()
        appendDigit(text)
        if (valueChangedListener != null) {
            valueChangedListener!!.onValueChanged(currentValue)
        }
    }

    interface OnValueChangedListener {
        fun onValueChanged(value: String)
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    fun setOnValueChangedListener(listener: OnValueChangedListener) {
        valueChangedListener = listener
    }

    private fun init(context: Context) {
        View.inflate(context, R.layout.number_pad_view, this)

        btn0.setOnClickListener(numberClickListener)
        btn1.setOnClickListener(numberClickListener)
        btn2.setOnClickListener(numberClickListener)
        btn3.setOnClickListener(numberClickListener)
        btn4.setOnClickListener(numberClickListener)
        btn5.setOnClickListener(numberClickListener)
        btn6.setOnClickListener(numberClickListener)
        btn7.setOnClickListener(numberClickListener)
        btn8.setOnClickListener(numberClickListener)
        btn9.setOnClickListener(numberClickListener)
        btnNegative.setOnClickListener(numberClickListener)
        btnDecimal.setOnClickListener(numberClickListener)

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
}