package com.dnion.app.android.injuriesapp;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
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
import com.dnion.app.android.injuriesapp.camera_tool.PointInfo3D;
import com.dnion.app.android.injuriesapp.dao.DeepCameraInfo;
import com.dnion.app.android.injuriesapp.ui.MeasureButton;
import com.dnion.app.android.injuriesapp.utils.AlertDialogUtil;
import com.dnion.app.android.injuriesapp.utils.BitmapUtils;
import com.dnion.app.android.injuriesapp.utils.ToastUtil;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by yy on 2017/6/18.
 */

public class RecordFragmentWoundMeasure extends Fragment {
    public static final String TAG = "record_fragment_camera";
    public static final double length_factor = 2.2;
    public static final double WIDTH_PER_PIX = (Math.tan(0.5236) / 320) * 1000 / length_factor;
    public static final double HEIGHT_PER_PIX = (Math.tan(0.4014) / 240) * 1000 / length_factor;
    public static final double AREA_PER_PIX = (Math.tan(0.5236) / 320) * (Math.tan(0.4014) / 240) * 10 / length_factor;
    private double min_deep = Double.MAX_VALUE;
    private double max_deep = 0;
    private MainActivity mActivity;

    private int displayMode = 1; // 1 rgb, 2 depth, 3 orgin rgb

    private Bitmap mWoundRgbBitmap;
    private Bitmap mAreaMeasureBitmap;
    private Bitmap mLengthMeasureBitmap;
    private Bitmap mWidthMeasureBitmap;
    private Bitmap mDeepMeasureBitmap;
    //    final Bitmap mBitmap = Bitmap.createBitmap(DEFAULT_PREVIEW_WIDTH, DEFAULT_PREVIEW_HEIGHT, Bitmap.Config.RGB_565);
    private Button mOpenButton;
    private TextView mAreaView;
    private TextView mVolumeView;
    private TextView mLengthView;
    private TextView mWidthView;
    private TextView mColorRedView;
    private TextView mColorBlackView;
    private TextView mColorYellowView;
    private TextView mDeepView;
    private int mMeasureStat = 0;
    PointInfo3D wound_deep_first;
    boolean deep_mode_first = true;
    private ImageView mWoundRgbView;
    private ImageView mWoundOrgRgbView;
    private ImageView mAreaMeasureView;
    private ImageView mLengthMeasureView;
    private ImageView mWidthMeasureView;
    private ImageView mDeepMeasureView;
    private DeepModelDisplayView mModelView;
    private GestureDetector gestureDetector;
    private Paint paint;
    private Canvas areaCanvas;
    private Canvas lengthCanvas;
    private Canvas widthCanvas;
    private Canvas deepCanvas;

    private DeepCameraInfo deepCameraInfo;
    private float displayFactor;


    private LinearLayout measure_bar;

    private ImageButton btn_measure_bar;

    private LinearLayout edit_bar;

    private ImageButton btn_measure_edit_bar;

    private LinearLayout menu_bar;

    private ImageButton btn_menu_bar;

    private Mat mFilterDepth;

