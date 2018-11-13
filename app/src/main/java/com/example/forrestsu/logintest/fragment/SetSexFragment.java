package com.example.forrestsu.logintest.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.forrestsu.logintest.R;

public class SetSexFragment extends Fragment {

    private static final String TAG = "SetSexFragment";

    private SendMessageSex sendMessageSex;

    private RadioGroup sexRG;
    private RadioButton manCK;
    private RadioButton womanCK;
    private Button confirmBT;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View square = inflater.inflate(R.layout.fragment_set_sex, container, false);

        initView(square);

        return square;
    }


    /*
    初始化
     */
    public void initView(View square) {
        sexRG = (RadioGroup) square.findViewById(R.id.rg_sex);
        manCK = (RadioButton) square.findViewById(R.id.rb_man);
        manCK = (RadioButton) square.findViewById(R.id.rb_woman);
        confirmBT = (Button) square.findViewById(R.id.bt_confirm);
        confirmBT.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if (sexRG.getCheckedRadioButtonId() == R.id.rb_man) {
                     sendMessageSex.getMessageSex("男");
                 } else if (sexRG.getCheckedRadioButtonId() == R.id.rb_woman) {
                     sendMessageSex.getMessageSex("女");
                 } else {
                     Toast.makeText(getContext(), "请先选择", Toast.LENGTH_SHORT).show();
                 }
             }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof SendMessageSex) {
            sendMessageSex = (SendMessageSex) context;
        } else {
            throw new IllegalArgumentException("Activity must implements CloseFragment");
        }
    }


    /**
     * 回调接口，用于向Activity传参
     */
    public interface SendMessageSex{
        void getMessageSex(String sex);
    }

}
