package com.defianttech.convertme;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/*
 * Copyright (c) 2014-2016 Dmitry Brant
 */
public class NumberPadView extends LinearLayout {

    public interface OnValueChangedListener {
        void onValueChanged(String value);
    }

    private String currentValue = "0";
    @Nullable private OnValueChangedListener valueChangedListener;

    private OnClickListener numberClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            String text = ((TextView) view).getText().toString();
            appendDigit(text);
            if (valueChangedListener != null) {
                valueChangedListener.onValueChanged(currentValue);
            }
        }
    };

    public NumberPadView(Context context) {
        super(context);
        init(context);
    }

    public NumberPadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public NumberPadView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void setOnValueChangedListener(@Nullable OnValueChangedListener listener) {
        valueChangedListener = listener;
    }

    public String getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(String value) {
        currentValue = value;
    }

    private void init(@NonNull Context context) {
        inflate(context, R.layout.number_pad_view, this);

        findViewById(R.id.btn0).setOnClickListener(numberClickListener);
        findViewById(R.id.btn1).setOnClickListener(numberClickListener);
        findViewById(R.id.btn2).setOnClickListener(numberClickListener);
        findViewById(R.id.btn3).setOnClickListener(numberClickListener);
        findViewById(R.id.btn4).setOnClickListener(numberClickListener);
        findViewById(R.id.btn5).setOnClickListener(numberClickListener);
        findViewById(R.id.btn6).setOnClickListener(numberClickListener);
        findViewById(R.id.btn7).setOnClickListener(numberClickListener);
        findViewById(R.id.btn8).setOnClickListener(numberClickListener);
        findViewById(R.id.btn9).setOnClickListener(numberClickListener);
        findViewById(R.id.btnNegative).setOnClickListener(numberClickListener);
        findViewById(R.id.btnDecimal).setOnClickListener(numberClickListener);

        findViewById(R.id.btnBackspace).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentValue.length() > 0) {
                    currentValue = currentValue.substring(0, currentValue.length() - 1);
                }
                if(currentValue.length() == 0) {
                    currentValue = "0";
                }
                if (valueChangedListener != null) {
                    valueChangedListener.onValueChanged(currentValue);
                }
            }
        });
        findViewById(R.id.btnClear).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                currentValue = "0";
                if (valueChangedListener != null) {
                    valueChangedListener.onValueChanged(currentValue);
                }
            }
        });
    }

    private void appendDigit(@NonNull String str) {
        String curVal = currentValue;
        if (str.equals(".")) {
            if (!curVal.contains(str)) {
                curVal += ".";
            }
        } else if (str.equals("±")) {
            if (curVal.startsWith("-")) {
                curVal = curVal.substring(1, curVal.length());
            } else {
                curVal = "-" + curVal;
            }
        } else {
            if (curVal.equals("0")) {
                curVal = str;
            } else if (curVal.equals("-0")) {
                curVal = "-" + str;
            } else {
                curVal += str;
            }
        }
        currentValue = curVal;
    }
}