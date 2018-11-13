package com.example.forrestsu.logintest;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;


public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        /**解决android 7.0系统拍照的问题
         *
         */
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        //使用百度地图SDK之前，初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this);
        //自4.0.3起，百度地图SDK所有接口均支持百度坐标和国测局坐标，使用此方法设置使用的坐标类型
        //默认是BD09LL坐标
        SDKInitializer.setCoordType(CoordType.BD09LL);
    }

    //方法：获取Context对象
    public static Context getContext() {
        return context;
    }
}