    public static RecordFragmentWoundMeasure createInstance() {
        RecordFragmentWoundMeasure fragment = new RecordFragmentWoundMeasure();

        Bundle bundle = new Bundle();
        //bundle.putLong(ARGUMENT_USER_ID, userId);
        //bundle.putString(ARGUMENT_USER_NAME, userName);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) RecordFragmentWoundMeasure.this.getActivity();
        //userDao = new UserDao(mActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.wound_measure, container, false);
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
        mWoundOrgRgbView = (ImageView) rootView.findViewById(R.id.org_rgb_image);
        gestureDetector = new GestureDetector(this.getContext(), onGestureListener);
        mAreaMeasureBitmap = Bitmap.createBitmap(GlobalDef.RES_CALC_WIDTH, GlobalDef.RES_CALC_HEIGHT, Bitmap.Config.ARGB_8888);
        mLengthMeasureBitmap = Bitmap.createBitmap(GlobalDef.RES_CALC_WIDTH, GlobalDef.RES_CALC_HEIGHT, Bitmap.Config.ARGB_8888);
        mWidthMeasureBitmap = Bitmap.createBitmap(GlobalDef.RES_CALC_WIDTH, GlobalDef.RES_CALC_HEIGHT, Bitmap.Config.ARGB_8888);
        mDeepMeasureBitmap = Bitmap.createBitmap(GlobalDef.RES_CALC_WIDTH, GlobalDef.RES_CALC_HEIGHT, Bitmap.Config.ARGB_8888);

        mAreaMeasureView = (ImageView) rootView.findViewById(R.id.area_image);
        mLengthMeasureView = (ImageView) rootView.findViewById(R.id.length_image);
        mWidthMeasureView = (ImageView) rootView.findViewById(R.id.width_image);
        mDeepMeasureView = (ImageView) rootView.findViewById(R.id.deep_image);
        //  手势相关
        mAreaMeasureView.setOnTouchListener(mTouchEvent);
        mAreaMeasureView.setClickable(true);
        mOpenButton = (Button) rootView.findViewById(R.id.measure_btn_area);
        mOpenButton.setOnClickListener(mMeasureAreaListener);
        mOpenButton = (Button) rootView.findViewById(R.id.measure_btn_length);
        mOpenButton.setOnClickListener(mMeasureLengthListener);
        mOpenButton = (Button) rootView.findViewById(R.id.measure_btn_width);
        mOpenButton.setOnClickListener(mMeasureWidthListener);
        mOpenButton = (Button) rootView.findViewById(R.id.measure_btn_deep);
        mOpenButton.setOnClickListener(mMeasureDeepListener);
        mOpenButton = (Button) rootView.findViewById(R.id.measure_btn_model);
        mOpenButton.setOnClickListener(mModelDisplayListener);
        mAreaView = (TextView) rootView.findViewById(R.id.area_view);
        mVolumeView = (TextView) rootView.findViewById(R.id.volume_view);
        mLengthView = (TextView) rootView.findViewById(R.id.length_view);
        mWidthView = (TextView) rootView.findViewById(R.id.width_view);
        mColorRedView = (TextView) rootView.findViewById(R.id.color_rate_red);
        mColorBlackView = (TextView) rootView.findViewById(R.id.color_rate_black);
        mColorYellowView = (TextView) rootView.findViewById(R.id.color_rate_yellow);
        mDeepView = (TextView) rootView.findViewById(R.id.wound_deep_view);
        wound_deep_first = new PointInfo3D();

        displayMat();

        paint = new Paint();
        paint.setStrokeWidth(10);
        paint.setTextSize(60);
        areaCanvas = new Canvas(mAreaMeasureBitmap);
        areaCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//设置为透明，画布也是透明
        lengthCanvas = new Canvas(mLengthMeasureBitmap);
        lengthCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//设置为透明，画布也是透明
        widthCanvas = new Canvas(mWidthMeasureBitmap);
        widthCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//设置为透明，画布也是透明
        deepCanvas = new Canvas(mDeepMeasureBitmap);
        deepCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//设置为透明，画布也是透明


        if (deepCameraInfo.getAreaPointList().size() > 0) {
            paint.setColor(GlobalDef.AREA_COLOR);
            fillArea();
            setArea();
        }
        if (deepCameraInfo.getLengthPointList().size() > 1) {
            paint.setColor(GlobalDef.LENGTH_COLOR);
            fillLength();
            setLength();
        }
        if (deepCameraInfo.getWidthPointList().size() > 1) {
            paint.setColor(GlobalDef.WIDTH_COLOR);
            fillWidth();
            setWidth();
        }
    }

