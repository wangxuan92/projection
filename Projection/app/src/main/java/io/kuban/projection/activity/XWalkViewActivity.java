package io.kuban.projection.activity;

import android.content.Intent;
import android.os.Bundle;

import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;
import org.xwalk.core.internal.XWalkSettings;

import io.kuban.projection.R;

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
        String url = getIntent().getStringExtra("url");
        mXwalkView = (XWalkView) findViewById(R.id.xwalkView);
        mXwalkView.setResourceClient(new MyResourceClient(mXwalkView));
        mXwalkView.setUIClient(new MyUIClient(mXwalkView));
        mXwalkView.load(url, null);
        XWalkSettings xWalkSettings = mXwalkView.getSettings();
        xWalkSettings.setAppCacheEnabled(false); //设置为可以缓存
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
            mXwalkView.clearCache(false);
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
