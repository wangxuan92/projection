package io.kuban.projection.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bugtags.library.Bugtags;
import com.bumptech.glide.Glide;
import com.facebook.stetho.common.LogUtil;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import io.kuban.projection.BuildConfig;
import io.kuban.projection.CustomerApplication;
import io.kuban.projection.R;
import io.kuban.projection.base.CommonHeaderInterceptor;
import io.kuban.projection.base.Constants;
import io.kuban.projection.service.KuBanApi;
import io.kuban.projection.util.ACache;
import io.kuban.projection.view.LoadingDialog;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by wangxuan on 16/9/22.
 */
public class BaseCompatActivity extends FragmentActivity {
    protected LoadingDialog ld;
    public ACache cache;
    public String LOG_TAG;
    public Activity activity;
    private PowerManager powerManager = null;
    private PowerManager.WakeLock wakeLock = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去掉Activity上面的状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //去掉虚拟按键全屏显示
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        //让虚拟键盘一直不显示
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
        window.setAttributes(params);
        //添加Activity到堆栈
        CustomerApplication.getInstance().addActivity(this);
        LOG_TAG = this.getClass().getSimpleName();
        activity = this;
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStatusBarUpperAPI21();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setStatusBarUpperAPI19();
        }
        cache = ACache.get(this);
        powerManager = (PowerManager) this.getSystemService(this.POWER_SERVICE);
        wakeLock = this.powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
