package com.example.forrestsu.logintest.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.forrestsu.logintest.MyApplication;
import com.example.forrestsu.logintest.MyUser;
import com.example.forrestsu.logintest.R;
import com.example.forrestsu.logintest.utils.ImageCompress;
import com.example.forrestsu.logintest.utils.ImageViewUtil;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.ref.WeakReference;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import static android.view.View.GONE;

public class VerifyIdentityActivity extends BaseActivity {

    private static final String TAG = "VerifyIdentityActivity";

    public static final int TAKE_PHOTO_1 = 1;
    public static final int TAKE_PHOTO_2 = 2;
    public static String IDENTITY_1 = "identity_image_1.jpg";
    public static String IDENTITY_2 = "identity_image_2.jpg";
    public static String COMPRESS_IDENTITY_1 = "compress_identity_image_1.jpeg";
    public static String COMPRESS_IDENTITY_2 = "compress_identity_image_2.jpeg";
    public static String VERIFY_SUCCESSFUL = "识别完成";

    private Button verifyBT;
    private ImageView showIdentity_1_IV;
    private ImageView showIdentity_2_IV;
    private EditText trueNameET;
    private EditText identityNumberET;
    private static ProgressBar verifyPB;

    //原图路径
    private String path_1, path_2;
    //压缩路径
    private String compress_path_1, compress_path_2;
    //压缩后正反面图片文件
    private File compress_file_1, compress_file_2;
    //用户真实姓名
    private String trueName;
    //用户身份证号码
    private String identityNumber;
    // 用户ocrKey
    private final String key = "";
    // 用户ocrSecret
    private final String secret = "";
    // 证件类型(二代证正面为"2",背面为"3"，身份核验为"3004"，详见文档说明)
    private final String typeId_1 = "2";
    private final String typeId_2 = "3";
    //(返回的格式可以是xml，也可以是json)
    private final String format = "xml";
    //private String format = "json";
    // http接口调用地址
    private final String url = "";
    //用户是否已验证
    static Boolean verifyIdentity;
    //当前用户
    private MyUser currentUser;
    private MyUser newUser;

    private ImageCompress imageCompress;

