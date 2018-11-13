package com.example.forrestsu.logintest;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class AddressManager {
    private static final String TAG = "AddressManager";

    //表名
    private static final String TABLE_NAME = "ssq";
    //列名province
    private static final String PROVINCE = "province";
    //列名city
    private static final String CITY = "city";
    //列名area
    private static final String AREA = "area";

    //数据库
    private SQLiteDatabase database;

    /**
     *
     * @param databasePath 数据库路径
     */
    public AddressManager(String databasePath) {
        database = SQLiteDatabase.openOrCreateDatabase(databasePath, null);
    }

    /*
    查询所有省
     */
    public List<String> getProvince() {
        List<String> provinceList = new ArrayList<String>();
        String sql = "SELECT DISTINCT " + PROVINCE + " FROM " + TABLE_NAME;
        Cursor cursor = database.rawQuery(sql, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String province = cursor.getString(cursor.getColumnIndex(PROVINCE));
                provinceList.add(province);
                Log.i(TAG, "getProvince: " + province);
            }
            cursor.close();
        }
        return provinceList;
    }

    /*
    根据省查询相应的市
     */
    public List<String> getCity(String province) {
        List<String> cityList = new ArrayList<String>();
        String sql = "SELECT DISTINCT " + CITY + " FROM " + TABLE_NAME + " WHERE " + PROVINCE + " = ?";
        Cursor cursor = database.rawQuery(sql, new String[]{province});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String city = cursor.getString(cursor.getColumnIndex(CITY));
                cityList.add(city);
                Log.i(TAG, "getCity: " + city);
            }
            cursor.close();
        }
       return cityList;
    }

    /*
    根据市查询相应的区/县
     */
    public List<String> getArea(String city) {
        List<String> areaList = new ArrayList<String>();
        String sql = "SELECT DISTINCT " + AREA + " FROM " + TABLE_NAME + " WHERE " + CITY + " = ?";
        Cursor cursor = database.rawQuery(sql, new String[]{city});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String area = cursor.getString(cursor.getColumnIndex(AREA));
                areaList.add(area);
                Log.i(TAG, "getArea: " + city);
            }
            cursor.close();
        }
        return areaList;
    }
}
