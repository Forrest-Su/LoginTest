package com.example.forrestsu.logintest.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.forrestsu.logintest.AddressManager;
import com.example.forrestsu.logintest.Database;
import com.example.forrestsu.logintest.MyApplication;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;

public class BmobDownload {
    private static final String TAG = "BmobDownload";

    private BmobDownloadListener bmobDownloadListener;
    public void setBmobDownloadListener(BmobDownloadListener bmobDownloadListener) {
        this.bmobDownloadListener = bmobDownloadListener;
    }

    /*
    获取城市数据库
     */
    public void getDatabase(final Context context, final String fileName) {
        BmobQuery<Database> query = new BmobQuery<Database>();
        query.addQueryKeys("databaseUrl");
        query.addWhereEqualTo("databaseName", "city");
        query.findObjects(new FindListener<Database>() {
            @Override
            public void done(List<Database> list, BmobException e) {
                if (e == null) {
                    String url = list.get(0).getDatabaseUrl();
                    BmobFile bmobFile = new BmobFile(fileName, "", url);
                    downloadBmobFile(context, bmobFile, fileName);
                } else {
                    Log.d(TAG, "error: " + e.getErrorCode() + "/" + e.getMessage());
                }
            }
        });
    }
    /*
   Bmob下载city数据库
    */
    private void downloadBmobFile(final Context context, BmobFile bmobFile, String fileName) {
        String savePath = (MyApplication.getContext().getExternalCacheDir() + "/" + fileName);
        File file = new File(savePath);
        if (file.exists()) {
            file.delete();
        }
        bmobFile.download(file, new DownloadFileListener() {

            @Override
            public void onStart() {
                Log.d(TAG, "开始下载数据库...");
            }

            @Override
            public void done(String savePath, BmobException e) {
                if(e==null){
                    //bmobDownloadListener.onSuccess();
                    //manager = new AddressManager(databasePath);
                    //mHandler.sendEmptyMessage(DATABASE_OK);
                    Log.d(TAG, "下载成功,保存路径:"+ savePath);
                }else{
                    //bmobDownloadListener.onFailed();
                    Toast.makeText(context, "下载失败："+e.getErrorCode()+","+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onProgress(Integer value, long networkSpeed) {
                Log.d(TAG,"下载进度："+ value + ", " + networkSpeed);
            }

        });
    }


    /**
     * Bmob下载
     * @param bmobFile
     * @param savePath  保存路径（不包含文件名）
     * @param fileName  文件名
     */
    public void downloadBmobFile(BmobFile bmobFile, String savePath, final String fileName) {

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
                    bmobDownloadListener.onSuccess();
                    Log.d(TAG, "下载成功,保存路径:"+ savePath);
                }else{
                    bmobDownloadListener.onFailed();
                    Log.d(TAG, "done: 加载头像失败");
                }
            }

            @Override
            public void onProgress(Integer value, long networkSpeed) {
                Log.d(TAG,"下载进度："+ value + ", " + networkSpeed);
            }

        });
    }

    public interface BmobDownloadListener {
        void onSuccess();
        void onFailed();
    }
}
