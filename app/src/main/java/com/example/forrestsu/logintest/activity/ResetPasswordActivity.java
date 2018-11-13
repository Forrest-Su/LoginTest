package com.example.forrestsu.logintest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.forrestsu.logintest.MyUser;
import com.example.forrestsu.logintest.R;
import com.example.forrestsu.logintest.SMS;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener {

    private Button verificationButton;
    private Button resetPassButton;

    private EditText phoneNumberET;
    private EditText newPassET;
    private EditText verificationET;

    private SMS sms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        verificationButton = (Button) findViewById(R.id.button_verification);
        verificationButton.setOnClickListener(this);
        resetPassButton = (Button) findViewById(R.id.button_resetPass);
        resetPassButton.setOnClickListener(this);

        phoneNumberET = (EditText) findViewById(R.id.et_phone_number);
        newPassET = (EditText) findViewById(R.id.et_newPass);
        verificationET = (EditText) findViewById(R.id.et_verification);

        sms = new SMS();
    }

    @Override
    public void onClick(View v) {
        String phoneNumber = phoneNumberET.getText().toString();
        String newPassword = newPassET.getText().toString();
        switch (v.getId()) {
            case R.id.button_verification:
                sms.requestSMSCode(phoneNumber, newPassword, verificationButton);
                break;
            case R.id.button_resetPass:
                String verication = verificationET.getText().toString();
                MyUser.resetPasswordBySMSCode(verication,newPassword, new UpdateListener() {

                    @Override
                    public void done(BmobException ex) {
                        if(ex==null){
                            Toast.makeText(ResetPasswordActivity.this,
                                    R.string.resetPass_success, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ResetPasswordActivity.this,
                                    LoginActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(ResetPasswordActivity.this,
                                    R.string.resetPass_fail, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            default:
                break;
        }
    }
}
