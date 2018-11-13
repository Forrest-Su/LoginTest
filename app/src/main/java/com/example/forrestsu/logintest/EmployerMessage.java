package com.example.forrestsu.logintest;

import com.example.forrestsu.logintest.MyUser;
import com.example.forrestsu.logintest.WorkerPost;

import cn.bmob.v3.BmobObject;

/**
 * 雇主留言
 */
public class EmployerMessage extends BmobObject {
    private static final String TAG = "EmployerMessage";

    private MyUser employer; //关联的用户
    private WorkerPost workerPost;  //关联的帖子,雇主的留言关联到护工的帖子
    private String content;  //留言内容

    //关联的用户
    public void setEmployer(MyUser employer) {
        this.employer = employer;
    }
    public MyUser getEmployer() {
        return employer;
    }

    //关联的帖子
    public void setWorkerPost(WorkerPost workerPost) {
        this.workerPost = workerPost;
    }
    public WorkerPost getWorkerPost() {
        return workerPost;
    }

    //留言内容
    public void setContent(String content) {
        this.content = content;
    }
    public String getContent() {
        return content;
    }
}
