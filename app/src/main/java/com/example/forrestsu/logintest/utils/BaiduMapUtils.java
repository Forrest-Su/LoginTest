package com.example.forrestsu.logintest.utils;

import android.util.Log;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

public class BaiduMapUtils {
   private static final String TAG = "BaiduMapUtils";

   private TransListener transListener;
   public void setTransListener(TransListener transListener) {
       this.transListener = transListener;
   }

    /**
     * 地理编码
     * @param city
     * @param address
     */
   public void addressToCoordinate(String city, String address, final String extraInfo) {

      final GeoCoder mSearch = GeoCoder.newInstance();
      //设置监听
      mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {

         @Override
         public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
            if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR){
                transListener.onFailed();
               Log.i(TAG, "onGetGeoCodeResult: 未能根据地址查询到坐标结果");
                //释放检索实例
                mSearch.destroy();
            } else {
                LatLng ll = geoCodeResult.getLocation();
                transListener.onSuccess(ll.latitude, ll.longitude, extraInfo);
                Log.i(TAG, "onGetGeoCodeResult: \n经度：" + ll.latitude + "\n纬度：" + ll.longitude);
                //释放检索实例
                mSearch.destroy();
            }
         }

         @Override
         public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
             //释放检索实例
            mSearch.destroy();
         }
      });

      //开始查询
       mSearch.geocode(new GeoCodeOption()
               .city(city)
               .address(address));
   }

   public interface TransListener {
       void onSuccess(Double latitude, Double longitude, String extraInfo);
       void onFailed();
   }


}
