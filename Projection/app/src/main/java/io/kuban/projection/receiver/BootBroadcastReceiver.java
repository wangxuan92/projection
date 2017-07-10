package io.kuban.projection.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import io.kuban.projection.activity.LogInActivity;
import io.kuban.projection.base.ActivityManager;

/**
 * Created by wangxuan on 17/6/19.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {

    /**
     * demo2: 可以实现开机自动打开软件并运行。
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        ActivityManager.toLogInActivity(context, new Intent());
    }
}