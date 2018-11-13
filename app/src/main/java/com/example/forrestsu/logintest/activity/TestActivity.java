package com.example.forrestsu.logintest.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.forrestsu.logintest.R;


public class TestActivity extends BaseActivity {

    private static final String TAG = "TestActivity";

    private EditText cityET, addressET;

    private GeoCoder mSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mSearch = GeoCoder.newInstance();

        cityET = (EditText) findViewById(R.id.et_city);
        addressET = (EditText) findViewById(R.id.et_address);
        Button startBT = (Button) findViewById(R.id.bt_start);
        startBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearch.geocode(new GeoCodeOption()
                        .city(cityET.getText().toString())
                        .address(addressET.getText().toString()));
            }
        });
        mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR){
                    Toast.makeText(TestActivity.this, "没有结果", Toast.LENGTH_SHORT).show();
                } else {
                    LatLng ll = geoCodeResult.getLocation();
                    Toast.makeText(TestActivity.this, "纬度：" + ll.latitude
                            + "\n经度：" + ll.longitude, Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "onGetGeoCodeResult: " + "\n纬度：" + ll.latitude
                            + "\n经度：" + ll.longitude);
                }
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                mSearch.destroy();
            }
        });
    }
}
