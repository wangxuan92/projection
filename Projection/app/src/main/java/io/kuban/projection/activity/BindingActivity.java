package io.kuban.projection.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.yanzhenjie.permission.AndPermission;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.kuban.projection.CustomerApplication;
import io.kuban.projection.R;
import io.kuban.projection.base.ActivityManager;
import io.kuban.projection.event.BindingResultsEvent;
import io.kuban.projection.base.Constants;
import io.kuban.projection.model.PadsModel;
import io.kuban.projection.model.ToKenModel;
import io.kuban.projection.util.EquipmentInformationUtil;
import io.kuban.projection.util.ErrorUtil;
import io.kuban.projection.util.StringUtil;
import io.kuban.projection.view.LetterSpacingTextView;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by wangxuan on 17/11/15.
 */

public class BindingActivity extends BaseCompatActivity {
    @BindView(R.id.validate_code)
    LetterSpacingTextView mValidateCode;
    @BindView(R.id.refresh)
    Button refresh;

    private int validateCode;
    private CountDownTimer timer;
    private PadsModel padsModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.binding_activity);
        ButterKnife.bind(this);
//        CustomerApplication.token = AESCipher.decrypt(cache.getAsString(Constants.TO_KEN));
        CustomerApplication.token = cache.getAsString(Constants.TO_KEN);
