package com.example.forrestsu.logintest;

import android.util.Log;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

public class WorkerPost extends BmobObject {
    private static final String TAG = "WorkerPost";

    private MyUser worker;  //关联的用户
    private BmobFile workerPhoto;  //头像
    private String workerPhotoUrl;  //头像Url
    private String workerName;  //姓名
    private int workerAge;  //年龄（根据生日计算）
    private String province;   //省
    private String city;  //市
    private String area;  //县/区
    private String specificAddress;  //详细地址
    private String workerLevel;  //级别
    private String workTime;  //工作时间
    private String withMeal;  //是否含餐
    private String withAccommodation;  //是否住宿
    private String workLocation;  //工作地点
    private int servicePrice;  //服务价格
    private String selfIntroduction;  //自我介绍

    private int workerDistance;  //距离（临时数据，测试用）

    //关联的用户
    public MyUser getWorker(){
        return worker;
    }
    public void setWorker(MyUser worker) {
        this.worker = worker;
    }

    //照片
    public void setWorkerPhoto(BmobFile workerPhoto) {
        this.workerPhoto = workerPhoto;
    }
    public BmobFile getWorkerPhoto() {
        return workerPhoto;
    }

    //照片URL
    public void setWorkerPhotoUrl(String workerPhotoUrl) {
        this.workerPhotoUrl = workerPhotoUrl;
    }
    public String getWorkerPhotoUrl() {
        return workerPhotoUrl;
    }

    //姓名
    public String getWorkerName(){
        return workerName;
    }
    public void setWorkerName(String workerName){
        this.workerName = workerName;
    }

    //年龄
    public int getWorkerAge() {
        return workerAge;
    }
    public void setWorkerAge(int workerAge) {
        this.workerAge = workerAge;
    }

    //省
    public void setProvince(String province) {
        this.province = province;
    }
    public String getProvince () {
        return province;
    }

    //市
    public void setCity(String city) {
        this.city = city;
    }
    public String getCity() {
        return city;
    }

    //县/区
    public void setArea(String area) {
        this.area = area;
    }
    public String getArea() {
        return area;
    }

    //详细地址
    public void setSpecificAddress(String specificAddress) {
        this.specificAddress = specificAddress;
    }
    public String getSpecificAddress() {
        return specificAddress;
    }

    //级别
    public String getWorkerLevel() {
        return workerLevel;
    }
    public void setWorkerLevel(String workerLevel) {
        this.workerLevel = workerLevel;
    }

    //工作时间
    public String getWorkTime() {
        return workTime;
    }
    public void setWorkTime(String workTime) {
        this.workTime = workTime;
    }

    //是否含餐
    public String getWithMeal() {
        return withMeal;
    }
    public void setWithMeal(String withMeal) {
        this.withMeal = withMeal;
    }

    //是否住宿
    public String getWithAccommodation() {
        return withAccommodation;
    }
    public void setWithAccommodation(String withAccommodation) {
        this.withAccommodation = withAccommodation;
    }

    //工作地点
    public String getWorkLocation() {
        return workLocation;
    }
    public void setWorkLocation(String workLocation) {
        this.workLocation = workLocation;
    }

    //服务价格
    public int getServicePrice() {
        return servicePrice;
    }
    public void setServicePrice(Integer servicePrice) {
        this.servicePrice = servicePrice;
    }

    //自我介绍
    public String getSelfIntroduction() {
        return selfIntroduction;
    }
    public void setSelfIntroduction(String selfIntroduction) {
        this.selfIntroduction = selfIntroduction;
    }

    //距离（临时，测试用）
    public int getWorkerDistance() {
        return workerDistance;
    }
    public void setWorkerDistance(int workerDistance) {
        this.workerDistance = workerDistance;
    }

    private static QueryWorkerPostListener queryWorkerPostListener;
    public void setQueryWorkerPostListener(QueryWorkerPostListener queryWorkerPostListener) {
        WorkerPost.queryWorkerPostListener = queryWorkerPostListener;
    }

    /**
     * 根据ObjectId查询WorkerPost信息
     * @param objectId
     */
    public static void queryWorkerPostInfo(String objectId) {

        Log.d(TAG, "queryWorkerPostInfo: 开始查询WorkerPost信息");

        BmobQuery<WorkerPost> query = new BmobQuery<WorkerPost>();
        query.include("worker");
        query.getObject(objectId, new QueryListener<WorkerPost>() {
            @Override
            public void done(WorkerPost workerPost, BmobException e) {
                if (e == null) {
                    Log.d(TAG, "done: 查询成功");
                    queryWorkerPostListener.onSuccess(workerPost);
                } else {
                    Log.d(TAG, "done: 查询WorkerPost信息失败" + e.getErrorCode() + e.getMessage());
                    queryWorkerPostListener.onFailed();
                }
            }
        });
    }

    public interface QueryWorkerPostListener {
        void onSuccess(WorkerPost workerPost);
        void onFailed();
    }

}
