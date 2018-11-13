package com.example.forrestsu.logintest.my_controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.forrestsu.logintest.R;

public class WorkerBar extends ConstraintLayout {

    private int color_text_bg_unSelected;
    private int color_text_bg_selected;
    private int color_text_unSelected;
    private int color_text_selected;

    private TextView titleTV;
    private TextView firstTV;
    private TextView secondTV;
    private TextView thirdTV;

    //三个TextView中，默认第二个被选中
    private int selected = 2;

    public WorkerBar(Context context) {
        super(context);
    }

    public WorkerBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public WorkerBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.workerBar);

        color_text_bg_unSelected = typedArray.getColor(
                R.styleable.workerBar_text_unSelected_bg_color, 0xffebebeb);
        color_text_bg_selected = typedArray.getColor(
                R.styleable.workerBar_text_unSelected_bg_color, 0xff5fc0cd);
        color_text_unSelected = typedArray.getColor(
                R.styleable.workerBar_text_unSelected_color, 0xffbababa);
       color_text_selected = typedArray.getColor(
               R.styleable.workerBar_text_unSelected_color, 0xffffffff);

        LayoutInflater.from(context).inflate(R.layout.my_control_bar_worker, this, true);
        titleTV = (TextView) findViewById(R.id.tv_title);
        firstTV = (TextView) findViewById(R.id.tv_1);
        secondTV = (TextView) findViewById(R.id.tv_2);
        thirdTV = (TextView) findViewById(R.id.tv_3);

        titleTV.setText(typedArray.getString(R.styleable.workerBar_title_text));
        firstTV.setText(typedArray.getString(R.styleable.workerBar_button_1_text));
        secondTV.setText(typedArray.getString(R.styleable.workerBar_button_2_text));
        thirdTV.setText(typedArray.getString(R.styleable.workerBar_button_3_text));

        firstTV.setTextColor(color_text_unSelected);
        firstTV.setBackgroundColor(color_text_bg_unSelected);
        secondTV.setTextColor(color_text_selected);
        secondTV.setBackgroundColor(color_text_bg_selected);
        thirdTV.setTextColor(color_text_unSelected);
        thirdTV.setBackgroundColor(color_text_bg_unSelected);

        firstTV.setOnClickListener(new MyOnClickListener());
        secondTV.setOnClickListener(new MyOnClickListener());
        thirdTV.setOnClickListener(new MyOnClickListener());
    }

    public class MyOnClickListener implements OnClickListener {
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.tv_1:
                    selected(R.id.tv_1);
                    break;
                case R.id.tv_2:
                    selected(R.id.tv_2);
                    break;
                case R.id.tv_3:
                    selected(R.id.tv_3);
                    break;
                default:
                    break;
            }
        }
    }


    /*
    重置
     */
    public void reset() {
        firstTV.setTextColor(color_text_unSelected);
        secondTV.setTextColor(color_text_unSelected);
        thirdTV.setTextColor(color_text_unSelected);
        firstTV.setBackgroundColor(color_text_bg_unSelected);
        secondTV.setBackgroundColor(color_text_bg_unSelected);
        thirdTV.setBackgroundColor(color_text_bg_unSelected);
    }


    /*
    选中
     */
    public void selected(int id) {
        reset();
        if(id == R.id.tv_1) {
            selected = 1;
            firstTV.setTextColor(color_text_selected);
            firstTV.setBackgroundColor(color_text_bg_selected);
        } else if (id == R.id.tv_2) {
            selected = 2;
            secondTV.setTextColor(color_text_selected);
            secondTV.setBackgroundColor(color_text_bg_selected);
        } else if (id == R.id.tv_3) {
            selected = 3;
            thirdTV.setTextColor(color_text_selected);
            thirdTV.setBackgroundColor(color_text_bg_selected);
        }
    }

    public int getSelected() {
        return selected;
    }

}
