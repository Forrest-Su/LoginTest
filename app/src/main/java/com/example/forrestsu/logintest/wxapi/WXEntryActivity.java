package com.example.forrestsu.logintest.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.forrestsu.logintest.activity.BaseActivity;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {
    private IWXAPI mWXApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWXApi = WXAPIFactory.createWXAPI(this, "");
        mWXApi.registerApp("");
        mWXApi.handleIntent(getIntent(), this);
    }

    /**
     * 如果分享的时候，该界面已经开启，那么微信开始这个Activity时会调用onNewIntent，
     * 所以这里要处理微信的返回结果
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mWXApi.handleIntent(intent, this);
    }

    /**
     * 微信发送请求到第三方应用时，会回调到该方法
     */
    @Override
    public void onReq(BaseReq baseReq) {

    }

    /**
     * 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
     */
    @Override
    public void onResp(BaseResp baseResp) {
        String result;
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK://发送成功
                result = "发送成功" ;
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL://发送取消
                result = "发送取消";
                break;
            case BaseResp.ErrCode.ERR_SENT_FAILED://发送失败
                result = "发送失败";
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED://发送被拒绝
                result = "发送被拒绝";
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT://不支持错误
                result = "不支持错误";
                break;
            case BaseResp.ErrCode.ERR_COMM://一般错误
                result = "一般错误";
                break;
            default:
                result = "不明错误";
                break;
        }

        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        this.finish();
    }
}
