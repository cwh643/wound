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
import com.dnion.app.android.injuriesapp.camera_tool.ModelPointinfo;
import com.dnion.app.android.injuriesapp.dao.DeepCameraInfo;
import com.dnion.app.android.injuriesapp.ui.MeasureButton;
import com.dnion.app.android.injuriesapp.utils.AlertDialogUtil;
import com.dnion.app.android.injuriesapp.utils.ToastUtil;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;

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
    //    final Bitmap mBitmap = Bitmap.createBitmap(DEFAULT_PREVIEW_WIDTH, DEFAULT_PREVIEW_HEIGHT, Bitmap.Config.RGB_565);
    private Button mOpenButton;
    private TextView mAreaView;
    private TextView mVolumeView;
    private TextView mMaxTempView;
    private TextView mMinTempView;
    private TextView mColorRedView;
    private TextView mColorBlackView;
    private TextView mColorYellowView;
    private TextView mDeepView;
    private int mMeasureStat = 0;
    private ImageView mWoundRgbView;
    private ImageView mAreaMeasureView;
    private ImageView mLegendView;
    private DeepModelDisplayView mModelView;
    private GestureDetector gestureDetector;
    private Paint paint;
    private Canvas areaCanvas;
    private Canvas lengthCanvas;
    private Canvas widthCanvas;

    private DeepCameraInfo deepCameraInfo;
    private int areaLayerID;
    private int lengthLayerID;
    private int widthLayerID;


    private LinearLayout measure_bar;

    private ImageButton btn_measure_bar;

    private LinearLayout edit_bar;

    private ImageButton btn_measure_edit_bar;

    private LinearLayout menu_bar;

    private ImageButton btn_menu_bar;


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

        ApplicationInfo appInfo = mActivity.getApplicationInfo();
        int resID = getResources().getIdentifier("ir_legend", "mipmap", appInfo.packageName);
        mLegendBitmap = BitmapFactory.decodeResource(getResources(), resID);

        //  手势相关
        mAreaMeasureView.setOnTouchListener(mTouchEvent);
        mAreaMeasureView.setClickable(true);

        mMaxTempView = (TextView) rootView.findViewById(R.id.max_temp_view);
        mMinTempView = (TextView) rootView.findViewById(R.id.min_temp_view);
       // mColorRedView = (TextView) rootView.findViewById(R.id.color_rate_red);
       // mColorBlackView = (TextView) rootView.findViewById(R.id.color_rate_black);
       // mColorYellowView = (TextView) rootView.findViewById(R.id.color_rate_yellow);
       // mDeepView = (TextView) rootView.findViewById(R.id.wound_deep_view);
        displayMat();
        int width = mWoundRgbBitmap.getWidth();
        int height = mWoundRgbBitmap.getHeight();
        mAreaMeasureBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //mLengthMeasureBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        //mWidthMeasureBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        paint = new Paint();
        paint.setColor(DRAW_COLOR);
        areaCanvas = new Canvas(mAreaMeasureBitmap);
        areaCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//设置为透明，画布也是透明

    }

    private void initMenuBar(View rootView) {
        mActivity.hiddenTopBar();
        mActivity.hiddenSubMenuBar();
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
                    ToastUtil.showLongToast(mActivity, "先填写患者基本信息");
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
                    ToastUtil.showLongToast(mActivity, "先填写患者基本信息");
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
        measure_btn_rgb.setText("彩 色");
        measure_btn_rgb.setImage(R.mipmap.measure_ok);
        measure_btn_rgb.setSelectImage(R.mipmap.measure_ok_s);
        measure_btn_rgb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                displayMode = 1;
                displayMat();
            }
        });

        MeasureButton measure_btn_depth = (MeasureButton) rootView.findViewById(R.id.measure_btn_depth);
        measure_btn_depth.setText("温 度");
        measure_btn_depth.setImage(R.mipmap.measure_delete);
        measure_btn_depth.setSelectImage(R.mipmap.measure_delete_s);
        measure_btn_depth.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                displayMode = 2;
                displayMat();
            }
        });

        MeasureButton measure_btn_modify = (MeasureButton) rootView.findViewById(R.id.measure_btn_mode);
        measure_btn_modify.setText("模 式");
        measure_btn_modify.setImage(R.mipmap.measure_modify);
        measure_btn_modify.setSelectImage(R.mipmap.measure_modify_s);
        measure_btn_modify.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_measure_edit_bar.callOnClick();
            }
        });

        MeasureButton measure_btn_save = (MeasureButton) rootView.findViewById(R.id.measure_btn_save);
        measure_btn_save.setText("保 存");
        measure_btn_save.setImage(R.mipmap.measure_modify);
        measure_btn_save.setSelectImage(R.mipmap.measure_modify_s);
        measure_btn_save.setOnClickListener(mSaveDataListener);

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
        String max_format = formatDouble(tempMax / 100);
        deepCameraInfo.setMaxDeep(new Float(max_format));
        String min_format = formatDouble(tempMin / 100);
        deepCameraInfo.setMaxDeep(new Float(min_format));
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
        int length = mLegendBitmap.getHeight();
        int pos = length - 1 - new Double(((temp - tempMin) / (tempMax - tempMin)) * (length - 1)).intValue();
        int color = mLegendBitmap.getPixel(10, pos);
        //Log.d(TAG, "temp:" + temp + "pos:" + pos + "color:" + color);
        return color;
    }

    private View.OnTouchListener mTouchEvent = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // 最后一个点
            //gestureDetector.onTouchEvent(event);
            getTemp(event);
            return true;
        }

    };

    private GestureDetector.OnGestureListener onGestureListener =
            new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    //getTemp(e2);
                    return true;
                }

                @Override
                public boolean onDown(MotionEvent e) {
//                    Log.i("touch", "down" + e.getX() + ","+ e.getY());
                    //getTemp(e);
                    return true;
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    Log.i("touch", "fling");
                    return true;
                }
            };

    private void getTemp(MotionEvent e) {
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        areaCanvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        Bitmap rgb = deepCameraInfo.getRgbBitmap();
        Mat depth = deepCameraInfo.getDepthMat();
        int viewWidth = mWoundRgbView.getWidth();
        int bWidth = rgb.getWidth();
        int bHeight = rgb.getHeight();
        int tempWidth = depth.cols();
        float viewFactor = new Float(bWidth) / viewWidth;
        float depthFactor = new Float(tempWidth) / bWidth;

        float t_x = new Float(e.getX() * viewFactor);
        int d_x = new Float(t_x * depthFactor).intValue();
        float t_y = new Float(e.getY() * viewFactor);
        int d_y = new Float(t_y * depthFactor).intValue();
        // 取温度数据
        double[] temps = depth.get(d_y, d_x);
        String temp = new DecimalFormat("#.00").format(temps[0] / 100);
        float text_x = t_x < 40 ? t_x : t_x - 40;
        float text_y = t_y < 13 ? t_y + 13 : t_y;
        areaCanvas.drawText(temp, text_x, text_y, paint);
        areaCanvas.drawLine(0, t_y, bWidth - 1, t_y, paint);
        areaCanvas.drawLine(t_x, 0, t_x, bHeight, paint);
        mAreaMeasureView.setImageBitmap(mAreaMeasureBitmap);
    }

    private final OnClickListener mModelDisplayListener = new OnClickListener() {
        @Override
        public void onClick(final View view) {
            if (deepCameraInfo.getVertexList().size() <= 0) {
                return;
            }
            RecordFragmentModelDisplay fragment = RecordFragmentModelDisplay.createInstance();
            mActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, fragment)
                    .commit();
        }

    };

    private void syncWoundNum1() {
        mActivity.getRecordInfo().setWoundArea(1.0f);
        mActivity.getRecordInfo().setWoundWidth(2.0f);
        mActivity.getRecordInfo().setWoundHeight(3.0f);
        mActivity.getRecordInfo().setWoundVolume(4.0f);
        mActivity.getRecordInfo().setWoundDeep(5.0f);
        mActivity.getRecordInfo().setWoundColorRed(6.0f);
        mActivity.getRecordInfo().setWoundColorYellow(7.0f);
        mActivity.getRecordInfo().setWoundColorBlack(8.0f);
    }

    private final OnClickListener mSaveDataListener = new OnClickListener() {
        @Override
        public void onClick(final View view) {
            view.setClickable(false);
            AlertDialogUtil.showAlertDialog(mActivity,
                    mActivity.getString(R.string.message_title_tip),
                    mActivity.getString(R.string.message_wait_save));
            mActivity.saveIRCameraInfo();
            ToastUtil.showShortToast(mActivity, "保存成功");
            AlertDialogUtil.dismissAlertDialog(mActivity);
            view.setClickable(true);
        }

    };

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
