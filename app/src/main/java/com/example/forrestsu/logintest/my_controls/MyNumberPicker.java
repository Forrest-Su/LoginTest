package com.example.forrestsu.logintest.my_controls;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import java.lang.reflect.Field;

public class MyNumberPicker extends NumberPicker {

    public MyNumberPicker(Context context) {
        super(context);
    }

    public MyNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /*
    public MyNumberPicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
     */

    @Override
    public void addView(View child) {
        super.addView(child);
        //updateView(child);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        super.addView(child, params);
        //updateView(child);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        //updateView(child);
    }


    public void updateView(View view) {
        if (view instanceof EditText) {
            //设置字体属性
            //((EditText) view).setTextColor(getResources().getColor(R.color.blue_5fc0cd));
        }
    }

    /*
    设置分割线颜色
     */
    public void setDividerColor(NumberPicker numberPicker, int resource) {
        NumberPicker picker = numberPicker;
        Field[] pickerFileds = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFileds) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    pf.set(picker, new ColorDrawable(this.getResources().getColor( resource)));
                } catch(IllegalArgumentException e) {
                    e.printStackTrace();
                } catch(Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch(IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    /*
    设置分割线高度（粗细）
     */
    public void setDividerHeight(NumberPicker numberPicker, int height) {
        NumberPicker picker = numberPicker;
        Field[] pickerFileds = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFileds) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    pf.set(picker, height);
                } catch(IllegalArgumentException e) {
                    e.printStackTrace();
                } catch(Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch(IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    /*
    设置间距
     */
    public void setPickerMargin(NumberPicker numberPicker) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) numberPicker.getLayoutParams();
        params.setMargins(0, 0, 0, 0);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            params.setMarginStart(0);
            params.setMarginEnd(0);
        }
    }

}
