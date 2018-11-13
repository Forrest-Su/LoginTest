package com.example.forrestsu.logintest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.example.forrestsu.logintest.activity.SettingActivity;


public class MyNotificationManager {
    private static final String TAG = "MyNotificationManager";

    //PendingIntent.getBroadcast()的第二个参数RequestCode
    private static final int PREPARING = 0;
    private static final int DOWNLOADING = 1;
    private static final int PAUSE = 2;
    private static final int CANCEL = 3;
    private static final int SUCCESS = 4;
    private static final int FAILED = 5;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void initChannel(Context context) {
        NotificationManager notificationManager
                = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        //创建NotificationChannel()
        NotificationChannel channelWithSound = new NotificationChannel(
                "channelWithSound", "channelWithSound", NotificationManager.IMPORTANCE_DEFAULT);
        channelWithSound.setShowBadge(true); //长按图标时是否显示通知内容
        //channelWithSound.enableLights(true); //设置通知来时，是否亮起呼吸灯
        //channelWithSound.setLightColor(Color.GREEN); //设置呼吸灯颜色
        //channelWithSound.setSound(null, null); /设置通知声音
        notificationManager.createNotificationChannel(channelWithSound);

        //创建NotificationChannel   IMPORTANCE_LOW
        NotificationChannel channelWithoutSound = new NotificationChannel(
                "channelWithoutSound", "channelWithoutSound", NotificationManager.IMPORTANCE_LOW);
        channelWithoutSound.setShowBadge(true); //长按图标时是否显示通知内容
        notificationManager.createNotificationChannel(channelWithoutSound);
    }


    /**
     * 默认通知，有声音提示，用于下载成功和下载失败
     * @param context
     * @param title 通知标题
     * @param content 通知内容
     */
   @RequiresApi(api = Build.VERSION_CODES.O)
   public static void showDefaultNotice(Context context, String title, String content) {
       int notificationId = 1;
       Intent intent = new Intent(context, SettingActivity.class);
       PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
       NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
               "channelWithSound");

       RemoteViews remoteViewsDefault = new RemoteViews(context.getPackageName(),
               R.layout.notification_download);
       remoteViewsDefault.setTextViewText(R.id.tv_title, title);
       remoteViewsDefault.setTextViewText(R.id.tv_content, content);

       builder.setContentIntent(pi)
               .setCustomContentView(remoteViewsDefault) //设置自定义布局
               .setCustomBigContentView(remoteViewsDefault)
               //.setContentTitle(title) //设置通知标题               //.setContentText(content) //设置通知内容
               .setWhen(System.currentTimeMillis()) //设置通知时间，这是获取了当前系统时间
               .setSmallIcon(R.drawable.android_white_18dp) //设置通知图标（系统状态栏上显示的小图标）
               //.setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE) //关闭声音、震动
               //.setLights(Color.GREEN, 1000, 1000);//设置呼吸灯
               .setAutoCancel(true); //点击通知后取消显示

