package com.example.forrestsu.logintest.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.forrestsu.logintest.ActivityCollector;
import com.example.forrestsu.logintest.MyUser;
import com.example.forrestsu.logintest.utils.PhoneNumber;
import com.example.forrestsu.logintest.utils.Pssword;
import com.example.forrestsu.logintest.R;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private Button closeButton;
    private Button registerButton;
    private Button loginButton;
    private Button resetPassButton;

    private ImageView clearPhoneNumberIV;
    private ImageView clearPasswordIV;

    private EditText phoneNumberET;
    private EditText passwordET;

    private CheckBox rememberPassCB;



    //AES
    /*
    private static AES aes = new AES("01234abcdef56789", "0123456789abcdef");
    */
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);

        // 初始化 Bmob SDK
        // 第二个参数是Bmob服务器端创建的Application ID
        Bmob.initialize(this, "");

        //自动登录,有效期为一年
        MyUser userInfo = BmobUser.getCurrentUser(MyUser.class);
        if (userInfo != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_login);

        pref = PreferenceManager.getDefaultSharedPreferences(this);

        closeButton = (Button) findViewById(R.id.button_close);
        closeButton.setOnClickListener(this);
        registerButton = (Button) findViewById(R.id.button_register);
        registerButton.setOnClickListener(this);
        loginButton = (Button) findViewById(R.id.button_login);
        loginButton.setOnClickListener(this);
        resetPassButton = (Button) findViewById(R.id.button_resetPass);
        resetPassButton.setOnClickListener(this);

        phoneNumberET = (EditText) findViewById(R.id.et_phone_number);
        passwordET = (EditText) findViewById(R.id.et_password);

        clearPhoneNumberIV = (ImageView) findViewById(R.id.iv_clear_phonenumber);
        clearPhoneNumberIV.setOnClickListener(this);
        clearPasswordIV = (ImageView) findViewById(R.id.iv_clear_password);
        clearPasswordIV.setOnClickListener(this);

        rememberPassCB = (CheckBox) findViewById(R.id.cb_rememberPass);

        boolean isRemember = pref.getBoolean("remember_password", false);
        if (isRemember) {
                //读取账号密码并加载到对应的EditText
                String phoneNumber = pref.getString("phoneNumber", "");
                String password = pref.getString("password", "");
                phoneNumberET.setText(phoneNumber);
                passwordET.setText(password);
                rememberPassCB.setChecked(true);

                //解密
            /*
            try {
                passwordET.setText(aes.decode(password));
            } catch (Exception e) {

            }
            */

            }
        }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_close:
                ActivityCollector.finishAll();
                break;
            case R.id.button_register:
                Intent toRegisterIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(toRegisterIntent);
                break;
            case R.id.button_resetPass:
                Intent toResetPassIntent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(toResetPassIntent);
                break;
            case R.id.iv_clear_phonenumber:
                phoneNumberET.getText().clear();
                    break;
            case R.id.iv_clear_password:
                passwordET.getText().clear();
                break;
            case R.id.button_login:
                doLogin();
                break;
            default:
                break;
        }
    }

    //登录功能
    private void doLogin() {
        String phoneNumber = phoneNumberET.getText().toString();
        String password  =passwordET.getText().toString();

        if (PhoneNumber.verifyPhoneNumber(phoneNumber, LoginActivity.this)
                && Pssword.verifyPassword(password, LoginActivity.this)) {
            editor = pref.edit();
            if (rememberPassCB.isChecked()) {//检查CheckBox是否被选中
                //如果选中，存储账号密码以及CheckBox的状态
                editor.putBoolean("remember_password", true);
                editor.putString("phoneNumber", phoneNumber);
                editor.putString("password", password);
                //加密
                /*
                try {
                    editor.putString("password", aes.encode(password));
                } catch(Exception e) {

                }
                */
            } else {
                //否则，清空数据
                editor.clear();
            }
            //提交数据，完成数据存储
            editor.apply();

            MyUser mu = new MyUser();
            mu.setUsername(phoneNumber);
            mu.setPassword(password);
            mu.login(new SaveListener<MyUser>() {
                @Override
                public void done(MyUser mu, BmobException e) {
                    if (mu != null) {
                        Toast.makeText(LoginActivity.this,
                                R.string.login_success, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this,
                                R.string.login_fail, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
