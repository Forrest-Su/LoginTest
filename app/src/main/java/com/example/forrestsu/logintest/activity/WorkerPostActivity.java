package com.example.forrestsu.logintest.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.forrestsu.logintest.MyUser;
import com.example.forrestsu.logintest.R;
import com.example.forrestsu.logintest.WorkerPost;
import com.example.forrestsu.logintest.fragment.SetAddressFragment;
import com.example.forrestsu.logintest.fragment.SetPhotoFragment;
import com.example.forrestsu.logintest.my_controls.WorkerBar;
import com.example.forrestsu.logintest.utils.ChoosePhoto;
import com.example.forrestsu.logintest.utils.ImageCompress;
import com.example.forrestsu.logintest.utils.ImageViewUtil;
import com.example.forrestsu.logintest.utils.MyCalendar;

import java.io.File;
import java.util.Date;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class WorkerPostActivity extends BaseActivity implements SetAddressFragment.SendMessageAddress{

    private static final String TAG = "WorkerPostActivity";

    private static final int CHOOSE_PHOTO = 1;
    private static final int TAKE_PHOTO = 2;

    //choice = 1
    private String[] level = {"普通护工", "高级护工", "护士"};
    //choice = 2
    private String[] workTime = {"全天", "白班", "夜班"};
    //choice = 3
    private String[] withMeal = {"不限", "是", "否"};
    //choice = 4
    private String[] withAccommodation = {"不限", "是", "否"};
    //choice = 5
    private String[] workLocation = {"不限", "医院", "居家"};

    private MyUser currentUser;
    private WorkerPost workerPost;

    //照片名称
    private static final String PHOTO_NAME = "photo_post.jpg";
    //照片路径
    private String photoPath;
    //头像路径
    private String compressPhotoPath;

    private ImageCompress imageCompress;
    private ChoosePhoto choosePhoto;

    private View glassView;
    private FrameLayout showFragmentFL;
    private Fragment currentFragment, setPhotoFragment, setAddressFragment;

    private WorkerBar levelBar, workTimeBar, withMealBar, withAccommodationBar, workLocationBar;

    private CircleImageView showPhotoCIV; //照片
    private EditText nameET;              //姓名
    private EditText ageET;           //年龄
    private TextView showAddressTV;       //现居地址
    private EditText specificAddressET;   //详细地址
    private EditText priceET;         //服务价格
    private EditText selfIntroductionET;  //自我介绍

    private ProgressDialog progressDialog;

    private Button confirmBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_post);

        initView();
        loadInfo();
    }

    public void initView() {
        currentUser = BmobUser.getCurrentUser(MyUser.class);
        workerPost = new WorkerPost();

        photoPath = getExternalCacheDir() + "/" + PHOTO_NAME;

        imageCompress = new ImageCompress();
        choosePhoto = new ChoosePhoto();

        showFragmentFL = (FrameLayout) findViewById(R.id.fl_show_fragment);
        showFragmentFL.setVisibility(GONE);
        currentFragment = new Fragment();
        glassView = (View) findViewById(R.id.view_glass);
        glassView.setOnClickListener(new MyOnClickListener());
        glassView.setVisibility(GONE);

        levelBar = (WorkerBar) findViewById(R.id.bar_level);
        workTimeBar = (WorkerBar) findViewById(R.id.bar_time);
        withMealBar = (WorkerBar) findViewById(R.id.bar_meal);
        withAccommodationBar = (WorkerBar) findViewById(R.id.bar_accommodation);
        workLocationBar = (WorkerBar) findViewById(R.id.bar_work_location);

        showPhotoCIV = (CircleImageView) findViewById(R.id.civ_photo);
        showPhotoCIV.setOnClickListener(new MyOnClickListener());
        nameET = (EditText) findViewById(R.id.et_name);
        ageET = (EditText) findViewById(R.id.et_age);
        showAddressTV = (TextView) findViewById(R.id.tv_show_address);
        showAddressTV.setOnClickListener(new MyOnClickListener());
        specificAddressET = (EditText) findViewById(R.id.et_specific_address);
        priceET = (EditText) findViewById(R.id.et_price);
        selfIntroductionET = (EditText) findViewById(R.id.et_self_introduction);

        confirmBT = (Button) findViewById(R.id.bt_confirm);
        confirmBT.setOnClickListener(new MyOnClickListener());
    }

    public class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.view_glass:
                    glassView.setVisibility(GONE);
                    showFragmentFL.setVisibility(GONE);
                    break;
                case R.id.civ_photo:
                    if (setPhotoFragment == null) {
                        setPhotoFragment = new SetPhotoFragment();
                    }
                    addOrShowFragment(getSupportFragmentManager().beginTransaction(), setPhotoFragment);
                    glassView.setVisibility(VISIBLE);
                    showFragmentFL.setVisibility(VISIBLE);
                    break;
                case R.id.tv_show_address:
                    if (setAddressFragment == null) {
                        setAddressFragment = new SetAddressFragment();
                    }
                    addOrShowFragment(getSupportFragmentManager().beginTransaction(), setAddressFragment);
                    glassView.setVisibility(VISIBLE);
                    showFragmentFL.setVisibility(VISIBLE);
                    break;
                case R.id.bt_confirm:
                    progressDialog = new ProgressDialog(WorkerPostActivity.this);
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
     *设置workerPost信息
     */
    private Boolean setInfo() {

        //关联到当前用户
        workerPost.setWorker(currentUser);

        //检查照片
        if (compressPhotoPath == null) {
            Toast.makeText(WorkerPostActivity.this, "照片不能为空", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return false;
        }

        //设置姓名
        if ((nameET.getText().toString()).equals("")) {
            Toast.makeText(WorkerPostActivity.this, "请输入姓名",
                    Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return false;
        } else {
            workerPost.setWorkerName(nameET.getText().toString());
        }

        //设置年龄
        if ((ageET.getText().toString()).equals("")) {
            Toast.makeText(WorkerPostActivity.this, "请输入年龄",
                    Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return false;
        } else {
            String ageRegex = "[0-9_]{2,3}";
            if ((ageET.getText().toString()).matches(ageRegex)) {
                int age = Integer.parseInt(ageET.getText().toString());
                if (age >= 18) {
                    workerPost.setWorkerAge(age);
                } else {
                    Toast.makeText(WorkerPostActivity.this,
                            "年龄不合法", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return false;
                }
            } else {
                Toast.makeText(WorkerPostActivity.this,
                        "请输入正确的年龄（只能使用数字0-9）", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                return false;
            }
        }

        //设置现居地址
        if ((showAddressTV.getText()).equals("")) {
            Toast.makeText(WorkerPostActivity.this, "请选择现居地址",
                    Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return false;
        }

        //设置详细地址
        if ((specificAddressET.getText().toString()).equals("")) {
            Toast.makeText(WorkerPostActivity.this, "请输入详细地址",
                    Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return false;
        } else {
            workerPost.setSpecificAddress(specificAddressET.getText().toString());
        }

        //设置服务价格
        if ((priceET.getText().toString()).equals("")) {
            Toast.makeText(WorkerPostActivity.this, "请输入服务价格",
                    Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return false;
        } else {
            String priceRegex = "[0-9_]{1,5}";
            if ((priceET.getText().toString()).matches(priceRegex)) {
                workerPost.setServicePrice(Integer.parseInt(priceET.getText().toString()));
            } else {
                Toast.makeText(WorkerPostActivity.this,
                        "请输入正确的价格（只能使用数字0-9）", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                return false;
            }
        }

        //设置自我介绍
        if ((selfIntroductionET.getText().toString()).equals("")) {
            Toast.makeText(WorkerPostActivity.this, "介绍一下自己",
                    Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return false;
        } else {
            workerPost.setSelfIntroduction(selfIntroductionET.getText().toString());
        }

        String level = setValue(levelBar.getSelected(), 1);
        String workTime = setValue(workTimeBar.getSelected(), 2);
        String withMeal = setValue(withMealBar.getSelected(), 3);
        String withAccommodation = setValue(withAccommodationBar.getSelected(), 4);
        String workLocation = setValue(workLocationBar.getSelected(), 5);
        workerPost.setWorkerLevel(level);
        workerPost.setWorkTime(workTime);
        workerPost.setWithMeal(withMeal);
        workerPost.setWithAccommodation(withAccommodation);
        workerPost.setWorkLocation(workLocation);
        return true;
    }


    private String setValue(int selected, int choice) {
        String value = "";
        switch(choice) {
            case 1:
                value = level[selected - 1];
                break;
            case 2:
                value = workTime[selected - 1];
                break;
            case 3:
                value = withMeal[selected - 1];
                break;
            case 4:
                value = withAccommodation[selected - 1];
                break;
            case 5:
                value = workLocation[selected - 1];
                break;
            default:
                break;
        }
        return value;
    }


    /**
     * 保存workerPost信息
     */
    private void saveInfo() {
        new Thread() {
            public void run() {
                    final BmobFile workerPhoto = new BmobFile(new File(compressPhotoPath));
                    workerPhoto.uploadblock(new UploadFileListener() {

                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                String url = workerPhoto.getFileUrl();
                                workerPost.setWorkerPhotoUrl(url);
                                workerPost.setWorkerPhoto(workerPhoto);
                                workerPost.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String objectId, BmobException e) {
                                        if (e == null) {
                                            progressDialog.dismiss();
                                            Intent intent = new Intent(WorkerPostActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            Toast.makeText(WorkerPostActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "上传成功" );
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(WorkerPostActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "上传失败：" + e.getMessage() + "," + e.getErrorCode());
                                        }
                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(WorkerPostActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "上传失败：" + e.getMessage() + "," + e.getErrorCode());
                            }
                        }
                    });
            }
        }.start();
    }

    /**
     * 加载当前用户部分信息
     * 根据用户生日设置年龄
     * 将用户地址加载到现居地址，如果现居地址与用户信息中的地址相同，用户就不需要再重复输入
     */
    private void loadInfo() {
        //Log.d(TAG, "loadUserInfo: ：" + currentUser.getBirthday());
        //Log.d(TAG, "loadUserInfo: ：" + currentUser.getProvince());
        //Log.d(TAG, "loadUserInfo: ：" + currentUser.getCity());
        //Log.d(TAG, "loadUserInfo: ：" + currentUser.getArea());
        //Log.d(TAG, "loadUserInfo: ：" + currentUser.getSpecificAddress());

        if (currentUser.getBirthday() != null) {
            //计算年龄
            Date birthDate = MyCalendar.strToDate(currentUser.getBirthday());
            int age = MyCalendar.getCurrentAge(birthDate);
            Log.d(TAG, "loadInfo: 计算得到年龄：" + age);
            ageET.setText(String.valueOf(age));
        }

        String province = currentUser.getProvince();
        String city = currentUser.getCity();
        String area = currentUser.getArea();
        if (province != null && city != null && area != null) {
            workerPost.setProvince(province);
            workerPost.setCity(city);
            workerPost.setArea(area);
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
        workerPost.setProvince(province);
        workerPost.setCity(city);
        workerPost.setArea(area);
        glassView.setVisibility(GONE);
        showFragmentFL.setVisibility(GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //如果希望结果传回Fragment，必须加上下面这一句
        super.onActivityResult(requestCode, resultCode,data);
        Log.d("请求码是", requestCode + "");
        Log.d("返回码是", resultCode + "");
        switch (requestCode) {
            case CHOOSE_PHOTO:
                showFragmentFL.setVisibility(GONE);
                glassView.setVisibility(GONE);
                if (resultCode == RESULT_OK) {
                    //判断手机版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        String path = choosePhoto.handleImageOnKitKat(this, data);
                        ImageCompress.compressWithLuban(path);
                        imageCompress.setLubanListener(new ImageCompress.LubanListener() {
                            @Override
                            public void onSuccess(String newPath) {
                                ImageViewUtil.displayImage(showPhotoCIV, newPath);
                                compressPhotoPath = newPath;
                            }
                            @Override
                            public void onFailed() {
                            }
                        });
                    } else {
                        String path = choosePhoto.handleImageBeforeKitKat(this, data);
                        ImageCompress.compressWithLuban(path);
                        imageCompress.setLubanListener(new ImageCompress.LubanListener() {
                            @Override
                            public void onSuccess(String newPath) {
                                ImageViewUtil.displayImage(showPhotoCIV, newPath);
                                compressPhotoPath = newPath;
                            }
                            @Override
                            public void onFailed() {
                            }
                        });
                    }
                }
                break;
            case TAKE_PHOTO:
                showFragmentFL.setVisibility(GONE);
                glassView.setVisibility(GONE);
                if (resultCode == RESULT_OK)
                    //Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    //压缩图片
                    ImageCompress.compressWithLuban(photoPath);
                imageCompress.setLubanListener(new ImageCompress.LubanListener() {
                    @Override
                    public void onSuccess(String newPath) {
                        ImageViewUtil.displayImage(showPhotoCIV, newPath);
                        compressPhotoPath = newPath;
                    }
                    @Override
                    public void onFailed() {
                    }
                });
                break;
            default:
                break;
        }
    }
}
