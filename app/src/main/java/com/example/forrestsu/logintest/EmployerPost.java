package com.example.forrestsu.logintest;

import android.util.Log;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

public class EmployerPost extends BmobObject {

    private static final String TAG = "EmployerPost";

    private MyUser employer;
    private String province;   //省
    private String city;  //市
    private String area;  //县/区
    private String specificAddress;  //详细地址
    private String workDateFrom;  //起始日期
    private String workDateTo;  //结束日期
    private String workTimeFrom;  //起始时间
    private String workTimeTo;  //结束时间
    private String workLocation;  //工作地点（医院/居家）
    private String employerSituation;  //病人情况
    private Integer employerPrice;  //雇主开的价格
    private String employerName;  //姓名
    private String note;  //备注

    private int employerDistance;  //距离（临时数据，测试用）

    //关联的用户
    public MyUser getEmployer(){
        return employer;
    }
    public void setEmployer(MyUser employer) {
        this.employer = employer;
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

    //起始日期
    public String getWorkDateFrom() {
        return workDateFrom;
    }
    public void setWorkDateFrom(String workDateFrom) {
        this.workDateFrom = workDateFrom;
    }

    //结束日期
    public void setWorkDateTo(String workDateTo) {
        this.workDateTo = workDateTo;
    }
    public String getWorkDateTo() {
        return workDateTo;
    }

    //起始时间
    public String getWorkTimeFrom() {
        return workTimeFrom;
    }
    public void setWorkTimeFrom(String workTimeFrom) {
        this.workTimeFrom = workTimeFrom;
    }

    //结束时间
    public void setWorkTimeTo(String workTimeTo) {
        this.workTimeTo = workTimeTo;
    }
    public String getWorkTimeTo() {
        return workTimeTo;
    }

    //工作地点
    public String getWorkLocation() {
        return workLocation;
    }
    public void setWorkLocation(String workLocation) {
        this.workLocation = workLocation;
    }

    //病人情况
    public String getEmployerSituation() {
        return employerSituation;
    }
    public void setEmployerSituation(String employerSituation) {
        this.employerSituation = employerSituation;
    }

    //雇主开的价格
    public Integer getEmployerPrice() {
        return employerPrice;
    }
    public void setEmployerPrice(Integer servicePrice) {
        this.employerPrice = servicePrice;
    }

    //雇主姓名
    public String getEmployerName(){
        return employerName;
    }
    public void setEmployerName(String workerName){
        this.employerName = workerName;
    }

    //备注
    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }


    //距离（临时数据，测试用）
    public int getEmployerDistance() {
        return employerDistance;
    }
    public void setEmployerDistance(int workerDistance) {
        this.employerDistance = workerDistance;
    }


    private static QueryEmployerPostListener queryEmployerPostListener;
    public void setQueryEmployerPostListener(QueryEmployerPostListener queryEmployerPostListener) {
        EmployerPost.queryEmployerPostListener = queryEmployerPostListener;
    }
    /**
     * 根据ObjectId查询EmployerPost信息
     * @param objectId
     */
    public static void queryEmployerPostInfo(String objectId) {

        Log.d(TAG, "queryEmployerPostInfo: 开始查询EmployerPost信息");

        BmobQuery<EmployerPost> query = new BmobQuery<EmployerPost>();
        query.include("employer");
        query.getObject(objectId, new QueryListener<EmployerPost>() {
            @Override
            public void done(EmployerPost employerPost, BmobException e) {
                if (e == null) {
                    Log.d(TAG, "done: 查询成功");
                    queryEmployerPostListener.onSuccess(employerPost);
                } else {
                    Log.d(TAG, "done: 查询WorkerPost信息失败" + e.getErrorCode() + e.getMessage());
                    queryEmployerPostListener.onFailed();
                }
            }
        });
    }

    public interface QueryEmployerPostListener {
        void onSuccess(EmployerPost employerPost);
        void onFailed();
    }

}
