package com.example.forrestsu.logintest.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.forrestsu.logintest.EmployerPost;
import com.example.forrestsu.logintest.R;
import com.example.forrestsu.logintest.WorkerPost;
import com.example.forrestsu.logintest.fragment.EmployerInMapFragment;
import com.example.forrestsu.logintest.fragment.WorkerInMapFragment;
import com.example.forrestsu.logintest.utils.BaiduMapUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MapModeActivity extends BaseActivity implements WorkerInMapFragment.CloseFragment,
        EmployerInMapFragment.CloseFragment {

    private static final String TAG = "MapModeActivity";

    private static final int COLOR_WHITE = 0xffffffff;
    private static final int COLOR_BLUE = 0xff5fc0cd;

    private static final int FIND_WORKER = 0;
    private static final int FIND_EMPLOYER = 1;
    private int currentMode;

    private String currentCity;
    private BDLocation currentLocation;

    private final BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.baseline_person_pin_circle);

    private FloatingActionButton myLocationFAB;
    private Button findWorkerBT, findEmployerBT;
    private FrameLayout showFragmentFL;
    private View glassView;
    private Fragment currentFragment, workerInfoFragment, employerInfoFragment;

    private MapView mapView;

    private LocationClient mLocationClient;
    private BaiduMap baiduMap;

    private ProgressDialog progressDialog;
    //防止多次调用animateMapStatus（），调用一次定位后将其设为false
    private Boolean isFirstLocate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_mode);

        Intent intent = getIntent();
        currentMode = intent.getIntExtra("currentMode", 0);

        initView();

        requestPermission();
    }

    //初始化
    public void initView() {
        progressDialog = new ProgressDialog(MapModeActivity.this);
        progressDialog.setTitle("");
        progressDialog.setMessage("...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        showFragmentFL = (FrameLayout) findViewById(R.id.fl_show_fragment);
        showFragmentFL.setVisibility(View.GONE);
        glassView = (View) findViewById(R.id.view_glass);
        glassView.setVisibility(GONE);
        glassView.setOnClickListener(new MyOnClickListener());
        currentFragment = new Fragment();

        myLocationFAB = (FloatingActionButton) findViewById(R.id.fab_my_location);
        myLocationFAB.setOnClickListener(new MyOnClickListener());
        findWorkerBT = (Button) findViewById(R.id.bt_find_worker);
        findWorkerBT.setOnClickListener(new MyOnClickListener());
        findEmployerBT = (Button) findViewById(R.id.bt_find_employer);
        findEmployerBT.setOnClickListener(new MyOnClickListener());

        if (currentMode == FIND_EMPLOYER) {
            findWorkerBT.setBackgroundColor(COLOR_BLUE);
            findWorkerBT.setTextColor(COLOR_WHITE);
            findEmployerBT.setBackgroundColor(COLOR_WHITE);
            findEmployerBT.setTextColor(COLOR_BLUE);
        }

        mapView = (MapView) findViewById(R.id.map_view);

        baiduMap = mapView.getMap();
        //开启定位图层
        baiduMap.setMyLocationEnabled(true);
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Bundle bundle = marker.getExtraInfo();
                //String objectId = bundle.getString("objectId", "");
                showFragmentFL.setVisibility(VISIBLE);
                glassView.setVisibility(VISIBLE);
                switch (currentMode) {
                    case FIND_WORKER:
                        if (workerInfoFragment == null) {
                            workerInfoFragment = new WorkerInMapFragment();
                        }
                        //将objectId传给workerInfoFragment
                        workerInfoFragment.setArguments(bundle);
                        addOrShowFragment(getSupportFragmentManager().beginTransaction(), workerInfoFragment);
                        break;
                    case FIND_EMPLOYER:
                        if (employerInfoFragment == null) {
                            employerInfoFragment = new EmployerInMapFragment();
                        }
                        //将objectId传给employerInfoFragment
                        employerInfoFragment.setArguments(bundle);
                        addOrShowFragment(getSupportFragmentManager().beginTransaction(), employerInfoFragment);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    //点击监听
    public class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_find_worker:
                    Log.i(TAG, "onClick: 点击了找护工");
                    if (currentMode == FIND_WORKER) {
                        return;
                    } else {
                        progressDialog.show();
                        if (showFragmentFL.getVisibility() == VISIBLE) {
                            showFragmentFL.setVisibility(GONE);
                            glassView.setVisibility(GONE);
                            getSupportFragmentManager().beginTransaction().hide(employerInfoFragment).commit();
                        }
                        currentMode = FIND_WORKER;
                        findWorkerBT.setTextColor(COLOR_BLUE);
                        findWorkerBT.setBackgroundColor(COLOR_WHITE);
                        findEmployerBT.setTextColor(COLOR_WHITE);
                        findEmployerBT.setBackgroundColor(COLOR_BLUE);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                queryUser(currentCity);
                            }
                        }).start();
                    }
                    break;
                case R.id.bt_find_employer:
                    Log.i(TAG, "onClick: 点击了找雇主");
                    if (currentMode == FIND_EMPLOYER) {
                        return;
                    } else {
                        progressDialog.show();
                        if (showFragmentFL.getVisibility() == VISIBLE) {
                            showFragmentFL.setVisibility(GONE);
                            glassView.setVisibility(GONE);
                            getSupportFragmentManager().beginTransaction().hide(workerInfoFragment).commit();
                        }
                        currentMode = FIND_EMPLOYER;
                        findEmployerBT.setTextColor(COLOR_BLUE);
                        findEmployerBT.setBackgroundColor(COLOR_WHITE);
                        findWorkerBT.setTextColor(COLOR_WHITE);
                        findWorkerBT.setBackgroundColor(COLOR_BLUE);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                queryUser(currentCity);
                            }
                        }).start();
                    }
                    break;
                case R.id.view_glass:
                    showFragmentFL.setVisibility(GONE);
                    glassView.setVisibility(GONE);
                    if (currentMode == FIND_WORKER) {
                        getSupportFragmentManager().beginTransaction().hide(workerInfoFragment).commit();
                    } else if (currentMode == FIND_EMPLOYER) {
                        getSupportFragmentManager().beginTransaction().hide(employerInfoFragment).commit();
                    }
                    break;
                case R.id.fab_my_location:
                    //移动到我的位置
                    isFirstLocate = true;
                    navigateTo(currentLocation);
                    break;
                default:
                    break;
            }
        }
    }

    private void requestPermission() {
        List<String> permissionList = new ArrayList<String>();
        //权限：定位
        if (ContextCompat.checkSelfPermission(MapModeActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        //权限：读取手机状态
        if (ContextCompat.checkSelfPermission(MapModeActivity.this,
                Manifest.permission.READ_PHONE_STATE) !=PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        //权限：读写SD卡
        if (ContextCompat.checkSelfPermission(MapModeActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(
                    new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MapModeActivity.this, permissions, 1);
        } else {
            requestLocation();
        }
    }

    private void requestLocation() {
        //配置定位SDK参数
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(new MyLocationListener());
        mLocationClient.start();
    }

    //更新当前位置
    private void initLocation() {
        LocationClientOption option  = new LocationClientOption();
        //设置更新时间间隔，5000毫秒
        option.setScanSpan(5000);
        mLocationClient.setLocOption(option);
    }

    private void navigateTo(BDLocation location) {
        if (isFirstLocate) {
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate update  = MapStatusUpdateFactory.newLatLng(ll);
            baiduMap.animateMapStatus(update);
            //缩放级别设置为16
            update = MapStatusUpdateFactory.zoomTo(16f);
            baiduMap.animateMapStatus(update);
            //防止多次调用animateMapStatus（）
            isFirstLocate = false;
        }
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.accuracy(location.getRadius());
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData myLocationData = locationBuilder.build();
        baiduMap.setMyLocationData(myLocationData);
    }

    private class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            currentLocation = location;
            //
            if (location.getLocType() == BDLocation.TypeGpsLocation
                    || location.getLocType() == BDLocation.TypeNetWorkLocation) {
                navigateTo(location);
                Log.i(TAG, "onReceiveLocation: 坐标：" + "\n经度：" + location.getLatitude() + "\n纬度：" + location.getLongitude());
                String city = location.getCity();
                Log.i(TAG, "onReceiveLocation: 当前所在城市：" + city);
                //根据city查询护工或者雇主
                //根据坐标获取的当前城市名中带有一个市，而数据库中的城市名没有“市”字，需要去掉
                currentCity = city.substring(0, city.length() - 1);
                queryUser(currentCity);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(MapModeActivity.this,
                                    "如果需要使用地图功能，必须授予权限", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(MapModeActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //管理地图的生命周期：Activity执行onDestroy时执行MapView.onDestroy()
        mapView.onDestroy();
        //活动销毁时停止定位
        mLocationClient.stop();
        //销毁活动时关闭此功能
        baiduMap.setMyLocationEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //管理地图的生命周期：Activity执行onResume时执行MapView.onResume()
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //管理地图的生命周期：Activity执行onPause时执行MapView.onPause()
        mapView.onPause();
    }

    //查询数据
    private void queryUser(String city) {
        switch (currentMode) {
            case FIND_WORKER:
                BmobQuery<WorkerPost> queryWorker = new BmobQuery<WorkerPost>();
                queryWorker.addWhereEqualTo("city", city);
                queryWorker.addQueryKeys("objectId,city,area,specificAddress");
                queryWorker.findObjects(new FindListener<WorkerPost>() {
                    @Override
                    public void done(List<WorkerPost> list, BmobException e) {
                        if (e == null) {
                            Log.i(TAG, "done: 查询 成功");
                            showWorkerMarker(list);
                        } else {
                            progressDialog.dismiss();
                            Log.i(TAG, "done: 查询失败  " + e.getErrorCode() + ":" + e.toString());
                        }
                    }
                });
                break;
            case FIND_EMPLOYER:
                BmobQuery<EmployerPost> queryEmployer = new BmobQuery<EmployerPost>();
                queryEmployer.addWhereEqualTo("city", city);
                queryEmployer.addQueryKeys("objectId,city,area,specificAddress");
                queryEmployer.findObjects(new FindListener<EmployerPost>() {
                    @Override
                    public void done(List<EmployerPost> list, BmobException e) {
                        if (e == null) {
                            Log.i(TAG, "done: 查询 成功");
                            showEmployerMarker(list);
                        } else {
                            progressDialog.dismiss();
                            Log.i(TAG, "done: 查询失败  " + e.getErrorCode() + ":" + e.toString());
                        }
                    }
                });
                break;
            default:
                break;
        }

    }

    //显示护工Marker
    private void showWorkerMarker(List<WorkerPost> list) {
        //清空地图
        baiduMap.clear();
        for (WorkerPost workerPost : list) {
            //获取地址
            String city = workerPost.getCity();
            String address = workerPost.getArea() + workerPost.getSpecificAddress();
            String objectId = workerPost.getObjectId();
            Log.i(TAG, "showWorkerMarker: \n城市："+ city + "\n详细：" + address);

            //将地址转换为地理坐标
            BaiduMapUtils baiduMapUtils = new BaiduMapUtils();
            baiduMapUtils.addressToCoordinate(city, address, objectId);
            baiduMapUtils.setTransListener(new BaiduMapUtils.TransListener() {
                @Override
                public void onSuccess(Double latitude, Double longitude, String extraInfo) {
                    OverlayOptions options = new MarkerOptions()
                            .position(new LatLng(latitude, longitude))  //设置坐标
                            .icon(bitmap)  //设置图标
                            .zIndex(9);  //设置层级
                    baiduMap.addOverlay(options);
                    Marker marker = (Marker) baiduMap.addOverlay(options);
                    Bundle bundle = new Bundle();
                    bundle.putString("objectId", extraInfo);
                    marker.setExtraInfo(bundle);
                    progressDialog.dismiss();
                }
                @Override
                public void onFailed() {
                    progressDialog.dismiss();
                }
            });
        }
    }

    //显示雇主marker
    private void showEmployerMarker(List<EmployerPost> list) {
        //清空地图
        baiduMap.clear();
        for (EmployerPost employerPost : list) {
            //获取地址
            String city = employerPost.getCity();
            String address = employerPost.getArea() + employerPost.getSpecificAddress();
            String objectId = employerPost.getObjectId();
            Log.i(TAG, "showWorkerMarker: \n城市："+ city + "\n详细：" + address);

            //将地址转换为地理坐标
            BaiduMapUtils baiduMapUtils = new BaiduMapUtils();
            baiduMapUtils.addressToCoordinate(city, address, objectId);
            baiduMapUtils.setTransListener(new BaiduMapUtils.TransListener() {
                @Override
                public void onSuccess(Double latitude, Double longitude, String extraInfo) {
                    OverlayOptions options = new MarkerOptions()
                            .position(new LatLng(latitude, longitude))  //设置坐标
                            .icon(bitmap)  //设置图标
                            .zIndex(9);  //设置层级
                    baiduMap.addOverlay(options);
                    Marker marker = (Marker) baiduMap.addOverlay(options);
                    Bundle bundle = new Bundle();
                    bundle.putString("objectId", extraInfo);
                    marker.setExtraInfo(bundle);
                    progressDialog.dismiss();
                }
                @Override
                public void onFailed() {
                    progressDialog.dismiss();
                }
            });
        }
    }

    /*
     添加或者显示Fragment
      */
    private void addOrShowFragment(FragmentTransaction transaction, Fragment fragment) {
        //if (currentFragment == fragment)
            //return;
        if (!fragment.isAdded()) {
            //transaction.hide(currentFragment).add(R.id.fl_show_fragment, fragment).commit();
            transaction.add(R.id.fl_show_fragment, fragment).commit();
        } else {
            //transaction.hide(currentFragment).show(fragment).commit();
            transaction.show(fragment).commit();
        }
        currentFragment = fragment;
    }

    /*
   实现WorkerInMapFragment的CloseFragment接口的回调方法
    */
    @Override
    public void closeWorkerInfoFragment() {
        showFragmentFL.setVisibility(GONE);
        glassView.setVisibility(GONE);
        //hide（workerInfoFragment），以便再次进入workerInfoFragment时刷新数据
        getSupportFragmentManager().beginTransaction().hide(workerInfoFragment).commit();
    }

    /*
   实现EmployerInMapFragment的CloseFragment接口的回调方法
    */
    @Override
    public void closeEmployerInfoFragment() {
        showFragmentFL.setVisibility(GONE);
        glassView.setVisibility(GONE);
        //hide（employerInfoFragment），以便再次进入employerInfoFragment时刷新数据
        getSupportFragmentManager().beginTransaction().hide(employerInfoFragment).commit();
    }

 }
