package com.example.forrestsu.logintest.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.forrestsu.logintest.R;
import com.example.forrestsu.logintest.utils.MyCalendar;

public class SetDateFragment extends Fragment {
    private static final String TAG = "SetDateFragment";

    private SendMessageDate sendMessageDate;

    private NumberPicker yearNP, monthNP, dayNP;
    int year = 1900;
    int month = 1;
    int day = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View square = inflater.inflate(R.layout.fragment_set_date, container, false);

        initView(square);

        return square;
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart: Fragment的onStart（）方法启动");
        super.onStart();
        if (isAdded()) {//判断Fragment已经依附Activity
            year = Integer.parseInt(getArguments().getString("year_limit"));
            Log.d(TAG, "onStart: year：" + year);
            initNumberPicker();
        }
    }

    /*
    初始化
     */
    public void initView(View square) {
        TextView confirmTV = (TextView) square.findViewById(R.id.tv_confirm);
        confirmTV.setOnClickListener(new MyOnClickListener());
        TextView cancelTV = (TextView) square.findViewById(R.id.tv_cancel);
        cancelTV.setOnClickListener(new MyOnClickListener());
        //yearNP
        yearNP = (NumberPicker) square.findViewById(R.id.np_hour);
        yearNP.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        //monthNP
        monthNP = (NumberPicker) square.findViewById(R.id.np_minute);
        monthNP.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        //dayNP
        dayNP = (NumberPicker) square.findViewById(R.id.np_area);
        dayNP.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    }

    public void initNumberPicker() {
        //设置NP
        yearNP.setMaxValue(year + 1);
        yearNP.setMinValue(year);
        yearNP.setValue(year);
        yearNP.setFocusable(true);
        yearNP.setFocusableInTouchMode(true);
        yearNP.setOnValueChangedListener(yearChangeListener);

        monthNP.setMaxValue(12);
        monthNP.setMinValue(1);
        monthNP.setValue(month);
        monthNP.setFocusable(true);
        monthNP.setFocusableInTouchMode(true);
        monthNP.setOnValueChangedListener(monthChangeListener);

        dayNP.setMaxValue(31);
        dayNP.setMinValue(1);
        dayNP.setValue(day);
        dayNP.setFocusable(true);
        dayNP.setFocusableInTouchMode(true);
        dayNP.setOnValueChangedListener(dayChangeListener);
    }

    public class MyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.tv_confirm:
                    String date = String.valueOf(yearNP.getValue()) + "-" + String.valueOf(monthNP.getValue())
                            + "-" + String.valueOf(dayNP.getValue());
                    sendMessageDate.getMessageDate(date);
                    break;
                case R.id.tv_cancel:
                    sendMessageDate.getMessageDate();
                    break;
                default:
                    break;
            }
        }
    }

    private NumberPicker.OnValueChangeListener yearChangeListener = new NumberPicker.OnValueChangeListener() {
        @Override
        /*
        普通闰年:能被4整除但不能被100整除的年份为普通闰年。（如2004年就是闰年，1999年不是闰年）；
        世纪闰年:能被400整除的为世纪闰年。（如2000年是闰年，1900年不是闰年）；
        计算公式：(hour % 4 == 0) && (hour % 100 != 0) || (hour % 400 == 0)
         */
        public void onValueChange(NumberPicker arg0, int arg1, int arg2) {
            year = yearNP.getValue();
            if (((year % 4 == 0) && (year % 100 != 0) && (monthNP.getValue() == 2))
                    || ((year % 400 == 0) && (monthNP.getValue() == 2))) {
                dayNP.setMaxValue(29);
            } else {
                dayNP.setMaxValue(28);
            }
        }
    };

    private NumberPicker.OnValueChangeListener monthChangeListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker arg0, int arg1, int arg2) {
            month = monthNP.getValue();
            switch(month) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    dayNP.setMaxValue(31);
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    dayNP.setMaxValue(30);
                    break;
                case 2:
                    int year = yearNP.getValue();
                    if ((year % 4 == 0) && (year % 100 != 0) || (year % 400 == 0)) {
                        dayNP.setMaxValue(29);
                    } else {
                        dayNP.setMaxValue(28);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private NumberPicker.OnValueChangeListener dayChangeListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker arg0, int arg1, int arg2) {
            day = dayNP.getValue();
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof SendMessageDate) {
            sendMessageDate = (SendMessageDate) context;
        } else {
            throw new IllegalArgumentException("Activity must implements SendMessageSituation");
        }
    }


    /**
     * 回调接口，用于向activity传参
     */
    public interface SendMessageDate {
        void getMessageDate(String date);
        void getMessageDate();
    }


}