    private MyHandler mHandler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_identity);

        initView();
    }

    /*
      初始化
     */
    public void initView () {

        currentUser = BmobUser.getCurrentUser(MyUser.class);
        newUser = new MyUser();
        verifyIdentity = currentUser.getVerifyIdentity();

        verifyBT = (Button) findViewById(R.id.bt_verify);
        verifyBT.setOnClickListener(new MyOnClickListenr());
        showIdentity_1_IV = (ImageView) findViewById(R.id.iv_show_identity_1);
        showIdentity_1_IV.setOnClickListener(new MyOnClickListenr());
        showIdentity_2_IV = (ImageView) findViewById(R.id.iv_show_identity_2);
        showIdentity_2_IV.setOnClickListener(new MyOnClickListenr());

        trueNameET = (EditText) findViewById(R.id.et_true_name);
        trueNameET.setVisibility(View.GONE);
        identityNumberET = (EditText) findViewById(R.id.et_identity_number);
        identityNumberET.setVisibility(View.GONE);

        trueName = trueNameET.getText().toString();
        identityNumber = identityNumberET.getText().toString();

        verifyPB = (ProgressBar) findViewById(R.id.pb_verify);
        verifyPB.setVisibility(GONE);

        path_1 = getExternalCacheDir() + "/" + IDENTITY_1;
        path_2 = getExternalCacheDir() + "/" + IDENTITY_2;
        compress_path_1 = getExternalCacheDir() + "/" + COMPRESS_IDENTITY_1;
        compress_path_2 = getExternalCacheDir() + "/" + COMPRESS_IDENTITY_2;

        compress_file_1 = new File(compress_path_1);
        compress_file_2 = new File(compress_path_2);

        imageCompress = new ImageCompress();
    }

    public class MyOnClickListenr implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_show_identity_1:
                   requestCameraPermission(IDENTITY_1, TAKE_PHOTO_1);
                    break;
                case R.id.iv_show_identity_2:
                    requestCameraPermission(IDENTITY_2, TAKE_PHOTO_2);
                        break;
                case R.id.bt_verify:
                    verifyPB.setVisibility(View.VISIBLE);
                    Log.d("当前验证状态", verifyIdentity + "");
                    if (verifyIdentity) {
                        //如果已经验证过
                        Toast.makeText(VerifyIdentityActivity.this,
                                "您已验证过身份", Toast.LENGTH_LONG).show();
                        verifyPB.setVisibility(GONE);
                    } else { //验证身份
                        verify();
                    }
                    break;
                default:
                    break;
            }
        }

        /**
         * 请求相机权限,拍照
         */
        public void requestCameraPermission(String outPutImageName, int requestCode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(VerifyIdentityActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    VerifyIdentityActivity.this.requestPermissions(
                            new String[]{Manifest.permission.CAMERA}, 1);
                } else {
                    takePhoto(outPutImageName, requestCode);
                }
            } else {
                takePhoto(outPutImageName, requestCode);
            }
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
                imageUri = FileProvider.getUriForFile(VerifyIdentityActivity.this,
                        "com.example.forrestsu.logintest.fileprovider", outPutImage);
            } else {
                imageUri = Uri.fromFile(outPutImage);
            }
            //启动相机
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, requestCode);
        }
    }


    /**
     * onActivityResult
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //如果希望结果传回Fragment，必须加上下面这一句
        super.onActivityResult(requestCode, resultCode,data);
        Log.d("请求码是", requestCode + "");
        Log.d("返回码是", resultCode + "");
        switch (requestCode) {
            case TAKE_PHOTO_1:
                if (resultCode == RESULT_OK) {
                    //压缩图片
                    ImageCompress.compressWithLuban(path_1, compress_path_1);
                    imageCompress.setLubanListener(new ImageCompress.LubanListener() {
                        @Override
                        public void onSuccess(String newPath) {
                            ImageViewUtil.displayImage(showIdentity_1_IV, newPath);
                        }
                        @Override
                        public void onFailed() {
                        }
                    });
                }
                break;
            case TAKE_PHOTO_2:
                if (resultCode == RESULT_OK) {
                    //压缩图片
                    ImageCompress.compressWithLuban(path_2, compress_path_2);
                    imageCompress.setLubanListener(new ImageCompress.LubanListener() {
                        @Override
                        public void onSuccess(String newPath) {
                            ImageViewUtil.displayImage(showIdentity_2_IV, newPath);
                        }
                        @Override
                        public void onFailed() {
                        }
                    });
                }
                break;
            default:
                break;
        }
    }


    /**
     * 验证身份证真实性
     */
    public void verify() {
        // 判断目标文件是否存在
        if (!compress_file_1.exists() || !compress_file_2.exists()) {
            Toast.makeText(getApplicationContext(), "照片不存在", Toast.LENGTH_LONG).show();
            return;
        }
        //验证
        new Thread() {
            public void run() {
                String xmlData_1 = doPost(url, compress_file_1, key, secret, typeId_1, format);
                Log.d("json", xmlData_1);
                String xmlData_2 = doPost(url, compress_file_2, key, secret, typeId_2, format);
                Log.d("json", xmlData_2);
                String value_1 = parseXMLWithPull(xmlData_1);
                String value_2 = parseXMLWithPull(xmlData_2);

                if (value_1.equals(VERIFY_SUCCESSFUL) && value_2.equals(VERIFY_SUCCESSFUL)) {
                    verifyIdentity = true;
                    //更新用户信息
                    newUser.setVerifyIdentity(true);
                    newUser.update(currentUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Log.d("更新用户信息成功", "已将verifyIdentity设为true");
                            } else {
                                Log.d("更新用户信息失败", e.getMessage());
                            }
                        }
                    });
                    Message msg = new Message();
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                } else {

                    Message msg = new Message();
                    msg.what = 2;
                    mHandler.sendMessage(msg);

                }
            }
        }.start();
    }


    /**
     * 静态内部类MyHandler
     */
    private static class MyHandler extends Handler {
        private final WeakReference<VerifyIdentityActivity> mActivity;

        public MyHandler (VerifyIdentityActivity activity) {
            mActivity = new WeakReference<VerifyIdentityActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(mActivity.get() == null) {
                return;
            }
            switch (msg.what) {
                case 1:
                    Toast.makeText(mActivity.get(),
                            "验证成功", Toast.LENGTH_LONG).show();
                    verifyPB.setVisibility(GONE);
                    break;
                case 2:
                    verifyPB.setVisibility(GONE);
                    Toast.makeText(mActivity.get(),"验证失败", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 验证方法
     */
    public final String doPost(String url, File file, String key, String secret, String typeId, String format) {
        String result = "";
        Log.d("验证", "开始");
        try {

            HttpClient client = new DefaultHttpClient(); // 1.创建httpclient对象
            HttpPost post = new HttpPost(url); // 2.通过url创建post方法

            if ("json".equalsIgnoreCase(format)) {
                post.setHeader("accept", "application/json");
            } else if ("xml".equalsIgnoreCase(format) || "".equalsIgnoreCase(format)) {
                post.setHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            }

            // ***************************************<向post方法中封装实体>************************************//3.向post方法中封装实体
            /*
             * post方式实现文件上传则需要使用multipart/form-data类型表单，httpclient4.
             * 3以后需要使用MultipartEntityBuilder来封装 对应的html页面表单： <form name="input"
             * action="http://netocr.com/api/recog.do" method="post"
             * enctype="multipart/form-data"> 请选择要上传的文件<input type="file"
             * NAME="file"><br /> key:<input type="text" name="key"
             * value="W8Nh5AU2xsTYzaduwkzEuc" /> <br /> secret:<input
             * type="text" name="secret"
             * value="9646d012210a4ba48b3ba16737d6f69f" /><br /> typeId:<input
             * type="text" name="typeId" value="2"/><br /> format:<input
             * type="text" name="format" value=""/><br /> <input type="submit"
             * value="提交"> </form>
             */

            MultipartEntityBuilder builder = MultipartEntityBuilder.create(); // 实例化实体构造器
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE); // 设置浏览器兼容模式

            builder.addPart("file", new FileBody(file)); // 添加"file"字段及其值；此处注意字段名称必须是"file"
            builder.addPart("key", new StringBody(key, ContentType.create("text/plain", Consts.UTF_8))); // 添加"key"字段及其值
            builder.addPart("secret", new StringBody(secret, ContentType.create("text/plain", Consts.UTF_8))); // 添加"secret"字段及其值
            builder.addPart("typeId", new StringBody(typeId, ContentType.create("text/plain", Consts.UTF_8))); // 添加"typeId"字段及其值
            builder.addPart("format", new StringBody(format, ContentType.create("text/plain", Consts.UTF_8))); // 添加"format"字段及其值

            HttpEntity reqEntity = builder.setCharset(CharsetUtils.get("UTF-8")).build(); // 设置请求的编码格式，并构造实体

            post.setEntity(reqEntity);
            // **************************************</向post方法中封装实体>************************************

            HttpResponse response = client.execute(post); // 4.执行post方法，返回HttpResponse的对象
            if (response.getStatusLine().getStatusCode() == 200) { // 5.如果返回结果状态码为200，则读取响应实体response对象的实体内容，并封装成String对象返回
                result = EntityUtils.toString(response.getEntity(), "UTF-8");
                Message mes = new Message();
                mes.obj = result;
                mes.what = 3;
                mHandler.sendMessage(mes);
            } else {
                System.out.println("服务器返回异常");
                Toast.makeText(getApplicationContext(), "服务器返回异常", Toast.LENGTH_LONG).show();
                Message mes = new Message();
                mes.obj = "服务器返回异常";
                mes.what = 4;
                mHandler.sendMessage(mes);
            }

            try {
                HttpEntity e = response.getEntity(); // 6.关闭资源
                if (e != null) {
                    InputStream instream = e.getContent();
                    instream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                ((InputStream) response).close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        Log.d("result(返回结果)", result);
        // 7.返回识别结果
        return result;
    }

    /**
     * 解析xml（返回的身份证信息）
     */
    private String parseXMLWithPull(String xmlData) {
        String value = "";
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType = xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = xmlPullParser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG : {
                        if ("value".equals(nodeName)) {
                            value = xmlPullParser.nextText();
                        }
                        break;
                    }
                        case XmlPullParser.END_TAG :{
                            if ("message".equals(nodeName)) {
                                Log.d("解析结果", "value is:" + value);
                            }
                            break;
                        }
                    default:
                        break;

                    }
                    eventType = xmlPullParser.next();
                }
        } catch (Exception e) {
             e.printStackTrace();
        }
        return value;
    }

}
