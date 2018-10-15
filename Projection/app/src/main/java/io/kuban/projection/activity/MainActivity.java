package io.kuban.projection.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.yanzhenjie.permission.AndPermission;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.kuban.projection.BuildConfig;
import io.kuban.projection.CustomerApplication;
import io.kuban.projection.R;
import io.kuban.projection.base.ActivityManager;
import io.kuban.projection.base.Constants;
import io.kuban.projection.event.RefreshEvent;
import io.kuban.projection.util.Utils;

/**
 * Created by wangxuan on 17/9/20.
 */

public class MainActivity extends BaseCompatActivity {

    private String url = "";
    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        requestAppPermissions();
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

    @Override
    protected void onResume() {
        super.onResume();
        url = getUrl();
        if (TextUtils.isEmpty(url)) {
            setServer(BuildConfig.SERVIER);
            setUrl(Utils.saveUrl(getServer(), getAreaId()));
        }
        Log.e("================   ", "url  " + Utils.saveUrl(getServer(), getAreaId()));
    }


    @OnClick({R.id.select_settings, R.id.enter_home_page})
    public void click(View view) {
        if (!checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO) || !checkSelfPermission(Manifest.permission.MODIFY_AUDIO_SETTINGS, PERMISSION_REQ_ID_RECORD_AUDIO)) {
            return;
        }
        switch (view.getId()) {
            case R.id.select_settings:
                ActivityManager.startLogInActivity(this, new Intent(), true);
                Toast.makeText(this, CustomerApplication.device_id, Toast.LENGTH_LONG).show();
                break;
            case R.id.enter_home_page:
                if (!TextUtils.isEmpty(getAreaId())) {
//                    ActivityManager.startXWalkViewActivity(this, new Intent(), url);
                    ActivityManager.startAgoraScreenSharingActivity(this, new Intent(), url);
                    finish();
                } else {
                    ActivityManager.startLogInActivity(this, new Intent(), false);
                }
                break;
        }
    }

    @Subscribe
    public void onEventMainThread(RefreshEvent event) {
        if (event.type.equals(Constants.FINISH)) {
            finish();
            Log.e("==================", "MainActivity    finish");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public boolean checkSelfPermission(String permission, int requestCode) {
        Log.e(LOG_TAG, "checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    requestCode);
            return false;
        }
        return true;
    }

}
