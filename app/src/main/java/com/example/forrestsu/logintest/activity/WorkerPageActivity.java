package com.example.forrestsu.logintest.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.forrestsu.logintest.MyUser;
import com.example.forrestsu.logintest.R;
import com.example.forrestsu.logintest.WorkerPost;
import com.example.forrestsu.logintest.my_controls.LeaveMessage;
import com.example.forrestsu.logintest.my_controls.TwoTextViewsControl;
import com.example.forrestsu.logintest.utils.ImageViewUtil;
import com.example.forrestsu.logintest.EmployerMessage;

import java.io.File;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.SaveListener;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;

public class WorkerPageActivity extends BaseActivity implements WorkerPost.QueryWorkerPostListener,
        LeaveMessage.LeaveMessageListener {

    private static final String TAG = "WorkerPageActivity";

    private static final int UPDATE_IMAGEVIEW = 1;

    private String objectId;
    private String workerPhoneNumber = "";

    private static ProgressDialog progressDialog;

    private static CircleImageView photoCIV;
    private ImageView mapIV;
    private ImageView starMealIV, starAccommodationIV, starWorkLocationIV;
    private TextView showMealTV, showAccommodationTV, showWorkLocationTV;
    private TextView showNameTV, showAgeTV, showAddressTV;
    private TwoTextViewsControl levelControl, timeControl, priceControl, introductionControl;

    private static LeaveMessage leaveMessage;
    private static View glassView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_page);

        Intent intent = getIntent();
        objectId = intent.getStringExtra("id");
        Log.d(TAG, "接收的objectId:" + objectId);

        initView();


        new Thread() {
            public void run() {
                WorkerPost.queryWorkerPostInfo(objectId);
            }
        }.start();
    }

    public void initView() {
        progressDialog = new ProgressDialog(WorkerPageActivity.this);
        progressDialog.setTitle("");
        progressDialog.setMessage("...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        WorkerPost workerPost = new WorkerPost();
        workerPost.setQueryWorkerPostListener(this);

        photoCIV = (CircleImageView) findViewById(R.id.civ_photo);
        mapIV = (ImageView) findViewById(R.id.iv_map_ico);
        showNameTV = (TextView) findViewById(R.id.tv_show_name);
        showAgeTV = (TextView) findViewById(R.id.tv_show_age);
        showAddressTV = (TextView) findViewById(R.id.tv_show_address);

        starMealIV = (ImageView) findViewById(R.id.iv_star_ico_1);
        starMealIV.setVisibility(GONE);
        starAccommodationIV = (ImageView) findViewById(R.id.iv_star_ico_2);
        starAccommodationIV.setVisibility(GONE);
        starWorkLocationIV = (ImageView) findViewById(R.id.iv_star_ico_3);
        starWorkLocationIV.setVisibility(GONE);

        showMealTV = (TextView) findViewById(R.id.tv_show_1);
        showMealTV.setVisibility(GONE);
        showAccommodationTV = (TextView) findViewById(R.id.tv_show_2);
        showAccommodationTV.setVisibility(GONE);
        showWorkLocationTV = (TextView) findViewById(R.id.tv_show_3);
        showWorkLocationTV.setVisibility(GONE);

        levelControl = (TwoTextViewsControl) findViewById(R.id.my_control_level);
        timeControl = (TwoTextViewsControl) findViewById(R.id.my_control_time);
        priceControl = (TwoTextViewsControl) findViewById(R.id.my_control_price);
        introductionControl = (TwoTextViewsControl) findViewById(R.id.my_control_note);

        Button messageBT = (Button) findViewById(R.id.bt_leave_message);
        messageBT.setOnClickListener(new MyOnClickListener());
        Button callBT = (Button) findViewById(R.id.bt_call);
        callBT.setOnClickListener(new MyOnClickListener());

        glassView = (View) findViewById(R.id.view_glass);
        glassView.setOnClickListener(new MyOnClickListener());
        glassView.setVisibility(GONE);

        leaveMessage = (LeaveMessage) findViewById(R.id.my_control_leave_message);
        leaveMessage.setLeaveMessageListener(LeaveMessage.EMPLOYER_MESSAGE, objectId, this);
        leaveMessage.setVisibility(GONE);
    }

    public class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_leave_message:
                    glassView.setVisibility(View.VISIBLE);
                    leaveMessage.setVisibility(View.VISIBLE);
                    break;
                case R.id.bt_call:
                    if (!workerPhoneNumber.equals("")) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + workerPhoneNumber));
                        startActivity(intent);
                    }
                    break;
                case R.id.view_glass:
                    leaveMessage.setMessage("");
                    leaveMessage.setVisibility(GONE);
                    glassView.setVisibility(GONE);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onSuccess(WorkerPost workerPost) {
        MyUser worker = workerPost.getWorker();
        workerPhoneNumber = worker.getMobilePhoneNumber();
        Log.i(TAG, "onSuccess: 护工电话：" + workerPhoneNumber);
        String url = workerPost.getWorkerPhotoUrl();
        String name = workerPost.getWorkerName();
        int age = workerPost.getWorkerAge();
        String province = workerPost.getProvince();
        String city = workerPost.getCity();
        String area = workerPost.getArea();
        String specificAddress = workerPost.getSpecificAddress();
        String withMeal = workerPost.getWithMeal();
        String withAccommodation = workerPost.getWithAccommodation();
        String workLocation = workerPost.getWorkLocation();
        String workTime = workerPost.getWorkTime();
        int servicePrice = workerPost.getServicePrice();
        String level = workerPost.getWorkerLevel();
        String selfIntroduction = workerPost.getSelfIntroduction();

        showNameTV.setText(name);
        showAgeTV.setText((age + "岁"));
        if (province.equals("台湾") || province.equals("澳门")
                || province.equals("香港") || province.equals("北京")
                || province.equals("天津") || province.equals("上海")
                || province.equals("重庆") || province.equals("钓鱼岛")) {
            showAddressTV.setText((city + "." + area + "\n" + specificAddress));
        } else {
            showAddressTV.setText((province + "." + city + "." + area + "\n" + specificAddress));
        }

        if (withMeal.equals("是")) {
            starMealIV.setVisibility(View.VISIBLE);
            showMealTV.setVisibility(View.VISIBLE);
            showMealTV.setText("含餐");
        }
        if (withAccommodation.equals("是")) {
            starAccommodationIV.setVisibility(View.VISIBLE);
            showAccommodationTV.setVisibility(View.VISIBLE);
            showAccommodationTV.setText("住宿");
        }
        if (!workLocation.equals("不限")) {
            starWorkLocationIV.setVisibility(View.VISIBLE);
            showWorkLocationTV.setVisibility(View.VISIBLE);
            showWorkLocationTV.setText(workLocation);
        }

        levelControl.set("护工等级", level);
        timeControl.set("工作时间", workTime);
        priceControl.set("服务价格", String.valueOf(servicePrice));
        introductionControl.set("自我介绍", selfIntroduction);

        progressDialog.dismiss();

        //检查远程文件在是否在本地已存在，如果存在，直接显示，否则调用下载
        //存储路径
        final String photoSavePath = getExternalCacheDir() + "/img_head/";
        //获取远程文件名
        final String photoName = url.substring(url.lastIndexOf("/") + 1);
        Log.d(TAG, "onSuccess: 远程文件名：" + photoName);
        if (new File(photoSavePath + photoName).exists()) {
            Log.d(TAG, "文件不变，直接显示 ");
            ImageViewUtil.displayImage(photoCIV, photoSavePath + photoName);
        } else {
            Log.d(TAG, "文件有变，调用下载");

            final BmobFile bmobFile = new BmobFile(photoName, "", url);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    downloadBmobFile(bmobFile, photoSavePath, photoName);
                }
            }).start();
        }
    }

    @Override
    public void onFailed() {
        //加载失败
        progressDialog.dismiss();
        Toast.makeText(WorkerPageActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
    }


    /*
    Bmob下载用户头像
     */
    private void downloadBmobFile(BmobFile bmobFile, String savePath, final String fileName) {

        File path = new File(savePath);
        Log.d(TAG, "downloadBmobFile: 检查路径是否存在");
        if (!path.exists()) {
            Log.d(TAG, "downloadBmobFile: 路径不存在");
            path.mkdirs();
        }

        File file = new File(savePath + fileName);
        Log.d(TAG, "downloadBmobFile: 检查文件是否存在");
        if (file.exists()) {
            Log.d(TAG, "downloadBmobFile: 文件存在");
            file.delete();
        }
        bmobFile.download(file, new DownloadFileListener() {
            @Override
            public void onStart() {
                Log.d(TAG, "开始下载文件...");
            }

            @Override
            public void done(String savePath, BmobException e) {
                if (e == null) {
                    Log.d(TAG, "下载成功,保存路径:"+ savePath);
                  Message message = new Message();
                  message.what = UPDATE_IMAGEVIEW;
                  message.obj = savePath;
                  MyHandler handler = new MyHandler();
                  handler.sendMessage(message);
                }else{
                    Log.d(TAG, "done: 加载头像失败");
                }
            }

            @Override
            public void onProgress(Integer value, long networkSpeed) {
                Log.d(TAG,"下载进度："+ value + ", " + networkSpeed);
            }

        });
    }

    @Override
    public void confirmMessage() {
        progressDialog.show();
    }

    @Override
    public void cancelMessage() {
        leaveMessage.setVisibility(GONE);
        glassView.setVisibility(GONE);
    }

    @Override
    public void successfulMessage() {
        Toast.makeText(WorkerPageActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
        leaveMessage.setMessage("");
        leaveMessage.setVisibility(GONE);
        glassView.setVisibility(GONE);
        progressDialog.dismiss();
    }

    @Override
    public void failedMessage() {
        Toast.makeText(WorkerPageActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
    }


    private static class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_IMAGEVIEW:
                    String savePath = (msg.obj).toString();
                    ImageViewUtil.displayImage(photoCIV, savePath);
                    break;
                default:
                    break;
            }
        }
    }

}
