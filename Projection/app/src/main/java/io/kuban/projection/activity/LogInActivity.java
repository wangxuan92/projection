package io.kuban.projection.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.kuban.projection.CustomerApplication;
import io.kuban.projection.R;
import io.kuban.projection.base.ActivityManager;
import io.kuban.projection.base.Constants;
import io.kuban.projection.model.ChooseModel;
import io.kuban.projection.model.TabletInformationModel;
import io.kuban.projection.util.ErrorUtil;
import io.kuban.projection.util.ToastUtils;
import io.kuban.projection.util.UserManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 登录界面
 * <p/>
 * Created by wangxuan on 16/10/28.
 */
public class LogInActivity extends BaseCompatActivity {
    @BindView(R.id.phone_num)
    EditText phone_num;
    @BindView(R.id.sms_code)
    EditText sms_code;

    private String app_id;
    private String app_secret;
    private String meeting_rooms_id = "";
    private TabletInformationModel tabletInformationModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tabletInformationModel = cache.getObject(CustomerApplication.TABLETINFORMATION, TabletInformationModel.class);
        if (tabletInformationModel != null) {
            meeting_rooms_id = tabletInformationModel.space_id;
        }
        CustomerApplication.Isexit = false;
        if (!TextUtils.isEmpty(meeting_rooms_id)) {
            http(UserManager.getAppId(), UserManager.getAppSecret(), false);
        }
        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);
    }

    /**
     * 查询该设备是否已存在
     *
     * @param app_id
     * @param app_secret
     */
    public void http(final String app_id, final String app_secret, final boolean isLogIn) {
        showProgressDialog();
        Call<TabletInformationModel> createSessionCall = getKubanApi().getTabletInformation(CustomerApplication.device_id, app_id, app_secret);
        createSessionCall.enqueue(new Callback<TabletInformationModel>() {
            @Override
            public void onResponse(Call<TabletInformationModel> call, Response<TabletInformationModel> response) {
                if (null != response.body()) {
                    if (!TextUtils.isEmpty(response.body().id)) {
                        TabletInformationModel tabletInformationModel = response.body();
                        cache.put(CustomerApplication.TABLETINFORMATION, tabletInformationModel);
                        UserManager.saveUserObject(app_id, app_secret);
                        StringBuffer url = new StringBuffer();
                        url.append(Constants.URL);
                        url.append(tabletInformationModel.area_id);
                        ActivityManager.startXWalkViewActivity(LogInActivity.this, new Intent(), url.toString());
                        finish();
                        dismissProgressDialog();
                        return;
                    } else {
                        if (isLogIn) {
                            login(app_id, app_secret);
                        } else {
                            dismissProgressDialog();
                        }
                    }
                } else {
                    ErrorUtil.handleError(activity, response);
                    dismissProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<TabletInformationModel> call, Throwable t) {
                Log.e(LOG_TAG, "查询该设备是否已存在  " + call + "失败" + t);
                ErrorUtil.handleError(activity, t);
            }
        });
    }

    public void login(final String app_id, final String app_secret) {
        showProgressDialog();
        Call<List<ChooseModel>> createSessionCall = getKubanApi().getLocations(app_id, app_secret);
        createSessionCall.enqueue(new Callback<List<ChooseModel>>() {
            @Override
            public void onResponse(Call<List<ChooseModel>> call, Response<List<ChooseModel>> response) {

                if (null != response.body() && response.body().size() > 0) {
                    UserManager.saveUserObject(app_id, app_secret);
                    CustomerApplication.lListcChooseMode = response.body();
                    ActivityManager.startChooseActivity(LogInActivity.this, new Intent());
                    dismissProgressDialog();
                    LogInActivity.this.finish();
                } else if (response.errorBody() != null) {
                    try {
                        String str = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(str);
                        JSONObject jsonObject1 = (JSONObject) jsonObject.get("_error");
                        ToastUtils.customShort(LogInActivity.this, jsonObject1.getString("message"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<List<ChooseModel>> call, Throwable t) {
                Log.e(LOG_TAG, call + "登录失败" + t);
                ErrorUtil.handleError(activity, t);
                dismissProgressDialog();
            }
        });
    }

    @OnClick(R.id.login_button)
    public void login(View view) {
//        ActivityManager.startWebActivity(LogInActivity.this, new Intent(), "https://192.168.2.67:8443/screensharingtest2.html", "测试");
        if (!isNetworkConnected()) {
            ToastUtils.showShort(this, "网络异常请检查网络");
        }
        app_id = phone_num.getText().toString().trim();
        app_secret = sms_code.getText().toString().trim();
        if (!TextUtils.isEmpty(app_id)) {
            if (!TextUtils.isEmpty(app_secret)) {
                showProgressDialog();
                http(app_id, app_secret, true);
            } else {
                ToastUtils.customShort(LogInActivity.this, "请输入密码");
            }
        } else {
            ToastUtils.customShort(LogInActivity.this, "请输入账号");
        }
    }

    @OnClick(R.id.exit_img)
    public void exit(View view) {
        finish();
        System.exit(0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            System.exit(0);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}