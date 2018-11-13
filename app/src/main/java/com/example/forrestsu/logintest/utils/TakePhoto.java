package com.example.forrestsu.logintest.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.IOException;

public class TakePhoto {

    private TakePhotoListener takePhotoListener;
    public void setTakePhotoListener(TakePhotoListener takePhotoListener) {
        this.takePhotoListener = takePhotoListener;
    }

    /**
     *拍照
     */
    public void takePhoto(Context context, String outPutImageName) {
        Uri imageUri;
        File outPutImage = new File(context.getExternalCacheDir(), outPutImageName);
        try {
            if (outPutImage.exists()) {
                outPutImage.delete();
            }
            outPutImage.createNewFile();
        } catch (IOException e){
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            imageUri = FileProvider.getUriForFile(context,
                    "com.example.forrestsu.logintest.fileprovider", outPutImage);
        } else {
            imageUri = Uri.fromFile(outPutImage);
        }
        //启动相机
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        //startActivityForResult(intent, requestCode);
        takePhotoListener.onSuccess(intent);
    }

    public interface TakePhotoListener {
        void onSuccess(Intent intent);
    }

}
