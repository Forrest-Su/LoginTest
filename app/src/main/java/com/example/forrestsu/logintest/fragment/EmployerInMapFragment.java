package com.example.forrestsu.logintest.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.forrestsu.logintest.EmployerPost;
import com.example.forrestsu.logintest.MyUser;
import com.example.forrestsu.logintest.R;
import com.example.forrestsu.logintest.my_controls.LeaveMessage;
import com.example.forrestsu.logintest.my_controls.TwoTextViewsControl;
import com.example.forrestsu.logintest.utils.ImageViewUtil;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;

public class EmployerInMapFragment extends Fragment
        implements EmployerPost.QueryEmployerPostListener, LeaveMessage.LeaveMessageListener {

    private static final String TAG = "EmployerInMapFragment";

    private static final int UPDATE_IMAGEVIEW = 1;

    private String objectId;
    private String employerPhoneNumber = "";

    private ProgressDialog progressDialog;

    private CloseFragment closeFragment;

    private CircleImageView photoCIV;
    private TextView showNameTV, showAddressTV;
    private ImageView starWorkLocationIV, startSituationIV;
    private TextView showWorkLocationTV, showSituationTV;
    private TwoTextViewsControl dateControl, timeControl, priceControl, noteControl;

    private static LeaveMessage leaveMessage;
    private static View glassView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View square = inflater.inflate(R.layout.fragment_employer_in_map, container, false);
        initView(square);

        return square;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isAdded()) { //判断Fragment已经依附Activity
            try {
                objectId = getArguments().getString("objectId", "");
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            if (!objectId.equals("")) {
                //开始查询
                new Thread() {
                    public void run() {
                        EmployerPost.queryEmployerPostInfo(objectId);
                    }
                }.start();
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.i(TAG, "onHiddenChanged: 回调onHiddenChanged");
        Log.i(TAG, "onHiddenChanged: 状态：" + hidden);
        super.onHiddenChanged(hidden);
        if (hidden) {
            return;
        } else {
            progressDialog.show();
            //刷新数据
            try {
                objectId = getArguments().getString("objectId", "");
                Log.i(TAG, "onHiddenChanged: \nobjectId:" + objectId);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            if (!objectId.equals("")) {
                //开始查询
                new Thread() {
                    public void run() {
                        EmployerPost.queryEmployerPostInfo(objectId);
                    }
                }.start();
            }
        }
    }

    public void initView(View square) {
        EmployerPost employerPost = new EmployerPost();
        employerPost.setQueryEmployerPostListener(this);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("");
        progressDialog.setMessage("...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ImageView closeIV = (ImageView) square.findViewById(R.id.iv_close);
        closeIV.setOnClickListener(new MyOnClickListener());

        photoCIV = (CircleImageView) square.findViewById(R.id.civ_photo);
        showNameTV = (TextView) square.findViewById(R.id.tv_show_name);
        showAddressTV = (TextView) square.findViewById(R.id.tv_show_address);
        showAddressTV.requestFocus();

        starWorkLocationIV = (ImageView) square.findViewById(R.id.iv_star_ico_1);
        showWorkLocationTV = (TextView) square.findViewById(R.id.tv_show_1);
        startSituationIV = (ImageView) square.findViewById(R.id.iv_star_ico_2);
        showSituationTV = (TextView) square.findViewById(R.id.tv_show_2);

        dateControl = (TwoTextViewsControl) square.findViewById(R.id.my_control_date);
        timeControl = (TwoTextViewsControl) square.findViewById(R.id.my_control_time);
        priceControl = (TwoTextViewsControl) square.findViewById(R.id.my_control_price);
        noteControl = (TwoTextViewsControl) square.findViewById(R.id.my_control_note);

        Button messageBT = (Button) square.findViewById(R.id.bt_leave_message);
        messageBT.setOnClickListener(new MyOnClickListener());
        Button callBT = (Button) square.findViewById(R.id.bt_call);
        callBT.setOnClickListener(new MyOnClickListener());

        glassView = (View) square.findViewById(R.id.view_glass);
        glassView.setOnClickListener(new MyOnClickListener());
        glassView.setVisibility(GONE);

        leaveMessage = (LeaveMessage) square.findViewById(R.id.my_control_leave_message);
        leaveMessage.setLeaveMessageListener(LeaveMessage.WORKER_MESSAGE, objectId, this);
        leaveMessage.setVisibility(GONE);
    }

    public class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_close:
                    closeFragment.closeEmployerInfoFragment();
                    break;
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
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof CloseFragment) {
            closeFragment = (CloseFragment) context;
        } else {
            throw new IllegalArgumentException("Activity must implements CloseFragment");
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
        String workLocation = employerPost.getWorkLocation();
        String situation = employerPost.getEmployerSituation();
        String workDateFrom = employerPost.getWorkDateFrom();
        String workDateTo = employerPost.getWorkDateTo();
        String workTimeFrom = employerPost.getWorkTimeFrom();
        String workTimeTo = employerPost.getWorkTimeTo();
        String servicePrice = String.valueOf(employerPost.getEmployerPrice());
        String note = employerPost.getNote();

        showNameTV.setText(name);
        if (province.equals("台湾") || province.equals("澳门")
                || province.equals("香港") || province.equals("北京")
                || province.equals("天津") || province.equals("上海")
                || province.equals("重庆") || province.equals("钓鱼岛")) {
            showAddressTV.setText((city + "." + area + "." + specificAddress));
        } else {
            showAddressTV.setText((province + "." + city + "." + area + "." + specificAddress));
        }

        showWorkLocationTV.setText(workLocation);
        showSituationTV.setText(situation);

        dateControl.set("服务日期", workDateFrom + "  至  " + workDateTo);
        timeControl.set("工作时间", workTimeFrom + "  至  " + workTimeTo);
        priceControl.set("服务价格", servicePrice);
        noteControl.set("自我介绍", note);

        progressDialog.dismiss();

    }

    @Override
    public void onFailed() {
        //加载失败
        progressDialog.dismiss();
        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(getContext(), "发送成功", Toast.LENGTH_SHORT).show();
        leaveMessage.setMessage("");
        leaveMessage.setVisibility(GONE);
        glassView.setVisibility(GONE);
        progressDialog.dismiss();
    }

    @Override
    public void failedMessage() {
        Toast.makeText(getContext(), "发送失败", Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
    }


    /**
     * 回调接口，用于向Activity传参
     */
    public interface CloseFragment {
        void closeEmployerInfoFragment();
    }


    private class MyHandler extends Handler {

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
