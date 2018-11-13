package com.example.forrestsu.logintest;

import cn.bmob.v3.BmobObject;

/**
 * 护工留言
 */
public class WorkerMessage extends BmobObject {
    private static final String TAG = "WorkerMessage";

    private MyUser worker; //关联的用户
    private EmployerPost employerPost;  //关联的帖子,护工留言关联到雇主的帖子
    private String content;  //留言内容

    //关联的用户
    public void setWorker(MyUser worker) {
        this.worker = worker;
    }
    public MyUser getWorker() {
        return worker;
    }

    //关联的帖子
    public void setEmployerPost(EmployerPost employerPost) {
        this.employerPost = employerPost;
    }
    public EmployerPost getWorkerPost() {
        return employerPost;
    }

    //留言内容
    public void setContent(String content) {
        this.content = content;
    }
    public String getContent() {
        return content;
    }

}