    private void initMenuBar(View rootView) {
        mActivity.hiddenTopBar();
        mActivity.hiddenSubMenuBar();
        //基本信息
        ImageButton btn_base_info_w = (ImageButton) rootView.findViewById(R.id.btn_base_info_w);
        btn_base_info_w.setOnClickListener(new View.OnClickListener() {
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
        btn_archives_w.setOnClickListener(new View.OnClickListener() {
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
        btn_photo_w.setOnClickListener(new View.OnClickListener() {
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


        MeasureButton measure_btn_rgb = (MeasureButton) rootView.findViewById(R.id.measure_btn_rgb);
        measure_btn_rgb.setText("彩 色");
        measure_btn_rgb.setImage(R.mipmap.measure_ok);
        measure_btn_rgb.setSelectImage(R.mipmap.measure_ok_s);
        measure_btn_rgb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayMode = 1;
                displayMat();
            }
        });

        MeasureButton measure_btn_org_rgb = (MeasureButton) rootView.findViewById(R.id.measure_btn_org_rgb);
        measure_btn_org_rgb.setText("底 片");
        measure_btn_org_rgb.setImage(R.mipmap.measure_ok);
        measure_btn_org_rgb.setSelectImage(R.mipmap.measure_ok_s);
        measure_btn_org_rgb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayMode = 3;
                displayMat();
            }
        });

        MeasureButton measure_btn_depth = (MeasureButton) rootView.findViewById(R.id.measure_btn_depth);
        measure_btn_depth.setText("深 度");
        measure_btn_depth.setImage(R.mipmap.measure_delete);
        measure_btn_depth.setSelectImage(R.mipmap.measure_delete_s);
        measure_btn_depth.setOnClickListener(new View.OnClickListener() {
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
        measure_btn_modify.setOnClickListener(new View.OnClickListener() {
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

        MeasureButton measure_btn_edit = (MeasureButton) rootView.findViewById(R.id.measure_btn_edit);
        measure_btn_edit.setText("测 量");
        measure_btn_edit.setImage(R.mipmap.measure_edit);
        measure_btn_edit.setSelectImage(R.mipmap.measure_edit_s);
        measure_btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_measure_bar.callOnClick();
            }
        });

        measure_bar = (LinearLayout) rootView.findViewById(R.id.measure_bar);
        btn_measure_bar = (ImageButton) rootView.findViewById(R.id.btn_measure_bar);
        btn_measure_bar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tagValue = v.getTag();
                final float distance = 535.0f;
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
        measure_bar.setY(measure_bar.getY() - 535.0f);

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
        btn_menu_bar.setTag(1);
        menu_bar.setY(menu_bar.getY());

        edit_bar = (LinearLayout) rootView.findViewById(R.id.edit_bar);
        btn_measure_edit_bar = (ImageButton) rootView.findViewById(R.id.btn_measure_edit_bar);
        btn_measure_edit_bar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tagValue = v.getTag();
                final float distance = 525.0f;
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
        edit_bar.setX(menu_bar.getX() + 525.0f);
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

    private void displayMat() {
        //Mat tmpMap = new Mat(mBitmap.getHeight(), mBitmap.getWidth(), CvType.CV_8UC1);
        //deepCameraInfo.getDepthMat().convertTo(tmpMap, tmpMap.type());
        Mat mDepth = deepCameraInfo.getDepthMat();
        // 在此加入中值滤波器，
        mFilterDepth = new Mat(mDepth.rows(), mDepth.cols(), mDepth.type());
        Imgproc.medianBlur(mDepth, mFilterDepth, 3);
        Bitmap rgb = deepCameraInfo.getRgbBitmap();
        int lx = deepCameraInfo.getDeep_lx();
        int ly = deepCameraInfo.getDeep_ly();
        int rx = deepCameraInfo.getDeep_rx();
        int ry = deepCameraInfo.getDeep_ry();
        int bitmapWidth = rx - lx;
        int bitmapHeight = ry - ly;
        mWoundOrgRgbView.setVisibility(View.INVISIBLE);
        if (displayMode == 1) {
            mWoundRgbBitmap = Bitmap.createBitmap(rgb, lx, ly, bitmapWidth, bitmapHeight);
            mWoundRgbView.setImageBitmap(mWoundRgbBitmap);
        } else if (displayMode == 2) {
            Mat tmpMap = new Mat(mDepth.rows(), mDepth.cols(), CvType.CV_8UC1);
            mDepth.convertTo(tmpMap, tmpMap.type());
            Bitmap orgDepth = Bitmap.createBitmap(mDepth.cols(), mDepth.rows(), Bitmap.Config.RGB_565);
            Utils.matToBitmap(tmpMap, orgDepth);
            Bitmap depthBitmap = Bitmap.createBitmap(orgDepth, lx, ly, bitmapWidth, bitmapHeight);
            mWoundRgbView.setImageBitmap(depthBitmap);
        } else if (displayMode == 3) {
            mWoundOrgRgbView.setImageBitmap(rgb);
            mWoundOrgRgbView.setVisibility(View.VISIBLE);
        }

    }

    private void displayMat1() {

        int lx = deepCameraInfo.getDeep_lx();
        int ly = deepCameraInfo.getDeep_ly();
        int rx = deepCameraInfo.getDeep_rx();
        int ry = deepCameraInfo.getDeep_ry();
        int bitmapWidth = rx - lx;
        int bitmapHeight = ry - ly;
        Mat mDepth = deepCameraInfo.getDepthMat();
        Bitmap rgb = deepCameraInfo.getRgbBitmap();
        mWoundRgbBitmap = Bitmap.createBitmap(rgb, lx, ly, bitmapWidth, bitmapHeight);
        mWoundRgbView.setImageBitmap(mWoundRgbBitmap);
    }

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
                        upLength(event);
                        break;
                    case 3:
                        upWidth(event);
                        break;
                    case 4:
                        upDeep(event);
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
                        case 2:
                            drawlength(e1, e2);
                            break;
                        case 3:
                            drawWidth(e1, e2);
                            break;
                        case 4:
                            drawDeep(e1, e2);
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
                            downLength(e);
                            break;
                        case 3:
                            downWidth(e);
                            break;
                        case 4:
                            downDeep(e);
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
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        areaCanvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
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
        areaCanvas.drawLine(x1, y1, x2, y2, paint);
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
        paint.setStyle(Paint.Style.FILL);
        areaCanvas.drawPath(path, paint);
        mAreaMeasureView.setImageBitmap(mAreaMeasureBitmap);
    }

    private double getDeep(double[] deep_arr) {
        if (deep_arr == null) {
            return 0;
        }
        double deep = deep_arr[0];
        if (deep == 0 || deep < GlobalDef.CALC_MIN_DEEP || deep > GlobalDef.CALC_MAX_DEEP) {
            return 0;
        }
        return deep;
    }

    private double filterPointMediaBule(Mat depth, int x, int y) {
        // 取周围的点， 如果和周围最近点的距离超过阈值，则抛弃
        int dis = GlobalDef.MODEL_DEEP_CENTER_DIS;
        double minDeep = Double.MAX_VALUE;
        double maxDeep = 0;
        List<Double> deep_list = new ArrayList<>();
        int lx = x - dis;
        int rx = x + dis + 2;
        int ly = y - dis;
        int ry = y + dis + 2;
        for (int i = lx; i < rx; i++) {
            if (i < 0 || i >= depth.cols()) {
                continue;
            }
            for (int j = ly; j < ry; j++) {
                if (j < 0 || j >= depth.rows()) {
                    continue;
                }
                double deep = getDeep(depth.get(j, i));
                if (deep == 0) {
                    continue;
                }
                deep_list.add(deep);
                minDeep = Math.min(minDeep, deep);
                maxDeep = Math.max(maxDeep, deep);
            }
        }
        double deep = getDeep(depth.get(y, x));
        if (deep == 0) {
            return deep;
        }
        if (deep_list.size() >= 2) {
            // 排序后取中位数
            Collections.sort(deep_list);
            int middle_pos = deep_list.size() / 2;
            double middle_deep = (deep_list.get(middle_pos) + deep_list.get(middle_pos + 1)) / 2;
            if (Math.abs(deep - middle_deep) > GlobalDef.MODEL_BACK_MIN_DEEP) {
                return 0;
            }
        }
        return deep;
    }

    private double filterPoint_dis(Mat depth, int x, int y) {
        // 取周围的点， 如果和周围最近点的距离超过阈值，则抛弃
        int dis = GlobalDef.MODEL_DEEP_CENTER_DIS;
        double minDeep = Double.MAX_VALUE;
        for (int i = 0; i < dis; i++) {
            int c_x = x - dis / 2 + i;
            if (c_x < 0 || c_x >= depth.cols()) {
                continue;
            }
            for (int j = 0; j < dis; j++) {
                int c_y = y - dis / 2 + j;
                if (c_y < 0 || c_y >= depth.rows()) {
                    continue;
                }
                double deep = getDeep(depth.get(c_y, c_x));
                if (deep == 0) {
                    continue;
                }
                minDeep = Math.min(minDeep, deep);
            }
        }

        double deep = getDeep(depth.get(y, x));
        double center_deep = deepCameraInfo.getCenterDeep();
        if (deep == 0 || Math.abs(deep - minDeep) > GlobalDef.MODEL_BACK_MIN_DEEP
                || Math.abs(deep - center_deep) > GlobalDef.MODEL_FRONT_MIN_DEEP) {
            return 0;
        }
        return deep;
    }

    private double filterPoint(Mat depth, int x, int y) {
        return getDeep(mFilterDepth.get(y, x));
    }

    private void clacColorRate(int color, ModelPointinfo mi) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        if (red > 0x60 && green < 0x50 && blue < 0x50) {
            mi.redNum++;
        } else if (red > 0x33 && green < 0x33 && blue < 0x33) {
            mi.blackNum++;
        } else if (red > 0x50 && green > 0x50 && blue < 0x40 && red - green < 0x5a && green - red < 0x10) {
            mi.yellowNum++;
        }
        mi.pixSize++;

    }

