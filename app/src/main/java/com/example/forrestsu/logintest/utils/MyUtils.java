package com.example.forrestsu.logintest.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static cn.volley.Request$Method.GET;

public class MyUtils {
    private static final String TAG = "MyUtils";

    public static void copyAssetsToDB(Context context,String fileName) throws IOException {
        //地区数据库路径
        String destPath = context.getDatabasePath(fileName).getPath();
        Log.d(TAG, "copyAssetsToDB: 数据库路径：" + destPath);
        File file = new File(destPath);
        if (!file.exists()) {
            file.mkdirs();
        } else {
            return;
        }
        InputStream is = context.getAssets().open(fileName);
        BufferedInputStream bis = new BufferedInputStream(is);
        FileOutputStream fos = new FileOutputStream(destPath + File.separator + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        byte[] data = new byte[2 * 1024];
        int len;
        while((len = bis.read(data)) != -1) {
            bos.write(data, 0, len);
        }
        bos.flush();
        bis.close();
        bos.close();
    }


    //查询本地版本号(API28之前)
    public static long getLocalVersionCode(Context context) throws Exception{
        PackageManager packageManger = context.getPackageManager();
        PackageInfo packageInfo = packageManger.getPackageInfo(context.getPackageName(), 0);
        if (Build.VERSION.SDK_INT < 28) {
            return (long)packageInfo.versionCode;
        } else {
            return packageInfo.getLongVersionCode();
        }
    }

    //查询本地版本名
    public static String getLocalVersionName(Context context) throws Exception{
        PackageManager packageManger = context.getPackageManager();
        PackageInfo packageInfo = packageManger.getPackageInfo(context.getPackageName(), 0);
        return packageInfo.versionName;
    }
}
