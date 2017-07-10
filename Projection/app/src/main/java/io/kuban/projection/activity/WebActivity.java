package io.kuban.projection.activity;

import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.kuban.projection.CustomerApplication;
import io.kuban.projection.R;
import io.kuban.projection.base.ActivityManager;
import io.kuban.projection.model.UserModel;
import io.kuban.projection.service.AlwaysOnService.Bootstrap;
import io.kuban.projection.util.ErrorUtil;
import io.kuban.projection.util.ToastUtils;
import io.kuban.projection.util.Utils;
import io.kuban.projection.view.CustomDialog;
import io.kuban.projection.view.ProgressWebView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by wangxuan on 16/9/21.
 */
public class WebActivity extends BaseCompatActivity implements CustomDialog.GainSmsCodeListener {
    @BindView(R.id.web_view)
    ProgressWebView webView;
    @BindView(R.id.set_up)
    ImageView setUp;
    private String url;
    private CustomDialog dialog;
    private EditText number;
    private EditText number1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_activity);
        ButterKnife.bind(this);
        Bootstrap.startAlwaysOnService(this, "Main");
        url = getIntent().getStringExtra("url");
//        url = "https://www.baidu.com/";
        init();
        setListener();
    }

    private void setListener() {
        setUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                UserManager.getRemove();
                new HideClick().start();
                if (HideClick.sIsAlive >= 5) {
                    dialog = new CustomDialog(WebActivity.this, WebActivity.this);
                    number = (EditText) dialog.getEditText();//方法在CustomDialog中实现
                    number1 = (EditText) dialog.getNumber();
                    dialog.setOnPositiveListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm.isActive(number)) {
                                imm.hideSoftInputFromWindow(number.getWindowToken(), 0);
                            }
                            if (imm.isActive(number1)) {
                                imm.hideSoftInputFromWindow(number1.getWindowToken(), 0);
                            }
                            dialog.remove();
                        }
                    });
                    dialog.setOnNegativeListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (TextUtils.isEmpty(number.getText().toString().trim())) {
                                ToastUtils.showShort(WebActivity.this, "请输入手机号");
                                return;
                            }
                            if (TextUtils.isEmpty(number1.getText().toString().trim())) {
                                ToastUtils.showShort(WebActivity.this, "请输入验证码");
                                return;
                            }
                            showProgressDialog();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm.isActive(number)) {
                                imm.hideSoftInputFromWindow(number.getWindowToken(), 0);
                            }
                            if (imm.isActive(number1)) {
                                imm.hideSoftInputFromWindow(number1.getWindowToken(), 0);
                            }
                            if (!Utils.isFastDoubleClick()) {
                                sessions(number.getText().toString().trim(), number1.getText().toString().trim());
                            }
                        }
                    });
                    dialog.show();
                }

            }
        });

    }

    private void init() {
        Log.e("=====url===========", "" + url);
        //  设置数据
        webView.getSettings().setJavaScriptEnabled(true);
        // 设置支持javascript
        webView.getSettings().setJavaScriptEnabled(true);
        //启动缓存
        webView.getSettings().setAppCacheEnabled(true);
        //设置缓存模式
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        //提高渲染的优先级
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        // 开启 DOM storage API 功能
        webView.getSettings().setDomStorageEnabled(true);
        //加载网页
        webView.setWebViewClient(new SSLTolerentWebViewClient());
        webView.loadUrl(url);
//        //在当前的浏览器中响应
//        webView.setWebViewClient(new WebViewClient());//
//        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
//        webView.setWebViewClient(new WebViewClient() {
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
////                dismissProgressDialog();
//            }
//
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                super.onPageStarted(view, url, favicon);
////                showProgressDialog();
//            }
//
//            @Override
//            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//                super.onReceivedError(view, request, error);
//                ToastUtils.showShort(WebActivity.this, "同步失败，请稍候再试");
////                dismissProgressDialog();
//            }
//        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (CustomerApplication.Isexit) {
            finish();
            System.exit(0);//正常退出App
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }


    private class SSLTolerentWebViewClient extends WebViewClient {

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed(); // Ignore SSL certificate errors
        }

    }

    @Override
    public void gainSmsCodeListener() {
        if (TextUtils.isEmpty(number.getText().toString().trim())) {
            ToastUtils.showShort(this, "请填写手机号");
            return;
        }
        Call<ResponseBody> announcementsCall = getKubanApi().sendSmsCode(number.getText().toString().trim());
        announcementsCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody responsebody = response.body();
                try {
                    if (null != responsebody) {
                        String str = responsebody.string();
                        JSONObject jsonObject = new JSONObject(str);
                    } else {
                        ErrorUtil.handleError(WebActivity.this, response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ErrorUtil.handleError(WebActivity.this, t);
                Log.e("=========onFailure", " Throwable  " + t);
            }
        });
    }


    //管理员登录
    private void sessions(String phone_num, String login_sms_code) {
        //更新平板信息
        Map<String, String> queries = new HashMap<>();
        queries.put("storage", Utils.memorySize("available"));
        Call<UserModel> createSession = getKubanApi().sendSessions(phone_num, login_sms_code);
        createSession.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                dismissProgressDialog();
                if (null != response.body()) {
                    ActivityManager.startSetUpTheActivity(WebActivity.this, new Intent());
                    dialog.remove();
                } else {
                    ErrorUtil.handleError(WebActivity.this, response);
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                ErrorUtil.handleError(WebActivity.this, t);
                dismissProgressDialog();
            }
        });
    }

    static class HideClick extends Thread {
        public static volatile int sIsAlive = 1;

        @Override
        public void run() {
            sIsAlive++;
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (sIsAlive > 0) {
                sIsAlive--;
            }
            super.run();

        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (!isAppOnForeground() && CustomerApplication.isRestart) {
            ActivityManager.toLogInActivity(this, new Intent());
        }
    }

    /**
     * 程序是否在前台运行
     *
     * @return
     */
    public boolean isAppOnForeground() {

        android.app.ActivityManager activityManager = (android.app.ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();
        /**
         * 获取Android设备中所有正在运行的App
         */
        List<android.app.ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (android.app.ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }
}