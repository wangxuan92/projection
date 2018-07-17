package io.kuban.projection.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.kuban.projection.CustomerApplication;
import io.kuban.projection.R;
import io.kuban.projection.model.TabletInformationModel;
import io.kuban.projection.service.AlwaysOnService.Bootstrap;
import io.kuban.projection.util.CheckUpdate;
import io.kuban.projection.util.ErrorUtil;
import io.kuban.projection.util.UpdateUtil;
import io.kuban.projection.util.UserManager;
import io.kuban.projection.view.InputNameDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 设置界面
 * Created by wangxuan on 16/10/26.
 */
public class SetUpTheActivity extends BaseCompatActivity implements CheckUpdate.Cancel {
    @BindView(R.id.version_update)
    RelativeLayout versionUpdate;
    @BindView(R.id.exit_program)
    RelativeLayout exitProgram;
    @BindView(R.id.sho_version)
    TextView shoVersion;
    @BindView(R.id.prompt)
    TextView prompt;
    private TabletInformationModel tabletInformationModel;
    private InputNameDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_up_the);
        ButterKnife.bind(this);
        String app_id = UserManager.getAppId();
        String app_secret = UserManager.getAppSecret();
        http(app_id, app_secret);
        shoVersion.setText("当前版本:" + UpdateUtil.getVersionName(SetUpTheActivity.this));
    }

    @OnClick({R.id.btn_back, R.id.version_update, R.id.exit_program})
    public void returnClick(View view) {
        switch (view.getId()) {
//            case R.id.switch_theme:
//                if (!Utils.isFastDoubleClick()) {
////                    ActivityManager.startSwitchThemeActivity(SetUpTheActivity.this, new Intent());
//                    SetUpTheActivity.this.finish();
//                }
//
//                break;
            case R.id.version_update:
                if (tabletInformationModel != null) {
                    //检测版本
                    CheckUpdate checkUpdate = new CheckUpdate(SetUpTheActivity.this, tabletInformationModel);
                    checkUpdate.setCancel(SetUpTheActivity.this);
                }


                break;
            case R.id.exit_program:
                dialog = new InputNameDialog(SetUpTheActivity.this, "是否退出应用");
                dialog.setOnPositiveListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.remove();
                    }
                });
                dialog.setOnNegativeListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.remove();
                        CustomerApplication.Isexit = true;
                        CustomerApplication.isRestart = false;
                        Bootstrap.stopAlwaysOnService(SetUpTheActivity.this);
                        finish();
                    }
                });
                dialog.show();
                break;
            case R.id.btn_back:
                finish();
                break;
        }
    }

    /**
     * 查询平板信息
     *
     * @param app_id
     * @param app_secret
     */
    public void http(final String app_id, final String app_secret) {
        Call<TabletInformationModel> createSessionCall = getKubanApi().getTabletInformation(CustomerApplication.device_id, app_id, app_secret);
        createSessionCall.enqueue(new Callback<TabletInformationModel>() {
            @Override
            public void onResponse(Call<TabletInformationModel> call, Response<TabletInformationModel> response) {
                if (null != response.body()) {
                    if (!TextUtils.isEmpty(response.body().id)) {
                        tabletInformationModel = response.body();
                        if (UpdateUtil.checkUpdate(SetUpTheActivity.this)) {
                            prompt.setVisibility(View.VISIBLE);
                            shoVersion.setText("可升级版本:" + tabletInformationModel.app_version);
                        } else {
                            prompt.setVisibility(View.GONE);
                            shoVersion.setText("当前版本:" + UpdateUtil.getVersionName(SetUpTheActivity.this));
                        }
                        return;
                    } else {
                    }
                } else {
                    ErrorUtil.handleError(SetUpTheActivity.this, response);
                }
            }

            @Override
            public void onFailure(Call<TabletInformationModel> call, Throwable t) {
                ErrorUtil.handleError(SetUpTheActivity.this, t);
            }
        });
    }


    @Override
    public void updateFinish() {
        finish();
    }

    @Override
    public void update() {
//        Intent intent = new Intent();
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setAction(android.content.Intent.ACTION_VIEW);
//        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), CheckUpdate.APK_FILENAME)),
//                "application/vnd.android.package-archive");
//        startActivity(intent);

//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), CheckUpdate.APK_FILENAME)),
//                "application/vnd.android.package-archive");
//        startActivity(intent);
    }
}
