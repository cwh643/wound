package com.dnion.app.android.injuriesapp.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.dnion.app.android.injuriesapp.R;
import com.dnion.app.android.injuriesapp.dao.RecordInfo;
import com.dnion.app.android.injuriesapp.utils.DrawView;

/**
 * Created by 卫华 on 2017/8/20.
 */

public class ClickView extends View implements View.OnTouchListener {

    private Context mContext;

    private Canvas canvas;

    private GestureDetector mGestureDetector;

    private Float[] position;

    private Bitmap image;

    private int width;

    private int height;

    private Paint paint;

    private RecordInfo recordInfo;

    public ClickView(Context context) {
        super(context);
        initView();
    }
    public ClickView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }
    public ClickView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mContext = getContext();
        paint = new Paint();

        image = ((BitmapDrawable)mContext.getResources().getDrawable(R.mipmap.position)).getBitmap();
        width = image.getWidth();
        height = image.getHeight();

        mGestureDetector = new GestureDetector(mContext,new MyGestureListener());
        setOnTouchListener(this);
        setClickable(true);
        setLongClickable(true);
        setFocusable(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas=canvas;
        drawImage();
    }

    private void drawImage() {
        if (position != null) {
            float x = position[0] - (float)width / 2;
            float y = position[1] - (float)height / 2;
            canvas.drawBitmap(image, x, y, paint);
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    public void setRecordInfo(RecordInfo recordInfo) {
        this.recordInfo = recordInfo;
    }

    public void setPosition(int x, int y) {
        position = new Float[]{(float)x, (float)y};
        invalidate();
    }

    /**
     * 手势类
     */
    class MyGestureListener implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            position = new Float[]{e.getX(), e.getY()};
            recordInfo.setWoundPositionx((int)e.getX());
            recordInfo.setWoundPositiony((int)e.getY());
            invalidate();
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            //Log.i("draw_view", "position ("+e.getX()+","+e.getY()+")");
            return false;//已经处理
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            position = new Float[]{e2.getX(), e2.getY()};
            recordInfo.setWoundPositionx((int)e2.getX());
            recordInfo.setWoundPositiony((int)e2.getY());
            invalidate();
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }
}