//        prefStore = SecuredPreferenceStore.getSharedInstance(this);
        ld = new LoadingDialog(this);
        LogUtil.e(this.getClass().getSimpleName(), "######################" + this.getClass().getSimpleName() + "###################");
    }


    public KuBanApi getKubanApi() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new CommonHeaderInterceptor(this))
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        KuBanApi kuBanApi = retrofit.create(KuBanApi.class);
        return kuBanApi;
    }

    public void showProgressDialog() {
        ld = new LoadingDialog(this);
        ld.show();
    }


    public void dismissProgressDialog() {
        if (ld != null) {
            ld.dismiss();
        }
    }

    public void setAreaId(String areaId, String area_name) {
        cache.put(Constants.AREA_ID, areaId);
        cache.put(Constants.AREA_NAME, area_name);
    }

    public void setAreaId(String areaId) {
        cache.put(Constants.AREA_ID, areaId);
    }

    public void setServer(String servier) {
        cache.put(Constants.SERVIER, servier);
    }

    public void setUrl(String url) {
        cache.put(Constants.URL, url);
    }

    protected String getUrl() {
        return cache.getAsString(Constants.URL);
    }

    public String getAreaId() {
        return cache.getAsString(Constants.AREA_ID);
    }

    public String getAreaName() {
        return cache.getAsString(Constants.AREA_NAME);
    }

    public String getServer() {
        return cache.getAsString(Constants.SERVIER);
    }

    public void initTitleAndBack(View toolbar, String title) {
        TextView title_text = (TextView) toolbar.findViewById(R.id.title_text);
        ImageView left_img_icon = (ImageView) toolbar.findViewById(R.id.title_left_img_icon);
        title_text.setText(title);
        title_text.setTextColor(getResources().getColor(R.color.white));
        left_img_icon.setVisibility(View.VISIBLE);
        left_img_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseCompatActivity.this.finish();
            }
        });
    }

    public void initTitleRightAndBack(View toolbar, String title, String rightTitle, int resources) {
        TextView title_text = (TextView) toolbar.findViewById(R.id.title_text);
        TextView title_right = (TextView) toolbar.findViewById(R.id.title_right_text);
        ImageView right_img_icon = (ImageView) toolbar.findViewById(R.id.title_left_img_icon);
        title_text.setText(title);
        title_text.setTextColor(getResources().getColor(R.color.white));
        if (!TextUtils.isEmpty(rightTitle)) {
            title_right.setVisibility(View.VISIBLE);
            title_right.setText(rightTitle);
            title_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onTitleTitleRightClick(view);
                }
            });
        }

        if (resources > -1) {
            right_img_icon.setVisibility(View.VISIBLE);
            right_img_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onTitleTitleRightClick(view);
                }
            });
            Glide.with(this)
                    .load(resources)
                    .into(right_img_icon);
        }

    }


    public void initTitleLeftAndBack(View toolbar, String title, String leftTitle, int resources) {
        TextView title_text = (TextView) toolbar.findViewById(R.id.title_text);
        TextView title_right = (TextView) toolbar.findViewById(R.id.title_left_text);
        ImageView left_img_icon = (ImageView) toolbar.findViewById(R.id.title_left_img_icon);
        title_text.setText(title);
        title_text.setTextColor(getResources().getColor(R.color.white));
        if (!TextUtils.isEmpty(leftTitle)) {
            title_right.setVisibility(View.VISIBLE);
            title_right.setText(leftTitle);
        }
        title_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTitleTitleRightClick(view);
            }
        });

        if (resources > -1) {
            left_img_icon.setVisibility(View.VISIBLE);
            left_img_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onTitleTitleLeftClick(view);
                }
            });
            Glide.with(this)
                    .load(resources)
                    .into(left_img_icon);
        }
    }

    public void initTitleAndBack(View toolbar, String left, String title, String rightTitle) {
        TextView title_text = (TextView) toolbar.findViewById(R.id.title_text);
        TextView title_right = (TextView) toolbar.findViewById(R.id.title_right_text);
        TextView text_left = (TextView) toolbar.findViewById(R.id.title_left_text);
        ImageView left_img_icon = (ImageView) toolbar.findViewById(R.id.title_left_img_icon);
        title_text.setText(title);
        title_text.setTextColor(getResources().getColor(R.color.white));
        if (!TextUtils.isEmpty(rightTitle)) {
            title_right.setVisibility(View.VISIBLE);
            title_right.setText(rightTitle);
            title_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onTitleTitleRightClick(view);
                }
            });
        }
        if (!TextUtils.isEmpty(left)) {
            text_left.setVisibility(View.VISIBLE);
            text_left.setText(left);
            text_left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onTitleTitleLeftClick(view);
                }
            });
        } else {
            left_img_icon.setVisibility(View.VISIBLE);
            left_img_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BaseCompatActivity.this.finish();
                }
            });
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void initTitleMainAndBack(View toolbar, String url) {
        ImageView title_img = (ImageView) toolbar.findViewById(R.id.title_img);
        ImageView title_left_img_icon = (ImageView) toolbar.findViewById(R.id.title_left_img_icon);
        ImageView title_right_img_icon = (ImageView) toolbar.findViewById(R.id.title_right_img_icon);
        title_right_img_icon.setVisibility(View.VISIBLE);
        title_left_img_icon.setVisibility(View.VISIBLE);
        title_img.setVisibility(View.VISIBLE);
//        title_right_img_icon.setImageResource(R.drawable.message_icon_selector);
//        title_left_img_icon.setImageResource(R.drawable.message_my_icon_selector);
        title_left_img_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTitleTitleLeftClick(view);
            }
        });
        title_right_img_icon.setVisibility(View.VISIBLE);
        title_right_img_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTitleTitleRightClick(view);
            }
        });
    }


    public void onTitleTitleRightClick(View view) {

    }

    public void onTitleTitleLeftClick(View view) {

    }

    private void setStatusBarUpperAPI21() {
        Window window = getWindow();
        //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏颜色
        //由于setStatusBarColor()这个API最低版本支持21, 本人的是15,所以如果要设置颜色,自行到style中通过配置文件设置
//        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        ViewGroup mContentView = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 预留出系统 View 的空间.
            ViewCompat.setFitsSystemWindows(mChildView, true);
        }
    }

    private void setStatusBarUpperAPI19() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        ViewGroup mContentView = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
        int statusBarHeight = getStatusBarHeight();
        int statusColor = getResources().getColor(R.color.calendar_bg_color);

        View mTopView = mContentView.getChildAt(0);
        if (mTopView != null && mTopView.getLayoutParams() != null &&
                mTopView.getLayoutParams().height == statusBarHeight) {
            //避免重复添加 View
            mTopView.setBackgroundColor(statusColor);
            return;
        }
        //使 ChildView 预留空间
        if (mTopView != null) {
            ViewCompat.setFitsSystemWindows(mTopView, true);
        }

        //添加假 View
        mTopView = new View(this);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
        mTopView.setBackgroundColor(statusColor);
        mContentView.addView(mTopView, 0, lp);
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            result = getResources().getDimensionPixelSize(resId);
        }
        return result;
    }


    /**
     * 检测网络是否可用
     *
     * @return
     */
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }


    /**
     * 获取当前网络类型
     *
     * @return 0：没有网络   1：WIFI网络   2：WAP网络    3：NET网络
     */
    public static final int NETTYPE_WIFI = 0x01;
    public static final int NETTYPE_CMWAP = 0x02;
    public static final int NETTYPE_CMNET = 0x03;

    public int getNetworkType() {
        int netType = 0;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            String extraInfo = networkInfo.getExtraInfo();
            if (!TextUtils.isEmpty(extraInfo)) {
                if (extraInfo.toLowerCase().equals("cmnet")) {
                    netType = NETTYPE_CMNET;
                } else {
                    netType = NETTYPE_CMWAP;
                }
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = NETTYPE_WIFI;
        }
        return netType;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //注：回调 1
        Bugtags.onResume(this);
        wakeLock.acquire();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //注：回调 2
        Bugtags.onPause(this);
        wakeLock.release();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //注：回调 3
        Bugtags.onDispatchTouchEvent(this, event);
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //结束Activity&从栈中移除该Activity
        CustomerApplication.getInstance().finishActivity(this);

    }
}