    private void clacArea() {
        List<Float> vertexList = deepCameraInfo.getVertexList();
        List<Float> colorList = deepCameraInfo.getColorList();
        //Mat mDepth = deepCameraInfo.getDepthMat();
        Mat mDepth = mFilterDepth;
        Bitmap rgbBitmap = deepCameraInfo.getRgbBitmap();
        vertexList.clear();
        colorList.clear();
        int calcWidth = deepCameraInfo.getDeep_rx() - deepCameraInfo.getDeep_lx();
        int calcHeight = deepCameraInfo.getDeep_ry() - deepCameraInfo.getDeep_ly();
        Bitmap tmpBitmap = BitmapUtils.scale_image(mAreaMeasureBitmap, 0, 0, mAreaMeasureBitmap.getWidth(),
                mAreaMeasureBitmap.getHeight(), calcWidth, calcHeight);
        int width = tmpBitmap.getWidth();
        int height = tmpBitmap.getHeight();
        // 缩放比例
        float rgbFactor = (float) rgbBitmap.getWidth() / mDepth.cols();
        // 存放每个轴上面最大和最小值
        double[][] lengthMap = new double[width][2];
        double[][] widthMap = new double[height][2];
        int lx = deepCameraInfo.getDeep_lx();
        int ly = deepCameraInfo.getDeep_ly();
        double area = 0;
        double volume = 0;
        min_deep = Double.MAX_VALUE;
        max_deep = 0;
        ModelPointinfo mi = new ModelPointinfo();

        mi.left_x = Integer.MAX_VALUE;
        mi.top_y = Integer.MAX_VALUE;
        mi.right_x = 0;
        mi.bottom_y = 0;
        mi.last_deep = 0;
        // 红、黄、黑比例
        mi.redNum = 0;
        mi.yellowNum = 0;
        mi.blackNum = 0;
        mi.pixSize = 1;

        for (int i = width - 1; i >= 0; i--) {
            mi.last_deep = 0;
            for (int j = height - 1; j > -0; j--) {
                if (tmpBitmap.getPixel(i, j) == GlobalDef.AREA_COLOR) {
                    int depth_j = j + ly;
                    int depth_i = i + lx;
                    mi.last_deep = filterPoint(mDepth, depth_i, depth_j);
                    if (mi.last_deep == 0) {
                        continue;
                    }
                    // 获取xy上最大的点和最小的点
                    if (lengthMap[i][0] == 0) {
                        lengthMap[i][0] = mi.last_deep;
                        lengthMap[i][1] = mi.last_deep;
                    } else {
                        lengthMap[i][0] = Math.min(mi.last_deep, lengthMap[i][0]);
                        lengthMap[i][1] = Math.max(mi.last_deep, lengthMap[i][1]);
                    }
                    if (widthMap[j][0] == 0) {
                        widthMap[j][0] = mi.last_deep;
                        widthMap[j][1] = mi.last_deep;
                    } else {
                        widthMap[j][0] = Math.min(mi.last_deep, widthMap[j][0]);
                        widthMap[j][1] = Math.max(mi.last_deep, widthMap[j][1]);
                    }
                    // 找到最近的点
                    min_deep = Math.min(mi.last_deep, min_deep);
                    max_deep = Math.max(mi.last_deep, max_deep);
                    mi.left_x = Math.min(depth_i, mi.left_x);
                    mi.right_x = Math.max(depth_i, mi.right_x);
                    mi.top_y = Math.min(depth_j, mi.top_y);
                    mi.bottom_y = Math.max(depth_j, mi.bottom_y);

                    int rgb_i = new Float(rgbFactor * depth_i).intValue();
                    int rgb_j = new Float(rgbFactor * depth_j).intValue();

                    int color = rgbBitmap.getPixel(rgb_i, rgb_j);
                    // testBm.setPixel(depth_i, depth_j, color);
                    int red = Color.red(color);
                    int green = Color.green(color);
                    int blue = Color.blue(color);
                    clacColorRate(color, mi);

                    // 计算模型的点集
                    vertexList.add((float) depth_i);
                    vertexList.add((float) depth_j);
                    vertexList.add((float) mi.last_deep);

                    colorList.add((float) red);
                    colorList.add((float) green);
                    colorList.add((float) blue);
                    colorList.add((float) Color.alpha(1));
                }
            }
        }
        //mAreaMeasureView.setImageBitmap(testBm);
        deepCameraInfo.setMinDeep(min_deep);
        deepCameraInfo.setMaxDeep(max_deep);

        //  中间点
        int test = 1;
        Point center_p = new Point((mi.left_x + mi.right_x) / 2, (mi.top_y + mi.bottom_y) / 2);
        deepCameraInfo.setModelCenter(center_p);
        float camera_size_factor = deepCameraInfo.getCamera_size_factor();
        for (int i = vertexList.size() - 1; i >= 0; i -= 3) {
            float deep = vertexList.get(i);
            int x = new Float(vertexList.get(i - 2)).intValue() - lx;
            int y = new Float(vertexList.get(i - 1)).intValue() - ly;
            int avgDeep = new Double((lengthMap[x][0] + lengthMap[x][1] + widthMap[y][0] + widthMap[y][1]) / 4).intValue();
            double area_per = avgDeep * AREA_PER_PIX / camera_size_factor / camera_size_factor;
            area += area_per;
            double volum_per = (deep - avgDeep) / 10 * area_per;
            volume += volum_per;
            if (volum_per > 0) {
                test = 1;
            }
        }
        volume = volume < 0 ? -volume : volume;
        //deepCameraInfo.setWoundWidth(new Double(test).floatValue());
        String format_area = new DecimalFormat("#.00").format(area);
        String format_volume = new DecimalFormat("#.00").format(volume);
        String format_red = new DecimalFormat("#.00").format((float) mi.redNum / mi.pixSize * 100);
        String format_yellow = new DecimalFormat("#.00").format((float) mi.yellowNum / mi.pixSize * 100);
        String format_black = new DecimalFormat("#.00").format((float) mi.blackNum / mi.pixSize * 100);
        double woundDeep = (deepCameraInfo.getMaxDeep() - deepCameraInfo.getMinDeep()) / 10;
        String format_deep = new DecimalFormat("#.00").format(woundDeep);
        deepCameraInfo.setWoundArea(new Float(format_area));
        deepCameraInfo.setWoundVolume(new Float(format_volume));
        deepCameraInfo.setWoundRedRate(new Float(format_red));
        deepCameraInfo.setWoundYellowRate(new Float(format_yellow));
        deepCameraInfo.setWoundBlackRate(new Float(format_black));
        deepCameraInfo.setWoundDeep(new Float(format_deep));
    }

