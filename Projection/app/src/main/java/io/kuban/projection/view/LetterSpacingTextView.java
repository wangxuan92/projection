package io.kuban.projection.view;

import android.content.Context;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ScaleXSpan;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by mars on 5/5/15.
 */
public class LetterSpacingTextView extends TextView {

    private float letterSpacing = LetterSpacing.BIGGEST;
    private CharSequence originalText = "";
    private boolean skip;
    private String spacingChar = " ";

    public LetterSpacingTextView(Context context) {
        super(context);
    }

    public LetterSpacingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getOriginalText(super.getText().toString());
        applyLetterSpacing();
        this.invalidate();

        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (skip) {
                    skip = false;
                    return;
                }

                skip = true;
                getOriginalText(s.toString());
                applyLetterSpacing();
            }
        });
    }

    public LetterSpacingTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public float getLetterSpacing() {
        return letterSpacing;
    }

    public void setLetterSpacing(float letterSpacing) {
        this.letterSpacing = letterSpacing;
        applyLetterSpacing();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        getOriginalText(text.toString());
        applyLetterSpacing();
    }

    private void getOriginalText(String text) {
        originalText = text.replaceAll("\\s", "");
    }

//    @Override
//    public Editable getText() {
//        return super.getText();
//    }

    private void applyLetterSpacing() {
        if (this == null || this.originalText == null) {
            return;
        }

        StringBuilder builder = new StringBuilder();
        int len = originalText.length();
        for (int i = 0; i < len; i++) {
            String c = "" + originalText.charAt(i);

//            skip already added
//            if(c.equals(spacingChar)){
//                continue;
//            }

            builder.append(c.toLowerCase());
            if (i + 1 < originalText.length()) {
                builder.append(spacingChar);//"\u00A0");
            }
        }
        SpannableString finalText = new SpannableString(builder.toString());
        if (builder.toString().length() > 1) {
            for (int i = 1; i < builder.toString().length(); i += 2) {
                finalText.setSpan(new ScaleXSpan((letterSpacing + 1) / 10), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        super.setText(finalText, BufferType.SPANNABLE);
//        //移动光标到最后，所以maxLength设置时为奇数
//        super.setSelection(finalText.length());
    }

    public class LetterSpacing {
        public final static float NORMAL = 0;
        public final static float NORMALBIG = (float) 0.025;
        public final static float BIG = (float) 0.05;
        public final static float BIGGEST = (float) 20.0;
    }
}
