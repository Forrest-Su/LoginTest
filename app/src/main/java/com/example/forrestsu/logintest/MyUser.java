package com.example.forrestsu.logintest;

import android.util.Log;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MyUser extends BmobUser {
    private static final String TAG = "MyUser";

    //是否验证过身份证
    private Boolean verifyIdentity;
    //头像
    private BmobFile head;
    //头像Url
    private String headUrl;
    //昵称
    private String nickname;
    //出生日期
    private String birthday;
    //性别
    private String sex;
    //省
    private String province;
    //市
    private String city;
    //区/县
    private String area;
    //详细地址
    private String specificAddress;

    //设置身份验证状态
    public void setVerifyIdentity(Boolean verifyIdentity) {
        this.verifyIdentity = verifyIdentity;
    }

    //获取身份验证状态
    public Boolean getVerifyIdentity() {
        return verifyIdentity;
    }

    //设置头像
    public void setHead(BmobFile head) {
        this.head = head;
    }

    //获取头像
    public BmobFile getHead() {
        return head;
    }

    //设置url
    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    //获取url
    public String getHeadUrl() {
        return headUrl;
    }

    //设置昵称
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    //获取昵称
    public String getNickname() {
        return nickname;
    }

    //设置出生日期
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    //获取出生日期
    public String getBirthday() {
        return birthday;
    }

    //设置性别
    public void setSex(String sex) {
        this.sex = sex;
    }

    //获取性别
    public String getSex() {
        return sex;
    }

    //设置省
    public void setProvince(String province) {
        this.province = province;
    }

    //获取省
    public String getProvince () {
        return province;
    }

    //设置市
    public void setCity(String city) {
       this.city = city;
    }

    //获取市
    public String getCity() {
        return city;
    }

    //设置县/区
    public void setArea(String area) {
        this.area = area;
    }

    //获取县/区
    public String getArea() {
        return area;
    }

    //设置详细地址
    public void setSpecificAddress(String specificAddress) {
        this.specificAddress = specificAddress;
    }

    //获取详细地址
    public String getSpecificAddress() {
        return specificAddress;
    }

    /**
     * 查询用户信息
     */
    public static void queryUserInfo() {
        final MyUser currentUser = BmobUser.getCurrentUser(MyUser.class);

        Log.d(TAG, "开始查询用户信息");
        BmobQuery<MyUser> query = new BmobQuery<MyUser>();
        query.addWhereEqualTo("objectId", currentUser.getObjectId());
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                if (e == null) {
                    MyUser user = list.get(0);
                    currentUser.setVerifyIdentity(user.getVerifyIdentity());
                    Log.d(TAG, "done: 验证状态：" + user.getVerifyIdentity());
                    Log.d(TAG, "done: 验证状态：" + currentUser.getVerifyIdentity());
                    currentUser.setNickname(user.getNickname());
                    currentUser.setHeadUrl(user.getHeadUrl());
                    currentUser.setBirthday(user.getBirthday());
                    currentUser.setSex(user.getSex());
                    currentUser.setProvince(user.getProvince());
                    currentUser.setCity(user.getCity());
                    currentUser.setArea(user.getArea());
                    currentUser.setSpecificAddress(user.getSpecificAddress());
                    Log.d(TAG, "done: 详细地址：" + user.getSpecificAddress());
                    currentUser.setHeadUrl(user.getHeadUrl());
                    Log.d(TAG, "查询用户信息成功");
                } else {
                    Log.d(TAG, "查询用户信息失败" + e.getMessage());
                }
            }
        });
    }

}