    private void downLength(MotionEvent e) {
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        lengthCanvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        deepCameraInfo.getLengthPointList().clear();
        float x = Math.round(e.getX() * displayFactor);
        float y = Math.round(e.getY() * displayFactor);
        deepCameraInfo.getLengthPointList().add(new Point(x, y));
    }

    private void drawlength(MotionEvent e1, MotionEvent e2) {
        Point lp = deepCameraInfo.getLengthPointList().get(0);
        Point rp = new Point(e2.getX() * displayFactor, e2.getY() * displayFactor);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        lengthCanvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        fillLine(lengthCanvas, lp, rp);
        mLengthMeasureView.setImageBitmap(mLengthMeasureBitmap);
    }

    private void upLength(MotionEvent e) {
        float x = Math.round(e.getX() * displayFactor);
        float y = Math.round(e.getY() * displayFactor);
        deepCameraInfo.getLengthPointList().add(new Point(x, y));
        clacLength();
        fillLength();
        setLength();
    }


    private void clacLength() {
        Point lp = deepCameraInfo.getLengthPointList().get(0);
        Point rp = deepCameraInfo.getLengthPointList().get(1);
        mLengthMeasureView.setImageBitmap(mLengthMeasureBitmap);
        double distince = calcDistince(lp, rp) / 10;
        String format = new DecimalFormat("#.00").format(distince);
        deepCameraInfo.setWoundWidth(new Float(format));
    }

