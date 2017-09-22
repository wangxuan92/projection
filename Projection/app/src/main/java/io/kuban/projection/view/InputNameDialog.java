package io.kuban.projection.view;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import io.kuban.projection.R;


/**
 * Created by wangxuan on 16/11/19.
 */
public class InputNameDialog extends Dialog {
    //    private EditText number;
    private Button positiveButton, negativeButton;
    private String titleStr;
    private String negativeStr;
    private Context context;


    public InputNameDialog(Context context, String title) {
        super(context);
        this.context = context;
        this.titleStr = title;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCustomDialog();
    }

    public InputNameDialog(Context context, String title, String negativeStr) {
        super(context);
        this.context = context;
        this.titleStr = title;
        this.negativeStr = negativeStr;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCustomDialog();
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_input_name_layout, null);
//        number = (EditText) mView.findViewById(R.id.number);
        this.setCancelable(false);// 设置点击屏幕Dialog不消失
        positiveButton = (Button) mView.findViewById(R.id.positiveButton);
        negativeButton = (Button) mView.findViewById(R.id.negativeButton);
        TextView title = (TextView) mView.findViewById(R.id.title);
        if (!TextUtils.isEmpty(negativeStr)) {
            negativeButton.setText(negativeStr);
        }
        title.setText(titleStr);
        //去掉Activity上面的状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //去掉虚拟按键全屏显示
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        //让虚拟键盘一直不显示
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
        window.setAttributes(params);
        super.setContentView(mView);
    }

//    public View getEditText() {
//        return number;
//    }


    @Override
    public void setContentView(int layoutResID) {
    }


    @Override
    public void setContentView(View view) {
    }


    public void remove() {
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
     * 确定键监听器
     *
     * @param listener
     */
    public void setOnNegativeListener(View.OnClickListener listener) {
        negativeButton.setOnClickListener(listener);
    }


}