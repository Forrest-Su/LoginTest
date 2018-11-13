package com.example.forrestsu.logintest.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import static com.example.forrestsu.logintest.MyApplication.getContext;

public class ImageViewUtil {
    private static final String TAG = "ImageViewUtil";

    /**
     * 再ImageView中显示图片
     ** @param imageView
     * @param imagePath
     */

    public static void displayImage(ImageView imageView, String imagePath) {
        Log.d(TAG, "调用displayImage显示图片");
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(bitmap);
        } else {
            Toast.makeText(getContext(), "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    设置ImageView中的图片自适应宽高不留空白
     */
    public static void matchAll(Context context,ImageView imageView) {
        //ImageView调整后的宽高
        int width, height;
        //获取屏幕宽高
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        int sWidth = metrics.widthPixels;
        int sHeight = metrics.heightPixels;
        //获取图片宽高
        Drawable drawable = imageView.getDrawable();
        int dWidth = drawable.getIntrinsicWidth();
        int dHeight = drawable.getIntrinsicHeight();
        //屏幕宽高比
        float sScale = (float) sWidth / sHeight;
        //图片宽高比
        float dScale = (float) dWidth / dHeight;
        /*
        缩放比
        如果sScale>dScale，表示在高相等的情况下，控屏幕比较宽，这时候要适应高度，缩放比就是两则的高之比，图片宽度用缩放比计算
        如果sScale<dScale，表示在高相等的情况下，图片比较宽，这时候要适应宽度，缩放比就是两则的宽之比，图片高度用缩放比计算
         */
        float scale = 1.0f;
        if (sScale > dScale) {
            scale = (float) dHeight / sHeight;
            //图片高度就是屏幕高度
            height = sHeight;
            //按照缩放比算出图片缩放后的宽度
            width = (int) (dWidth * scale);
        } else if (sScale < dScale) {
            scale = (float) dWidth / sWidth;
            width = sWidth;
            height = (int) (dHeight / scale);
        } else {
            //如果两者比例相同
            width = sWidth;
            height = sHeight;
        }
        //重设ImageView宽高
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        imageView.setLayoutParams(params);
    }
}