       NotificationManager notificationManager
               = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
       notificationManager.notify(notificationId, builder.build());
   }


    /**
     * 静音通知，用于下载中和暂停
     * @param context
     * @param title 通知标题
     * @param content 通知内容
     * @param progress 下载进度
     * @param downloadState 下载状态（下载中/暂停）
     */
   @RequiresApi(api = Build.VERSION_CODES.O)
   public static void showSilentNotice(Context context, String title, String content,
                                       int progress, int downloadState) {
       int notificationId = 5;
       Intent intent = new Intent(context, SettingActivity.class);
       PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
       NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
               "channelWithoutSound");

       RemoteViews remoteViewsSilent
               = new RemoteViews(context.getPackageName(), R.layout.notification_download);
       remoteViewsSilent.setTextViewText(R.id.tv_title, title);
       remoteViewsSilent.setTextViewText(R.id.tv_content, content);
       //remoteViewsLow.setImageViewResource(R.id.iv_start_pause, R.drawable.baseline_pause_circle_outline_black_24dp);
       remoteViewsSilent.setImageViewResource(R.id.iv_cancel, R.drawable.outline_delete_forever_black_24dp);

       if (progress > 0) {
           remoteViewsSilent.setProgressBar(R.id.pb_downloading, 100, progress, false);
       }

       if (downloadState == PREPARING) {
           remoteViewsSilent.setImageViewResource(R.id.iv_start_pause,
                   R.drawable.baseline_pause_circle_outline_black_24dp);
       }

       String data = "";
       //设置点击事件，开始或暂停
       if (downloadState == DOWNLOADING) {
           remoteViewsSilent.setImageViewResource(R.id.iv_start_pause,
                   R.drawable.baseline_pause_circle_outline_black_24dp);
           data = "暂停";
       } else if (downloadState == PAUSE) {
           remoteViewsSilent.setImageViewResource(R.id.iv_start_pause,
                   R.drawable.baseline_play_circle_outline_black_24dp);
           data = "开始";
       }
       Intent startIntent = new Intent();
       startIntent.setAction("com.example.forrestsu.logintest.START_PAUSE");
       startIntent.putExtra("动作", data);
       PendingIntent startPendingIntent = PendingIntent.getBroadcast(
               context, PAUSE, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);
       remoteViewsSilent.setOnClickPendingIntent(R.id.iv_start_pause, startPendingIntent);

       //设置点击事件，取消
       String cancel = "取消";
       Intent cancelIntent = new Intent();
       cancelIntent.setAction("com.example.forrestsu.logintest.START_PAUSE");
       cancelIntent.putExtra("动作", cancel);
       PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(
               context, CANCEL, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
       remoteViewsSilent.setOnClickPendingIntent(R.id.iv_cancel, cancelPendingIntent);

       builder.setContentIntent(pi)
               .setCustomContentView(remoteViewsSilent) //设置自定义布局
               .setCustomBigContentView(remoteViewsSilent)
               //.setContentTitle(title) //设置通知标题
               //.setContentText(content) //设置通知内容
               .setWhen(System.currentTimeMillis()) //设置通知时间，这是获取了当前系统时间
               .setSmallIcon(R.drawable.android_white_18dp) //设置通知图标（系统状态栏上显示的小图标）
               //.setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE) //关闭声音、震动
               //.setLights(Color.GREEN, 1000, 1000);//设置呼吸灯
               .setAutoCancel(true); //点击通知后取消显示

       NotificationManager notificationManager
               = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
       notificationManager.notify(notificationId, builder.build());
   }



