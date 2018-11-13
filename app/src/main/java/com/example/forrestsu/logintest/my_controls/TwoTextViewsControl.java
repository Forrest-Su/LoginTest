package com.example.forrestsu.logintest.my_controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.constraint.ConstraintLayout;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.forrestsu.logintest.R;


public class TwoTextViewsControl extends ConstraintLayout {

    private int color_text_title;
    private int color_text_content;
    private int contentTextSize;

    private ProgressBar progressBar;

    private TextView titleTV, contentTV;

    public TwoTextViewsControl (Context context) {
        super(context);
    }
    public TwoTextViewsControl (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public TwoTextViewsControl (Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.twoTextViews);

        color_text_title = typedArray.getColor(R.styleable.twoTextViews_title_text_color, 0xffbababa);
        color_text_content = typedArray.getColor(R.styleable.twoTextViews_content_text_color, 0xff000000);

        LayoutInflater.from(context).inflate(R.layout.my_control_two_textviews, this, true);
        titleTV = (TextView) findViewById(R.id.tv_title);
        contentTV = (TextView) findViewById(R.id.tv_content);
        contentTV.setMovementMethod(ScrollingMovementMethod.getInstance());

        titleTV.setText(typedArray.getString(R.styleable.twoTextViews_title_text_1));
        contentTV.setText(typedArray.getString(R.styleable.twoTextViews_content_text));

        contentTextSize = typedArray.getInteger(R.styleable.twoTextViews_content_text_size, 16);
        contentTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, contentTextSize);

        titleTV.setTextColor(color_text_title);
        contentTV.setTextColor(color_text_content);
    }

    public void set(String title, String content) {
        titleTV.setText(title);
        contentTV.setText(content);
    }
}
