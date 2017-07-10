package io.kuban.projection.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import io.kuban.projection.R;

/**
 * Created by wangxuan on 16/11/19.
 */
public class CustomDialog extends Dialog {
    private EditText number;
    private EditText number1;
    private Button positiveButton, negativeButton;
    private TextView gainSmsCode;
    private Context context;
    private GainSmsCodeListener gainSmsCodeListener;
    private int SHOW_ANOTHER_ACTIVITY = 0x1234;
    private boolean isShowing = true;


    public CustomDialog(Context context, GainSmsCodeListener gainSmsCodeListener) {
        super(context);
        this.gainSmsCodeListener = gainSmsCodeListener;
        this.context = context;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        isShowing = true;
        setCustomDialog();
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_normal_layout, null);
        gainSmsCode = (TextView) mView.findViewById(R.id.gain_sms_code);
        number = (EditText) mView.findViewById(R.id.number);
        number1 = (EditText) mView.findViewById(R.id.number1);
        this.setCancelable(false);// 设置点击屏幕Dialog不消失
        positiveButton = (Button) mView.findViewById(R.id.positiveButton);
        negativeButton = (Button) mView.findViewById(R.id.negativeButton);
        gainSmsCode.setClickable(false);
        resetTime();

        //去掉Activity上面的状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //去掉虚拟按键全屏显示
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        //让虚拟键盘一直不显示
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
        window.setAttributes(params);
        number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 10) {
                    gainSmsCode.setClickable(true);
                }
                resetTime();
            }
        });
        number1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                resetTime();
            }
        });
        gainSmsCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gainSmsCodeListener != null) {
                    gainSmsCodeListener.gainSmsCodeListener();
                }
            }
        });
        super.setContentView(mView);
    }

    public View getEditText() {
        return number;
    }

    public View getNumber() {
        return number1;
    }

    @Override
    public void setContentView(int layoutResID) {
    }


    @Override
    public void setContentView(View view) {
    }


    /**
     * 六秒没有操作关闭弹窗
     */


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        resetTime();
        return super.dispatchTouchEvent(ev);
    }

    private void resetTime() {
        // TODO Auto-generated method stub
        mHandler.removeMessages(SHOW_ANOTHER_ACTIVITY);//從消息隊列中移除
        Message msg = mHandler.obtainMessage(SHOW_ANOTHER_ACTIVITY);
        mHandler.sendMessageDelayed(msg, 6000);//無操作5分钟后進入屏保
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if (msg.what == SHOW_ANOTHER_ACTIVITY) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive(number)) {
                    imm.hideSoftInputFromWindow(number.getWindowToken(), 0);
                }
                if (imm.isActive(number1)) {
                    imm.hideSoftInputFromWindow(number1.getWindowToken(), 0);
                }
                Log.e("============   ", "======  " + isShowing);
                if (isShowing) {
                    dismiss();
                }
            }
        }
    };


    public void remove() {
        Log.e("取消键监听器", "setOnPositiveListener");
        isShowing = false;
        mHandler.removeMessages(SHOW_ANOTHER_ACTIVITY);//從消息隊列中移除
        dismiss();
    }

    /**
     * 取消键监听器
     *
     * @param listener
     */
    public void setOnPositiveListener(View.OnClickListener listener) {
        positiveButton.setOnClickListener(listener);
    }

    /**
     * 登录键监听器
     *
     * @param listener
     */
    public void setOnNegativeListener(View.OnClickListener listener) {
        negativeButton.setOnClickListener(listener);
    }

    public interface GainSmsCodeListener {
        public void gainSmsCodeListener();
    }


}