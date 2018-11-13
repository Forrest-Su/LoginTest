package com.example.forrestsu.logintest.my_interface;

//下载回调接口
public interface DownloadListener {
    //下载进度
    void onProgress(int progress);
    //下载成功
    void onSuccess();
    //下载失败
    void onFailed();
    //下载暂停
    void onPaused(int lastProgress);
    //下载取消
    void onCanceled();
}