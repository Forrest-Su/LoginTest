package com.example.forrestsu.logintest.utils;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static com.example.forrestsu.logintest.MyApplication.getContext;


public class ImageCompress {
    private static LubanListener lubanListener;
    public void setLubanListener (LubanListener lubanListener) {
        ImageCompress.lubanListener = lubanListener;
    }

    /**
     * Luban压缩
     * file是要压缩的文件
     * 默认输出路径
     */
    public static void compressWithLuban(File file) {
        Luban.with(getContext())
                .load(file)
                .ignoreBy(0)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        Log.d("Luban", "onStart: 开始压缩");
                    }

                    @Override
                    public void onSuccess(File file) {
                        String newPath = file.getAbsolutePath();
                        lubanListener.onSuccess(newPath);
                        Log.d("Luban", "onSuccess: 压缩成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Luban", "onError: 压缩失败");
                    }
                }).launch();
    }

    /**
     * Luban压缩
     * path是要压缩的文件路径
     * 默认输出路径：/storage/emulated/0/Android/data/包名/cache/luban_disk_cache/保存文件的时间戳.**
     */
    public static void compressWithLuban(String path) {
        Luban.with(getContext())
                .load(path)
                .ignoreBy(0)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        Log.d("Luban", "onStart: 开始压缩");
                    }

                    @Override
                    public void onSuccess(File file) {
                        Log.d("Luban", "onSuccess: 压缩成功");
                        String newPath = file.getAbsolutePath();
                        lubanListener.onSuccess(newPath);
                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.d("Luban", "onError: 压缩失败");
                    }
                }).launch();
    }

    /**
     * Luban压缩
     * path是要压缩的文件路径
     * newPath是指定的输出路径
     */
    public static void compressWithLuban(String path, final String savePath) {
        Luban.with(getContext())
                .load(path)
                .ignoreBy(0)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        Log.d("Luban", "onStart: 开始压缩");
                    }

                    @Override
                    public void onSuccess(File file) {
                        Log.d("Luban", "onSuccess: 压缩成功");
                        try {
                            int byteread = 0;
                            InputStream ins = new FileInputStream(file.getAbsolutePath());
                            FileOutputStream fos = new FileOutputStream(savePath);
                            byte[] buffer = new byte[1024];
                            while ((byteread = ins.read(buffer)) != -1) {
                                fos.write(buffer, 0, byteread);
                            }
                            ins.close();
                            lubanListener.onSuccess(savePath);
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Luban", "onError: 压缩失败");
                    }
                }).launch();
    }

    /**
     * 质量压缩
     */
    public static void qualityCompress(Bitmap bmp, File file) {
        // 0-100 100为不压缩
        int quality = 30;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 把压缩后的数据存放到baos中
        bmp.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface LubanListener {
        void onSuccess(String newPath);
        void onFailed();
    }

}