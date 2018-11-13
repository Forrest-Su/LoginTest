package com.example.forrestsu.logintest.utils;

/*
 *核对密码格式
 * 修改密码
 */

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;

import com.example.forrestsu.logintest.R;

public class Pssword {
    //核对密码格式：由大小写字母+数字+下划线组成的6-16位组合
    public static boolean verifyPassword(String password, Context context) {
        String passwordRegex = "[A-Za-z0-9_]{6,16}";
        if (TextUtils.isEmpty(password) || !password.matches(passwordRegex)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle(R.string.wrong);
            dialog.setMessage(R.string.please_input_password);
            dialog.setCancelable(true);
            dialog.show();
            return false;
        } else return true;
    }

    //修改密码
    public static void resetPassword(String oldPassword, String newPassword) {

    }
}
