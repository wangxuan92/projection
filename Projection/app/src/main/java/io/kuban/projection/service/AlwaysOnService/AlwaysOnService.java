package io.kuban.projection.service.AlwaysOnService;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.kuban.projection.base.ActivityManager;
import io.kuban.projection.base.Constants;


public class AlwaysOnService extends BaseService {
    private static String LOG_TAG = AlwaysOnService.class.getSimpleName();
    public static boolean isRunning = false;
    private ScheduledExecutorService backgroundService;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isRunning == false) {
            // run something
            backgroundService = Executors.newSingleThreadScheduledExecutor();
            backgroundService.scheduleAtFixedRate(new TimerIncreasedRunnable(
                    this), 0, 1000, TimeUnit.MILLISECONDS);
            isRunning = true;
        }
        // the following will return START_STICKY
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // stop running
        isRunning = false;
        backgroundService.shutdownNow();
        super.onDestroy();
    }

    public class TimerIncreasedRunnable implements Runnable {
        private SharedPreferences currentSharedPreferences;

        public TimerIncreasedRunnable(Context context) {
            this.currentSharedPreferences = context.getSharedPreferences(
                    Constants.SHAREDPREF_APP_STRING, MODE_PRIVATE);
        }

        @Override
        public void run() {
            Log.e(LOG_TAG, "run:   "
                    + isAppOnForeground());
            if (!isAppOnForeground()) {
                ActivityManager.toLogInActivity(getBaseContext(), new Intent());
            }
        }

        private int readTimeCount() {
            return this.currentSharedPreferences.getInt(
                    Constants.SHAREDPREF_RUNNINGTIMECOUNT_STRING, 0);
        }

        private void writeTimeCount(int timeCount) {
            this.currentSharedPreferences.edit().putInt(
                    Constants.SHAREDPREF_RUNNINGTIMECOUNT_STRING,
                    timeCount).commit();
        }
    }

    /**
     * 程序是否在前台运行
     *
     * @return
     */
    public boolean isAppOnForeground() {

        android.app.ActivityManager activityManager = (android.app.ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();
        /**
         * 获取Android设备中所有正在运行的App
         */
        List<android.app.ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (android.app.ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }
}
