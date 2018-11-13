package com.example.forrestsu.logintest.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.forrestsu.logintest.R;

public class SetTimeFragment extends Fragment {

    private static final String TAG = "SetTimeFragment";

    private SetTimeFragment.SendMessageTime sendMessageTime;

    private NumberPicker hourNP, minuteNP;
    int hour = 12;
    int minute = 30;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View square = inflater.inflate(R.layout.fragment_set_time, container, false);

        initView(square);

        return square;
    }

    /*
  初始化
   */
    public void initView(View square) {
        TextView confirmTV = (TextView) square.findViewById(R.id.tv_confirm);
        confirmTV.setOnClickListener(new MyOnClickListener());
        TextView cancelTV = (TextView) square.findViewById(R.id.tv_cancel);
        cancelTV.setOnClickListener(new MyOnClickListener());

        //hourNP
        hourNP = (NumberPicker) square.findViewById(R.id.np_hour);
        hourNP.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        hourNP.setMaxValue(24);
        hourNP.setMinValue(0);
        //设置hour初始值
        hourNP.setValue(hour);
        hourNP.setFocusable(true);
        hourNP.setFocusableInTouchMode(true);

        //minuteNP
        minuteNP = (NumberPicker) square.findViewById(R.id.np_minute);
        minuteNP.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        minuteNP.setMaxValue(60);
        minuteNP.setMinValue(0);
        //设置minute初始值
        minuteNP.setValue(minute);
        minuteNP.setFocusable(true);
        minuteNP.setFocusableInTouchMode(true);
    }

    public class MyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.tv_confirm:
                    String time = String.valueOf(hourNP.getValue()) + ":" + String.valueOf(minuteNP.getValue());
                    sendMessageTime.getMessageTime(time);
                    break;
                case R.id.tv_cancel:
                    sendMessageTime.getMessageTime();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof SendMessageTime) {
            sendMessageTime = (SendMessageTime) context;
        } else {
            throw new IllegalArgumentException("Activity must implements SendMessageSituation");
        }
    }


    /**
     * 回调接口，用于向activity传参
     */
    public interface SendMessageTime {
        void getMessageTime(String time);
        void getMessageTime();
    }

}
