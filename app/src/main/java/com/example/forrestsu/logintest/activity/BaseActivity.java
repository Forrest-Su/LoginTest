package com.example.forrestsu.logintest.activity;

/*
BaseActivity为所有活动的父类
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.example.forrestsu.logintest.ActivityCollector;

public class BaseActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
    /*
        //隐藏标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    */

        //当前活动一被创建，就会添加到List中
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //当前活动被销毁时，就会从List中移除
        ActivityCollector.removeActivity(this);
    }

}
