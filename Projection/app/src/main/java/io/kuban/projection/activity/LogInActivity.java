package io.kuban.projection.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.kuban.projection.BuildConfig;
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
    private boolean isJumpBack = true;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getIntent().getExtras()) {
            isJumpBack = getIntent().getExtras().getBoolean(Constants.IS_JUMP_BACK);
        }
        CustomerApplication.Isexit = false;
        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);
    }

    public void login(final String app_id, final String app_secret) {
        showProgressDialog();
        Call<List<ChooseModel>> createSessionCall = getKubanApi().getLocations(app_id, app_secret);
        createSessionCall.enqueue(new Callback<List<ChooseModel>>() {
            @Override
            public void onResponse(Call<List<ChooseModel>> call, Response<List<ChooseModel>> response) {

                if (null != response.body() && response.body().size() > 0) {
                    UserManager.saveUserObject(app_id, app_secret);
                    Constants.lListcChooseMode = response.body();
                    ActivityManager.startChooseActivity(LogInActivity.this, new Intent(), isJumpBack);
                    dismissProgressDialog();
                    finish();
                } else if (response.errorBody() != null) {
                    ErrorUtil.handleError(activity, response);
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
        if (!isNetworkConnected()) {
            ToastUtils.showShort(this, "网络异常请检查网络");
        }
        app_id = phone_num.getText().toString().trim();
        app_secret = sms_code.getText().toString().trim();
        if (!TextUtils.isEmpty(app_id)) {
            if (!TextUtils.isEmpty(app_secret)) {
                login(app_id, app_secret);
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