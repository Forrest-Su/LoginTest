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

import com.example.forrestsu.logintest.R;
import com.example.forrestsu.logintest.SubMenu;
import com.example.forrestsu.logintest.WorkerPost;
import com.example.forrestsu.logintest.activity.EmployerPostActivity;
import com.example.forrestsu.logintest.activity.WorkerPageActivity;
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

public class FindWorkerFragment extends Fragment implements WorkerPostAdapter.OnItemClickListener{

    private static final String TAG = "FindWorkerFragment";

    private Boolean isLoading = false;
    private String levelCondition, ageCondition, distanceCondition, locationCondition;
    private String currentLevelCondition, currentAgeCondition
            , currentDistanceCondition, currentLocationCondition;

    private String level[] = {"不限", "普通护工", "高级护工", "护士"};
    private String age[] = {"不限", "18-25", "26-35", "36-45", "46-55", "56以上"};
    private String distance[] = {"不限", "1公里", "3公里", "5公里", "10公里"};
    private String location[] = {"不限", "医院", "居家"};

    //顶部菜单部分
    private View levelView, ageView, distanceView, locationView;
    private TextView levelTV, ageTV, distanceTV, locationTV;
    private ImageView levelIV, ageIV, distanceIV, locationIV;

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
    private RecyclerView workerRecyclerView;

    private List<WorkerPost> workerList;
    private List<SubMenu> subMenuList;

    //WorkerPost适配器
    private static WorkerPostAdapter workerPostAdapter;
    //SubMenu适配器
    private static SubMenuAdapter subMenuAdapter;

    private String lastTime = null;
    private int currentSub = -1;

    private static final int CHANGE_WORKERRV = 0;
    private static final int CHANGE_SUBMENU = 1;

    //查询条件
    private String levelLimit;
    private int minAge, maxAge;
    private int minDistance, maxDistance;
    private String locationLimit;

