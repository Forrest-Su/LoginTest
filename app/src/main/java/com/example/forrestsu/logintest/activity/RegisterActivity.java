package com.example.forrestsu.logintest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.forrestsu.logintest.ActivityCollector;
import com.example.forrestsu.logintest.MyUser;
import com.example.forrestsu.logintest.utils.PhoneNumber;
import com.example.forrestsu.logintest.utils.Pssword;
import com.example.forrestsu.logintest.R;
import com.example.forrestsu.logintest.SMS;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private Button closeButton;
    private Button loginButton;
    private Button verificationButton;
    private Button registerButton;

    private EditText phoneNumberET;
    private EditText passwordET;
    private EditText verificationET;

    private com.example.forrestsu.logintest.SMS sms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        closeButton = (Button) findViewById(R.id.button_close);
        closeButton.setOnClickListener(this);
        loginButton = (Button) findViewById(R.id.button_login);
        loginButton.setOnClickListener(this);
        verificationButton = (Button) findViewById(R.id.button_verification);
        verificationButton.setOnClickListener(this);
        registerButton = (Button) findViewById(R.id.button_register);
        registerButton.setOnClickListener(this);

        phoneNumberET = (EditText) findViewById(R.id.et_phone_number);
        passwordET = (EditText) findViewById(R.id.et_password);
        verificationET = (EditText) findViewById(R.id.et_verification);

        sms = new SMS();
    }

    @Override
    public void onClick(View v) {
        String phoneNumber = phoneNumberET.getText().toString();
        String password = passwordET.getText().toString();
        switch (v.getId()) {
            case R.id.button_close:
               ActivityCollector.finishAll();
               break;
            case R.id.button_login:
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.button_verification:
                sms.requestSMSCode(phoneNumber, password, verificationButton);
                break;
            case R.id.button_register:
                String verification = verificationET.getText().toString();
                if (sms.verifySMSCode (phoneNumber, verification)) {
                    doRegister();
                } else {
                    Toast.makeText(RegisterActivity.this,
                            R.string.verification_wrong, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    //注册功能
    private void doRegister() {
        String phoneNumber = phoneNumberET.getText().toString();
        String password  =passwordET.getText().toString();

        if (PhoneNumber.verifyPhoneNumber(phoneNumber, RegisterActivity.this)
                && Pssword.verifyPassword(password, RegisterActivity.this)) {
            MyUser mu = new MyUser();
            mu.setUsername(phoneNumber);
            mu.setMobilePhoneNumber(phoneNumber);
            mu.setPassword(password);
            mu.signUp(new SaveListener<MyUser>() {
                @Override
                public void done(MyUser mu, BmobException e) {
                    if (e == null) {
                        Toast.makeText(RegisterActivity.this,
                                R.string.register_success, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(RegisterActivity.this,
                                R.string.register_fail, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
