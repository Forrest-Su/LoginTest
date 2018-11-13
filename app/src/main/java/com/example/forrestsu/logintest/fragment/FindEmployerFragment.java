package com.example.forrestsu.logintest.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.forrestsu.logintest.EmployerPost;
import com.example.forrestsu.logintest.R;
import com.example.forrestsu.logintest.SubMenu;
import com.example.forrestsu.logintest.WorkerPost;
import com.example.forrestsu.logintest.activity.EmployerPageActivity;
import com.example.forrestsu.logintest.activity.EmployerPostActivity;
import com.example.forrestsu.logintest.activity.WorkerPostActivity;
import com.example.forrestsu.logintest.adapter.EmployerPostAdapter;
import com.example.forrestsu.logintest.adapter.SubMenuAdapter;
import com.example.forrestsu.logintest.adapter.WorkerPostAdapter;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static android.view.View.GONE;

public class FindEmployerFragment extends Fragment implements EmployerPostAdapter.OnItemClickListener {

    private static final String TAG = "FindEmployerFragment";

    private Boolean isLoading = false;
    private String distanceCondition, locationCondition, situationCondition;
    private String currentDistanceCondition, currentLocationCondition, currentSituationCondition;


    private String distance[] = {"不限", "1公里", "3公里", "5公里", "10公里"};
    private String location[] = {"不限", "医院", "居家"};
    private String situation[] = {"不限", "能自理", "不能自理", "半失能", "手术后"};

    //顶部菜单部分
    private View distanceView, locationView, situationView;
    private TextView distanceTV, locationTV, situationTV;
    private ImageView distanceIV, locationIV, situationIV;

    //存放TextView
    private List<TextView> textViewList;
    //存放ImageView
    private List<ImageView> imageViewList;

    //container层（包含子菜单列表层、透明层、主要内容层）
    private FrameLayout containerFL;
    //子菜单列表层
    private RecyclerView subMenuRV;
    //透明层
    private View glassView;
    //主要内容层SwipeRefreshLayout(包含RecyuclerView)
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView employerRecyclerView;

    private List<EmployerPost> employerList;
    private List<SubMenu> subMenuList;

    //WorkerPost适配器
    private static EmployerPostAdapter employerPostAdapter;
    //SubMenu适配器
    private SubMenuAdapter subMenuAdapter;

    private String lastTime = null;
    private int currentSub = -1;

    private static final int CHANGE_EMPLOYERRV = 0;
    private static final int CHANGE_SUBMENU = 1;

    //查询条件
    private int minDistance, maxDistance;
    private String locationLimit;
    private String situationLimit;


