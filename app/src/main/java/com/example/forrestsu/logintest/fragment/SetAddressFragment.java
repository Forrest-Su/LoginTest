package com.example.forrestsu.logintest.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.forrestsu.logintest.AddressManager;
import com.example.forrestsu.logintest.MyApplication;
import com.example.forrestsu.logintest.R;

import java.util.List;

public class SetAddressFragment extends Fragment {

    private static final String TAG = "SetAddressFragment";

    private static final String DATABASE_NAME = "city.db";
    private String databasePath = MyApplication.getContext().getExternalCacheDir() + "/" + DATABASE_NAME;

    private TextView confirmTV, cancelTV;
    private static NumberPicker provinceNP, cityNP, areaNP;
    private SendMessageAddress sendMessageAddress;
    private static AddressManager manager;
    private static String[] provinces;
    private static String[] cities;
    private static String[] areas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle saveInstanceState) {
        View square = inflater.inflate(R.layout.fragment_set_address, container, false);

        initView(square);
        showProvince();

        return square;
    }

    //初始化
    public void initView(View square) {
        confirmTV = (TextView) square.findViewById(R.id.tv_confirm);
        confirmTV.setOnClickListener(new MyOnClickListener());
        cancelTV = (TextView) square.findViewById(R.id.tv_cancel);
        cancelTV.setOnClickListener(new MyOnClickListener());
        provinceNP = (NumberPicker) square.findViewById(R.id.np_hour);
        provinceNP.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        cityNP = (NumberPicker) square.findViewById(R.id.np_minute);
        cityNP.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        areaNP = (NumberPicker) square.findViewById(R.id.np_area);
        areaNP.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        manager = new AddressManager(databasePath);
    }

    //Click
    public class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.tv_confirm:
                    final String province = provinces[provinceNP.getValue()];
                    final String city = cities[cityNP.getValue()];
                    final String area = areas[areaNP.getValue()];
                    sendMessageAddress.getMessageAddress(province, city, area);
                    break;
                case R.id.tv_cancel:
                    sendMessageAddress.getMessageAddress();
                    break;
                default:
                    break;
            }
        }
    }

   /*
    加载省
   */
    private static void showProvince() {
        List<String> provinceList = manager.getProvince();
        provinces = provinceList.toArray(new String[provinceList.size()]);
        provinceNP.setDisplayedValues(provinces);
        provinceNP.setMinValue(0);
        provinceNP.setMaxValue(provinces.length - 1);
        provinceNP.setValue(0);
        showCityByProvince(provinces[provinceNP.getValue()]);

        provinceNP.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                //
                showCityByProvince(provinces[newVal]);
            }
        });
    }

    /*
    根据省加载市
     */
    private static void showCityByProvince(String province) {
        List<String> cityList = manager.getCity(province);
        cities = cityList.toArray(new String[cityList.size()]);
        cityNP.setDisplayedValues(null);
        cityNP.setMinValue(0);
        cityNP.setMaxValue(cities.length - 1);
        cityNP.setDisplayedValues(cities);
        showAreaByCity(cities[cityNP.getValue()]);
        cityNP.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                //
                showAreaByCity(cities[newVal]);
            }
        });
    }

    /*
    根据市加载区/县
     */
    private static void showAreaByCity(String city) {
        List<String> areaList = manager.getArea(city);
        areas = areaList.toArray(new String[areaList.size()]);
        areaNP.setDisplayedValues(null);
        areaNP.setMinValue(0);
        areaNP.setMaxValue(areas.length - 1);
        areaNP.setDisplayedValues(areas);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof SetAddressFragment.SendMessageAddress) {
            sendMessageAddress = (SetAddressFragment.SendMessageAddress) context;
        } else {
            throw new IllegalArgumentException("Activity must implements SendMessageSituation");
        }
    }

    /**
     * 回调接口，用于向activity传参
     */
    public interface SendMessageAddress {
        void getMessageAddress(String province, String city, String area);
        void getMessageAddress();
    }
}
