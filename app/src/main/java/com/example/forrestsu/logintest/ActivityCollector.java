package com.example.forrestsu.logintest;

/*
用于管理所有活动
提供一系列管理活动的方法
 */

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class ActivityCollector {

    //创建List用于暂存活动
    public static List<Activity> activities = new ArrayList<>();

    //方法：将活动添加到List
    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    //方法：将活动从List中移除
    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    //方法：结束所有活动
    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}
