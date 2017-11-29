package com.dnion.app.android.injuriesapp.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by 卫华 on 2017/5/30.
 */

public class DrawView extends View implements View.OnTouchListener {

    public static final String TAG = "draw_view";

    private Paint linePaint;

    private Canvas canvas;

    private GestureDetector mGestureDetector;

    private Context mContext;

    private DisplayMetrics metrics = new DisplayMetrics();

    private Float[] postionBegin;

    private Float[] postionEnd;

    private int count = 0;

    public DrawView(Context context) {
        super(context);
        initView();
    }
    public DrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }
    public DrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mContext = getContext();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getRealMetrics(metrics);
        //获取的像素宽高不包含虚拟键所占空间
        //context.getWindowManager().getDefaultDisplay().getMetrics(metric);

        mGestureDetector = new GestureDetector(mContext,new MyGestureListener());
        setOnTouchListener(this);
        setClickable(true);
        setLongClickable(true);
        setFocusable(true);

        linePaint = new Paint();
        linePaint.setStrokeWidth(1);
        linePaint.setColor(Color.RED);

        postionBegin = new Float[]{300f, 100f};
        postionEnd = new Float[]{100f, 400f};
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas=canvas;
        drawTria(postionBegin[0], postionBegin[1], postionEnd[0], postionEnd[1], 50, 10);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    protected void drawTria(float fromX, float fromY, float toX, float toY, int heigth, int bottom) {
        // heigth和bottom分别为三角形的高与底的一半,调节三角形大小
        //baseBitmap = Bitmap.createBitmap(iv_canvas.getWidth(), iv_canvas.getHeight(), Bitmap.Config.ARGB_8888);
        //canvas = new Canvas(baseBitmap);
        //canvas.drawColor(Color.YELLOW);// 设置底色
        canvas.drawLine(fromX, fromY, toX, toY, linePaint);
        float juli = (float) Math.sqrt((toX - fromX) * (toX - fromX) + (toY - fromY) * (toY - fromY));// 获取线段距离
        float juliX = toX - fromX;// 有正负，不要取绝对值
        float juliY = toY - fromY;// 有正负，不要取绝对值
        float dianX = toX - (heigth / juli * juliX);
        float dianY = toY - (heigth / juli * juliY);
        float dian2X = fromX + (heigth / juli * juliX);
        float dian2Y = fromY + (heigth / juli * juliY);
        //终点的箭头
        Path path = new Path();
        path.moveTo(toX, toY);// 此点为三边形的起点
        path.lineTo(dianX + (bottom / juli * juliY), dianY - (bottom / juli * juliX));
        path.lineTo(dianX - (bottom / juli * juliY), dianY + (bottom / juli * juliX));
        path.close(); // 使这些点构成封闭的三边形
        canvas.drawPath(path, linePaint);

        //起点的箭头
        Path path2 = new Path();
        path2.moveTo(fromX, fromY);// 此点为边形的起点
        path2.lineTo(dian2X + (bottom / juli * juliY), dian2Y  - (bottom / juli * juliX));
        path2.lineTo(dian2X - (bottom / juli * juliY), dian2Y  + (bottom / juli * juliX));
        path2.close(); // 使这些点构成封闭的三边形
        canvas.drawPath(path2, linePaint);

        //显示长度
        double L = Math.abs(toX - fromX);
        double w = Math.abs(toY - fromY);
        double px = Math.sqrt(L*L + w*w);
        float mm = computerLength((float)px);
        double centerx = fromX + ((toX - fromX) / 2.0);
        double centery = fromY + ((toY - fromY) / 2.0);
        String text = "" + mm + "mm";
        canvas.drawText(text, 0, text.length(), (float)centerx, (float)centery, linePaint);
    }

    private float computerLength(float value) {
        /*
        int width = metrics.widthPixels;  // 宽度（像素）1960
        float density = metrics.density;  // dp缩放因子2
        float xdpi = metrics.xdpi;//x轴方向的真实密度225
        float dpToCm = density * 2.54f / xdpi;//0.0225
        Log.i("draw_view", "width="+width+",density="+density+",xdpi="+xdpi+",dpToCm="+dpToCm);
        //*/
        //return value * metrics.xdpi * (1.0f/25.4f);
        return value * 25.4f / metrics.xdpi - 10.0f;
    }

    /*
    public static float applyDimension(int unit, float value,
                                       DisplayMetrics metrics)
    {
        switch (unit) {
            case COMPLEX_UNIT_PX:
                return value;
            case COMPLEX_UNIT_DIP:
                return value * metrics.density;
            case COMPLEX_UNIT_SP:
                return value * metrics.scaledDensity;
            case COMPLEX_UNIT_PT:
                return value * metrics.xdpi * (1.0f/72);
            case COMPLEX_UNIT_IN:
                return value * metrics.xdpi;
            case COMPLEX_UNIT_MM:
                return value * metrics.xdpi * (1.0f/25.4f);
        }
        return 0;
      }
*/
    /**
     * 手势类
     */
    class MyGestureListener implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (count == 0) {
                count = 1;
                postionBegin = new Float[]{e.getX(), e.getY()};
            } else {
                count = 0;
                postionEnd = new Float[]{e.getX(), e.getY()};
                invalidate();
            }
            //Log.i("draw_view", "position ("+e.getX()+","+e.getY()+")");
            return true;//已经处理
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
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
