package com.dnion.app.android.injuriesapp;

import android.animation.ValueAnimator;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dnion.app.android.injuriesapp.camera_tool.DeepModelDisplayView;
import com.dnion.app.android.injuriesapp.camera_tool.GlobalDef;
import com.dnion.app.android.injuriesapp.camera_tool.ModelPointinfo;
import com.dnion.app.android.injuriesapp.dao.DeepCameraInfo;
import com.dnion.app.android.injuriesapp.ui.MeasureButton;
import com.dnion.app.android.injuriesapp.utils.AlertDialogUtil;
import com.dnion.app.android.injuriesapp.utils.BitmapUtils;
import com.dnion.app.android.injuriesapp.utils.ToastUtil;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Point3;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yy on 2017/6/18.
 */

public class RecordFragmentWoundIRMeasure extends Fragment {
    public static final String TAG = "record_fragment_camera";
    public static final double length_factor = 2.2;
    public static final double WIDTH_PER_PIX = (Math.tan(0.5236) / 320) * 1000 / length_factor;
    public static final double HEIGHT_PER_PIX = (Math.tan(0.4014) / 240) * 1000 / length_factor;
    public static final double AREA_PER_PIX = (Math.tan(0.5236) / 320) * (Math.tan(0.4014) / 240) * 10 / length_factor;
    public static final int DRAW_COLOR = Color.YELLOW;
    private double min_deep = Double.MAX_VALUE;
    private double max_deep = 0;
    private MainActivity mActivity;

    private int displayMode = 1; // 1 rgb, 2 depth

    private Bitmap mWoundRgbBitmap;
    private Bitmap mAreaMeasureBitmap;
    private Bitmap mLegendBitmap;
    private Bitmap mTempPointBitmap;
    //    final Bitmap mBitmap = Bitmap.createBitmap(DEFAULT_PREVIEW_WIDTH, DEFAULT_PREVIEW_HEIGHT, Bitmap.Config.RGB_565);
    private Button mOpenButton;
    private TextView mAreaView;
    private TextView mVolumeView;
    private TextView mMaxTempView;
    private TextView mMinTempView;
    private TextView mMaxTempTipView;
    private TextView mMinTempTipView;
    private TextView mTempTipView;
    private TextView mColorRedView;
    private TextView mColorBlackView;
    private TextView mColorYellowView;
    private TextView mDeepView;
    private int mMeasureStat = 0;
    private ImageView mWoundRgbView;
    private ImageView mAreaMeasureView;
    private ImageView mTempMeasureView;
    private ImageView mLegendView;
    private DeepModelDisplayView mModelView;
    private GestureDetector gestureDetector;
    private Paint areaPaint;
    private Paint tempPointpaint;
    private Canvas areaCanvas;
    private Canvas tempPointCanvas;
    private Canvas widthCanvas;

    private DeepCameraInfo deepCameraInfo;

    private LinearLayout measure_bar;

    private ImageButton btn_measure_bar;

    private LinearLayout edit_bar;

    private ImageButton btn_measure_edit_bar;

    private LinearLayout menu_bar;

    private ImageButton btn_menu_bar;

    private float displayFactor;


    public static RecordFragmentWoundIRMeasure createInstance() {
        RecordFragmentWoundIRMeasure fragment = new RecordFragmentWoundIRMeasure();

        Bundle bundle = new Bundle();
        //bundle.putLong(ARGUMENT_USER_ID, userId);
        //bundle.putString(ARGUMENT_USER_NAME, userName);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) RecordFragmentWoundIRMeasure.this.getActivity();
        //userDao = new UserDao(mActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.wound_measure_ir, container, false);
        configView(rootView);
        return rootView;
    }

    private void configView(View rootView) {
        initMenuBar(rootView);
        initRgbview(rootView);
    }

