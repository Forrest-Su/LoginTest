package com.example.forrestsu.logintest.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;

import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.forrestsu.logintest.ActivityCollector;
import com.example.forrestsu.logintest.MyUser;
import com.example.forrestsu.logintest.R;
import com.example.forrestsu.logintest.service.DownloadService;

import cn.bmob.v3.BmobUser;


public class SettingActivity extends BaseActivity{
    private static final String TAG = "SettingActivity";

    private Button verifyBT;
    private Button logOutBT;
    private EditText urlET;

    //urlET的状态
    private int index = 0;
    private int inType;

    //用户是否已验证
    static Boolean verifyIdentity;
    private MyUser currentUser;

    private IntentFilter intentFilter;
    private Receiver receiver;


    private DownloadService.DownloadBinder downloadBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadService.DownloadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();

        Intent intent = new Intent(this, DownloadService.class);
        //启动服务
        startService(intent);
        //绑定服务
        bindService(intent, connection, BIND_AUTO_CREATE);
        if (ContextCompat.checkSelfPermission(SettingActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SettingActivity.this, new String[] {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    public void initView() {
        currentUser = BmobUser.getCurrentUser(MyUser.class);
        verifyIdentity = currentUser.getVerifyIdentity();
        Log.d(TAG, "initView:用户验证状态：" + verifyIdentity);
        Log.d(TAG, "initView:用户验证状态：" + verifyIdentity);
        verifyBT = (Button) findViewById(R.id.bt_verify);
        verifyBT.setOnClickListener(new MyOnClickListener());
        logOutBT = (Button)findViewById(R.id.bt_log_out);
        logOutBT.setOnClickListener(new MyOnClickListener());

        Button startDownloadBT = (Button) findViewById(R.id.bt_start_download);
        startDownloadBT.setOnClickListener(new MyOnClickListener());
        Button pauseDownloadBT = (Button) findViewById(R.id.bt_pause_download);
        pauseDownloadBT.setOnClickListener(new MyOnClickListener());
        Button cancelDownloadBT = (Button) findViewById(R.id.bt_cancel_download);
        cancelDownloadBT.setOnClickListener(new MyOnClickListener());

        Button testBT = (Button) findViewById(R.id.bt_test);
        testBT.setOnClickListener(new MyOnClickListener());

        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.forrestsu.logintest.START_PAUSE");
        receiver = new Receiver();
        //动态注册
        registerReceiver(receiver, intentFilter);

        urlET = (EditText) findViewById(R.id.et_url);
        /*
        urlET.setFocusableInTouchMode(false);
        urlET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index == 0) {
                    inType = urlET.getInputType();
                    urlET.setInputType(1);
                    urlET.setFocusableInTouchMode(false);
                    urlET.clearFocus();
                    index = 1;
                } else if (index == 1) {
                    urlET.setInputType(inType);
                    urlET.setFocusableInTouchMode(true);
                    urlET.requestFocus();
                    index = 0;
                }
            }
        });
        */
    }

    public class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_verify:
                    if (verifyIdentity) {
                        Toast.makeText(SettingActivity.this,
                                "您已验证过身份", Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent =new Intent(SettingActivity.this, VerifyIdentityActivity.class);
                        startActivity(intent);
                    }
                    break;
                case R.id.bt_log_out:
                    MyUser.logOut();
                    ActivityCollector.finishAll();
                    break;
                case R.id.bt_start_download:
                    startDownload();
                    break;
                case R.id.bt_pause_download:
                    pauseDownload();
                    break;
                case R.id.bt_cancel_download:
                    downloadBinder.cancelDownload();
                    break;
                case R.id.bt_test:
                    Intent intent = new Intent(SettingActivity.this, TestActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "请授予权限，否则无法使用", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }


    class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //
            Log.d(TAG, "onReceive:接收到广播");
            String data = intent.getStringExtra("动作");
            Log.d(TAG, "接收的动作：" + data);
            if (data.equals("暂停")) {
                pauseDownload();
            } else if(data.equals("开始")) {
                startDownload();
            } else if (data.equals("取消")) {
                downloadBinder.cancelDownload();
            }
        }
    }


    public void startDownload() {
        String url = urlET.getText().toString();
        downloadBinder.startDownload(url);
    }

    public void pauseDownload() {
        downloadBinder.pauseDownload();
    }

}
