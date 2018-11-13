package com.example.forrestsu.logintest.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.forrestsu.logintest.R;

import java.io.File;
import java.io.IOException;

public class SetPhotoFragment extends Fragment {

    private static final String TAG = "SetPhotoFragment";

    public static final int CHOOSE_PHOTO = 1;
    public static final int TAKE_PHOTO = 2;

    //照片名称
    private static final String PHOTO_NAME = "photo_post.jpg";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View square = inflater.inflate(R.layout.fragment_set_photo, container, false);

        initView(square);

        return square;
    }

    public void initView(View square) {
        TextView choosePhotoTV = (TextView) square.findViewById(R.id.tv_choose_photo);
        choosePhotoTV.setOnClickListener(new MyOnClickListener());
        TextView takePhotoTV = (TextView) square.findViewById(R.id.tv_take_photo);
        takePhotoTV.setOnClickListener(new MyOnClickListener());
    }

    public class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_choose_photo:
                    //检查权限，从相册选择图片
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CHOOSE_PHOTO);
                    } else {
                        openAlbum();
                    }
                    break;
                case R.id.tv_take_photo:
                    //检查权限，拍照
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(getContext(),
                                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            getActivity().requestPermissions(
                                    new String[]{Manifest.permission.CAMERA}, 1);
                        } else {
                            takePhoto(PHOTO_NAME, TAKE_PHOTO);
                        }
                    } else {
                        takePhoto(PHOTO_NAME, TAKE_PHOTO);
                    }
                    break;
                default:
                    break;
            }
        }
    }


    /**
     *打开相册
     */
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        getActivity().startActivityForResult(intent, CHOOSE_PHOTO);
    }

    /**
     *拍照
     */
    private void takePhoto(String outPutImageName, int requestCode) {
        Uri imageUri;
        File outPutImage = new File(getContext().getExternalCacheDir(), outPutImageName);
        try {
            if (outPutImage.exists()) {
                outPutImage.delete();
            }
            outPutImage.createNewFile();
        } catch (IOException e){
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            imageUri = FileProvider.getUriForFile(getContext(),
                    "com.example.forrestsu.logintest.fileprovider", outPutImage);
        } else {
            imageUri = Uri.fromFile(outPutImage);
        }
        //启动相机
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        //如果希望调用Activity中的onActivityResult()，需要加上getActivity(),也就是将结果传回Activity
        getActivity().startActivityForResult(intent, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permission, int[] grantResults) {
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(getContext(), "You denied the permission",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case TAKE_PHOTO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto(PHOTO_NAME, requestCode);
                } else {
                    Toast.makeText(getContext(), "You denied the permission",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

}
