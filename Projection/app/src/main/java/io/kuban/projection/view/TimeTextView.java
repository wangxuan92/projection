package io.kuban.meetingdisplay.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.kuban.projection.CustomerApplication;
import io.kuban.projection.R;


/**
 * Created by Administrator on 2016/9/8.
 */
public class TimeTextView extends TextView implements View.OnClickListener {
    private long lenght = 60 * 1000;// 倒计时长度,这里给了默认60秒
    private String textafter = "重新发送";
    private String textbefore = "点击获取验证码";
    private final String TIME = "time";
    private final String CTIME = "ctime";
    private OnClickListener mOnclickListener;
    private Timer t;
    private TimerTask tt;
    private long time;
    Map<String, Long> map = new HashMap<>();

    public TimeTextView(Context context) {
        super(context);
        setOnClickListener(this);

    }

    public TimeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(this);
    }

    @SuppressLint("HandlerLeak")
    Handler han = new Handler() {
        public void handleMessage(android.os.Message msg) {
            TimeTextView.this.setText(textafter + "(" + time / 1000 + ")");
            TimeTextView.this.setTextColor(getResources().getColor(R.color.lightgrey));
            time -= 1000;
            if (time < 0) {
                TimeTextView.this.setEnabled(true);
                TimeTextView.this.setText(textbefore);
                TimeTextView.this.setTextColor(getResources().getColor(R.color.black));
                clearTimer();
            }
        }

        ;
    };

    private void initTimer() {
        time = lenght;
        t = new Timer();
        tt = new TimerTask() {

            @Override
            public void run() {
//                Log.e("yung", time / 1000 + "");
                han.sendEmptyMessage(0x01);
            }
        };
    }

    private void clearTimer() {
        if (tt != null) {
            tt.cancel();
            tt = null;
        }
        if (t != null)
            t.cancel();
        t = null;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        if (l instanceof TimeTextView) {
            super.setOnClickListener(l);
        } else
            this.mOnclickListener = l;
    }

    @Override
    public void onClick(View v) {
        if (mOnclickListener != null)
            mOnclickListener.onClick(v);
        initTimer();
        this.setText(textafter + "(" + time / 1000 + ")");
        this.setTextColor(getResources().getColor(R.color.lightgrey));
        this.setEnabled(false);
        t.schedule(tt, 0, 1000);
        // t.scheduleAtFixedRate(task, delay, period);
    }

    /**
     * 和activity的onDestroy()方法同步
     */
    public void onDestroy() {
        if (CustomerApplication.map == null)
            CustomerApplication.map = new HashMap<String, Long>();
        CustomerApplication.map.put(TIME, time);
        CustomerApplication.map.put(CTIME, System.currentTimeMillis());
        clearTimer();
//        Log.e("yung", "onDestroy");
    }

    /**
     * 和activity的onCreate()方法同步
     */
    public void onCreate(Bundle bundle) {
        Log.e("yung", CustomerApplication.map + "");
        if (CustomerApplication.map == null)
            return;
        if (CustomerApplication.map.size() <= 0)// 这里表示没有上次未完成的计时
            return;
        long time = System.currentTimeMillis() - CustomerApplication.map.get(CTIME)
                - CustomerApplication.map.get(TIME);
        CustomerApplication.map.clear();
        if (time > 0)
            return;
        else {
            initTimer();
            this.time = Math.abs(time);
            t.schedule(tt, 0, 1000);
            this.setText(textafter + "(" + time + ")");
            this.setTextColor(getResources().getColor(R.color.lightgrey));
            this.setEnabled(false);
        }
    }

    /**
     * 设置计时时候显示的文本
     */
    public TimeTextView setTextAfter(String text1) {
        this.textafter = text1;
        return this;
    }

    /**
     * 设置点击之前的文本
     */
    public TimeTextView setTextBefore(String text0) {
        this.textbefore = text0;
        this.setText(textbefore);
        this.setTextColor(getResources().getColor(R.color.black));
        return this;
    }

    /**
     * 设置到计时长度
     *
     * @param lenght 时间 默认毫秒
     * @return
     */
    public TimeTextView setLenght(long lenght) {
        this.lenght = lenght * 1000;
        return this;
    }


}
