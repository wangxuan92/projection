package io.kuban.projection.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;

import org.greenrobot.eventbus.EventBus;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;
import org.xwalk.core.internal.XWalkSettings;

import io.kuban.projection.R;
import io.kuban.projection.base.Constants;
import io.kuban.projection.event.RefreshEvent;
import io.kuban.projection.service.AlwaysOnService.Bootstrap;

/**
 * Created by wangxuan on 17/7/27.
 */

public class XWalkViewActivity extends BaseCompatActivity {
    private XWalkView mXwalkView;

    class MyResourceClient extends XWalkResourceClient {
        MyResourceClient(XWalkView view) {
            super(view);
        }
    }

    class MyUIClient extends XWalkUIClient {
        MyUIClient(XWalkView view) {
            super(view);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.x_web_activity);
        EventBus.getDefault().post(new RefreshEvent(Constants.FINISH));
//        Bootstrap.startAlwaysOnService(this, "Main");
        String url = cache.getAsString(Constants.URL);
//        url = "http://10.0.107.209:8080/projector/screensharing.html";

        Log.e(LOG_TAG, "url=====" + url);
        mXwalkView = (XWalkView) findViewById(R.id.xwalkView);
        mXwalkView.setResourceClient(new MyResourceClient(mXwalkView));
        mXwalkView.setUIClient(new MyUIClient(mXwalkView));
        mXwalkView.setDrawingCacheEnabled(false);//不使用缓存
        XWalkSettings xWalkSettings = mXwalkView.getSettings();
        if (xWalkSettings != null) {
            xWalkSettings.setAppCacheEnabled(false); //设置为可以缓存
            xWalkSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//不使用缓存
        }
        mXwalkView.load(url, null);


//        mXwalkView.getSettings().setBlockNetworkLoads(true);
//        mXwalkView.setResourceClient(new XWalkResourceClient(mXwalkView) {
//            @Override
//            public void onLoadFinished(XWalkView view, String url) {
//                super.onLoadFinished(view, url);
//            }
//
//            @Override
//            public void onProgressChanged(XWalkView view, int progressInPercent) {
//                super.onProgressChanged(view, progressInPercent);
//                if (progressInPercent >= 100) {
//                    mXwalkView.getSettings().setBlockNetworkLoads(false);
//                }
//            }
//
//            @Override
//            public void onProgressChanged(XWalkViewInternal view, int progressInPercent) {
//                super.onProgressChanged(view, progressInPercent);
//            }
//        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mXwalkView != null) {
            mXwalkView.pauseTimers();
            mXwalkView.onHide();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mXwalkView != null) {
            mXwalkView.resumeTimers();
            mXwalkView.onShow();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mXwalkView != null) {
            mXwalkView.clearCache(true);
            mXwalkView.onDestroy();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mXwalkView != null) {
            mXwalkView.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (mXwalkView != null) {
            mXwalkView.onNewIntent(intent);
        }
    }
}