    private void initRgbview(View rootView) {
        deepCameraInfo = mActivity.getDeepCameraInfo();
        mWoundRgbView = (ImageView) rootView.findViewById(R.id.wound_rgb_image);
        gestureDetector = new GestureDetector(this.getContext(), onGestureListener);
        mAreaMeasureView = (ImageView) rootView.findViewById(R.id.area_image);
        mAreaMeasureView.setImageBitmap(mAreaMeasureBitmap);

        mLegendView = (ImageView) rootView.findViewById(R.id.legend_image);
        mLegendView.setImageBitmap(mLegendBitmap);

        mTempMeasureView = (ImageView) rootView.findViewById(R.id.temp_point_image);
        mTempMeasureView.setImageBitmap(mTempPointBitmap);

        mOpenButton = (Button) rootView.findViewById(R.id.measure_btn_area);
        mOpenButton.setOnClickListener(mMeasureAreaListener);
        mOpenButton = (Button) rootView.findViewById(R.id.measure_btn_temp);
        mOpenButton.setOnClickListener(mMeasureTmepListener);
        //  手势相关
        mAreaMeasureView.setOnTouchListener(mTouchEvent);
        mAreaMeasureView.setClickable(true);

        ApplicationInfo appInfo = mActivity.getApplicationInfo();
        int resID = getResources().getIdentifier("ir_legend", "mipmap", appInfo.packageName);
        mLegendBitmap = BitmapFactory.decodeResource(getResources(), resID);

        mMaxTempView = (TextView) rootView.findViewById(R.id.max_temp_view);
        mMinTempView = (TextView) rootView.findViewById(R.id.min_temp_view);
        mMaxTempTipView = (TextView) rootView.findViewById(R.id.measure_tip_max_temp);
        mMinTempTipView = (TextView) rootView.findViewById(R.id.measure_tip_min_temp);
        mTempTipView = (TextView) rootView.findViewById(R.id.measure_tip_temp);
        // mColorRedView = (TextView) rootView.findViewById(R.id.color_rate_red);
        // mColorBlackView = (TextView) rootView.findViewById(R.id.color_rate_black);
        // mColorYellowView = (TextView) rootView.findViewById(R.id.color_rate_yellow);
        // mDeepView = (TextView) rootView.findViewById(R.id.wound_deep_view);
        displayMat();
        int width = mWoundRgbBitmap.getWidth();
        int height = mWoundRgbBitmap.getHeight();
        mAreaMeasureBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mTempPointBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        areaPaint = new Paint();
        areaPaint.setColor(GlobalDef.AREA_COLOR);
        tempPointpaint = new Paint();
        tempPointpaint.setColor(GlobalDef.DEEP_COLOR);

        areaCanvas = new Canvas(mAreaMeasureBitmap);
        areaCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//设置为透明，画布也是透明
        tempPointCanvas = new Canvas(mTempPointBitmap);
        tempPointCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//设置为透明，画布也是透明

    }

