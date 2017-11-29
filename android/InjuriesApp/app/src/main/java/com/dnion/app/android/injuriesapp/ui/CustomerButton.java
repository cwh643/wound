package com.dnion.app.android.injuriesapp.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dnion.app.android.injuriesapp.R;

/**
 * Created by 卫华 on 2017/8/3.
 */

public class CustomerButton extends FrameLayout {

    private Context mContext;

    private ImageButton image_btn;

    private TextView btn_title;

    private OnClickListener mListener;

    private int resourceId;

    private int selectResourceId;

    private String TAG = "CustomerButton";

    public CustomerButton(@NonNull Context context) {
        super(context);
        initView(context);
    }
    public CustomerButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    public CustomerButton(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(@NonNull Context context) {
        mContext = context;
        //initStateListDrawable();
        // 加载自定义布局到当前 ViewGroup
        LayoutInflater.from(mContext).inflate(R.layout.customer_btn_view, this);
        RelativeLayout image_text_btn = (RelativeLayout) findViewById(R.id.image_text_btn);
        //image_text_btn.setClickable(true);
        image_btn = (ImageButton) findViewById(R.id.image_btn);
        image_btn.setOnClickListener(ocl);
        image_btn.setOnTouchListener(otl);
        btn_title = (TextView) findViewById(R.id.btn_title);
    }

    private int dp2px(float dpValue){
        return (int)(dpValue * (getResources().getDisplayMetrics().density) + 0.5f);
    }

    public void setOnClickListener(OnClickListener l) {
        mListener = l;
    }

    public void setSelectImage(int resource) {
        selectResourceId = resource;
    }

    public void setImage(int resource) {
        resourceId = resource;
        image_btn.setImageResource(resourceId);
    }

    public void setText(String text) {
        btn_title.setText(text);
    }

    public OnClickListener ocl=new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onClick(v);
            }
        }
    };

    public OnTouchListener otl=new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction()==MotionEvent.ACTION_DOWN){
                image_btn.setImageResource(selectResourceId);
                btn_title.setTextColor(Color.rgb(0,188,188));
            }else if(event.getAction()==MotionEvent.ACTION_UP) {
                image_btn.setImageResource(resourceId);
                btn_title.setTextColor(Color.rgb(182,182,182));
            }
            return false;
        }
    };

}