    private MyHandler mHandler = new MyHandler(this);
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInsatnceState) {
        View square = inflater.inflate(R.layout.fragment_findemployer, container, false);

        initView(square);

        return square;
    }

    /*
    初始化界面
     */
    public void initView(View square) {


        currentDistanceCondition = "不限";
        currentLocationCondition = "不限";
        currentSituationCondition = "不限";

        //初始化查询条件

        minDistance = 0;
        maxDistance = 10000;
        locationLimit = "不限";
        situationLimit = "不限";

        //初始化顶部tab

        distanceView = square.findViewById(R.id.view_distance);
        locationView = square.findViewById(R.id.view_location);
        situationView = square.findViewById(R.id.view_situation);
        //为顶部tab设置监听
        distanceView.setOnClickListener(new MyOnClcikListener());
        locationView.setOnClickListener(new MyOnClcikListener());
        situationView.setOnClickListener(new MyOnClcikListener());

        distanceTV = square.findViewById(R.id.tv_distance);
        locationTV = square.findViewById(R.id.tv_location);
        situationTV = square.findViewById(R.id.tv_situation);

        textViewList = new ArrayList<TextView>();
        textViewList.add(distanceTV);
        textViewList.add(locationTV);
        textViewList.add(situationTV);

        distanceIV = square.findViewById(R.id.iv_distance);
        locationIV = square.findViewById(R.id.iv_location);
        situationIV = square.findViewById(R.id.iv_situation);

        imageViewList = new ArrayList<ImageView>();
        imageViewList.add(distanceIV);
        imageViewList.add(locationIV);
        imageViewList.add(situationIV);

        containerFL = (FrameLayout) square.findViewById(R.id.fl_container) ;

        subMenuRV = (RecyclerView) square.findViewById(R.id.rv_subMenu);
        //制定subMenuRV为线性布局
        subMenuRV.setLayoutManager(new LinearLayoutManager(getContext()));
        subMenuRV.setVisibility(GONE);
        subMenuList = new ArrayList<SubMenu>();
        subMenuAdapter = new SubMenuAdapter(subMenuList);
        //为subMenuRV设置子项点击事件
        subMenuAdapter.setOnItemClickListener(new MyOnItemClickListener() );
        subMenuRV.setAdapter(subMenuAdapter);

        glassView = (View) square.findViewById(R.id.view_glass);
        glassView.setOnClickListener(new MyOnClcikListener());
        glassView.setVisibility(GONE);

        swipeRefreshLayout = (SwipeRefreshLayout) square.findViewById(R.id.srl_findemployer);
        //为swipeRefreshLayout设置监听
        swipeRefreshLayout.setOnRefreshListener(new MyOnRefrshListener());
        //进入界面时自动刷新,记得数据查询完毕后关闭刷新
        refresh();

        employerRecyclerView = (RecyclerView)square.findViewById(R.id.rv_employer);
        //制定recyclerView为线性布局
        employerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //为RecyclerView设置监听
        employerRecyclerView.addOnScrollListener(new MyOnScrollListener());

        employerList = new ArrayList<EmployerPost>();
        employerPostAdapter = new EmployerPostAdapter(getContext(), employerList);
        employerPostAdapter.setOnItemClickListener(this);
        employerRecyclerView.setAdapter(employerPostAdapter);

        //悬浮按钮
        FloatingActionButton postFAB = (FloatingActionButton) square.findViewById(R.id.fab_post);
        postFAB.setOnClickListener(new MyOnClcikListener());
    }

    @Override
    public void onItemClick(int position) {
        Log.d(TAG, "当前position: " + position);
        EmployerPost employerPost = employerList.get(position);
        String id = employerPost.getObjectId();
        Log.d(TAG, "onCreate: id:" + id );
        Intent intent = new Intent(getContext(), EmployerPageActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }


    /*
    SwipeRefreshLayout.OnRefreshListener监听
     */
    public class MyOnRefrshListener implements SwipeRefreshLayout.OnRefreshListener {

        @Override
        public void onRefresh() {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "正在刷新", Toast.LENGTH_SHORT).show();
                    Log.d("下拉刷新：", "开始查询");
                    //queryData(0, STATE_REFRESH);
                    queryLimitData(0, STATE_REFRESH, maxDistance, locationLimit, situationLimit);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, 2000);
        }
    }

    /*
    RecyclerView.OnScrollListener监听
     */
    public class MyOnScrollListener extends RecyclerView.OnScrollListener {
        //最后一个可见的item的位置
        private int lastVisibleItemPosition;
        //当前的滑动状态
        private int currentScrollState;

        @Override
        //滚动时回调
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            lastVisibleItemPosition = ((LinearLayoutManager)layoutManager).findLastVisibleItemPosition();

        }

        @Override
        //滚动状态变化时回调
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            currentScrollState = newState;
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();

            if (newState ==RecyclerView.SCROLL_STATE_IDLE
                    && lastVisibleItemPosition >= (totalItemCount - 1)
                    && totalItemCount > visibleItemCount
                    && !isLoading) {
                employerPostAdapter.changeFooterState(1);
                isLoading = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("上拉加载：", "开始查询");
                        //queryData(currentPage, STATE_MORE);
                        queryLimitData(currentPage, STATE_MORE, maxDistance, locationLimit, situationLimit);
                    }
                }, 2000);
            }

        }
    }

    //Tab监听
    public class MyOnClcikListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.view_distance:
                    if (currentSub == -1) {
                        subMenuRV.setVisibility(View.VISIBLE);
                        glassView.setVisibility(View.VISIBLE);
                        selectedDistance();
                    } else if (currentSub == 0) {
                        closeSubMenu();
                        currentSub = -1;
                    } else {
                        selectedDistance();
                    }
                    break;
                case R.id.view_location:
                    if (currentSub == -1) {
                        subMenuRV.setVisibility(View.VISIBLE);
                        glassView.setVisibility(View.VISIBLE);
                        selectedLocation();
                    } else if (currentSub == 1) {
                        closeSubMenu();
                        currentSub = -1;
                    } else {
                        selectedLocation();
                    }
                    break;
                case R.id.view_situation:
                    if (currentSub == -1) {
                        subMenuRV.setVisibility(View.VISIBLE);
                        glassView.setVisibility(View.VISIBLE);
                        selectedSituation();
                    } else if (currentSub == 2) {
                        closeSubMenu();
                        currentSub = -1;
                    } else {
                        selectedSituation();
                    }
                    break;
                case R.id.view_glass:
                    closeSubMenu();
                    break;
                case R.id.fab_post:
                    Intent intent = new Intent(getContext(), WorkerPostActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    }

    //subMenuRV监听
    public class MyOnItemClickListener implements SubMenuAdapter.OnItemClickListener {

        @Override
        public void onItemClick(View view, int position) {
            switch(currentSub) {
                case 0:
                    distanceCondition = subMenuList.get(position).getContent();
                    if (distanceCondition.equals("不限")) {
                        distanceTV.setText("距离");
                    } else {
                        distanceTV.setText(distanceCondition);
                    }
                    //获取距离限制（minDistance、maxDistance）
                    if (distanceCondition.equals("不限")) {
                        maxDistance = 10000;
                    } else if (distanceCondition.equals("1公里")) {
                        maxDistance =1;
                    } else if (distanceCondition.equals("3公里")) {
                        maxDistance = 3;
                    } else if (distanceCondition.equals("5公里")) {
                        maxDistance = 5;
                    } else if (distanceCondition.equals("10公里")) {
                        maxDistance = 10;
                    }
                    //如果当前条件不是点击的条件，执行刷新
                    if (currentDistanceCondition != distanceCondition) {
                        refresh();
                        currentDistanceCondition = distanceCondition;
                    }
                    closeSubMenu();
                    break;
                case 1:
                    locationCondition = subMenuList.get(position).getContent();
                    if (locationCondition.equals("不限")) {
                        locationTV.setText("地点");
                    } else {
                        locationTV.setText(locationCondition);
                    }
                    //获取地点限制
                    if (locationCondition.equals("不限")) {
                        locationLimit = "不限";
                    } else if(locationCondition.equals("居家")) {
                        locationLimit = "居家";
                    } else if(locationCondition.equals("医院")) {
                        locationLimit = "医院";
                    }
                    //如果当前条件不是点击的条件，执行刷新
                    if (currentLocationCondition != locationCondition) {
                        refresh();
                        currentLocationCondition = locationCondition;
                    }
                    closeSubMenu();
                    break;
                case 2:
                    situationCondition = subMenuList.get(position).getContent();
                    if (situationCondition.equals("不限")) {
                        situationTV.setText("级别");
                    } else {
                        situationTV.setText(situationCondition);
                    }
                    //获取等级限制
                    if (situationCondition.equals("不限")) {
                        situationLimit = "不限";
                    } else if (situationCondition.equals("能自理")) {
                        situationLimit = "能自理";
                    } else if (situationCondition.equals("不能自理")) {
                        situationLimit = "不能自理";
                    } else if (situationCondition.equals("半失能")) {
                        situationLimit = "半失能";
                    } else if (situationCondition.equals("手术后")) {
                        situationLimit = "手术后";
                    }
                    //如果当前条件不是点击的条件，执行刷新
                    if (currentSituationCondition != situationCondition) {
                        refresh();
                        currentSituationCondition = situationCondition;
                    }
                    closeSubMenu();
                    break;
                default:
                    break;
            }
        }
    }


    private static final int STATE_REFRESH = 0;// 下拉刷新
    private static final int STATE_MORE = 1;// 加载更多

    private int limit = 10; // 每页的数据是10条
    private int currentPage = 0; // 当前页的编号，从0开始


    /**
     * 按条件查询数据
     */
    private void queryLimitData(int page, final int actionType, int maxDistance,
                                String  locationLimit, String situationLimit) {
        Log.d("Bmob", "最大距离：" + maxDistance + "地点："
                + locationLimit + "病人情况：" + situationLimit);
        Log.d("Bmob", "pageN:" + page + " limit:" + limit + " actionType:" + actionType);

        //最大距离
        BmobQuery<EmployerPost> eq1 = new BmobQuery<EmployerPost>();
        eq1.addWhereLessThanOrEqualTo("employerDistance", maxDistance);
        //地点
        BmobQuery<EmployerPost> eq2 = new BmobQuery<EmployerPost>();
        eq2.addWhereEqualTo("workLocation", locationLimit);
        //病人情况限制
        BmobQuery<EmployerPost> eq3 = new BmobQuery<EmployerPost>();
        eq3.addWhereEqualTo("employerSituation", situationLimit);

        //整合条件
        List<BmobQuery<EmployerPost>> andQuerys = new ArrayList<BmobQuery<EmployerPost>>();
        if (situationLimit.equals("不限") && locationLimit.equals("不限")) {
            andQuerys.add(eq1);
        } else if (situationLimit.equals("不限") && !locationLimit.equals("不限")) {
            andQuerys.add(eq1);
            andQuerys.add(eq2);
        } else if (!situationLimit.equals("不限") && locationLimit.equals("不限")) {
            andQuerys.add(eq1);
            andQuerys.add(eq3);
        } else {
            andQuerys.add(eq1);
            andQuerys.add(eq2);
            andQuerys.add(eq3);
        }

        BmobQuery<EmployerPost> query = new BmobQuery<EmployerPost>();
        query.and(andQuerys);
        // 按时间降序查询
        query.order("-createdAt");
        // 如果是加载更多
        if (actionType == STATE_MORE) {
            // 处理时间查询
            Date date = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = sdf.parse(lastTime);
                Log.d("LastTime最后的时间", date.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // 只查询小于等于最后一个item发表时间的数据
            query.addWhereLessThanOrEqualTo("createdAt", new BmobDate(date));
            // 跳过之前页数并去掉重复数据
            //query.setSkip(page * count + 1);
        } else {
            // 下拉刷新
            query.setSkip(page * limit);
        }
        // 设置每页数据个数
        query.setLimit(limit);
        // 查找数据
        query.findObjects( new FindListener<EmployerPost>() {
            @Override
            public void done(List<EmployerPost> list, BmobException e) {

                if (e == null) {
                    Log.d("正在加载数据（查询）", list.size() + "条");
                    if (list.size() > 0) {

                        if (actionType == STATE_REFRESH) {
                            // 当是下拉刷新操作时，将当前页的编号重置为0，并把workerList清空
                            currentPage = 0;
                            employerList.clear();
                            Log.d("操作：", "清空workerList");
                        }

                        // 将本次查询的数据添加到workerList中
                        for (EmployerPost employer : list) {
                            employerList.add(employer);
                            Log.d("添加元素到workerList：",employer.getEmployerName());
                        }

                        // 获取最后时间
                        lastTime = employerList.get(employerList.size() - 1).getCreatedAt();
                        Log.d("获取LastTime", "");

                        Log.d("", "数据查询已完成，通知主线程更新UI");
                        mHandler.sendEmptyMessage(CHANGE_EMPLOYERRV);

                        // 这里在每次加载完数据后，将当前页码+1，这样在上拉刷新的onPullUpToRefresh方法中就不需要操作curPage了
                        currentPage++;
                    } else if (actionType == STATE_MORE) {
                        isLoading = true;
                        employerPostAdapter.changeFooterState(0);
                        Toast.makeText(getActivity(),"没有更多数据了", Toast.LENGTH_SHORT).show();
                    } else if (actionType == STATE_REFRESH) {
                        employerList.clear();
                        //通知主线程更新UI
                        Log.d("", "数据查询已完成，通知主线程更新UI");
                        mHandler.sendEmptyMessage(CHANGE_EMPLOYERRV);
                        Toast.makeText(getActivity(),"没有更多数据了", Toast.LENGTH_SHORT).show();
                    }
                    //刷新完成后关闭
                    swipeRefreshLayout.setRefreshing(false);
                    isLoading = false;

                } else {
                    isLoading = false;
                    Log.d("bmob", "失败" + e.getMessage() + "," + e.getErrorCode());
                    Toast.makeText(getActivity(), "加载失败（查询）", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    /**
     * 关闭subMenu
     */
    public void closeSubMenu() {
        if (currentSub != -1) {
            resetSubMenu();
            subMenuRV.setVisibility(GONE);
            glassView.setVisibility(GONE);
            currentSub = -1;
        }
    }

    /**
     * 重置subMenu
     */
    public void resetSubMenu() {
        distanceTV.setTextColor(getResources().getColor(R.color.grey_bababa));
        distanceIV.setImageResource(R.drawable.keyboard_down_grey);
        locationTV.setTextColor(getResources().getColor(R.color.grey_bababa));
        locationIV.setImageResource(R.drawable.keyboard_down_grey);
        situationTV.setTextColor(getResources().getColor(R.color.grey_bababa));
        situationIV.setImageResource(R.drawable.keyboard_down_grey);
    }


    /**
     * 设置distanceTab为选中状态
     */
    public void selectedDistance() {
        resetSubMenu();
        distanceTV.setTextColor(getResources().getColor(R.color.blue_5fc0cd));
        distanceIV.setImageResource(R.drawable.keyboard__up_blue);
        subMenuList.clear();
        for(int i = 0; i < distance.length; i++){
            subMenuList.add(new SubMenu(distance[i]));
        }
        Log.d("FindWorkerFragment", "通知主线程更新SUBMENU");
        subMenuAdapter.notifyDataSetChanged();
        currentSub = 0;
    }

    /**
     * 设置locationTab为选中状态
     */
    public void selectedLocation() {
        resetSubMenu();
        locationTV.setTextColor(getResources().getColor(R.color.blue_5fc0cd));
        locationIV.setImageResource(R.drawable.keyboard__up_blue);
        subMenuList.clear();
        for(int i = 0; i < location.length; i++){
            subMenuList.add(new SubMenu(location[i]));
        }
        Log.d("FindWorkerFragment", "通知主线程更新SUBMENU");
        subMenuAdapter.notifyDataSetChanged();
        currentSub = 1;
    }

    /**
     * 设置situationTab为选中状态
     */
    public void selectedSituation() {
        resetSubMenu();
        situationTV.setTextColor(getResources().getColor(R.color.blue_5fc0cd));
        situationIV.setImageResource(R.drawable.keyboard__up_blue);
        subMenuList.clear();
        for(int i = 0; i < situation.length; i++){
            subMenuList.add(new SubMenu(situation[i]));
        }
        for (SubMenu subMenu : subMenuList) {
            Log.d("测试", subMenu.getContent());
        }
        Log.d("FindWorkerFragment", "通知主线程更新SUBMENU");
        //mHandler.sendEmptyMessage(CHANGE_WORKERRV);
        subMenuAdapter.notifyDataSetChanged();
        currentSub = 2;
    }


    /**
     * 刷新
     */
    public void refresh () {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "正在刷新", Toast.LENGTH_SHORT).show();
                        Log.d("下拉刷新：", "开始查询");
                        queryLimitData(0, STATE_REFRESH, maxDistance, locationLimit, situationLimit);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    /**
     * 静态内部类MyHandler
     */
    private static class MyHandler extends Handler {
        private final WeakReference<FindEmployerFragment> mFragment;

        public MyHandler (FindEmployerFragment mFragment) {
            this.mFragment = new WeakReference<FindEmployerFragment>(mFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(mFragment.get() == null) {
                return;
            }
            switch(msg.what) {
                case CHANGE_EMPLOYERRV:
                    Log.d("FindEmployerFragment", "更新RecyclerView");
                    employerPostAdapter.notifyDataSetChanged();
                default:
                    break;
            }
        }
    }
}