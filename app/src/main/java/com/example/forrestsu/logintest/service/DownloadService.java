package com.example.forrestsu.logintest.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.forrestsu.logintest.DownloadTask;
import com.example.forrestsu.logintest.MyNotificationManager;
import com.example.forrestsu.logintest.R;
import com.example.forrestsu.logintest.activity.SettingActivity;
import com.example.forrestsu.logintest.my_interface.DownloadListener;

import org.apache.http.impl.conn.BasicHttpClientConnectionManager;

import java.io.File;

import static com.example.forrestsu.logintest.MyApplication.getContext;

public class DownloadService extends Service {
    private static final String TAG = "DownloadService";

    private static final int PREPARING = 0;
    private static final int DOWNLOADING = 1;
    private static final int PAUSE = 2;
    private static final int CANCEL = 3;
    private static final int SUCCESS = 4;
    private static final int FAILED = 5;

    private DownloadTask downloadTask;

    private String downloadUrl;
    private EditText urlET;

    public static RemoteViews remoteViewsDefault = new RemoteViews(
            getContext().getPackageName(), R.layout.notification_download);
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= 26) {
            MyNotificationManager.initChannel(this);
        }
    }

    private DownloadListener listener = new DownloadListener() {
        @Override
        public void onProgress(int progress) {
            if (Build.VERSION.SDK_INT >= 26) {
                getNotificationManager().cancel(PAUSE);
                MyNotificationManager.showSilentNotice(DownloadService.this,
                        "下载", "正在下载... " + progress + "%", progress, DOWNLOADING);
            } else {
                getNotificationManager().notify(1, getNotification(
                        "下载", "正在下载... " + progress + "%", progress, DOWNLOADING));
            }
            /*
            NotificationManager manager = getNotificationManager();
            Log.d(TAG, "onProgress: 创建NotificationManager");
            if (Build.VERSION.SDK_INT >= 26) {
                Log.d(TAG, "onProgress: manager，创建channel");
                manager.createNotificationChannel(getChannel_low());
            }
            manager.notify(1, getNotification("Downloading...", progress));
            */
            //getNotificationManager().notify(1, getNotification("Downloading...", progress));
        }

        @Override
        public void onPaused(int lastProgress) {
            downloadTask = null;
            if (Build.VERSION.SDK_INT >= 26) {
                getNotificationManager().cancel(DOWNLOADING);
                MyNotificationManager.showSilentNotice(DownloadService.this,
                        "下载暂停", "当前进度：" + lastProgress + "%", lastProgress, PAUSE);
            } else {
                getNotificationManager().notify(1, getNotification(
                        "下载暂停", "当前进度：" + lastProgress + "%", -1, PAUSE));
            }
            Toast.makeText(DownloadService.this, "下载暂停", Toast.LENGTH_SHORT).show();
        }


        @Override
        public void onSuccess() {
            downloadTask = null;
            stopForeground(true);
            if (Build.VERSION.SDK_INT >= 26) {
                getNotificationManager().cancel(DOWNLOADING);
                MyNotificationManager.showDefaultNotice(DownloadService.this,
                        "下载", "下载成功");
            } else {
                getNotificationManager().notify(1, getNotification(
                        "下载", "下载成功", -1, SUCCESS));
            }
           // getNotificationManager().notify(1, getNotification("下载成功", -1));
            Toast.makeText(DownloadService.this, "下载成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed() {
            downloadTask = null;
            stopForeground(true);
            getNotificationManager().cancel(DOWNLOADING);
            if (Build.VERSION.SDK_INT >= 26) {
                MyNotificationManager.showDefaultNotice(DownloadService.this,
                        "下载", "下载失败");
            } else {
                getNotificationManager().notify(1, getNotification(
                        "下载", "下载失败", -1, FAILED));
            }
            //getNotificationManager().notify(1, getNotification("下载失败", -1));
            Toast.makeText(DownloadService.this, "下载失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCanceled() {
            downloadTask = null;
            stopForeground(true);
            Toast.makeText(DownloadService.this, "下载取消", Toast.LENGTH_SHORT).show();
        }
    };

    private DownloadBinder mBinder = new DownloadBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class DownloadBinder extends Binder {

        public void startDownload(String url) {
            if (Build.VERSION.SDK_INT >= 26) {
                MyNotificationManager.showSilentNotice(DownloadService.this, "下载",
                        "开始下载...", -1, PREPARING);
            } else {
                getNotificationManager().notify(1, getNotification(
                        "下载", "开始下载...", -1, PREPARING));
            }

            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                if (downloadTask == null) {
                    downloadUrl = url;
                    downloadTask = new DownloadTask(listener);
                    downloadTask.execute(downloadUrl);
                    Toast.makeText(DownloadService.this,
                            "正在准备下载，请稍等", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "请检查网络", Toast.LENGTH_SHORT).show();
                pauseDownload();
            }




        }

        public void pauseDownload() {
            if (downloadTask != null) {
                downloadTask.pauseDownload();
            }
        }

        public void cancelDownload() {
            getNotificationManager().cancelAll();
            if (downloadTask != null) {
                downloadTask.cancelDownload();
            } else {
                if (downloadUrl != null) {
                    String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                    String directory = Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS).getPath();
                    File file = new File(directory + fileName);
                    if (file.exists()) {
                        file.delete();
                    }
                    stopForeground(true);
                    Toast.makeText(DownloadService.this, "下载取消", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    //适用于api小于26
    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }
    //适用于api小于26
    private Notification getNotification(String title, String content, int progress, int downloadState) {
        Intent intent = new Intent(this, SettingActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, null);

        remoteViewsDefault.setTextViewText(R.id.tv_title, title);
        remoteViewsDefault.setTextViewText(R.id.tv_content, content);
        //remoteViewsDefault.setImageViewResource(R.id.iv_start_pause, R.drawable.baseline_pause_circle_outline_black_24dp);
        remoteViewsDefault.setImageViewResource(R.id.iv_cancel, R.drawable.outline_delete_forever_black_24dp);

        if (progress > 0) {
            remoteViewsDefault.setProgressBar(R.id.pb_downloading, 100, progress, false);
        }

        if (downloadState == PREPARING) {
            remoteViewsDefault.setImageViewResource(R.id.iv_start_pause,
                    R.drawable.baseline_pause_circle_outline_black_24dp);
        }

        String data = "";
        //设置点击事件，开始或暂停
        if (downloadState == DOWNLOADING) {
            remoteViewsDefault.setImageViewResource(R.id.iv_start_pause,
                    R.drawable.baseline_play_circle_outline_black_24dp);
            data = "暂停";
        } else if (downloadState == PAUSE) {
            remoteViewsDefault.setImageViewResource(R.id.iv_start_pause,
                    R.drawable.baseline_pause_circle_outline_black_24dp);
            data = "开始";
        }
        Intent startIntent = new Intent();
        startIntent.setAction("com.example.forrestsu.logintest.START_PAUSE");
        startIntent.putExtra("动作", data);
        PendingIntent startPendingIntent = PendingIntent.getBroadcast(
                this, PAUSE, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViewsDefault.setOnClickPendingIntent(R.id.iv_start_pause, startPendingIntent);

        //设置点击事件，取消
        String cancel = "取消";
        Intent cancelIntent = new Intent();
        cancelIntent.setAction("com.example.forrestsu.logintest.START_PAUSE");
        cancelIntent.putExtra("动作", cancel);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(
                this, CANCEL, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViewsDefault.setOnClickPendingIntent(R.id.iv_cancel, cancelPendingIntent);

        mBuilder.setContentIntent(pi)
                .setCustomContentView(remoteViewsDefault) //设置自定义布局
                .setCustomBigContentView(remoteViewsDefault)
                //.setContentTitle(title) //设置通知标题
                //.setContentText(content) //设置通知内容
                //.setWhen(System.currentTimeMillis()) //设置通知时间，这是获取了当前系统时间
                .setSmallIcon(R.drawable.android_white_18dp) //设置通知图标（系统状态栏上显示的小图标）
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.drawable.android_white_24dp)) //设置通知图标（下拉系统状态栏时显示的图标）
                //.setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE) //关闭声音、震动
                //.setLights(Color.GREEN, 1000, 1000)//设置呼吸灯
                .setAutoCancel(true); //点击通知后取消显示
        if (downloadState == DOWNLOADING || downloadState == PAUSE) {
            mBuilder.setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE); //关闭声音、震动
        } else if (downloadState == SUCCESS || downloadState == FAILED) {
            mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND); //默认声音
        }
        return mBuilder.build();
    }

}
