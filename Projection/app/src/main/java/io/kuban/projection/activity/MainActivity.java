package io.kuban.projection.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.kuban.projection.BuildConfig;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
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
        switch (view.getId()) {
            case R.id.select_settings:
                ActivityManager.startLogInActivity(this, new Intent(), true);
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
}
