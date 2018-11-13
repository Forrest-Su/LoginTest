package com.example.forrestsu.logintest.fragment;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.forrestsu.logintest.R;
import com.example.forrestsu.logintest.utils.ImageViewUtil;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMusicObject;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;


public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    public static final int CHOOSE_PHOTO = 0;
    private static final int TYPE_TEXT = 1;
    private static final int TYPE_IMAGE = 2;
    private static final int TYPE_MUSIC = 3;
    private int shareType;

    private String imagePath;
    private String newPath;

    private static final String APP_ID = "w";
    private IWXAPI mIWXAPI;

    private View glassView;
    private ConstraintLayout shareCL;
    private EditText shareET, shareMusicET;
    private ImageView shareIV;

    //构造Req
    private SendMessageToWX.Req reqText = new SendMessageToWX.Req();
    private SendMessageToWX.Req reqImage = new SendMessageToWX.Req();
    private SendMessageToWX.Req reqMusic  = new SendMessageToWX.Req();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInsatnceState) {
        View square = inflater.inflate(R.layout.fragment_home, container, false);

        //注册到微信终端
        mIWXAPI = WXAPIFactory.createWXAPI(getContext(), APP_ID);
        mIWXAPI.registerApp(APP_ID);

        initView(square);
        return square;
    }

    public void initView(View square) {
        glassView = (View) square.findViewById(R.id.view_glass);
        glassView.setOnClickListener(new MyOnClickListener());
        glassView.setVisibility(GONE);
        shareCL = (ConstraintLayout) square.findViewById(R.id.cl_share);
        shareCL.setVisibility(GONE);

        Button shareTextBT = (Button) square.findViewById(R.id.bt_share_text);
        shareTextBT.setOnClickListener(new MyOnClickListener());
        Button shareImageBT = (Button) square.findViewById(R.id.bt_share_image);
        shareImageBT.setOnClickListener(new MyOnClickListener());
        Button shareMusicBT = (Button) square.findViewById(R.id.bt_share_music);
        shareMusicBT.setOnClickListener(new MyOnClickListener());

        ImageView toCircleOfFriendsIV = (ImageView) square.findViewById(R.id.iv_circle_friends);
        toCircleOfFriendsIV.setOnClickListener(new MyOnClickListener());
        ImageView toWeChatIV = (ImageView) square.findViewById(R.id.iv_wecaht);
        toWeChatIV.setOnClickListener(new MyOnClickListener());

        shareET = (EditText) square.findViewById(R.id.et_share_text);
        shareIV = (ImageView) square.findViewById(R.id.iv_share_image);
        shareIV.setOnClickListener(new MyOnClickListener());
        shareMusicET = (EditText) square.findViewById(R.id.et_share_music);
    }

    public class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.view_glass:
                    hideGlass();
                    break;
                case R.id.bt_share_text:
                    shareType = TYPE_TEXT;
                    if (!TextUtils.isEmpty(shareET.getText())) {
                        glassView.setVisibility(View.VISIBLE);
                        shareCL.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(getContext(), "内容不能为空", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.bt_share_image:
                    shareType = TYPE_IMAGE;
                    glassView.setVisibility(View.VISIBLE);
                    shareCL.setVisibility(View.VISIBLE);
                    break;
                case R.id.bt_share_music:
                    shareType = TYPE_MUSIC;
                    Log.d(TAG, "shareType:" + TYPE_MUSIC + "音乐类型");
                    if (!TextUtils.isEmpty(shareMusicET.getText())) {
                        glassView.setVisibility(View.VISIBLE);
                        shareCL.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(getContext(), "内容不能为空", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.iv_share_image:
                    //从相册选择图片
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    } else {
                        openAlbum();
                    }
                    break;
                case R.id.iv_circle_friends:
                    if ( isWeChatInstalled()) {
                        if (shareType == TYPE_TEXT) {
                            shareText(shareET.getText().toString());
                            reqText.scene = SendMessageToWX.Req.WXSceneTimeline;
                            mIWXAPI.sendReq(reqText);
                        }
                        if (shareType == TYPE_IMAGE) {
                            shareImage(newPath);
                            reqImage.scene = SendMessageToWX.Req.WXSceneTimeline;
                            mIWXAPI.sendReq(reqImage);
                        }
                        if (shareType == TYPE_MUSIC) {
                            shareMusic(shareMusicET.getText().toString());
                            reqMusic.scene = SendMessageToWX.Req.WXSceneTimeline;
                            mIWXAPI.sendReq(reqMusic);
                        }
                    } else {
                        Toast.makeText(getContext(), "请先安装微信", Toast.LENGTH_SHORT).show();
                    }
                    hideGlass();
                    break;
                case R.id.iv_wecaht:
                    Log.d(TAG, "点击分享到微信");
                    if (isWeChatInstalled()) {
                        if (shareType == TYPE_TEXT) {
                            shareText(shareET.getText().toString());
                            reqText.scene = SendMessageToWX.Req.WXSceneSession;
                            mIWXAPI.sendReq(reqText);
                        }
                        if (shareType == TYPE_IMAGE) {
                            shareImage(newPath);
                            reqImage.scene = SendMessageToWX.Req.WXSceneSession;
                            mIWXAPI.sendReq(reqImage);
                        }
                        if (shareType == TYPE_MUSIC) {
                            Log.d(TAG, "判断：类型为 " + TYPE_MUSIC + "音乐");
                            shareMusic(shareMusicET.getText().toString());
                            Log.d(TAG, "音乐URL：" + shareMusicET.getText().toString());
                            reqMusic.scene = SendMessageToWX.Req.WXSceneSession;
                            mIWXAPI.sendReq(reqMusic);
                        }
                    } else {
                        Toast.makeText(getContext(), "请先安装微信", Toast.LENGTH_SHORT).show();
                    }
                    hideGlass();
                    break;
                default:
                    break;
            }
        }
    }

    public void shareText(String text) {
        //初始化WXTextObject对象，传入text
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;

        //初始化WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = text;

        //唯一标识一个请求
        reqText.transaction = String.valueOf(System.currentTimeMillis());
        reqText.message = msg;
    }

    public void shareImage(String imagePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        //初始化WXTextObject对象，传入text
        WXImageObject imageObj = new WXImageObject(bitmap);
        //初始化WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imageObj;
        //设置缩略图
        Bitmap thumbBitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, true);
        bitmap.recycle();
        msg.thumbData = bmpToByteArray(thumbBitmap, true);
        //唯一标识一个请求
        reqImage.transaction = String.valueOf(System.currentTimeMillis());
        reqImage.message = msg;
    }

    public void shareMusic(String url) {
        WXMusicObject musicObj = new WXMusicObject();
        musicObj.musicUrl = url;
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = musicObj;
        //音乐标题
        msg.title = "111111111";
        //音乐描述
        msg.description="111";
        //音乐缩略图
        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.baseline_music_note_black_48dp);
        //Bitmap thumbBitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, true);
        //bitmap.recycle();
        Bitmap thumbBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.baseline_music_note_black_48dp);
        msg.thumbData = bmpToByteArray(thumbBitmap, true);
        //构造Req
        reqMusic.transaction = String.valueOf(System.currentTimeMillis());
        reqMusic.message = msg;
    }

    /*
    检查是否安装微信
     */
    public Boolean isWeChatInstalled() {
        try {
            mIWXAPI.isWXAppInstalled();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void hideGlass() {
        glassView.setVisibility(GONE);
        shareCL.setVisibility(GONE);
    }


    /**
     *打开相册
     */
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permission,
                                           int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(getContext(), "You denied the permission",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //如果希望结果传回Fragment，必须加上下面这一句
        super.onActivityResult(requestCode, resultCode,data);

        Log.d("请求码是", requestCode + "");
        Log.d("返回码是", resultCode + "");
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //判断手机版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        imagePath = handleImageOnKitKat(data);
                        compressWithLuban(imagePath);
                    } else {
                        imagePath = handleImageBeforeKitKat(data);
                        compressWithLuban(imagePath);
                    }
                }
                break;
            default:
                break;
        }
    }

    /*
    Android_4.4以上系统
     */
    private String handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(getContext(), uri)) {
            //如果是document类型的uri，通过document id来处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                //解析出数字格式id
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.document".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse(
                        "content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的uri，则用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file类型的uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        return imagePath;
    }

    /*
    Android_4.4以下的系统
     */
    private String handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        return imagePath;
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取图片的真实路径
        Cursor cursor = getActivity().getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    /**
     * Luban压缩
     * file是要压缩的文件
     */
    public void compressWithLuban(String path) {
        Luban.with(getContext())
                .load(path)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        Log.d("Luban", "onStart: 开始压缩");
                    }

                    @Override
                    public void onSuccess(File file) {
                        Log.d("Luban", "onSuccess: 压缩成功");
                        newPath = file.getAbsolutePath();
                        ImageViewUtil.displayImage(shareIV, newPath);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Luban", "onError: 压缩失败");
                    }
                }).launch();
    }


    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }



}
