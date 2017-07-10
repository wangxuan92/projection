package io.kuban.projection.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import io.kuban.projection.R;

public class LoadingDialog extends Dialog {
    private TextView tv_text;
    private Context context;

    public LoadingDialog(Context context) {
        super(context, R.style.dialog);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_progress_layout, null);
        tv_text = (TextView) view.findViewById(R.id.tv_text);
        setContentView(view);
    }

    public void setContent(String content) {
        tv_text.setText(content);
    }

}
