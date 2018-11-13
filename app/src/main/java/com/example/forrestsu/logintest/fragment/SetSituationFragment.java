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

public class SetSituationFragment extends Fragment {

    private static final String TAG = "SetSituationFragment";

    private String[] situations = {"能自理", "不能自理", "半失能", "手术后"};

    private SendMessageSituation sendMessageSituation;

    private NumberPicker situationNP;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View square = inflater.inflate(R.layout.fragment_set_situation, container, false);

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

        //situationNP
        situationNP = (NumberPicker) square.findViewById(R.id.np_situation);
        situationNP.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        situationNP.setDisplayedValues(situations);
        situationNP.setMaxValue(situations.length - 1);
        situationNP.setMinValue(0);
        //设置year初始值
        situationNP.setValue(0);
        situationNP.setFocusable(true);
        situationNP.setFocusableInTouchMode(true);
    }

    public class MyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.tv_confirm:
                    final String situation = situations[situationNP.getValue()];
                    sendMessageSituation.getMessageSituation(situation);
                    break;
                case R.id.tv_cancel:
                    sendMessageSituation.getMessageSituation();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof SendMessageSituation) {
            sendMessageSituation = (SendMessageSituation) context;
        } else {
            throw new IllegalArgumentException("Activity must implements SendMessageSituation");
        }
    }


    /**
     * 回调接口，用于向activity传参
     */
    public interface SendMessageSituation {
        void getMessageSituation(String situation);
        void getMessageSituation();
    }
}
