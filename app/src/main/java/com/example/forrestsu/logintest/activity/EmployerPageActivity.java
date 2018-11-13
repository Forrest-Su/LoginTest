package com.example.forrestsu.logintest.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.forrestsu.logintest.EmployerPost;
import com.example.forrestsu.logintest.MyUser;
import com.example.forrestsu.logintest.R;
import com.example.forrestsu.logintest.WorkerMessage;
import com.example.forrestsu.logintest.my_controls.LeaveMessage;
import com.example.forrestsu.logintest.my_controls.TwoTextViewsControl;
import com.example.forrestsu.logintest.utils.ImageViewUtil;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

import static android.view.View.GONE;

public class EmployerPageActivity extends BaseActivity implements
        EmployerPost.QueryEmployerPostListener, LeaveMessage.LeaveMessageListener {

    private static final String TAG = "EmployerPageActivity";

    private static final int UPDATE_UI = 1;
    private String objectId;
    private String employerPhoneNumber = "";

    private static ProgressDialog progressDialog;

    private TwoTextViewsControl showName, showAddress, showDate, showTime, showWorkLocation,
                                showSituation, showPrice, showNote;
    private static LeaveMessage leaveMessage;
    private static View glassView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_page);

        Intent intent = getIntent();
        objectId = intent.getStringExtra("id");
        Log.d(TAG, "接收的objectId:" + objectId);

        initView();

        new Thread() {
            public void run() {
               EmployerPost.queryEmployerPostInfo(objectId);
            }
        }.start();
    }

    public void initView() {
        progressDialog = new ProgressDialog(EmployerPageActivity.this);
        progressDialog.setTitle("");
        progressDialog.setMessage("...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        EmployerPost employerPost = new EmployerPost();
        employerPost.setQueryEmployerPostListener(this);

        showName = (TwoTextViewsControl) findViewById(R.id.my_control_name);
        showAddress = (TwoTextViewsControl) findViewById(R.id.my_control_address);
        showDate = (TwoTextViewsControl) findViewById(R.id.my_control_date);
        showTime = (TwoTextViewsControl) findViewById(R.id.my_control_time);
        showWorkLocation = (TwoTextViewsControl) findViewById(R.id.my_control_location);
        showSituation = (TwoTextViewsControl) findViewById(R.id.my_control_situation);
        showPrice = (TwoTextViewsControl) findViewById(R.id.my_control_price);
        showNote = (TwoTextViewsControl) findViewById(R.id.my_control_note);

        Button messageBT = (Button) findViewById(R.id.bt_leave_message);
        messageBT.setOnClickListener(new MyOnClickListener());
        Button callBT = (Button) findViewById(R.id.bt_call);
        callBT.setOnClickListener(new MyOnClickListener());

        glassView = (View) findViewById(R.id.view_glass);
        glassView.setOnClickListener(new MyOnClickListener());
        glassView.setVisibility(GONE);
        leaveMessage = (LeaveMessage) findViewById(R.id.my_control_leave_message);
        leaveMessage.setLeaveMessageListener(LeaveMessage.WORKER_MESSAGE, objectId, this);
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
                    if (!employerPhoneNumber.equals("")) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + employerPhoneNumber));
                        startActivity(intent);
                    }
                    break;
                case R.id.view_glass:
                    leaveMessage.setMessage("");
                    leaveMessage.setVisibility(GONE);
                    glassView.setVisibility(GONE);
                default:
                    break;
            }
        }
    }


    @Override
    public void onSuccess(EmployerPost employerPost) {
        MyUser employer = employerPost.getEmployer();
        employerPhoneNumber = employer.getMobilePhoneNumber();
        Log.i(TAG, "onSuccess: 雇主电话：" + employerPhoneNumber);
        String name = employerPost.getEmployerName();
        String province = employerPost.getProvince();
        String city = employerPost.getCity();
        String area = employerPost.getArea();
        String specificAddress = employerPost.getSpecificAddress();
        String workDateFrom = employerPost.getWorkDateFrom();
        String workDateTo = employerPost.getWorkDateTo();
        String workTimeFrom = employerPost.getWorkTimeFrom();
        String workTimeTo = employerPost.getWorkTimeTo();
        String workLocation = employerPost.getWorkLocation();
        String situation = employerPost.getEmployerSituation();
        String servicePrice = String.valueOf(employerPost.getEmployerPrice());
        String note = employerPost.getNote();

        showName.set("雇主称呼", name);
        if (province.equals("台湾") || province.equals("澳门")
                || province.equals("香港") || province.equals("北京")
                || province.equals("天津") || province.equals("上海")
                || province.equals("重庆") || province.equals("钓鱼岛")) {
            showAddress.set("雇主地址", (city + "." + area + "\n" + specificAddress));
        } else {
            showAddress.set("雇主地址", (province + "." + city + "." + area + "\n" + specificAddress));
        }

        showDate.set("服务日期", workDateFrom + "  至  " + workDateTo);
        showTime.set("工作时间", workTimeFrom + "  至  " +workTimeTo);
        showWorkLocation.set("工作地点", workLocation);
        showSituation.set("病人情况", situation);
        showPrice.set("服务价格", servicePrice);
        showNote.set("雇主备注", note);

        progressDialog.dismiss();
    }

    @Override
    public void onFailed(){
        //加载失败
        Toast.makeText(EmployerPageActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
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
        Toast.makeText(EmployerPageActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
        leaveMessage.setMessage("");
        glassView.setVisibility(GONE);
        leaveMessage.setVisibility(GONE);
        progressDialog.dismiss();
    }

    @Override
    public void failedMessage() {
        Toast.makeText(EmployerPageActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
    }

}