//        CustomerApplication.appPassword = cache.getAsString(Constants.APP_PASSWORD);
//        if (TextUtils.isEmpty(CustomerApplication.appPassword)) {
//            CustomerApplication.appPassword = "12345";
//            cache.put(Constants.APP_PASSWORD, CustomerApplication.appPassword);
//        }
        EventBus.getDefault().register(this);
        requestAppPermissions();
        if (TextUtils.isEmpty(CustomerApplication.token)) {
            generateCode();
        }

        PadsModel padsModel = cache.getObject(Constants.PADS_MODEL, PadsModel.class);
        if (null != padsModel) {
            getPads(true);
            CustomerApplication.spaceId = String.valueOf(padsModel.space_id);
            CustomerApplication.locationId = String.valueOf(padsModel.location_id);
//            ActivityManager.startMainActivity(BindingActivity.this, new Intent());
        }

    }


    @OnClick(R.id.refresh)
    public void onClick(View view) {
        generateCode();
//        ActivityManager.startEntranceGuardActivity(BindingActivity.this);
    }

    public void generateCode() {
        refresh.setEnabled(false);
        validateCode = (int) ((Math.random() * 9 + 1) * 100000);
        mValidateCode.setText(String.valueOf(validateCode));
        postRegister();
        long millisInFuture = 120000;
        if (null == timer) {
            timer = new CountDownTimer(millisInFuture, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    refresh.setText(StringUtil.getString(R.string.again_generate_code_hint, (millisUntilFinished / 1000)));
                    getPads(false);
                }

                @Override
                public void onFinish() {
                    initButton();
                }
            };
            timer.start();
        } else {
            timer.cancel();
            timer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (null != timer) {
            timer.cancel();
        }
    }

    protected void initButton() {
        timer.cancel();
        refresh.setEnabled(true);
        refresh.setText(CustomerApplication.getStringResources(R.string.again_generate_code));
    }

    protected void postRegister() {
        Map<String, String> queries = new HashMap<>();
        queries.put("code", String.valueOf(validateCode));
        queries.put("device_id", EquipmentInformationUtil.getDeviceId(BindingActivity.this));
        queries.put("model", EquipmentInformationUtil.getDeviceInformation(EquipmentInformationUtil.MANUFACTURER) + " " + EquipmentInformationUtil.getDeviceInformation(EquipmentInformationUtil.MODEL));
        queries.put("app_version", EquipmentInformationUtil.getVersionName(BindingActivity.this));
        queries.put("os_version", EquipmentInformationUtil.getDeviceInformation(EquipmentInformationUtil.RELEASE));
        queries.put("os", "android");
        queries.put("apptype", "meetingroom");
        queries.put("subtype", "tv_screen");
//        queries.put("subtype", "meeting_display");
        queries.put("screen_size", EquipmentInformationUtil.getScreenSize(this));
        Call<ToKenModel> createSessionCall = getKubanApi().postRegister(queries);
        createSessionCall.enqueue(new Callback<ToKenModel>() {
            @Override
            public void onResponse(Call<ToKenModel> call, retrofit2.Response<ToKenModel> response) {
                dismissProgressDialog();
                if (response.isSuccessful()) {
                    ToKenModel toKenModel = response.body();
//                    cache.put(Constants.TO_KEN, AESCipher.encrypt(Constants.TO_KEN, toKenModel.token));
//                    CustomerApplication.token = AESCipher.decrypt(Constants.TO_KEN, cache.getAsString(Constants.TO_KEN));
                    cache.put(Constants.TO_KEN, toKenModel.token);
                    CustomerApplication.token = cache.getAsString(Constants.TO_KEN);
                } else {
//                    ErrorUtil.handleError(BindingActivity.this, response);
                }
            }

            @Override
            public void onFailure(Call<ToKenModel> call, Throwable t) {
                ErrorUtil.handleError(BindingActivity.this, t);
            }
        });
    }

    protected void getPads(final boolean isExistence) {
        Call<PadsModel> createSessionCall = getKubanApi().getPads("1");
        createSessionCall.enqueue(new Callback<PadsModel>() {
            @Override
            public void onResponse(Call<PadsModel> call, retrofit2.Response<PadsModel> response) {
                dismissProgressDialog();
                if (response.isSuccessful()) {
                    padsModel = response.body();
                    if (null != padsModel && null != padsModel.meeting_screen) {
                        if (isExistence) {
                            ActivityManager.startAgoraScreenSharingActivity(BindingActivity.this, new Intent());
                            finish();
                        } else {
                            initButton();
                            CustomerApplication.spaceId = String.valueOf(padsModel.space_id);
                            if (!TextUtils.isEmpty(padsModel.passcode)) {
//                            CustomerApplication.appPassword = padsModel.passcode;
//                            cache.put(Constants.APP_PASSWORD, CustomerApplication.appPassword);
                            }
//                        if () {
                            ActivityManager.startBindingSuccessfulActivity(BindingActivity.this, padsModel, false);
//                        }else {
//                            ActivityManager.startBindingSuccessfulActivity(BindingActivity.this, padsModel);
//                        }
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<PadsModel> call, Throwable t) {

            }
        });
    }

//    /**
//     * 查询平板信息
//     */
//    public void getMeetingRooms() {
//        Call<TabletInformationModel> createSessionCall = getKubanApi().getTabletInformation("40");
//        createSessionCall.enqueue(new Callback<TabletInformationModel>() {
//            @Override
//            public void onResponse(Call<TabletInformationModel> call, Response<TabletInformationModel> response) {
//                if (null != response.body()) {
//                    if (!TextUtils.isEmpty(response.body().id)) {
//                        TabletInformationModel tabletInformationModel = response.body();
//                        cache.put(CustomerApplication.TABLETINFORMATION, tabletInformationModel);
//                        Log.e("查询平板信息", "===========查询平2222板信息");
////                        ActivityManager.startMainActivity(BindingActivity.this, new Intent());
////                        finish();
//                        return;
//                    } else {
//                        ToastUtils.showShort(BindingActivity.this, "未查询到平板信息,可能该设备已绑定别的会议室");
//                    }
//                } else {
//                    ToastUtils.showShort(BindingActivity.this, "未请求到平板信息");
//                }
//                dismissProgressDialog();
//            }
//
//            @Override
//            public void onFailure(Call<TabletInformationModel> call, Throwable t) {
//                dismissProgressDialog();
//                Log.e("查询该设备是否已存在===========", call + "失败" + t);
//            }
//        });
//    }

    @Subscribe
    public void scanNetworkRequestStatusEvent(BindingResultsEvent bindingResultsEvent) {
        if (bindingResultsEvent.isResults()) {
            cache.put(Constants.PADS_MODEL, padsModel);
//            getMeetingRooms();   ActivityManager.startAgoraScreenSharingActivity(this, new Intent(), url);
//            ActivityManager.startMainActivity(BindingActivity.this, new Intent());
            ActivityManager.startAgoraScreenSharingActivity(this, new Intent());
            finish();
        } else {
//            generateCode();
        }
    }

    private void requestAppPermissions() {
        AndPermission.with(this)
                .requestCode(100)
                .permission(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .send();
    }

}