    private void initMenuBar(View rootView) {
        mActivity.hiddenTopBar();
        mActivity.hiddenSubMenuBar();
        //返回
        Button btn_save_mbi = (Button) rootView.findViewById(R.id.btn_save_mbi);
        btn_save_mbi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inpatientNo = mActivity.getPatientInfo().getInpatientNo();
                if (inpatientNo == null || inpatientNo.length() == 0) {
                    ToastUtil.showLongToast(mActivity, getString(R.string.message_fill_info));
                    return;
                }
                mActivity.showTopBar();
                mActivity.showSubMenuBar();
                //save_layout.setVisibility(View.GONE);
                mActivity.selectMenuButton(R.id.btn_photo);
                Fragment fragment;
                if (deepCameraInfo.isNew()) {
                    fragment = RecordFragmentDeepCamera.createInstance();
                } else {
                    fragment = PhotoListFragment.createInstance();
                }
                mActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, fragment)
                        .commit();
            }
        });
        //基本信息
        ImageButton btn_base_info_w = (ImageButton) rootView.findViewById(R.id.btn_base_info_w);
        btn_base_info_w.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.showTopBar();
                mActivity.showSubMenuBar();
                //save_layout.setVisibility(View.VISIBLE);
                mActivity.selectMenuButton(R.id.btn_base_info);
                BaseInfoFragment fragment = BaseInfoFragment.createInstance();
                mActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, fragment)
                        .commit();
            }
        });

        //档案
        ImageButton btn_archives_w = (ImageButton) rootView.findViewById(R.id.btn_archives_w);
        btn_archives_w.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String inpatientNo = mActivity.getPatientInfo().getInpatientNo();
                if (inpatientNo == null || inpatientNo.length() == 0) {
                    ToastUtil.showLongToast(mActivity, getString(R.string.message_fill_info));
                    return;
                }
                mActivity.showTopBar();
                mActivity.showSubMenuBar();
                //save_layout.setVisibility(View.VISIBLE);
                mActivity.selectMenuButton(R.id.btn_archives);
                RecordFragment fragment = RecordFragment.createInstance();
                mActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, fragment)
                        .commit();
            }
        });

        //照片
        ImageButton btn_photo_w = (ImageButton) rootView.findViewById(R.id.btn_photo_w);
        btn_photo_w.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String inpatientNo = mActivity.getPatientInfo().getInpatientNo();
                if (inpatientNo == null || inpatientNo.length() == 0) {
                    ToastUtil.showLongToast(mActivity, getString(R.string.message_fill_info));
                    return;
                }
                mActivity.showTopBar();
                mActivity.showSubMenuBar();
                //save_layout.setVisibility(View.GONE);
                mActivity.selectMenuButton(R.id.btn_photo);
                PhotoListFragment fragment = PhotoListFragment.createInstance();
                mActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, fragment)
                        .commit();
            }
        });


        MeasureButton measure_btn_rgb = (MeasureButton) rootView.findViewById(R.id.measure_btn_rgb);
        measure_btn_rgb.setText(getString(R.string.measure_color));
        //measure_btn_rgb.setImage(R.mipmap.measure_ok);
        //measure_btn_rgb.setSelectImage(R.mipmap.measure_ok_s);
        measure_btn_rgb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                displayMode = 1;
                displayMat();
            }
        });

        MeasureButton measure_btn_depth = (MeasureButton) rootView.findViewById(R.id.measure_btn_depth);
        measure_btn_depth.setText("温 度");
        //measure_btn_depth.setImage(R.mipmap.measure_delete);
        //measure_btn_depth.setSelectImage(R.mipmap.measure_delete_s);
        measure_btn_depth.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                displayMode = 2;
                displayMat();
            }
        });

        MeasureButton measure_btn_modify = (MeasureButton) rootView.findViewById(R.id.measure_btn_mode);
        measure_btn_modify.setText(getString(R.string.measure_pattern));
        //measure_btn_modify.setImage(R.mipmap.measure_modify);
        //measure_btn_modify.setSelectImage(R.mipmap.measure_modify_s);
        measure_btn_modify.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_measure_edit_bar.callOnClick();
            }
        });

        MeasureButton measure_btn_save = (MeasureButton) rootView.findViewById(R.id.measure_btn_save);
        measure_btn_save.setText(getString(R.string.save));
        measure_btn_save.setImage(R.mipmap.measure_modify);
        measure_btn_save.setSelectImage(R.mipmap.measure_modify_s);
        measure_btn_save.setOnClickListener(mSaveDataListener);


        measure_bar = (LinearLayout) rootView.findViewById(R.id.measure_bar);
        btn_measure_bar = (ImageButton) rootView.findViewById(R.id.btn_measure_bar);
        btn_measure_bar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tagValue = v.getTag();
                float scale=getContext().getResources().getDisplayMetrics().density;
                final float distance = 145* scale;
                float y = measure_bar.getY();

                ValueAnimator valueAnimator = null;
                if (Integer.parseInt("" + tagValue) == 1) {
                    valueAnimator = ValueAnimator.ofFloat(y, y - distance);
                    v.setTag(2);
                } else {
                    valueAnimator = ValueAnimator.ofFloat(y, y + distance);
                    v.setTag(1);
                }
                measureBarAnimator(valueAnimator);
            }
        });
        btn_measure_bar.setTag(2);
        float scale=getContext().getResources().getDisplayMetrics().density;
        measure_bar.setY(measure_bar.getY() - (145* scale));

        menu_bar = (LinearLayout) rootView.findViewById(R.id.menu_bar);
        btn_menu_bar = (ImageButton) rootView.findViewById(R.id.btn_menu_bar);
        btn_menu_bar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tagValue = v.getTag();
                final float distance = 625.0f;
                float y = menu_bar.getY();
                ValueAnimator valueAnimator = null;
                if (Integer.parseInt("" + tagValue) == 1) {
                    valueAnimator = ValueAnimator.ofFloat(0, -1 * distance);
                    v.setTag(2);
                } else {
                    valueAnimator = ValueAnimator.ofFloat(-1 * distance, 0);
                    v.setTag(1);
                }
                valueAnimator.setDuration(400);//设置动画持续时间
                valueAnimator.setRepeatCount(0);//设置重复次数
                valueAnimator.setTarget(menu_bar);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        float currentValue = (Float) animator.getAnimatedValue();
                        // 获得每次变化后的属性值
                        //Log.e("chenwh", "" + currentValue);
                        menu_bar.setY(currentValue);
                        menu_bar.requestLayout();
                    }
                });
                valueAnimator.start();
            }
        });
        btn_menu_bar.setTag(2);
        menu_bar.setY(menu_bar.getY() - 625.0f);

        edit_bar = (LinearLayout) rootView.findViewById(R.id.edit_bar);
        btn_measure_edit_bar = (ImageButton) rootView.findViewById(R.id.btn_measure_edit_bar);
        btn_measure_edit_bar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tagValue = v.getTag();
                final float distance = 350.0f;
                float x = edit_bar.getX();
                float max_x = x + distance;
                ValueAnimator valueAnimator = null;
                if (Integer.parseInt("" + tagValue) == 1) {
                    valueAnimator = ValueAnimator.ofFloat(x, x + distance);
                    v.setTag(2);
                } else {
                    valueAnimator = ValueAnimator.ofFloat(x, x - distance);
                    v.setTag(1);
                }
                valueAnimator.setDuration(300);//设置动画持续时间
                valueAnimator.setRepeatCount(0);//设置重复次数
                valueAnimator.setTarget(edit_bar);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        float currentValue = (Float) animator.getAnimatedValue();
                        // 获得每次变化后的属性值
                        //Log.e("chenwh", "" + currentValue);
                        edit_bar.setX(currentValue);
                        edit_bar.requestLayout();
                    }
                });
                valueAnimator.start();
            }
        });
        btn_measure_edit_bar.setTag(2);
        edit_bar.setX(menu_bar.getX() + 350.0f);
    }

    private void measureBarAnimator(ValueAnimator valueAnimator) {
        valueAnimator.setDuration(300);//设置动画持续时间
        valueAnimator.setRepeatCount(0);//设置重复次数
        valueAnimator.setTarget(measure_bar);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                float currentValue = (Float) animator.getAnimatedValue();
                // 获得每次变化后的属性值
                //Log.e("chenwh", "" + currentValue);
                measure_bar.setY(currentValue);
                measure_bar.requestLayout();
            }
        });
        valueAnimator.start();
    }

    private String formatDouble(double num) {
        String format = new DecimalFormat("#.00").format(num);
        return format;
    }

    private void displayMat() {
        Mat depth = deepCameraInfo.getDepthMat();
        double tempMax = 0;
        double tempMin = Double.MAX_VALUE;
        for (int x = 0; x < depth.cols(); x++) {
            for (int y = 0; y < depth.rows(); y++) {
                double temp = depth.get(y, x)[0];
                tempMax = Math.max(temp, tempMax);
                tempMin = Math.min(temp, tempMin);
            }
        }
        String max_format = formatDouble(tempMax);
        deepCameraInfo.setMaxDeep(deepCameraInfo.getMaxDeep());
        String min_format = formatDouble(tempMin);
        deepCameraInfo.setMaxDeep(deepCameraInfo.getMinDeep());
        mMaxTempView.setText("最大温度：" + max_format + " ℃");
        mMinTempView.setText("最小温度：" + min_format + " ℃");
        if (displayMode == 2) {
            Bitmap rgb = deepCameraInfo.getRgbBitmap();
            mWoundRgbBitmap = rgb;
            mWoundRgbView.setImageBitmap(mWoundRgbBitmap);
        } else if (displayMode == 1) {
            mWoundRgbBitmap = Bitmap.createBitmap(depth.cols(), depth.rows(), Bitmap.Config.ARGB_8888);
            for (int x = 0; x < depth.cols(); x++) {
                for (int y = 0; y < depth.rows(); y++) {
                    double temp = depth.get(y, x)[0];
                    int color = transTemp2Color(temp, tempMax, tempMin);
                    mWoundRgbBitmap.setPixel(x, y, color);
                }
            }
            mWoundRgbView.setImageBitmap(mWoundRgbBitmap);
        }
    }

    private int transTemp2Color(double temp, double tempMax, double tempMin) {
        int length = mLegendBitmap.getHeight() - 1;
        int pos = length - new Double(((temp - tempMin) / (tempMax - tempMin)) * length).intValue();
        int color = mLegendBitmap.getPixel(10, pos);
        //Log.d(TAG, "temp:" + temp + "pos:" + pos + "color:" + color);
        return color;
    }

    private void getTemp(MotionEvent e) {
        tempPointpaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        tempPointCanvas.drawPaint(tempPointpaint);
        tempPointpaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        Bitmap rgb = deepCameraInfo.getRgbBitmap();
        Mat depth = deepCameraInfo.getDepthMat();
        int viewWidth = mWoundRgbView.getWidth();
        int bWidth = mWoundRgbBitmap.getWidth();
        int bHeight = mWoundRgbBitmap.getHeight();
        int tempWidth = depth.cols();
        float depthFactor = new Float(tempWidth) / bWidth;
        //float viewFactor = depthFactor;

        float t_x = new Float(e.getX() * displayFactor);
        int d_x = new Float(t_x * depthFactor).intValue();
        float t_y = new Float(e.getY() * displayFactor);
        int d_y = new Float(t_y * depthFactor).intValue();

        double[] temps = depth.get(d_y, d_x);
        String temp = new DecimalFormat("#.00°C").format(temps[0]);

        mTempTipView.setText(temp);
        mTempTipView.setX(e.getX());
        mTempTipView.setY(e.getY());
        mTempTipView.setVisibility(View.VISIBLE);
    }

    private void getTempOld(MotionEvent e) {
        tempPointpaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        tempPointCanvas.drawPaint(tempPointpaint);
        tempPointpaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        Bitmap rgb = deepCameraInfo.getRgbBitmap();
        Mat depth = deepCameraInfo.getDepthMat();
        int viewWidth = mWoundRgbView.getWidth();
        int bWidth = mWoundRgbBitmap.getWidth();
        int bHeight = mWoundRgbBitmap.getHeight();
        int tempWidth = depth.cols();
        float depthFactor = new Float(tempWidth) / bWidth;
        //float viewFactor = depthFactor;

        float t_x = new Float(e.getX() * displayFactor);
        int d_x = new Float(t_x * depthFactor).intValue();
        float t_y = new Float(e.getY() * displayFactor);
        int d_y = new Float(t_y * depthFactor).intValue();

        int text_witdh_diff = 50;
        int text_heigth_diff = 10;
        int tc_diff = 20;
        float text_x = t_x < text_witdh_diff ? t_x : t_x - text_witdh_diff;
        float text_y = (t_y < text_heigth_diff ? t_y + text_heigth_diff : t_y) - 15;
        float bolb = tempPointpaint.getStrokeWidth();
        double[] temps = depth.get(d_y, d_x);
        String temp = new DecimalFormat("#.00°C").format(temps[0]);

        tempPointpaint.setStrokeWidth(GlobalDef.FOCUS_STROKE_WIDTH);
        tempPointCanvas.drawText(temp, text_x, text_y, tempPointpaint);
        tempPointCanvas.drawLine(t_x - tc_diff, t_y, t_x + tc_diff, t_y, tempPointpaint);
        tempPointCanvas.drawLine(t_x, t_y - tc_diff, t_x, t_y + tc_diff, tempPointpaint);
        tempPointpaint.setStrokeWidth(bolb);

        mTempMeasureView.setImageBitmap(mTempPointBitmap);
    }

    private final OnClickListener mMeasureAreaListener = new OnClickListener() {
        @Override
        public void onClick(final View view) {
            mMeasureStat = 1;
            ToastUtil.showLongToast(mActivity, "请圈选伤口边缘");
            //canvas.restoreToCount(areaLayerID);
        }
    };

    private final OnClickListener mMeasureTmepListener = new OnClickListener() {
        @Override
        public void onClick(final View view) {
            mMeasureStat = 2;
            ToastUtil.showLongToast(mActivity, "请划定伤口位置");
            //canvas.restoreToCount(lengthLayerID);
        }
    };

    private final OnClickListener mSaveDataListener = new OnClickListener() {
        @Override
        public void onClick(final View view) {
            view.setClickable(false);
            AlertDialogUtil.showAlertDialog(mActivity,
                    mActivity.getString(R.string.message_title_tip),
                    mActivity.getString(R.string.message_wait_save));

            Bitmap pdf = BitmapUtils.mergeBitmap(mWoundRgbBitmap, mAreaMeasureBitmap,
                    BitmapUtils.getViewBitmap(mMaxTempTipView),
                    BitmapUtils.getViewBitmap(mMinTempTipView));
            deepCameraInfo.setPdfBitmap(pdf);
            mActivity.saveIRCameraInfo();
            ToastUtil.showShortToast(mActivity, "保存成功");
            AlertDialogUtil.dismissAlertDialog(mActivity);
            view.setClickable(true);
        }

    };

    private View.OnTouchListener mTouchEvent = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // 最后一个点
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Log.i("touch", "up");
                switch (mMeasureStat) {
                    case 1:
                        upArea(event);
                        break;
                    case 2:
                        getTemp(event);
                        break;
                }

                return true;
            }
            if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                Log.i("touch", "cancel");
            }
            gestureDetector.onTouchEvent(event);
            return true;
        }

    };

    private GestureDetector.OnGestureListener onGestureListener =
            new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    switch (mMeasureStat) {
                        case 1:
                            drawArea(e1, e2);
                            break;
                    }
                    return true;
                }

                @Override
                public boolean onDown(MotionEvent e) {
//                    Log.i("touch", "down" + e.getX() + ","+ e.getY());
                    displayFactor = new Float(mAreaMeasureBitmap.getWidth()) / mWoundRgbView.getWidth();
                    switch (mMeasureStat) {
                        case 1:
                            downArea(e);
                            break;
                        case 2:
                            getTemp(e);
                            break;

                    }
                    return true;

                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    Log.i("touch", "fling");
                    return true;
                }
            };

    private void downArea(MotionEvent e) {
        areaPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        areaCanvas.drawPaint(areaPaint);
        areaPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        deepCameraInfo.getAreaPointList().clear();

        float x = Math.round(e.getX() * displayFactor);
        float y = Math.round(e.getY() * displayFactor);
        deepCameraInfo.getAreaPointList().add(new Point(x, y));
    }

    private void drawArea(MotionEvent e1, MotionEvent e2) {
        List<Point> areaPointList = deepCameraInfo.getAreaPointList();
        Point last_p = areaPointList.get(areaPointList.size() - 1);
        float x1 = (float) last_p.x;
        float y1 = (float) last_p.y;
        float x2 = Math.round(e2.getX() * displayFactor);
        float y2 = Math.round(e2.getY() * displayFactor);
        areaCanvas.drawLine(x1, y1, x2, y2, areaPaint);
        mAreaMeasureView.setImageBitmap(mAreaMeasureBitmap);
        areaPointList.add(new Point(x2, y2));

    }

    private void upArea(MotionEvent e) {
        fillArea();
        // 计算面积
        clacArea();
        setArea();
    }

    private void fillArea() {
        List<Point> areaPointList = deepCameraInfo.getAreaPointList();
        if (areaPointList.size() <= 0) {
            return;
        }
        Path path = new Path();
        Point p = areaPointList.get(areaPointList.size() - 1);
        path.moveTo((float) p.x, (float) p.y);
        for (int i = areaPointList.size() - 2; i >= 0; i--) {
            p = areaPointList.get(i);
            path.lineTo((float) p.x, (float) p.y);
        }
        path.close();
        areaPaint.setStyle(Paint.Style.FILL);
        areaCanvas.drawPath(path, areaPaint);
        mAreaMeasureView.setImageBitmap(mAreaMeasureBitmap);
    }

    private void clacArea() {
        Mat tempMat = deepCameraInfo.getDepthMat();
        int m_width = mAreaMeasureBitmap.getWidth();
        int m_heigth = mAreaMeasureBitmap.getHeight();
        int d_i = 0;
        int d_j = 0;
        float tempFactor = ((float) tempMat.cols()) / mAreaMeasureBitmap.getWidth();
        Point3 max_temp = new Point3(0, 0, 0);
        Point3 min_temp = new Point3(0, 0, Integer.MAX_VALUE);
        for (int i = m_width - 1; i >= 0; i--) {
            for (int j = m_heigth - 1; j > -0; j--) {
                if (mAreaMeasureBitmap.getPixel(i, j) == GlobalDef.AREA_COLOR) {
                    d_i = new Float(i * tempFactor).intValue();
                    d_j = new Float(j * tempFactor).intValue();

                    double[] temps = tempMat.get(d_j, d_i);
                    if (temps[0] > max_temp.z) {
                        max_temp.z = temps[0];
                        max_temp.x = d_i;
                        max_temp.y = d_j;
                    }
                    if (temps[0] < min_temp.z) {
                        min_temp.z = temps[0];
                        min_temp.x = d_i;
                        min_temp.y = d_j;
                    }
                }
            }
        }
        deepCameraInfo.setMaxDeepPoint(max_temp);
        deepCameraInfo.setMinDeepPoint(min_temp);
    }

    private void setArea() {
        String max_format = formatDouble(deepCameraInfo.getMaxDeep());
        String min_format = formatDouble(deepCameraInfo.getMinDeep());
        float tip_factor = (float) mAreaMeasureView.getWidth() / deepCameraInfo.getDepthMat().cols();
        //float tip_factor = (float) mAreaMeasureBitmap.getWidth() / deepCameraInfo.getDepthMat().cols();

        mMaxTempTipView.setText("max：" + max_format + "°C");
        mMaxTempTipView.setX(new Double(deepCameraInfo.getMaxDeepPoint().x).floatValue() * tip_factor);
        mMaxTempTipView.setY(new Double(deepCameraInfo.getMaxDeepPoint().y).floatValue() * tip_factor);
        mMaxTempTipView.setVisibility(View.VISIBLE);

        mMinTempTipView.setText("min：" + min_format + "°C");
        mMinTempTipView.setX(new Double(deepCameraInfo.getMinDeepPoint().x).floatValue() * tip_factor);
        mMinTempTipView.setY(new Double(deepCameraInfo.getMinDeepPoint().y).floatValue() * tip_factor);
        mMinTempTipView.setVisibility(View.VISIBLE);

        mMaxTempView.setText("最大温度：" + max_format + "°C");
        mMinTempView.setText("最小温度：" + min_format + "°C");
    }

    private final OnClickListener mBackListener = new OnClickListener() {
        @Override
        public void onClick(final View view) {
            Fragment fragment;
            fragment = RecordFragmentWoundImage.createInstance();
            mActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, fragment)
                    .commit();

        }

    };

    @Override
    public void onPause() {
        super.onPause();
    }

    public void onDestroy() {
        super.onDestroy();
    }


}
