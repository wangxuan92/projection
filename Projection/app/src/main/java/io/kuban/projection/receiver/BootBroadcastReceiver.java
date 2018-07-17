package io.kuban.projection.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import io.kuban.projection.activity.LogInActivity;
import io.kuban.projection.activity.MainActivity;
import io.kuban.projection.base.ActivityManager;

/**
 * Created by wangxuan on 17/6/19.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {

    /**
     * demo2: 可以实现开机自动打开软件并运行。
     */
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        ActivityManager.toLogInActivity(context);
//    }
    static final String action_boot ="android.intent.action.BOOT_COMPLETED";


    @Override

    public void onReceive (Context context, Intent intent) {

        Log.i("charge completed", "启动完成");

        if (intent.getAction().equals(action_boot)){

            Intent mBootIntent = new Intent(context, MainActivity.class);
            // 下面这句话必须加上才能开机自动运行app的界面
            mBootIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mBootIntent);
        }
    }
}