/*

   //正在下载，静音
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void showDownLoadingNotice(Context context, String title, String content, int progress) {
        Intent intent = new Intent(context, SettingActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                "importance_low");
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_download);
        remoteViews.setTextViewText(R.id.tv_title, title);
        remoteViews.setImageViewResource(R.id.iv_start_pause, R.drawable.baseline_pause_circle_outline_black_24dp);
        remoteViews.setImageViewResource(R.id.iv_cancel, R.drawable.outline_delete_forever_black_24dp);


        //设置点击事件，暂停
        String pause = "暂停";
        Intent pauseIntent = new Intent();
        pauseIntent.setAction("com.example.forrestsu.logintest.START_PAUSE");
        pauseIntent.putExtra("动作", pause);
        PendingIntent pausePendingIntent = PendingIntent.getBroadcast(
                context, DOWNLOADING, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.iv_start_pause, pausePendingIntent);
        //设置点击事件，取消
        String cancel = "取消";
        Intent cancelIntent = new Intent();
        cancelIntent.setAction("com.example.forrestsu.logintest.START_PAUSE");
        cancelIntent.putExtra("动作", cancel);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(
                context, CANCEL, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.iv_cancel, cancelPendingIntent);


        builder.setContentIntent(pi)
                .setCustomContentView(remoteViews) //设置自定义布局
                .setCustomBigContentView(remoteViews)
                //.setContentTitle(title) //设置通知标题
                //.setContentText(content) //设置通知内容
                .setWhen(System.currentTimeMillis()) //设置通知时间，这是获取了当前系统时间
                .setSmallIcon(R.drawable.android_white_18dp); //设置通知图标（系统状态栏上显示的小图标）
                //.setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE) //关闭声音、震动
                //.setLights(Color.GREEN, 1000, 1000);//设置呼吸灯
                //.setAutoCancel(true); //点击通知后取消显示
        NotificationManager notificationManager
                = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(DOWNLOADING, builder.build());
    }


   //暂停下载，静音
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void showPauseNotice(Context context, String title, String content, int progress) {
        Intent intent = new Intent(context, SettingActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                "importance_low");

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_download);
        remoteViews.setTextViewText(R.id.tv_title, title);
        remoteViews.setTextViewText(R.id.tv_content, content);
        remoteViews.setImageViewResource(R.id.iv_start_pause, R.drawable.baseline_play_circle_outline_black_24dp);
        remoteViews.setImageViewResource(R.id.iv_cancel, R.drawable.outline_delete_forever_black_24dp);


        builder.setContentIntent(pi)
                .setCustomContentView(remoteViews) //设置自定义布局
                .setCustomBigContentView(remoteViews)
                //.setContentTitle(title) //设置通知标题
                //.setContentText(content) //设置通知内容
                .setWhen(System.currentTimeMillis()) //设置通知时间，这是获取了当前系统时间
                .setSmallIcon(R.drawable.android_white_18dp); //设置通知图标（系统状态栏上显示的小图标）
                //.setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE) //关闭声音、震动
                //.setLights(Color.GREEN, 1000, 1000);//设置呼吸灯
                //.setAutoCancel(true); //点击通知后取消显示

        NotificationManager notificationManager
                = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(PAUSE, builder.build());
    }


   //下载成功，提示音
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void showSuccessNotice(Context context, String title, String content) {
        Intent intent = new Intent(context, SettingActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                "importance_default");

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_download);
        remoteViews.setTextViewText(R.id.tv_title, title);
        remoteViews.setTextViewText(R.id.tv_content, content);

        builder.setContentIntent(pi)
                .setCustomContentView(remoteViews) //设置自定义布局
                .setCustomBigContentView(remoteViews)
                //.setContentTitle(title) //设置通知标题
                //.setContentText(content) //设置通知内容
                .setWhen(System.currentTimeMillis()) //设置通知时间，这是获取了当前系统时间
                .setSmallIcon(R.drawable.android_white_18dp) //设置通知图标（系统状态栏上显示的小图标）
                //.setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE) //关闭声音、震动
                //.setLights(Color.GREEN, 1000, 1000);//设置呼吸灯
                .setAutoCancel(true); //点击通知后取消显示

        NotificationManager notificationManager
                = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(SUCCESS, builder.build());
    }


   //下载失败，提示音
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void showFailedNotice(Context context, String title, String content) {
        Intent intent = new Intent(context, SettingActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                "importance_default");

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_download);
        remoteViews.setTextViewText(R.id.tv_title, title);
        remoteViews.setTextViewText(R.id.tv_content, content);

        builder.setContentIntent(pi)
                .setCustomContentView(remoteViews) //设置自定义布局
                .setCustomBigContentView(remoteViews)
                //.setContentTitle(title) //设置通知标题
                //.setContentText(content) //设置通知内容
                .setWhen(System.currentTimeMillis()) //设置通知时间，这是获取了当前系统时间
                .setSmallIcon(R.drawable.android_white_18dp) //设置通知图标（系统状态栏上显示的小图标）
                //.setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE) //关闭声音、震动
                //.setLights(Color.GREEN, 1000, 1000);//设置呼吸灯
                .setAutoCancel(true); //点击通知后取消显示

        NotificationManager notificationManager
                = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(FAILED, builder.build());
    }
    */



}
