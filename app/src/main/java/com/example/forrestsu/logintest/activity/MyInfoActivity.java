package com.example.forrestsu.logintest.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.forrestsu.logintest.MyUser;
import com.example.forrestsu.logintest.R;
import com.example.forrestsu.logintest.fragment.SetAddressFragment;
import com.example.forrestsu.logintest.fragment.SetDateFragment;
import com.example.forrestsu.logintest.fragment.SetSexFragment;
import com.example.forrestsu.logintest.utils.BmobDownload;
import com.example.forrestsu.logintest.utils.ChoosePhoto;
import com.example.forrestsu.logintest.utils.ImageCompress;
import com.example.forrestsu.logintest.utils.ImageViewUtil;

import java.io.File;
import java.io.IOException;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MyInfoActivity extends BaseActivity implements SetSexFragment.SendMessageSex,
        SetDateFragment.SendMessageDate, SetAddressFragment.SendMessageAddress {

    private static final String TAG = "MyInfoActivity";

    public static final int CHOOSE_PHOTO = 1;
    public static final int TAKE_PHOTO = 2;
    //照片名称
    private static final String PHOTO_NAME = "photo_head.jpg";
    //照片路径
    private String photoPath;
    //头像路径
    private String headPath;

    private FrameLayout showFragmentFL;
    private Fragment currentFragment, setBirthFragment, setSexFragment, setAddressFragment;
    private static CircleImageView headCIV;
    private View glassView;
    private LinearLayout choosePhotoLL;
    private TextView showBirthTV, showSexTV, showAreaTV;
    private EditText specificAddressET, nicknameET;

    private MyUser currentUser;
    private MyUser newUser;

    private ImageCompress imageCompress;
    private ChoosePhoto choosePhoto;

    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);

        initView();
        //加载用户信息
        loadUserInfo();

        //传值给SetDateFragment
        bundle = new Bundle();
        bundle.putString("year_limit", "1900");
    }

    /*
    初始化
     */
    public void initView() {
        photoPath = getExternalCacheDir() + "/" + PHOTO_NAME;

        imageCompress = new ImageCompress();
        choosePhoto = new ChoosePhoto();

        showFragmentFL = (FrameLayout) findViewById(R.id.fl_show_fragment);
        showFragmentFL.setVisibility(GONE);
        currentFragment = new Fragment();

        Button saveBT = (Button) findViewById(R.id.bt_save);
        saveBT.setOnClickListener(new MyOnClickListener());
        headCIV = (CircleImageView) findViewById(R.id.civ_head);
        headCIV.setOnClickListener(new MyOnClickListener());

        glassView = (View) findViewById(R.id.view_glass);
        glassView.setOnClickListener(new MyOnClickListener());
        glassView.setVisibility(GONE);

        choosePhotoLL = (LinearLayout) findViewById(R.id.ll_choosePhoto);
        choosePhotoLL.setVisibility(GONE);
        TextView choosePhotoTV = (TextView) findViewById(R.id.tv_choosePhoto);
        choosePhotoTV.setOnClickListener(new MyOnClickListener());
        TextView takePhotoTV = (TextView) findViewById(R.id.tv_takePhoto);
        takePhotoTV.setOnClickListener(new MyOnClickListener());

        View nameView = (View) findViewById(R.id.view_name);
        nameView.setOnClickListener(new MyOnClickListener());
        View birthView = (View) findViewById(R.id.view_birthday);
        birthView.setOnClickListener(new MyOnClickListener());
        View sexView = (View) findViewById(R.id.view_sex);
        sexView.setOnClickListener(new MyOnClickListener());
        View areaView = (View) findViewById(R.id.view_area);
        areaView.setOnClickListener(new MyOnClickListener());

        nicknameET = (EditText) findViewById(R.id.et_nickname);
        showBirthTV = (TextView) findViewById(R.id.et_age);
        showSexTV = (TextView) findViewById(R.id.tv_show_sex);
        showAreaTV = (TextView) findViewById(R.id.tv_show_area);
        specificAddressET = (EditText) findViewById(R.id.et_specific_address);

        currentUser = BmobUser.getCurrentUser(MyUser.class);
        newUser = new MyUser();
    }

    /*
    Click监听
     */
    public class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.view_glass:
                    glassView.setVisibility(GONE);
                    showFragmentFL.setVisibility(GONE);
                    choosePhotoLL.setVisibility(GONE);
                    break;
                case R.id.bt_save:
                    //上传个人信息
                    if (TextUtils.isEmpty(nicknameET.getText())
                            || TextUtils.isEmpty(showBirthTV.getText())
                            || TextUtils.isEmpty(showSexTV.getText())) {
                        Toast.makeText(MyInfoActivity.this, "请完善个人信息", Toast.LENGTH_SHORT).show();
                    } else{
                        saveInfo();
                    }
                    saveInfo();
                    break;
                case R.id.civ_head:
                    if (choosePhotoLL.getVisibility() == GONE) {
                        glassView.setVisibility(VISIBLE);
                        choosePhotoLL.setVisibility(VISIBLE);
                    }
                    break;
                case R.id.tv_choosePhoto:
                    choosePhotoLL.setVisibility(GONE);
                    glassView.setVisibility(GONE);
                    //检查权限，从相册选择图片
                    if (ContextCompat.checkSelfPermission(MyInfoActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MyInfoActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CHOOSE_PHOTO);
                    } else {
                        openAlbum();
                    }
                    break;
                case R.id.tv_takePhoto:
                    choosePhotoLL.setVisibility(GONE);
                    glassView.setVisibility(GONE);
                    //检查权限，拍照
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(MyInfoActivity.this,
                                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            MyInfoActivity.this.requestPermissions(
                                    new String[]{Manifest.permission.CAMERA}, 1);
                        } else {
                            takePhoto(PHOTO_NAME, TAKE_PHOTO);
                        }
                    } else {
                        takePhoto(PHOTO_NAME, TAKE_PHOTO);
                    }
                    break;
                case R.id.view_birthday:
                    if (setBirthFragment == null) {
                        setBirthFragment = new SetDateFragment();
                        setBirthFragment.setArguments(bundle);
                    }
                    addOrShowFragment(getSupportFragmentManager().beginTransaction(), setBirthFragment);
                    glassView.setVisibility(VISIBLE);
                    showFragmentFL.setVisibility(VISIBLE);
                    break;
                case R.id.view_sex:
                    if (setSexFragment == null) {
                        setSexFragment = new SetSexFragment();
                    }
                    addOrShowFragment(getSupportFragmentManager().beginTransaction(), setSexFragment);
                    glassView.setVisibility(VISIBLE);
                    showFragmentFL.setVisibility(VISIBLE);
                    break;
                case R.id.view_area:
                    if (setAddressFragment == null) {
                        setAddressFragment = new SetAddressFragment();
                    }
                    addOrShowFragment(getSupportFragmentManager().beginTransaction(), setAddressFragment);
                    glassView.setVisibility(VISIBLE);
                    showFragmentFL.setVisibility(VISIBLE);
                    break;
                default:
                    break;
            }
        }
    }

    /*
    添加或显示Fragment
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
     * 保存用户信息
     */
    public void saveInfo() {

        final ProgressDialog progressDialog = new ProgressDialog(MyInfoActivity.this);
        progressDialog.setTitle("");
        progressDialog.setMessage("正在上传...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        newUser.setNickname(nicknameET.getText().toString());
        newUser.setBirthday(showBirthTV.getText().toString());
        newUser.setSex(showSexTV.getText().toString());
        newUser.setSpecificAddress(specificAddressET.getText().toString());

        /*
        初始的headPath为null,如果设置了头像，headPath就会被赋值，保存用户信息时就应该将新头像上传；
        否则如果没有设置头像，headPath就会为null，保存用户信息时就不需要上传头像了。
         */
        new Thread() {
            public void run() {
                if (headPath != null) {
                    Log.d(TAG, "headPath不为空");
                    final BmobFile headFile = new BmobFile(new File(headPath));
                    headFile.uploadblock(new UploadFileListener() {

                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                String url = headFile.getFileUrl();
                                newUser.setHeadUrl(url);
                                newUser.setHead(headFile);
                                newUser.update(currentUser.getObjectId(), new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            progressDialog.dismiss();
                                            Toast.makeText(MyInfoActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "上传成功" );
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(MyInfoActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "上传失败：" + e.getMessage() + "," + e.getErrorCode());
                                        }
                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(MyInfoActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "上传失败：" + e.getMessage() + "," + e.getErrorCode());
                            }
                        }
                    });
                } else {
                    Log.d(TAG, "headPath为空");
                    newUser.update(currentUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                progressDialog.dismiss();
                                Toast.makeText(MyInfoActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "上传成功");
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(MyInfoActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "上传失败：" + e.getMessage() + "," + e.getErrorCode());
                            }
                        }
                    });
                }
            }
        }.start();
    }


    /*
    加载用户信息
     */
    private void loadUserInfo() {
        Log.d(TAG, "loadUserInfo: ：" + currentUser.getNickname());
        Log.d(TAG, "loadUserInfo: ：" + currentUser.getBirthday());
        Log.d(TAG, "loadUserInfo: ：" + currentUser.getProvince());
        Log.d(TAG, "loadUserInfo: ：" + currentUser.getCity());
        Log.d(TAG, "loadUserInfo: ：" + currentUser.getArea());
        Log.d(TAG, "loadUserInfo: ：" + currentUser.getSpecificAddress());
        Log.d(TAG, "loadUserInfo: 验证：" + currentUser.getVerifyIdentity());
        if (currentUser.getNickname() != null) {
            nicknameET.setText(currentUser.getNickname());
        }
        if (currentUser.getBirthday() != null) {
            showBirthTV.setText(currentUser.getBirthday());
        }
        if (currentUser.getSex() != null) {
            showSexTV.setText(currentUser.getSex());
        }
        String province = currentUser.getProvince();
        String city = currentUser.getCity();
        String area = currentUser.getArea();
        if (province != null && city != null && area != null) {
            if (province.equals("台湾") || province.equals("澳门")
                    || province.equals("香港") || province.equals("北京")
                    || province.equals("天津") || province.equals("上海")
                    || province.equals("重庆") || province.equals("钓鱼岛")) {
                showAreaTV.setText((city + area));
            } else {
                showAreaTV.setText((province + city + area));
            }
        }
        if (currentUser.getSpecificAddress() != null) {
            specificAddressET.setText(currentUser.getSpecificAddress());
        }

        String url = currentUser.getHeadUrl();
        if (url != null && !url.equals("")) {
            //检查远程文件在是否在本地已存在，如果存在，直接显示，否则调用下载
            //存储路径
            final String photoSavePath = getExternalCacheDir() + "/";
            //获取远程文件名
            final String photoName = url.substring(url.lastIndexOf("/") + 1);
            Log.d(TAG, "onSuccess: 远程文件名：" + photoName);
            if (new File(photoSavePath + photoName).exists()) {
                Log.d(TAG, "文件不变，直接显示 ");
                ImageViewUtil.displayImage(headCIV, photoSavePath + photoName);
            } else {
                Log.d(TAG, "文件有变，调用下载");

                final BmobFile bmobFile = new BmobFile(photoName, "", url);
                final BmobDownload bmobDownload = new BmobDownload();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        bmobDownload.downloadBmobFile(bmobFile, photoSavePath, photoName);
                    }
                }).start();

                bmobDownload.setBmobDownloadListener(new BmobDownload.BmobDownloadListener() {
                    @Override
                    public void onSuccess() {
                        ImageViewUtil.displayImage(headCIV, photoSavePath + photoName);
                    }

                    @Override
                    public void onFailed() {
                        //
                    }
                });
            }
        }
    }



    /**
     *打开相册
     */
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    /**
     *拍照
     */
    private void takePhoto(String outPutImageName, int requestCode) {
        Uri imageUri;
        File outPutImage = new File(getExternalCacheDir(), outPutImageName);
        try {
            if (outPutImage.exists()) {
                outPutImage.delete();
            }
            outPutImage.createNewFile();
        } catch (IOException e){
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            imageUri = FileProvider.getUriForFile(MyInfoActivity.this,
                    "com.example.forrestsu.logintest.fileprovider", outPutImage);
        } else {
            imageUri = Uri.fromFile(outPutImage);
        }
        //启动相机
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, requestCode);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permission, int[] grantResults) {
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(MyInfoActivity.this, "You denied the permission",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case TAKE_PHOTO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto(PHOTO_NAME, requestCode);
                } else {
                    Toast.makeText(MyInfoActivity.this, "You denied the permission",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("请求码是", requestCode + "");
        Log.d("返回码是", resultCode + "");
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //判断手机版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        String path = choosePhoto.handleImageOnKitKat(this, data);
                        ImageCompress.compressWithLuban(path);
                        imageCompress.setLubanListener(new ImageCompress.LubanListener() {
                            @Override
                            public void onSuccess(String newPath) {
                                ImageViewUtil.displayImage(headCIV, newPath);
                                headPath = newPath;
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
                                ImageViewUtil.displayImage(headCIV, newPath);
                                headPath = newPath;
                            }

                            @Override
                            public void onFailed() {

                            }
                        });
                    }
                }
                break;
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK)
                        //Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        //压缩图片
                        ImageCompress.compressWithLuban(photoPath);
                        imageCompress.setLubanListener(new ImageCompress.LubanListener() {
                            @Override
                            public void onSuccess(String newPath) {
                                ImageViewUtil.displayImage(headCIV, newPath);
                                headPath = newPath;
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

    /*
   实现SetSexFragment的SendMessageSex接口的回调方法
    */
    @Override
    public void getMessageSex(String sex) {
        glassView.setVisibility(GONE);
        showFragmentFL.setVisibility(GONE);
        showSexTV.setText(sex);
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
    public void getMessageDate(String birth) {
        glassView.setVisibility(GONE);
        showFragmentFL.setVisibility(GONE);
        showBirthTV.setText(birth);
    }

    /*
   实现SetAddresshFragment的SendMessageAddress接口的回调方法
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
            showAreaTV.setText((city + area));
        } else {
            showAreaTV.setText((province + city + area));
        }
        newUser.setProvince(province);
        newUser.setCity(city);
        newUser.setArea(area);
        glassView.setVisibility(GONE);
        showFragmentFL.setVisibility(GONE);
    }

}

