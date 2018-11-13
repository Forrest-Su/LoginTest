package com.example.forrestsu.logintest;

import android.os.AsyncTask;
import android.os.Environment;

import com.example.forrestsu.logintest.my_interface.DownloadListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadTask extends AsyncTask<String , Integer, Integer> {

    public static final int STATE_SUCCESS = 0;
    public static final int STATE_FAILED = 1;
    public static final int STATE_PAUSED = 2;
    public static final int STATE_CANCELED = 3;

    //创建回调接口
    private DownloadListener listener;
    //初始取消状态为false
    private boolean isCanceled = false;
    //初始暂停状态为fasle
    private boolean isPaused = false;
    //下载进度
    private int lastProgress;

    public DownloadTask(DownloadListener listener) {
        this.listener = listener;
    }

    @Override
    //在子线程中执行耗时操作
    protected Integer doInBackground(String... params) {
        InputStream is = null;
        RandomAccessFile savedFile = null;
        File file = null;

        try {
            //已下载的文件长度
            long downloadedLength = 0;
            String downloadUrl = params[0];
            //获取文件名
            String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
            String directory = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS).getPath();
            file = new File(directory + fileName);
            if (file.exists()) {
                downloadedLength = file.length();
            }
            long contentLength = getContentLength(downloadUrl);
            if (contentLength == 0) {
                return STATE_FAILED;
            } else if (contentLength == downloadedLength) {
                return STATE_SUCCESS;
            }
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .addHeader("RANGE", "bytes=" + downloadedLength + "-")
                    .url(downloadUrl)
                    .build();
            Response response = client.newCall(request).execute();
            if (response != null) {
                is = response.body().byteStream();
                savedFile = new RandomAccessFile(file, "rw");
                savedFile.seek(downloadedLength);
                byte[] b = new byte[1024];
                int total = 0;
                int len;
                while ((len = is.read(b)) != -1) {
                    if (isCanceled) {
                        return STATE_CANCELED;
                    } else if (isPaused) {
                        return STATE_PAUSED;
                    } else {
                        total += len;
                        savedFile.write(b, 0, len);
                        int progress = (int) ((total + downloadedLength) * 100 / contentLength);
                        publishProgress(progress);
                    }
                }
                response.close();
                return STATE_SUCCESS;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }

                if (savedFile != null) {
                    savedFile.close();
                }

                if (isCanceled && file != null) {
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return STATE_FAILED;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];
        if (progress > lastProgress) {
            listener.onProgress(progress);
            lastProgress = progress;
        }
    }

    @Override
    protected void onPostExecute(Integer status) {
        switch (status) {
            case STATE_SUCCESS:
                listener.onSuccess();
                break;
            case STATE_FAILED:
                listener.onFailed();
                break;
            case STATE_PAUSED:
                listener.onPaused(lastProgress);
                break;
            case STATE_CANCELED:
                listener.onCanceled();
                break;
            default:
                break;
        }
    }

    public void pauseDownload() {
        isPaused = true;
    }

    public void cancelDownload() {
        isCanceled = true;
    }

    private long getContentLength(String downloadUrl) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(downloadUrl).build();
        Response response = client.newCall(request).execute();
        if (response != null && response.isSuccessful()) {
            long contentLength = response.body().contentLength();
            return contentLength;
        }
        return 0;
    }

}