    private void fillLength() {
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        lengthCanvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        Point lp = deepCameraInfo.getLengthPointList().get(0);
        Point rp = deepCameraInfo.getLengthPointList().get(1);
        fillLine(lengthCanvas, lp, rp);
        mLengthMeasureView.setImageBitmap(mLengthMeasureBitmap);
    }

    private void downWidth(MotionEvent e) {
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        widthCanvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        deepCameraInfo.getWidthPointList().clear();
        float x = Math.round(e.getX() * displayFactor);
        float y = Math.round(e.getY() * displayFactor);
        deepCameraInfo.getWidthPointList().add(new Point(x, y));
    }

    private void drawWidth(MotionEvent e1, MotionEvent e2) {
        Point lp = deepCameraInfo.getWidthPointList().get(0);
        Point rp = new Point(e2.getX() * displayFactor, e2.getY() * displayFactor);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        widthCanvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        fillLine(widthCanvas, lp, rp);
        mWidthMeasureView.setImageBitmap(mWidthMeasureBitmap);
    }

    private void upWidth(MotionEvent e) {
        float x = Math.round(e.getX() * displayFactor);
        float y = Math.round(e.getY() * displayFactor);
        deepCameraInfo.getWidthPointList().add(new Point(x, y));
        fillWidth();
        clacWidth();
        setWidth();
    }

    private void downDeep(MotionEvent e) {
        getEventDeep(e);
    }

    private void drawDeep(MotionEvent e1, MotionEvent e2) {
        //getEventDeep(e2);
    }

    private void upDeep(MotionEvent e) {
        //getEventDeep(e);
    }

    private PointInfo3D getDeepPoint(MotionEvent e) {
        Mat depth = deepCameraInfo.getDepthMat();
        int viewWidth = mDeepMeasureView.getWidth();
        int bWidth = mDeepMeasureBitmap.getWidth();
        int bHeight = mDeepMeasureBitmap.getHeight();
        int lx = deepCameraInfo.getDeep_lx();
        int ly = deepCameraInfo.getDeep_ly();
        int rx = deepCameraInfo.getDeep_rx();
        int ry = deepCameraInfo.getDeep_ry();
        int bitmapWidth = rx - lx;
        int bitmapHeight = ry - ly;
        float viewFactor = new Float(bWidth) / viewWidth;
        float deepFactor = new Float(bitmapWidth) / viewWidth;

        float t_x = new Float(e.getX() * viewFactor);
        int d_x = new Float(e.getX() * deepFactor).intValue() + lx;
        float t_y = new Float(e.getY() * viewFactor);
        int d_y = new Float(e.getY() * deepFactor).intValue() + ly;
        // 取温度数据
        double deep = depth.get(d_y, d_x)[0];
        PointInfo3D point = new PointInfo3D();
        point.x = t_x;
        point.y = t_y;
        point.z = new Float(deep);
        return point;
    }

