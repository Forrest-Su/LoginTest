package com.example.forrestsu.logintest;

import android.content.Context;
import android.widget.Button;
import android.widget.Toast;

import com.example.forrestsu.logintest.utils.PhoneNumber;
import com.example.forrestsu.logintest.utils.Pssword;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class SMS {

    //布尔值b用来表示是否成功发送验证码，true为成功，false为失败
    private boolean b = false;

    //申请短信验证码功能
    public void requestSMSCode(String phoneNumber, String password, Button button) {
        Context context = MyApplication.getContext();
        if (PhoneNumber.verifyPhoneNumber(phoneNumber, context)
                && Pssword.verifyPassword(password, context)) {
            //启动倒计时设置总时间为60秒，每隔1秒更新一次，对象为verificationButton
            final MyCountDownTimer myCountDownTimer = new MyCountDownTimer(
                    (60 * 1000), 1000, button);
            myCountDownTimer.start();
            //申请验证码
            BmobSMS.requestSMSCode(phoneNumber, "LoginDemo", new QueryListener<Integer>() {
                @Override
                public void done(Integer integer, BmobException e) {
                    if (e == null){
                        Toast.makeText(MyApplication.getContext(),"验证码发送成功",
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        myCountDownTimer.cancel();
                    }
                }
            });
        }
    }

    //验证短信验证码功能
    public boolean verifySMSCode(String phoneNumber, String verification) {
        BmobSMS.verifySmsCode(phoneNumber, verification, new UpdateListener() {
            @Override
            public void done(BmobException e ) {
                if (e == null) {
                    b = true;
                }
                else {
                    b = false;
                    Toast.makeText(MyApplication.getContext(), "验证失败：code=" +
                            e.getErrorCode()+"，错误描述：" + e.getLocalizedMessage(),Toast.LENGTH_SHORT ).show();
                }
            }
        });
        return b;
    }
}
