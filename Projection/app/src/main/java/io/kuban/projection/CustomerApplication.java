package io.kuban.projection;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.List;
import java.util.Map;

import io.kuban.projection.base.ActivityManager;
import io.kuban.projection.base.CommonHeaderInterceptor;
import io.kuban.projection.base.MyApplication;
import io.kuban.projection.model.ChooseModel;
import io.kuban.projection.service.KuBanApi;
import io.kuban.projection.util.ACache;
import io.kuban.projection.util.UserManager;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by wang on 2016/8/2.
 */

public class CustomerApplication extends MyApplication {
    public static final String TABLETINFORMATION = "tablet_information";
    public static final String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "kuban";
    public static String mac;// MAC
    public static String device_id; // 设备ID
    public static String model;// 型号
    public static String manufacturer;// 制造商
    public static List<ChooseModel> lListcChooseMode;
    public static boolean Isexit = false;
    // 用于存放倒计时时间
    public static Map<String, Long> map;
    public static Context context;
    public static boolean isRestart;
    private KuBanApi kuBanApi;
    public ACache cache;//存储数据

    private void initKubanApi() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new CommonHeaderInterceptor(this))
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .build();
        kuBanApi = retrofit.create(KuBanApi.class);
    }

    public KuBanApi getKubanApi() {
        return kuBanApi;
    }

    // 2016-08-22 17:17:32+0800
    public Gson getGson() {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss Z").create();
        return gson;
    }

    public Gson getGlobalGson() {
        return new Gson();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        cache = ACache.get(this);
        context = this;
        isRestart = true;
        UserManager.init(this);
        obtainMAC();
        obtainMessage();
        initKubanApi();
        Thread.setDefaultUncaughtExceptionHandler(restartHandler); // 程序崩溃时触发线程  以下用来捕获程序崩溃异常



        //----------------保持长亮
//        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
    }

    /**
     * 方法说明：获取MAC 和设备ID
     */
    private void obtainMAC() {
        WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
        if (null != info) {
            mac = info.getMacAddress();
        }
        device_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        device_id = "12314342566724332";
    }

    /**
     * 方法说明：获取设备型号，厂商信息
     */
    private void obtainMessage() {
        Build bd = new Build();
        model = bd.MODEL;
        manufacturer = bd.MANUFACTURER;
    }

    // 创建服务用于捕获崩溃异常
    private Thread.UncaughtExceptionHandler restartHandler = new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread thread, Throwable ex) {
            Log.e("XRGPS", "=======     ");
            restartApp();//发生崩溃异常时,重启应用
        }
    };
    @Override
    public void onTerminate() {
        restartApp();
        super.onTerminate();
    }

    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        restartApp();
        super.onTrimMemory(level);
    }
    public void restartApp() {
        if (isRestart) {
            ActivityManager.toLogInActivity(context, new Intent());
        } }

}
