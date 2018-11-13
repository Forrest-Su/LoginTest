package com.example.forrestsu.logintest.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.forrestsu.logintest.R;
import com.example.forrestsu.logintest.activity.MapModeActivity;

public class ExploreFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ExploreFragment";

    private static final int FIND_WORKER = 0;
    private static final int FIND_EMPLOYER = 1;
    private int currentMode;
    private ImageView findWorkerIV, findEmployerIV, toMapIV;
    private TextView findWorkerTV, findEmployerTV;
    private Fragment findWorkerFragment, findEmployerFragment, currentFragment;
    private View square;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        square = inflater.inflate(R.layout.fragment_explore, container, false);

        initView();
        initTopTab();

        return square;
    }

    /*
   初始化界面
    */
    public void initView() {
        currentMode = FIND_WORKER;
        findWorkerIV = (ImageView) square.findViewById(R.id.iv_findWorker);
        findEmployerIV = (ImageView) square.findViewById(R.id.iv_findEmployer);
        findWorkerTV = (TextView) square.findViewById(R.id.tv_findWorker);
        findEmployerTV = (TextView) square.findViewById(R.id.tv_findEmployer);
        toMapIV = (ImageView) square.findViewById(R.id.iv_map_ico);
        findWorkerIV.setOnClickListener(this);
        findEmployerIV.setOnClickListener(this);
        toMapIV.setOnClickListener(this);
    }


    /*
     初始化顶部标签
     */
    private void initTopTab() {
        if (findWorkerFragment == null) {
            findWorkerFragment = new FindWorkerFragment();
        }
        if(!findWorkerFragment.isAdded()) {
            getChildFragmentManager().beginTransaction()
                    .add(R.id.showfragment_explore, findWorkerFragment).commit();
            currentFragment = findWorkerFragment;
            findWorkerIV.setImageResource(R.drawable.find_white);
            findEmployerIV.setImageResource(R.drawable.find_blue);
            findWorkerTV.setTextColor(getResources().getColor(R.color.blue_51A8B4));
            findEmployerTV.setTextColor(getResources().getColor(R.color.white_ffffff));
        }
    }


    @Override
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.iv_findWorker:
                currentMode = FIND_WORKER;
                clickFindWorker();
                break;
            case R.id.iv_findEmployer:
                currentMode = FIND_EMPLOYER;
                clickFindEmployer();
                break;
            case R.id.iv_map_ico:
                Intent intent = new Intent(getContext(), MapModeActivity.class);
                intent.putExtra("currentMode", currentMode);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    public void clickFindWorker() {
        if (findWorkerFragment == null) {
            findWorkerFragment = new FindWorkerFragment();
        }
        addOrShowFragment(getChildFragmentManager().beginTransaction(), findWorkerFragment);
        findWorkerIV.setImageResource(R.drawable.find_white);
        findEmployerIV.setImageResource(R.drawable.find_blue);
        findWorkerTV.setTextColor(getResources().getColor(R.color.blue_51A8B4));
        findEmployerTV.setTextColor(getResources().getColor(R.color.white_ffffff));
    }

    public void clickFindEmployer() {
        if (findEmployerFragment == null) {
            findEmployerFragment = new FindEmployerFragment();
        }
        addOrShowFragment(getChildFragmentManager().beginTransaction(), findEmployerFragment);
        findWorkerIV.setImageResource(R.drawable.find_blue);
        findEmployerIV.setImageResource(R.drawable.find_white);
        findWorkerTV.setTextColor(getResources().getColor(R.color.white_ffffff));
        findEmployerTV.setTextColor(getResources().getColor(R.color.blue_51A8B4));
    }



    /*
     添加或者显示Fragment
      */
    private void addOrShowFragment(FragmentTransaction transaction, Fragment fragment) {
        if (currentFragment == fragment)
            return;
        if (!fragment.isAdded()) {
            transaction.hide(currentFragment).add(R.id.showfragment_explore, fragment).addToBackStack(null).commit();
        } else {
            transaction.hide(currentFragment).show(fragment).addToBackStack(null).commit();
        }
        currentFragment = fragment;
    }

}
