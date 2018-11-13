package com.example.forrestsu.logintest.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.forrestsu.logintest.MyUser;
import com.example.forrestsu.logintest.adapter.MyFragmentPagerAdapter;
import com.example.forrestsu.logintest.fragment.HomeFragment;
import com.example.forrestsu.logintest.fragment.MeFragment;
import com.example.forrestsu.logintest.fragment.ExploreFragment;
import com.example.forrestsu.logintest.R;
import com.example.forrestsu.logintest.utils.BmobDownload;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private static final String DATABASE_NAME = "city.db";

    private ViewPager viewPager;
    private HomeFragment homeFragment;
    private ExploreFragment exploreFragment;
    private MeFragment meFragment;
    private View homeView, exploreView, meView;
    private ImageView homeIV, exploreIV, meIV;
    private TextView homeTV, exploreTV, meTV;
    //存放Fragment
    private List<Fragment> fragmentList;
    //存放ImageView（底栏图标）
    private List<ImageView> imageViewList;
    //存放TextView（底栏文本）
    private List<TextView> textViewList;
    private int currentIndex = 0;

    private static int[] imageOutlined
            = {R.drawable.home_outlined, R.drawable.explore_outlined, R.drawable.me_outlined};
    private static int[] imageFilled
            = {R.drawable.home_filled, R.drawable.explore_filled, R.drawable.me_filled};

    private BmobDownload bmobDownload;

    //用户是否已验证
    //static Boolean verifyIdentity;
    //private MyUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        //查询用户信息,加载数据库
        new Thread() {
            public void run() {
                MyUser.queryUserInfo();
                bmobDownload.getDatabase(MainActivity.this, DATABASE_NAME);
            }
        }.start();
    }

    /*
    初始化界面
     */
    private void initView() {

        //currentUser = BmobUser.getCurrentUser(MyUser.class);
        bmobDownload = new BmobDownload();

        viewPager = (ViewPager) findViewById(R.id.show_fragment_main);

        homeFragment = new HomeFragment();
        exploreFragment = new ExploreFragment();
        meFragment = new MeFragment();

        homeView = (View) findViewById(R.id.view_home);
        exploreView = (View) findViewById(R.id.view_explore);
        meView = (View) findViewById(R.id.view_me);
        //设置监听
        homeView.setOnClickListener(new MyOnClickListener(0));
        exploreView.setOnClickListener(new MyOnClickListener(1));
        meView.setOnClickListener(new MyOnClickListener(2));

        homeIV = (ImageView) findViewById(R.id.iv_home);
        exploreIV = (ImageView) findViewById(R.id.iv_explore);
        meIV = (ImageView) findViewById(R.id.iv_me);

        homeTV = (TextView) findViewById(R.id.tv_home);
        exploreTV = (TextView) findViewById(R.id.tv_explore);
        meTV = (TextView) findViewById(R.id.tv_me);

        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(homeFragment);
        fragmentList.add(exploreFragment);
        fragmentList.add(meFragment);

        imageViewList = new ArrayList<ImageView>();
        imageViewList.add(homeIV);
        imageViewList.add(exploreIV);
        imageViewList.add(meIV);

        textViewList = new ArrayList<TextView>();
        textViewList.add(homeTV);
        textViewList.add(exploreTV);
        textViewList.add(meTV);

        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
        //缓存1个页面,如果不设置，默认缓存1个页面
        viewPager.setOffscreenPageLimit(1);
        viewPager.setCurrentItem(0);
        resetBottomTab();
        setBottomTab(currentIndex);

        //设置监听
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener()) ;
    }

    /**
     * 设置底栏点击事件监听
     */
    public class MyOnClickListener implements View.OnClickListener {

        private int index = 0;

        public MyOnClickListener(int index) {
            this.index = index;
        }

        @Override
        public void onClick(View view) {
            viewPager.setCurrentItem(index);
        }
    }

    /**
     * 设置页面滑动事件监听
     */
    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
         public void onPageSelected(int position) {
             switch(position) {

                 //滑动到页面0（主页home）
                 case 0:
                     if (currentIndex == 1) { //从页面1到页面0
                         resetBottomTab();
                         setBottomTab(position);
                     } else if(currentIndex == 2) {//从页面2到页面0
                         resetBottomTab();
                         setBottomTab(position);
                     }
                     break;

                 //滑动到页面1（浏览页explore）
                 case 1:
                     if (currentIndex == 0) { //从页面0到页面1
                         resetBottomTab();
                         setBottomTab(position);
                     } else if(currentIndex == 2) {//从页面2到页面1
                         resetBottomTab();
                         setBottomTab(position);
                     }
                     break;
                 //滑动到页面2（我的页me）
                 case 2:
                     if (currentIndex == 0) { //从页面0到页面2
                         resetBottomTab();
                         setBottomTab(position);
                     } else if(currentIndex == 1) {//从页面1到页面2
                         resetBottomTab();
                         setBottomTab(position);
                     }
                     break;
             }
             currentIndex = position;
         }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }

    }

    /*
    重置底栏图标和文本颜色
     */
   public void resetBottomTab() {
       //重置文本颜色
       for (TextView viewer :
               textViewList) {
           viewer.setTextColor(getResources().getColor(R.color.grey_bababa));

       }
       //重置图标颜色
       for (int index = 0; index < 3; index++) {
           imageViewList.get(index).setImageResource(imageOutlined[index]);
       }
   }

    /*
     设置当前底栏图标和文本颜色
      */
    public void setBottomTab(int position) {
        textViewList.get(position).setTextColor(getResources().getColor(R.color.blue_51A8B4));
        imageViewList.get(position).setImageResource(imageFilled[position]);
    }



 }