    private void drawDeepPoint(PointInfo3D point) {
        String temp;
        float deep = point.z;
        float t_x = point.x;
        float t_y = point.y;
        if (deep == 0) {
            temp = "未采集到";
        } else {
            temp = new DecimalFormat("#.00").format(deep / 10) + "厘米";
        }
        int text_witdh_diff = 300;
        int text_heigth_diff = 80;
        int tc_diff = 50;
        float text_x = t_x < text_witdh_diff ? t_x : t_x - text_witdh_diff;
        float text_y = (t_y < text_heigth_diff ? t_y + text_heigth_diff : t_y) - 15;
        float bolb = paint.getStrokeWidth();
        paint.setStrokeWidth(4);
        deepCanvas.drawText(temp, text_x, text_y, paint);
        deepCanvas.drawLine(t_x - tc_diff, t_y, t_x + tc_diff, t_y, paint);
        deepCanvas.drawLine(t_x, t_y - tc_diff, t_x, t_y + tc_diff, paint);
        paint.setStrokeWidth(bolb);
        mDeepMeasureView.setImageBitmap(mDeepMeasureBitmap);
    }

    private void getEventDeep(MotionEvent e) {
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        deepCanvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        PointInfo3D point = getDeepPoint(e);
        drawDeepPoint(point);
        if (point.z == 0) {
            return;
        }
        deep_mode_first = !deep_mode_first;
        if (!deep_mode_first) {
            wound_deep_first = point;
            return;
        }
        drawDeepPoint(wound_deep_first);
        double distince = Math.abs(point.z - wound_deep_first.z) / 10;
        String format = new DecimalFormat("#.00").format(distince);
        deepCameraInfo.setWoundDeep(new Float(format));
        setDeep();

    }

    private void clacWidth() {
        Point lp = deepCameraInfo.getWidthPointList().get(0);
        Point rp = deepCameraInfo.getWidthPointList().get(1);
        double distince = calcDistince(lp, rp) / 10;
        String format = new DecimalFormat("#.00").format(distince);
        deepCameraInfo.setWoundHeight(new Float(format));

    }

    private void fillWidth() {
        Point lp = deepCameraInfo.getWidthPointList().get(0);
        Point rp = deepCameraInfo.getWidthPointList().get(1);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        widthCanvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        fillLine(widthCanvas, lp, rp);
        mWidthMeasureView.setImageBitmap(mWidthMeasureBitmap);
    }

    private void fillLine(Canvas canvas, Point lp, Point rp) {
        float lx = (float) lp.x;
        float ly = (float) lp.y;
        float rx = (float) rp.x;
        float ry = (float) rp.y;
        canvas.drawCircle(lx, ly, 10f, paint);
        canvas.drawCircle(rx, ry, 10f, paint);
        canvas.drawLine(lx, ly, rx, ry, paint);
    }

    private double calcDistince(Point lp, Point rp) {
        int lx = new Double(lp.x).intValue();
        int ly = new Double(lp.y).intValue();
        int rx = new Double(rp.x).intValue();
        int ry = new Double(rp.y).intValue();
        double default_deep = (deepCameraInfo.getDeep_far() + deepCameraInfo.getDeep_near()) / 2;
        double ldeep = default_deep;
        double rdeep = default_deep;
        Mat depth = deepCameraInfo.getDepthMat();
        double[] ldeeps = depth.get(ly, lx);
        if (ldeeps != null && ldeeps.length > 0 && ldeeps[0] > 0) {
            ldeep = ldeeps[0];
        }
        double[] rdeeps = depth.get(ry, rx);
        if (rdeeps != null && rdeeps.length > 0 && rdeeps[0] > 0) {
            rdeep = rdeeps[0];
        }
        double first_deep = Math.min(ldeep, rdeep);
        double d_with = (rx - lx) * WIDTH_PER_PIX;
        double d_height = (ry - ly) * HEIGHT_PER_PIX;
        double d_deep = (rdeep - ldeep);
        double length = Math.sqrt(Math.pow(d_with, 2) + Math.pow(d_height, 2)) * first_deep / 1000;
        double line = Math.sqrt(Math.pow(length, 2) + Math.pow(d_deep, 2));
        return line;
    }

    private void setArea() {
        mAreaView.setText("面积：" + deepCameraInfo.getWoundArea() + "cm²");
        mVolumeView.setText("体积：" + deepCameraInfo.getWoundVolume() + " cm³");
        mDeepView.setText("深度：" + deepCameraInfo.getWoundDeep() + " cm");
        mColorRedView.setText("红色组织：" + deepCameraInfo.getWoundRedRate() + "%");
        mColorBlackView.setText("黄色组织：" + deepCameraInfo.getWoundYellowRate() + "%");
        mColorYellowView.setText("黑色组织：" + deepCameraInfo.getWoundBlackRate() + "%");
    }

