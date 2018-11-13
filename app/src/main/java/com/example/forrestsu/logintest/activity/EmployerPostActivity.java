package com.example.forrestsu.logintest.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.forrestsu.logintest.EmployerPost;
import com.example.forrestsu.logintest.MyUser;
import com.example.forrestsu.logintest.R;
import com.example.forrestsu.logintest.fragment.SetAddressFragment;
import com.example.forrestsu.logintest.fragment.SetDateFragment;
import com.example.forrestsu.logintest.fragment.SetSituationFragment;
import com.example.forrestsu.logintest.fragment.SetTimeFragment;
import com.example.forrestsu.logintest.utils.MyCalendar;

import java.util.Date;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class EmployerPostActivity extends BaseActivity implements
        SetAddressFragment.SendMessageAddress, SetDateFragment.SendMessageDate,
        SetTimeFragment.SendMessageTime, SetSituationFragment.SendMessageSituation {

    private static final String TAG = "EmployerPostActivity";

    private final static int DATE_FROM = 1;
    private final static int DATE_TO = 2;
    private int dateSite;
    private final static int TIME_FROM = 1;
    private final static int TIME_TO = 2;
    private int timeSite;
    private String workLocation = "医院";


    private MyUser currentUser;
    private EmployerPost employerPost;

    private View glassView;
    private FrameLayout showFragmentFL;
    private Fragment currentFragment, setAddressFM, setDateFM, setTimeFM, setSituationFM;
    private Button hospitalBT, homeBT, confirmBT;
    private ImageView dateFromIV, dateToIV, timeFromIV, timeToIV;
    private EditText specificAddressET, priceET, nameET, noteET;
    private TextView showAddressTV, showDateFromTV, showDateToTV,
                     showTimeFromTV, showTimeToTV, showSituationTV;

    private ProgressDialog progressDialog;

    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_post);

        initView();
        loadInfo();

        //传值给SetDateFragment
        String year = String.valueOf(MyCalendar.getSysYearInt());
        bundle = new Bundle();
        bundle.putString("year_limit", year);
    }

    //初始化
    public void initView() {

        currentUser = BmobUser.getCurrentUser(MyUser.class);
        employerPost = new EmployerPost();

        glassView = (View) findViewById(R.id.view_glass);
        glassView.setOnClickListener(new MyOnClickListener());
        glassView.setVisibility(GONE);
        showFragmentFL = (FrameLayout) findViewById(R.id.fl_show_fragment);
        showFragmentFL.setVisibility(GONE);

        currentFragment = new Fragment();

        hospitalBT = (Button) findViewById(R.id.bt_hospital);
        hospitalBT.setOnClickListener(new MyOnClickListener());
        homeBT = (Button) findViewById(R.id.bt_home);
        homeBT.setOnClickListener(new MyOnClickListener());
        confirmBT = (Button) findViewById(R.id.bt_confirm);
        confirmBT.setOnClickListener(new MyOnClickListener());

        specificAddressET = (EditText) findViewById(R.id.et_specific_address);
        priceET = (EditText) findViewById(R.id.et_price);
        nameET = (EditText) findViewById(R.id.et_name);
        noteET = (EditText) findViewById(R.id.et_note);

        showAddressTV = (TextView) findViewById(R.id.tv_show_address);
        showAddressTV.setOnClickListener(new MyOnClickListener());
        showSituationTV = (TextView) findViewById(R.id.tv_show_situation);
        showSituationTV.setOnClickListener(new MyOnClickListener());

        dateFromIV = (ImageView) findViewById(R.id.iv_date_1);
        dateFromIV.setOnClickListener(new MyOnClickListener());
        dateToIV = (ImageView) findViewById(R.id.iv_date_2);
        dateToIV.setOnClickListener(new MyOnClickListener());
        timeFromIV = (ImageView) findViewById(R.id.iv_time_1);
        timeFromIV.setOnClickListener(new MyOnClickListener());
        timeToIV = (ImageView) findViewById(R.id.iv_time_2);
        timeToIV.setOnClickListener(new MyOnClickListener());

        showDateFromTV = (TextView) findViewById(R.id.tv_date_from);
        showDateToTV = (TextView) findViewById(R.id.tv_date_to);
        showTimeFromTV = (TextView) findViewById(R.id.tv_time_from);
        showTimeToTV = (TextView) findViewById(R.id.tv_time_to);
    }

    //MyOnClickListener点击监听
    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.view_glass:
                    glassView.setVisibility(GONE);
                    showFragmentFL.setVisibility(GONE);
                    break;
                case R.id.tv_show_address:
                    if (setAddressFM == null) {
                        setAddressFM = new SetAddressFragment();
                    }
                    addOrShowFragment(getSupportFragmentManager().beginTransaction(), setAddressFM);
                    glassView.setVisibility(VISIBLE);
                    showFragmentFL.setVisibility(VISIBLE);
                    break;
                case R.id.iv_date_1:
                    dateSite = DATE_FROM;
                    if (setDateFM == null) {
                        setDateFM = new SetDateFragment();
                        setDateFM.setArguments(bundle);
                    }
                    addOrShowFragment(getSupportFragmentManager().beginTransaction(), setDateFM);
                    glassView.setVisibility(VISIBLE);
                    showFragmentFL.setVisibility(VISIBLE);
                    break;
                case R.id.iv_date_2:
                    dateSite = DATE_TO;
                    if (setDateFM == null) {
                        setDateFM = new SetDateFragment();
                        setDateFM.setArguments(bundle);
                    }
                    addOrShowFragment(getSupportFragmentManager().beginTransaction(), setDateFM);
                    glassView.setVisibility(VISIBLE);
                    showFragmentFL.setVisibility(VISIBLE);
                    break;
                case R.id.iv_time_1:
                    timeSite = TIME_FROM;
                    if (setTimeFM == null) {
                        setTimeFM = new SetTimeFragment();
                    }
                    addOrShowFragment(getSupportFragmentManager().beginTransaction(), setTimeFM);
                    glassView.setVisibility(VISIBLE);
                    showFragmentFL.setVisibility(VISIBLE);
                    break;
                case R.id.iv_time_2:
                    timeSite = TIME_TO;
                    if (setTimeFM == null) {
                        setTimeFM = new SetTimeFragment();
                    }
                    addOrShowFragment(getSupportFragmentManager().beginTransaction(), setTimeFM);
                    glassView.setVisibility(VISIBLE);
                    showFragmentFL.setVisibility(VISIBLE);
                    break;
                case R.id.bt_hospital:
                    //
                    workLocation = "医院";
                    hospitalBT.setBackgroundColor(0xff5fc0cd);
                    homeBT.setBackgroundColor(0xffbababa);
                    break;
                case R.id.bt_home:
                    //
                    workLocation = "居家";
                    homeBT.setBackgroundColor(0xff5fc0cd);
                    hospitalBT.setBackgroundColor(0xffbababa);
                    break;
                case R.id.tv_show_situation:
                    if (setSituationFM == null) {
                        setSituationFM = new SetSituationFragment();
                    }
                    addOrShowFragment(getSupportFragmentManager().beginTransaction(), setSituationFM);
                    glassView.setVisibility(VISIBLE);
                    showFragmentFL.setVisibility(VISIBLE);
                    break;
                case R.id.bt_confirm:
                    progressDialog = new ProgressDialog(EmployerPostActivity.this);
                    progressDialog.setTitle("");
                    progressDialog.setMessage("正在上传...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    if (setInfo()) {
                        saveInfo();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /*
    addOrShowFragment
     */
    public void addOrShowFragment(FragmentTransaction transaction, Fragment fragment) {
        if (currentFragment == fragment) {
            return;
        }
        if (!fragment.isAdded()) {
            transaction.hide(currentFragment).add(R.id.fl_show_fragment, fragment).commit();
        } else {
            transaction.hide(currentFragment).show(fragment).commit();
        }
        currentFragment = fragment;
    }

    /**
     *设置employerPost信息
     */
    private Boolean setInfo() {

        //关联到当前用户
        employerPost.setEmployer(currentUser);

        //设置所在地
        if ((showAddressTV.getText()).equals("")) {
            Toast.makeText(EmployerPostActivity.this, "请选择现居地址",
                    Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return false;
        }

        //设置详细地址
        if ((specificAddressET.getText().toString()).equals("")) {
            Toast.makeText(EmployerPostActivity.this, "请输入详细地址",
                    Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return false;
        } else {
            employerPost.setSpecificAddress(specificAddressET.getText().toString());
        }

        //设置起始日期和结束日期
        if ((showDateFromTV.getText().toString()).equals("")
                || ((showDateToTV.getText().toString()).equals(""))) {
            Toast.makeText(EmployerPostActivity.this, "请输入日期",
                    Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return false;
        } else {
            //比较起始日期和结束日期是否合理
            Date dateFrom = MyCalendar.strToDate(showDateFromTV.getText().toString());
            Date dateTo = MyCalendar.strToDate(showDateToTV.getText().toString());
            int differenceOfDays = MyCalendar.differenceOfDays(dateFrom, dateTo);
            Log.d(TAG, "setInfo: 相差的天数：" + differenceOfDays);
            if (differenceOfDays < 0) {
                Toast.makeText(EmployerPostActivity.this, "请输入正确的日期",
                        Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                return false;
            } else {
                employerPost.setWorkDateFrom(showDateFromTV.getText().toString());
                employerPost.setWorkDateTo(showDateToTV.getText().toString());
            }
        }

        //设置起始时间和结束时间
        if ((showTimeFromTV.getText().toString()).equals("")
                || (showTimeToTV.getText().toString()).equals("")) {
            Toast.makeText(EmployerPostActivity.this, "请输入时间",
                    Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return false;
        } else {
            employerPost.setWorkTimeFrom(showTimeFromTV.getText().toString());
            employerPost.setWorkTimeTo(showTimeToTV.getText().toString());
        }

        //设置工作地点
        if (workLocation.equals("医院")) {
            employerPost.setWorkLocation("医院");
        } else if (workLocation.equals("居家")) {
            employerPost.setWorkLocation("居家");
        }

        //设置病人情况
        if ((showSituationTV.getText().toString()).equals("")) {
            Toast.makeText(EmployerPostActivity.this, "请选择情况",
                    Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return false;
        } else {
            employerPost.setEmployerSituation(nameET.getText().toString());
        }

        //设置服务价格
        if ((priceET.getText().toString()).equals("")) {
            Toast.makeText(EmployerPostActivity.this, "请输入服务价格",
                    Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return false;
        } else {
            String priceRegex = "[0-9_]{1,5}";
            if ((priceET.getText().toString()).matches(priceRegex)) {
                employerPost.setEmployerPrice(Integer.parseInt(priceET.getText().toString()));
            } else {
                Toast.makeText(EmployerPostActivity.this,
                        "请输入正确的价格（只能使用数字0-9）", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                return false;
            }
        }

        //设置姓名
        if ((nameET.getText().toString()).equals("")) {
            Toast.makeText(EmployerPostActivity.this, "请输入姓名",
                    Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return false;
        } else {
            employerPost.setEmployerName(nameET.getText().toString());
        }

        //设置备注
            employerPost.setNote(noteET.getText().toString());

        return true;
    }


    /**
     * 保存employerPost信息
     */
    private void saveInfo() {
        new Thread() {
            public void run() {
                employerPost.save(new SaveListener<String>() {
                    @Override
                    public void done(String objectId, BmobException e) {
                        if (e == null) {
                            progressDialog.dismiss();
                            Intent intent = new Intent(EmployerPostActivity.this, MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(EmployerPostActivity.this, "保存成功",
                                    Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "上传成功" );
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(EmployerPostActivity.this, "上传失败",
                                    Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "上传失败：" + e.getMessage() + "," + e.getErrorCode());
                        }
                    }
                });
            }
        }.start();
    }


    /**
     * 加载当前用户部分信息
     * 将用户地址加载到现居地址，如果现居地址与用户信息中的地址相同，用户就不需要再重复输入
     */
    private void loadInfo() {
        //Log.d(TAG, "loadUserInfo: ：" + currentUser.getProvince());
        //Log.d(TAG, "loadUserInfo: ：" + currentUser.getCity());
        //Log.d(TAG, "loadUserInfo: ：" + currentUser.getArea());
        //Log.d(TAG, "loadUserInfo: ：" + currentUser.getSpecificAddress());

        String province = currentUser.getProvince();
        String city = currentUser.getCity();
        String area = currentUser.getArea();
        if (province != null && city != null && area != null) {
            employerPost.setProvince(province);
            employerPost.setCity(city);
            employerPost.setArea(area);
            if (province.equals("台湾") || province.equals("澳门")
                    || province.equals("香港") || province.equals("北京")
                    || province.equals("天津") || province.equals("上海")
                    || province.equals("重庆") || province.equals("钓鱼岛")) {
                showAddressTV.setText((city + area));
            } else {
                showAddressTV.setText((province + city + area));
            }
        }
        if (currentUser.getSpecificAddress() != null) {
            specificAddressET.setText(currentUser.getSpecificAddress());
        }
    }


    /*
 实现SetAddressFragment的SendMessageAddress接口的回调方法
  */
    @Override
    public void getMessageAddress() {
        glassView.setVisibility(GONE);
        showFragmentFL.setVisibility(GONE);
    }

    @Override
    public void getMessageAddress(String province, String city, String area) {
        if (province.equals("台湾") || province.equals("澳门")
                || province.equals("香港") || province.equals("北京")
                || province.equals("天津") || province.equals("上海")
                || province.equals("重庆") || province.equals("钓鱼岛")) {
            showAddressTV.setText((city + " " + area));
        } else {
            showAddressTV.setText((province + " " + city + " " + area));
        }
        employerPost.setProvince(province);
        employerPost.setCity(city);
        employerPost.setArea(area);
        glassView.setVisibility(GONE);
        showFragmentFL.setVisibility(GONE);
    }

    /*
   实现SetDateFragment的SendMessageDate接口的回调方法
    */
    @Override
    public void getMessageDate() {
        glassView.setVisibility(GONE);
        showFragmentFL.setVisibility(GONE);
    }

    @Override
    public void getMessageDate(String date) {
        glassView.setVisibility(GONE);
        showFragmentFL.setVisibility(GONE);
        if (dateSite == DATE_FROM) {
            showDateFromTV.setText(date);
        } else if (dateSite == DATE_TO) {
            showDateToTV.setText(date);
        }
    }

    /*
   实现SetTimeFragment的SendMessageTime接口的回调方法
    */
    @Override
    public void getMessageTime() {
        glassView.setVisibility(GONE);
        showFragmentFL.setVisibility(GONE);
    }

    @Override
    public void getMessageTime(String time) {
        glassView.setVisibility(GONE);
        showFragmentFL.setVisibility(GONE);
        if (timeSite == TIME_FROM) {
            showTimeFromTV.setText(time);
        } else if (timeSite == TIME_TO) {
            showTimeToTV.setText(time);
        }
    }

    /*
   实现SetSituationFragment的SendMessageSituation接口的回调方法
    */
    @Override
    public void getMessageSituation() {
        glassView.setVisibility(GONE);
        showFragmentFL.setVisibility(GONE);
    }

    @Override
    public void getMessageSituation(String situation) {
        glassView.setVisibility(GONE);
        showFragmentFL.setVisibility(GONE);
        showSituationTV.setText(situation);
    }

}
