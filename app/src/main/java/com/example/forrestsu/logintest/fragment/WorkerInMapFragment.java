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

import com.example.forrestsu.logintest.MyUser;
import com.example.forrestsu.logintest.R;
import com.example.forrestsu.logintest.WorkerPost;
import com.example.forrestsu.logintest.my_controls.LeaveMessage;
import com.example.forrestsu.logintest.my_controls.TwoTextViewsControl;
import com.example.forrestsu.logintest.utils.BmobDownload;
import com.example.forrestsu.logintest.utils.ImageViewUtil;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;

public class WorkerInMapFragment extends Fragment implements WorkerPost.QueryWorkerPostListener,
        LeaveMessage.LeaveMessageListener {

    private static final String TAG = "WorkerInMapFragment";

    private static final int UPDATE_IMAGEVIEW = 1;

    private String objectId;
    private String workerPhoneNumber = "";

    private ProgressDialog progressDialog;

    private CloseFragment closeFragment;

    private CircleImageView photoCIV;
    private TextView showNameTV, showSexTV, showAgeTV, showAddressTV;
    private ImageView levelIV;
    private ImageView starMealIV, starAccommodationIV, starWorkLocationIV;
    private TextView showMealTV, showAccommodationTV, showWorkLocationTV;
    private TwoTextViewsControl timeControl, priceControl, introductionControl;

    private static LeaveMessage leaveMessage;
    private static View glassView;

     @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
         View square = inflater.inflate(R.layout.fragment_worker_in_map, container, false);
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
                        WorkerPost.queryWorkerPostInfo(objectId);
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
                         WorkerPost.queryWorkerPostInfo(objectId);
                     }
                 }.start();
             }
         }
    }

     public void initView(View square) {
         WorkerPost workerPost = new WorkerPost();
         workerPost.setQueryWorkerPostListener(this);

         progressDialog = new ProgressDialog(getContext());
         progressDialog.setTitle("");
         progressDialog.setMessage("...");
         progressDialog.setCancelable(false);
         progressDialog.show();

         ImageView closeIV = (ImageView) square.findViewById(R.id.iv_close);
         closeIV.setOnClickListener(new MyOnClickListener());

         photoCIV = (CircleImageView) square.findViewById(R.id.civ_photo);
         showNameTV = (TextView) square.findViewById(R.id.tv_show_name);
         showSexTV = (TextView) square.findViewById(R.id.tv_show_sex);
         showAgeTV = (TextView) square.findViewById(R.id.tv_show_age);
         showAddressTV = (TextView) square.findViewById(R.id.tv_show_address);
         showAddressTV.requestFocus();
         levelIV = (ImageView) square.findViewById(R.id.iv_level);

         starMealIV = (ImageView) square.findViewById(R.id.iv_star_ico_1);
         starMealIV.setVisibility(GONE);
         starAccommodationIV = (ImageView) square.findViewById(R.id.iv_star_ico_2);
         starAccommodationIV.setVisibility(GONE);
         starWorkLocationIV = (ImageView) square.findViewById(R.id.iv_star_ico_3);
         starWorkLocationIV.setVisibility(GONE);

         showMealTV = (TextView) square.findViewById(R.id.tv_show_1);
         showMealTV.setVisibility(GONE);
         showAccommodationTV = (TextView) square.findViewById(R.id.tv_show_2);
         showAccommodationTV.setVisibility(GONE);
         showWorkLocationTV = (TextView) square.findViewById(R.id.tv_show_3);
         showWorkLocationTV.setVisibility(GONE);

         timeControl = (TwoTextViewsControl) square.findViewById(R.id.my_control_time);
         priceControl = (TwoTextViewsControl) square.findViewById(R.id.my_control_price);
         introductionControl = (TwoTextViewsControl) square.findViewById(R.id.my_control_introduction);

         Button messageBT = (Button) square.findViewById(R.id.bt_leave_message);
         messageBT.setOnClickListener(new MyOnClickListener());
         Button callBT = (Button) square.findViewById(R.id.bt_call);
         callBT.setOnClickListener(new MyOnClickListener());

         glassView = (View) square.findViewById(R.id.view_glass);
         glassView.setOnClickListener(new MyOnClickListener());
         glassView.setVisibility(GONE);

         leaveMessage = (LeaveMessage) square.findViewById(R.id.my_control_leave_message);
         leaveMessage.setLeaveMessageListener(LeaveMessage.EMPLOYER_MESSAGE, objectId, this);
         leaveMessage.setVisibility(GONE);
     }

     public class MyOnClickListener implements View.OnClickListener {

         @Override
         public void onClick(View view) {
             switch (view.getId()) {
                 case R.id.iv_close:
                     closeFragment.closeWorkerInfoFragment();
                     break;
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
        String level = workerPost.getWorkerLevel();
        String withMeal = workerPost.getWithMeal();
        String withAccommodation = workerPost.getWithAccommodation();
        String workLocation = workerPost.getWorkLocation();
        String workTime = workerPost.getWorkTime();
        String servicePrice = String.valueOf(workerPost.getServicePrice());
        String selfIntroduction = workerPost.getSelfIntroduction();

        Log.i(TAG, "onSuccess: \n工作时间：" + workTime +  "\n服务价格：" + servicePrice +  "\n自我介绍：" + selfIntroduction);

        showNameTV.setText(name);
        showAgeTV.setText((age + "岁"));
        if (province.equals("台湾") || province.equals("澳门")
                || province.equals("香港") || province.equals("北京")
                || province.equals("天津") || province.equals("上海")
                || province.equals("重庆") || province.equals("钓鱼岛")) {
            showAddressTV.setText((city + "." + area + "." + specificAddress));
        } else {
            showAddressTV.setText((province + "." + city + "." + area + "." + specificAddress));
        }

        if (level.equals("普通护工")) {
            levelIV.setImageResource(R.drawable.ic_level_simple);
        } else if (level.equals("高级护工")) {
            levelIV.setImageResource(R.drawable.ic_level_senior);
        } else if (level.equals("护士")) {
            levelIV.setImageResource(R.drawable.ic_level_nurse);
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

        timeControl.set("工作时间", workTime);
        priceControl.set("服务价格", servicePrice);
        introductionControl.set("自我介绍", selfIntroduction);

        progressDialog.dismiss();

        //检查远程文件在是否在本地已存在，如果存在，直接显示，否则调用下载
        //存储路径
        final String photoSavePath = getActivity().getExternalCacheDir() + "/img_head/";
        //获取远程文件名
        final String photoName = url.substring(url.lastIndexOf("/") + 1);
        Log.d(TAG, "onSuccess: 远程文件名：" + photoName);
        if (new File(photoSavePath + photoName).exists()) {
            Log.d(TAG, "文件不变，直接显示 ");
            ImageViewUtil.displayImage(photoCIV, photoSavePath + photoName);
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
                    ImageViewUtil.displayImage(photoCIV, photoSavePath + photoName);
                }

                @Override
                public void onFailed() {
                    //
                }
            });
        }
    }

    @Override
    public void onFailed() {
        //加载失败
        progressDialog.dismiss();
        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
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

    /**
     * 回调接口，用于向Activity传参
     */
    public interface CloseFragment {
        void closeWorkerInfoFragment();
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


}