    private void setLength() {
        mLengthView.setText("长度：" + deepCameraInfo.getWoundWidth() + " cm");
    }

    private void setWidth() {
        mWidthView.setText("宽度：" + deepCameraInfo.getWoundHeight() + " cm");
    }

    private void setDeep() {
        mDeepView.setText("深度：" + deepCameraInfo.getWoundDeep() + " cm");
    }

    private final OnClickListener mMeasureAreaListener = new OnClickListener() {
        @Override
        public void onClick(final View view) {
            mMeasureStat = 1;
            paint.setColor(GlobalDef.AREA_COLOR);
            ToastUtil.showLongToast(mActivity, "请圈选伤口边缘");
            //canvas.restoreToCount(areaLayerID);
        }
    };

    private final OnClickListener mMeasureLengthListener = new OnClickListener() {
        @Override
        public void onClick(final View view) {
            mMeasureStat = 2;
            paint.setColor(GlobalDef.LENGTH_COLOR);
            ToastUtil.showLongToast(mActivity, "请划定伤口最大长度");
            //canvas.restoreToCount(lengthLayerID);
        }
    };

    private final OnClickListener mMeasureWidthListener = new OnClickListener() {
        @Override
        public void onClick(final View view) {
            mMeasureStat = 3;
            paint.setColor(GlobalDef.WIDTH_COLOR);
            ToastUtil.showLongToast(mActivity, "请划定伤口最大宽度");
            //canvas.restoreToCount(lengthLayerID);
        }
    };

    private final OnClickListener mMeasureDeepListener = new OnClickListener() {
        @Override
        public void onClick(final View view) {
            mMeasureStat = 4;
            paint.setColor(GlobalDef.DEEP_COLOR);
            ToastUtil.showLongToast(mActivity, "请选择位置");
            //canvas.restoreToCount(lengthLayerID);
        }
    };

    private final OnClickListener mModelDisplayListener = new OnClickListener() {
        @Override
        public void onClick(final View view) {
            if (deepCameraInfo.getVertexList().size() <= 0) {
                ToastUtil.showLongToast(mActivity, "未选中深度数据");
                return;
            }
            AlertDialogUtil.showAlertDialogAsync(mActivity, view, mActivity.getString(R.string.message_title_tip)
                    , mActivity.getString(R.string.message_wait_model)
                    , new AlertDialogUtil.AlertCallback() {
                        @Override
                        public void post() {
                            super.post();
                            RecordFragmentModelDisplay fragment = RecordFragmentModelDisplay.createInstance();
                            mActivity.getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.detail_container, fragment)
                                    .commit();
                        }
                    });

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

    private void syncWoundNum() {
        if (deepCameraInfo.getWoundArea() > 0) {
            mActivity.getRecordInfo().setWoundArea(deepCameraInfo.getWoundArea());
        }
        if (deepCameraInfo.getWoundWidth() > 0) {
            mActivity.getRecordInfo().setWoundWidth(deepCameraInfo.getWoundWidth());
        }
        if (deepCameraInfo.getWoundHeight() > 0) {
            mActivity.getRecordInfo().setWoundHeight(deepCameraInfo.getWoundHeight());
        }
        if (deepCameraInfo.getWoundVolume() > 0) {
            mActivity.getRecordInfo().setWoundVolume(deepCameraInfo.getWoundVolume());
        }
        if (deepCameraInfo.getWoundDeep() > 0) {
            mActivity.getRecordInfo().setWoundDeep(deepCameraInfo.getWoundDeep());
        }
        if (deepCameraInfo.getWoundRedRate() > 0) {
            mActivity.getRecordInfo().setWoundColorRed(deepCameraInfo.getWoundRedRate());
        }
        if (deepCameraInfo.getWoundYellowRate() > 0) {
            mActivity.getRecordInfo().setWoundColorYellow(deepCameraInfo.getWoundYellowRate());
        }
        if (deepCameraInfo.getWoundBlackRate() > 0) {
            mActivity.getRecordInfo().setWoundColorBlack(deepCameraInfo.getWoundBlackRate());
        }
    }

    private final OnClickListener mSaveDataListener = new OnClickListener() {
        @Override
        public void onClick(final View view) {
            AlertDialogUtil.showAlertDialogAsync(mActivity, view, mActivity.getString(R.string.message_title_tip)
                    , mActivity.getString(R.string.message_wait_save)
                    , new AlertDialogUtil.AlertCallback() {
                        @Override
                        public void exec() {
                            super.exec();
                            mActivity.saveDeepCameraInfo();
                            syncWoundNum();
                            // ToastUtil.showShortToast(mActivity, "保存成功");
                            AlertDialogUtil.dismissAlertDialog(mActivity);
                        }
                    });
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
