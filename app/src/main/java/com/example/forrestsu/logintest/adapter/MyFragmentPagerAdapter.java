package com.example.forrestsu.logintest.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    public List<Fragment> fragmentList;

    public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position){
        if (fragmentList == null) {
            return null;
        } else {
            return fragmentList.get(position);
        }
    }

    @Override
    public int getCount() {
        if (fragmentList == null) {
            return 0;
        } else {
            return fragmentList.size();
        }
    }
}

