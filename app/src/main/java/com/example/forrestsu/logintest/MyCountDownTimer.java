package com.example.forrestsu.logintest;

import android.os.CountDownTimer;
import android.widget.Button;

/*
倒计时函数
 */

public class MyCountDownTimer extends CountDownTimer {
    private Button verificationButton;
    public MyCountDownTimer(long millisInFuture, long countDownInterval, Button verificationButton) {
        super(millisInFuture, countDownInterval);
        this.verificationButton = verificationButton;
    }

    //计时过程
    @Override
    public void onTick(long l) {
        //防止用户在计时过程中重复点击按钮
        verificationButton.setClickable(false);
        verificationButton.setText(l / 1000 + "秒后重获");
    }

    //计时结束后重设Button
    @Override
    public void onFinish() {
        verificationButton.setText("获取验证码");
        verificationButton.setClickable(true);
    }

}
