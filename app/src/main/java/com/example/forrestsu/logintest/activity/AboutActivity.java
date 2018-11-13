package com.example.forrestsu.logintest.activity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.forrestsu.logintest.MyApplication;
import com.example.forrestsu.logintest.R;
import com.example.forrestsu.logintest.Version;
import com.example.forrestsu.logintest.utils.MyUtils;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static com.example.forrestsu.logintest.MyApplication.getContext;

public class AboutActivity extends BaseActivity {

    private static final String TAG = "AboutActivity";

    private View versionView;
    private TextView versionTV;
    private long localVersionCode;
    private String localVersionName;

    @Override
    protected void onCreate(Bundle savedInstandeState) {
        super.onCreate(savedInstandeState);
        setContentView(R.layout.activity_about);
        //检查本地版本
        try {
            localVersionName = MyUtils.getLocalVersionName(getContext());
            localVersionCode = MyUtils.getLocalVersionCode(getContext());
        }catch (Exception e) {
            e.printStackTrace();
        }

        initView();
    }

    //初始化
    public void initView() {
        versionView = (View) findViewById(R.id.view_version);
        versionView.setOnClickListener(new MyOnClickListener());
        versionTV = (TextView) findViewById(R.id.tv_version);
        versionTV.setText(localVersionName);
    }

    public class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.view_version:
                    //检查版本更新
                    BmobQuery<Version> query = new BmobQuery<Version>();
                    query.order("-versionCode");
                    query.setLimit(1);
                    query.findObjects(new FindListener<Version>() {
                        @Override
                        public void done(List<Version> list, BmobException e) {
                            if (e == null) {
                                final long newVersionCode = list.get(0).getVersionCode();
                                final String newVersionName = list.get(0).getVersionName();
                                if (newVersionCode > localVersionCode) {
                                    Toast.makeText(AboutActivity.this,
                                            "检查到新版本：" + newVersionName, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.d(TAG, "done: 查询版本号失败");
                            }
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }
}
