package com.defianttech.convertme

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.defianttech.convertme.databinding.NumberPadViewBinding

/*
 * Copyright (c) 2014-2020 Dmitry Brant
 */
class NumberPadView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : ConstraintLayout(context, attrs, defStyle), View.OnClickListener {

    interface OnValueChangedListener {
        fun onValueChanged(value: String)
    }

    private val binding = NumberPadViewBinding.inflate(LayoutInflater.from(context), this)

    var currentValue = "0"
    var valueChangedListener: OnValueChangedListener? = null

    init {
        binding.btn0.setOnClickListener(this)
        binding.btn1.setOnClickListener(this)
        binding.btn2.setOnClickListener(this)
        binding.btn3.setOnClickListener(this)
        binding.btn4.setOnClickListener(this)
        binding.btn5.setOnClickListener(this)
        binding.btn6.setOnClickListener(this)
        binding.btn7.setOnClickListener(this)
        binding.btn8.setOnClickListener(this)
        binding.btn9.setOnClickListener(this)
        binding.btnNegative.setOnClickListener(this)
        binding.btnDecimal.setOnClickListener(this)

        binding.btnBackspace.setOnClickListener {
            if (currentValue.isNotEmpty()) {
                currentValue = currentValue.substring(0, currentValue.length - 1)
            }
            if (currentValue.isEmpty()) {
                currentValue = "0"
            }
            valueChangedListener?.onValueChanged(currentValue)
        }
        binding.btnClear.setOnClickListener {
            currentValue = "0"
            valueChangedListener?.onValueChanged(currentValue)
        }
    }

    private fun appendDigit(str: String) {
        var curVal = currentValue
        if (str == ".") {
            if (!curVal.contains(str)) {
                curVal += "."
            }
        } else if (str == "±") {
            curVal = if (curVal.startsWith("-")) {
                curVal.substring(1, curVal.length)
            } else {
                "-$curVal"
            }
        } else {
            when (curVal) {
                "0" -> { curVal = str }
                "-0" -> { curVal = "-$str" }
                else -> { curVal += str }
            }
        }
        currentValue = curVal
    }

    override fun onClick(v: View?) {
        val text = (v as TextView).text.toString()
        appendDigit(text)
        valueChangedListener?.onValueChanged(currentValue)
    }
}
