package com.example.forrestsu.logintest.fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.forrestsu.logintest.MyUser;
import com.example.forrestsu.logintest.R;
import com.example.forrestsu.logintest.activity.AboutActivity;
import com.example.forrestsu.logintest.activity.FeedBackActivity;
import com.example.forrestsu.logintest.activity.MyInfoActivity;
import com.example.forrestsu.logintest.activity.MyPostActivity;
import com.example.forrestsu.logintest.activity.SettingActivity;
import com.example.forrestsu.logintest.utils.BmobDownload;
import com.example.forrestsu.logintest.utils.ImageViewUtil;

import java.io.File;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;


public class MeFragment extends Fragment {
    private static final String TAG = "MeFragment";

    private CircleImageView headCIV;
    private TextView nicknameTV;

    private MyUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInsatnceState) {

        View square = inflater.inflate(R.layout.fragment_me, container, false);

        initView(square);
        loadUserInfo();

        return square;
    }

    //初始化
    public void initView (View square) {

        headCIV = (CircleImageView) square.findViewById(R.id.civ_head);
        nicknameTV = (TextView) square.findViewById(R.id.tv_nickname);

        View myPostView = (View) square.findViewById(R.id.view_my_post);
        View myInfoView = (View) square.findViewById(R.id.view_my_info);
        View settingView = (View) square.findViewById(R.id.view_setting);
        View feedbackView = (View) square.findViewById(R.id.view_feedback);
        View aboutView = (View) square.findViewById(R.id.view_about);

        myPostView.setOnClickListener(new MyOnClickListener());
        myInfoView.setOnClickListener(new MyOnClickListener());
        settingView.setOnClickListener(new MyOnClickListener());
        feedbackView.setOnClickListener(new MyOnClickListener());
        aboutView.setOnClickListener(new MyOnClickListener());

       currentUser = BmobUser.getCurrentUser(MyUser.class);
    }

    public class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.view_my_post:
                    startActivity(new Intent(getContext(),MyPostActivity.class));
                    break;
                case R.id.view_my_info:
                    startActivity(new Intent(getContext(), MyInfoActivity.class));
                    break;
                case R.id.view_setting:
                    startActivity(new Intent(getContext(), SettingActivity.class));
                    break;
                case R.id.view_feedback:
                    startActivity(new Intent(getContext(), FeedBackActivity.class));
                    break;
                case R.id.view_about:
                    startActivity(new Intent(getContext(), AboutActivity.class));
                    break;
                default:
                    break;
            }
        }
    }

    /*
    加载用户信息
     */
    private void loadUserInfo() {
        nicknameTV.setText(currentUser.getNickname());

        String url = currentUser.getHeadUrl();
        if (url != null && !url.equals("")) {
            //检查远程文件在是否在本地已存在，如果存在，直接显示，否则调用下载
            //存储路径
            final String photoSavePath = getContext().getExternalCacheDir() + "/";
            //获取远程文件名
            final String photoName = url.substring(url.lastIndexOf("/") + 1);
            Log.d(TAG, "onSuccess: 远程文件名：" + photoName);
            if (new File(photoSavePath + photoName).exists()) {
                Log.d(TAG, "文件不变，直接显示 ");
                ImageViewUtil.displayImage(headCIV, photoSavePath + photoName);
            } else {
                Log.d(TAG, "文件有变，调用下载");

                final BmobFile bmobFile = new BmobFile(photoName, "", url);
                final BmobDownload bmobDownload = new BmobDownload();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        bmobDownload.downloadBmobFile(bmobFile, photoSavePath, photoName);
                    }
                }).start();

                bmobDownload.setBmobDownloadListener(new BmobDownload.BmobDownloadListener() {
                    @Override
                    public void onSuccess() {
                        ImageViewUtil.displayImage(headCIV, photoSavePath + photoName);
                    }

                    @Override
                    public void onFailed() {
                        //
                    }
                });
            }
        }

    }

}
