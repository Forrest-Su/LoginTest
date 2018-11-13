package com.example.forrestsu.logintest.my_controls;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.forrestsu.logintest.EmployerMessage;
import com.example.forrestsu.logintest.EmployerPost;
import com.example.forrestsu.logintest.MyUser;
import com.example.forrestsu.logintest.R;
import com.example.forrestsu.logintest.WorkerMessage;
import com.example.forrestsu.logintest.WorkerPost;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class LeaveMessage extends ConstraintLayout {

    private static final String TAG = "LeaveMessage";

    public static final int WORKER_MESSAGE = 1;  //留言类型：护工留言
    public static final int EMPLOYER_MESSAGE = 2;  //留言类型：雇主留言

    private EditText messageET;  //留言输入
    private TextView countTV;  //显示字数

    public LeaveMessage (Context context) {
        super(context);
    }

    public LeaveMessage (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public LeaveMessage (Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.my_control_leave_message, this, true);

        TextView confirmTV = (TextView) findViewById(R.id.tv_confirm);
        confirmTV.setOnClickListener(new MyOnClickListener());
        TextView cancelTV = (TextView) findViewById(R.id.tv_cancel);
        cancelTV.setOnClickListener(new MyOnClickListener());
        countTV = (TextView) findViewById(R.id.tv_count);
        messageET = (EditText) findViewById(R.id.et_message);
        //设置监听
        messageET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //输入前
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //输入中
            }

            @Override
            public void afterTextChanged(Editable s) {
                //输入后
                if (s.length() <= 400) {
                    //显示当前的字数
                    countTV.setText((s.length() + "/400"));
                } else {
                    //提示字数超过400
                    Toast.makeText(getContext(), "字数超过400，无法继续输入", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //点击事件监听
    public class MyOnClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_confirm:
                    leaveMessageListener.confirmMessage();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            saveMessage();
                        }
                    }).start();
                    break;
                case R.id.tv_cancel:
                    leaveMessageListener.cancelMessage();
                    messageET.setText("");
                    break;

            }
        }
    }

    /**
     *上传留言
     */
    private void saveMessage() {
        String message = messageET.getText().toString();
        if (message.equals("")) {
            Toast.makeText(getContext(), "不能为空", Toast.LENGTH_SHORT).show();
        } else {
            //根据留言类型判断用哪种类型上传
            if (messageType == EMPLOYER_MESSAGE) {
                //当前是护工页面，所以上传的是雇主的留言
                MyUser currentUser = BmobUser.getCurrentUser(MyUser.class);
                WorkerPost workerPost = new WorkerPost();
                workerPost.setObjectId(objectId);
                EmployerMessage employerMessage = new EmployerMessage();
                employerMessage.setContent(message);
                employerMessage.setEmployer(currentUser);
                employerMessage.setWorkerPost(workerPost);
                employerMessage.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            leaveMessageListener.successfulMessage();
                            Log.i(TAG, "done: 雇主留言上传成功,object:" + objectId);
                        } else {
                            leaveMessageListener.failedMessage();
                            Log.i(TAG, "done: 雇主留言上传失败," + e.getErrorCode() + ":" + e.getMessage());
                        }
                    }
                });
            } else if (messageType == WORKER_MESSAGE) {
                //当前是雇主页面，所以上传的是护工的留言
                MyUser currentUser = BmobUser.getCurrentUser(MyUser.class);
                EmployerPost employerPost = new EmployerPost();
                employerPost.setObjectId(objectId);
                WorkerMessage workerMessage = new WorkerMessage();
                workerMessage.setContent(message);
                workerMessage.setWorker(currentUser);
                workerMessage.setEmployerPost(employerPost);
                workerMessage.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            leaveMessageListener.successfulMessage();
                            Log.i(TAG, "done: 护工留言上传成功,object:" + objectId);
                        } else {
                            leaveMessageListener.failedMessage();
                            Log.i(TAG, "done: 护工留言上传失败," + e.getErrorCode() + ":" + e.getMessage());
                        }
                    }
                });
            }

        }
    }


    //获取留言
    public String getMessage() {
        return messageET.getText().toString();
    }

    //设置留言
    public void setMessage(String str) {
        messageET.setText(str);
    }

    //接口
    public interface LeaveMessageListener {
        //发送
        void confirmMessage ();
        //取消发送
        void cancelMessage ();
        //发送成功
        void successfulMessage();
        //发送失败
        void failedMessage();
    }


    private int messageType;
    private String objectId;
    private LeaveMessageListener leaveMessageListener;

    /**
     * 设置接口的方法
     * @param messageType  留言类型：护工留言、雇主留言
     * @param objectId  留言对应的帖子id
     * @param leaveMessageListener  接口对象
     */
    public void setLeaveMessageListener (int messageType, String objectId,
                                         LeaveMessageListener leaveMessageListener) {

        this.messageType = messageType;
        this.objectId = objectId;
        this.leaveMessageListener = leaveMessageListener;
    }



}