    private MyHandler mHandler = new MyHandler(this);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInsatnceState) {
        View square = inflater.inflate(R.layout.fragment_findworker, container, false);

        initView(square);

        return square;
    }

    /*
    初始化界面
     */
    public void initView(View square) {

        currentLevelCondition = "不限";
        currentAgeCondition = "不限";
        currentDistanceCondition = "不限";
        currentLocationCondition = "不限";

        //初始化查询条件
        levelLimit = "不限";
        minAge = 0;
        maxAge = 100;
        minDistance = 0;
        maxDistance = 10000;
        locationLimit = "不限";

        //初始化顶部tab
        levelView = square.findViewById(R.id.view_situation);
        ageView = square.findViewById(R.id.view_age);
        distanceView = square.findViewById(R.id.view_distance);
        locationView = square.findViewById(R.id.view_location);
        //为顶部tab设置监听
        levelView.setOnClickListener(new MyOnClickListener());
        ageView.setOnClickListener(new MyOnClickListener());
        distanceView.setOnClickListener(new MyOnClickListener());
        locationView.setOnClickListener(new MyOnClickListener());

        levelTV = square.findViewById(R.id.tv_level);
        ageTV = square.findViewById(R.id.tv_age);
        distanceTV = square.findViewById(R.id.tv_distance);
        locationTV = square.findViewById(R.id.tv_location);

        textViewList = new ArrayList<TextView>();
        textViewList.add(levelTV);
        textViewList.add(ageTV);
        textViewList.add(distanceTV);
        textViewList.add(locationTV);

        levelIV = square.findViewById(R.id.iv_level);
        ageIV = square.findViewById(R.id.iv_age);
        distanceIV = square.findViewById(R.id.iv_distance);
        locationIV = square.findViewById(R.id.iv_location);

        imageViewList = new ArrayList<ImageView>();
        imageViewList.add(levelIV);
        imageViewList.add(ageIV);
        imageViewList.add(distanceIV);
        imageViewList.add(locationIV);

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
        glassView.setOnClickListener(new MyOnClickListener());
        glassView.setVisibility(GONE);

        swipeRefreshLayout = (SwipeRefreshLayout) square.findViewById(R.id.srl_findworker);
        //为swipeRefreshLayout设置监听
        swipeRefreshLayout.setOnRefreshListener(new MyOnRefreshListener());
        //进入界面时自动刷新,记得数据查询完毕后关闭刷新
        refresh();

        workerRecyclerView = (RecyclerView)square.findViewById(R.id.rv_worker);
        //制定recyclerView为线性布局
        workerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //为RecyclerView设置监听
        workerRecyclerView.addOnScrollListener(new MyOnScrollListener());

        workerList = new ArrayList<WorkerPost>();
        workerPostAdapter = new WorkerPostAdapter(getContext(), workerList);
        workerPostAdapter.setOnItemClickListener(this);
        workerRecyclerView.setAdapter(workerPostAdapter);


        //悬浮按钮
        FloatingActionButton postFAB = (FloatingActionButton) square.findViewById(R.id.fab_post);
        postFAB.setOnClickListener(new MyOnClickListener());
    }

    @Override
    public void onItemClick(int position) {
        Log.d(TAG, "当前position: " + position);
        WorkerPost workerPost = workerList.get(position);
        String id = workerPost.getObjectId();
        Log.d(TAG, "onCreate: id:" + id );
        Intent intent = new Intent(getContext(), WorkerPageActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }


    /*
    SwipeRefreshLayout.OnRefreshListener监听
     */
    public class MyOnRefreshListener implements SwipeRefreshLayout.OnRefreshListener {

        @Override
        public void onRefresh() {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "正在刷新", Toast.LENGTH_SHORT).show();
                    Log.d("下拉刷新：", "开始查询");
                    //queryData(0, STATE_REFRESH);
                    queryLimitData(0, STATE_REFRESH, levelLimit, minAge, maxAge, maxDistance, locationLimit);
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

            if (newState == RecyclerView.SCROLL_STATE_IDLE
                    && lastVisibleItemPosition >= (totalItemCount - 1)
                    && totalItemCount > visibleItemCount
                    && !isLoading) {
                workerPostAdapter.changeFooterState(1);
                isLoading = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("上拉加载：", "开始查询");
                        //queryData(currentPage, STATE_MORE);
                        queryLimitData(currentPage, STATE_MORE, levelLimit, minAge, maxAge, maxDistance, locationLimit);
                    }
                }, 2000);
            }

        }
    }

    //Tab监听
    public class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.view_situation:
                    if (currentSub == -1) {
                        subMenuRV.setVisibility(View.VISIBLE);
                        glassView.setVisibility(View.VISIBLE);
                        selectedLevel();
                    } else if (currentSub == 0) {
                        closeSubMenu();
                        currentSub = -1;
                    } else {
                       selectedLevel();
                    }
                    break;
                case R.id.view_age:
                    if (currentSub == -1) {
                        subMenuRV.setVisibility(View.VISIBLE);
                        glassView.setVisibility(View.VISIBLE);
                        selectedAge();
                    } else if (currentSub == 1) {
                        closeSubMenu();
                        currentSub = -1;
                    } else {
                        selectedAge();
                    }
                    break;
                case R.id.view_distance:
                    if (currentSub == -1) {
                        subMenuRV.setVisibility(View.VISIBLE);
                        glassView.setVisibility(View.VISIBLE);
                        selectedDistance();
                    } else if (currentSub == 2) {
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
                    } else if (currentSub == 3) {
                        closeSubMenu();
                        currentSub = -1;
                    } else {
                        selectedLocation();
                    }
                    break;
                case R.id.view_glass:
                    closeSubMenu();
                    break;
                case R.id.fab_post:
                    Intent intent = new Intent(getContext(), EmployerPostActivity.class);
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
                    levelCondition = subMenuList.get(position).getContent();
                    if (levelCondition.equals("不限")) {
                        levelTV.setText("级别");
                    } else {
                        levelTV.setText(levelCondition);
                        }
                    //获取等级限制
                    if (levelCondition.equals("不限")) {
                        levelLimit = "不限";
                    } else if (levelCondition.equals("普通护工")) {
                        levelLimit = "普通护工";
                    } else if (levelCondition.equals("高级护工")) {
                        levelLimit = "高级护工";
                    } else if (levelCondition.equals("护士")) {
                        levelLimit = "护士";
                    }
                    //如果当前条件不是点击的条件，执行刷新
                    if (!currentLevelCondition.equals(levelCondition)) {
                        refresh();
                        currentLevelCondition = levelCondition;
                    }
                    closeSubMenu();
                    break;
                case 1:
                    ageCondition = subMenuList.get(position).getContent();
                    if (ageCondition.equals("不限")) {
                        ageTV.setText("年龄");
                    } else {
                        ageTV.setText(ageCondition);
                    }
                    //获取年龄限制（minAge、maxAge）
                    if (ageCondition.equals("不限")) {
                        minAge = 0;
                        maxAge = 100;
                    } else if(ageCondition.equals("18-25")) {
                        minAge = 18;
                        maxAge = 25;
                    } else if(ageCondition.equals("26-35")) {
                        minAge = 26;
                        maxAge = 35;
                    } else if(ageCondition.equals("36-45")) {
                        minAge = 36;
                        maxAge = 45;
                    } else if(ageCondition.equals("46-55")) {
                        minAge = 46;
                        maxAge = 55;
                    } else if(ageCondition.equals("56以上")) {
                        minAge = 56;
                        maxAge = 100;
                    }
                    //如果当前条件不是点击的条件，执行刷新
                    if (currentAgeCondition != levelCondition) {
                        refresh();
                        currentAgeCondition = ageCondition;
                    }
                    closeSubMenu();
                    break;
                case 2:
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
                case 3:
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
    private void queryLimitData(int page, final int actionType, String levelLimit, int minAge
            , int maxAge, int maxDistance, String  locationLimit) {
        Log.d("Bmob", "等级:" + levelLimit + "最小年龄:" + minAge + "最大年龄：" + maxAge
                + "最大距离：" + maxDistance + "地点：" + locationLimit);
        Log.d("Bmob", "pageN:" + page + " limit:" + limit + " actionType:"
                + actionType);

        //等级限制
        BmobQuery<WorkerPost> eq1 = new BmobQuery<WorkerPost>();
        eq1.addWhereEqualTo("workerLevel", levelLimit);
        //最小年龄限制
        BmobQuery<WorkerPost> eq2 = new BmobQuery<WorkerPost>();
        eq2.addWhereGreaterThanOrEqualTo("workerAge", minAge);
        //最大年龄
        BmobQuery<WorkerPost> eq3 = new BmobQuery<WorkerPost>();
        eq3.addWhereLessThanOrEqualTo("workerAge", maxAge);
        //最大距离
        //BmobQuery<WorkerPost> eq4 = new BmobQuery<WorkerPost>();
        //eq4.addWhereLessThanOrEqualTo("workerDistance", maxDistance);
        //地点
        BmobQuery<WorkerPost> eq5 = new BmobQuery<WorkerPost>();
        eq5.addWhereEqualTo("workLocation", locationLimit);

        //整合条件
        List<BmobQuery<WorkerPost>> andQuerys = new ArrayList<BmobQuery<WorkerPost>>();
        if (levelLimit.equals("不限") && locationLimit.equals("不限")) {
            andQuerys.add(eq2);
            andQuerys.add(eq3);
            //andQuerys.add(eq4);
        } else if (levelLimit.equals("不限") && !locationLimit.equals("不限")) {
            andQuerys.add(eq2);
            andQuerys.add(eq3);
            //andQuerys.add(eq4);
            andQuerys.add(eq5);
        } else if (!levelLimit.equals("不限") && locationLimit.equals("不限")) {
            andQuerys.add(eq1);
            andQuerys.add(eq2);
            andQuerys.add(eq3);
           //andQuerys.add(eq4);
        } else {
            andQuerys.add(eq1);
            andQuerys.add(eq2);
            andQuerys.add(eq3);
            //andQuerys.add(eq4);
            andQuerys.add(eq5);
        }

        BmobQuery<WorkerPost> query = new BmobQuery<>();
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
        query.findObjects( new FindListener<WorkerPost>() {
            @Override
            public void done(List<WorkerPost> list, BmobException e) {

                if (e == null) {
                    Log.d("正在加载数据（查询）", list.size() + "条");
                    if (list.size() > 0) {

                        if (actionType == STATE_REFRESH) {
                            // 当是下拉刷新操作时，将当前页的编号重置为0，并把workerList清空
                            currentPage = 0;
                            workerList.clear();
                            Log.d("操作：", "清空workerList");
                        }

                        // 将本次查询的数据添加到workerList中
                        for (WorkerPost worker : list) {
                            workerList.add(worker);
                            Log.d("添加元素到workerList：",worker.getWorkerName());
                        }

                        // 获取最后时间
                        lastTime = workerList.get(workerList.size() - 1).getCreatedAt();
                        Log.d("获取LastTime", "");

                        Log.d("", "数据查询已完成，通知主线程更新UI");
                        mHandler.sendEmptyMessage(CHANGE_WORKERRV);

                        // 这里在每次加载完数据后，将当前页码+1，这样在上拉刷新的onPullUpToRefresh方法中就不需要操作curPage了
                        currentPage++;
                    } else if (actionType == STATE_MORE) {
                        isLoading = true;
                        workerPostAdapter.changeFooterState(0);
                        Toast.makeText(getActivity(),"没有更多数据了", Toast.LENGTH_SHORT).show();
                    } else if (actionType == STATE_REFRESH) {
                        workerList.clear();
                        //通知主线程更新UI
                        Log.d("", "数据查询已完成，通知主线程更新UI");
                        mHandler.sendEmptyMessage(CHANGE_WORKERRV);
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
        levelTV.setTextColor(getResources().getColor(R.color.grey_bababa));
        levelIV.setImageResource(R.drawable.keyboard_down_grey);
        ageTV.setTextColor(getResources().getColor(R.color.grey_bababa));
        ageIV.setImageResource(R.drawable.keyboard_down_grey);
        distanceTV.setTextColor(getResources().getColor(R.color.grey_bababa));
        distanceIV.setImageResource(R.drawable.keyboard_down_grey);
        locationTV.setTextColor(getResources().getColor(R.color.grey_bababa));
        locationIV.setImageResource(R.drawable.keyboard_down_grey);
    }

    /**
     * 设置levelTab为选中状态
     */
    public void selectedLevel() {
        resetSubMenu();
        levelTV.setTextColor(getResources().getColor(R.color.blue_5fc0cd));
        levelIV.setImageResource(R.drawable.keyboard__up_blue);
        subMenuList.clear();
        for(int i = 0; i < level.length; i++){
            subMenuList.add(new SubMenu(level[i]));
        }
        for (SubMenu subMenu : subMenuList) {
            Log.d("测试", subMenu.getContent());
        }
        Log.d("FindWorkerFragment", "通知主线程更新SUBMENU");
        //mHandler.sendEmptyMessage(CHANGE_WORKERRV);
        subMenuAdapter.notifyDataSetChanged();
        currentSub = 0;
    }

    /**
     * 设置ageTab为选中状态
     */
    public void selectedAge() {
        resetSubMenu();
        ageTV.setTextColor(getResources().getColor(R.color.blue_5fc0cd));
        ageIV.setImageResource(R.drawable.keyboard__up_blue);
        subMenuList.clear();
        for(int i = 0; i < age.length; i++){
            subMenuList.add(new SubMenu(age[i]));
        }
        Log.d("FindWorkerFragment", "通知主线程更新SUBMENU");
        subMenuAdapter.notifyDataSetChanged();
        currentSub = 1;
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
        currentSub = 2;
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
        currentSub = 3;
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
                        queryLimitData(0, STATE_REFRESH, levelLimit, minAge, maxAge, maxDistance, locationLimit);
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
        private final WeakReference<FindWorkerFragment> mFragment;

        public MyHandler (FindWorkerFragment mFragment) {
            this.mFragment = new WeakReference<FindWorkerFragment>(mFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(mFragment.get() == null) {
                return;
            }
            switch(msg.what) {
                case CHANGE_WORKERRV:
                    Log.d("FindWorkerFragment", "更新RecyclerView");
                    workerPostAdapter.notifyDataSetChanged();
                    break;
                case CHANGE_SUBMENU:
                    Log.d("FindWorkerFragment", "更新SubMenu");
                    subMenuAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    }

}