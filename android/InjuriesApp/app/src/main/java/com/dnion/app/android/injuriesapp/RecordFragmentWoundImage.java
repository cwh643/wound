package com.dnion.app.android.injuriesapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.ColorUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dnion.app.android.injuriesapp.camera_tool.DeepModelDisplayView;
import com.dnion.app.android.injuriesapp.dao.DeepCameraInfo;
import com.dnion.app.android.injuriesapp.dao.RecordImage;
import com.dnion.app.android.injuriesapp.utils.AlertDialogUtil;
import com.dnion.app.android.injuriesapp.utils.BitmapUtils;
import com.dnion.app.android.injuriesapp.utils.DateUtils;
import com.dnion.app.android.injuriesapp.utils.ToastUtil;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.flipInterval;
import static android.R.attr.fragment;
import static android.R.attr.max;
import static android.R.attr.top;

/**
 * Created by yy on 2017/6/18.
 */

public class RecordFragmentWoundImage extends Fragment {
    public static final String TAG = "record_fragment_camera";
    public static final double length_factor = 2.2;
    public static final double WIDTH_PER_PIX = (Math.tan(0.5236) / 320) * 1000 / length_factor;
    public static final double HEIGHT_PER_PIX = (Math.tan(0.4014) / 240) * 1000 / length_factor;
    public static final double AREA_PER_PIX = (Math.tan(0.5236) / 320) * (Math.tan(0.4014) / 240) * 10 / length_factor;
    public static final int DRAW_COLOR = Color.YELLOW;
    private double min_deep = Double.MAX_VALUE;
    private double max_deep = 0;
    private MainActivity mActivity;

    private Bitmap mWoundRgbBitmap;
    private Bitmap mAreaMeasureBitmap;
    private Bitmap mLengthMeasureBitmap;
    private Bitmap mWidthMeasureBitmap;
    //    final Bitmap mBitmap = Bitmap.createBitmap(DEFAULT_PREVIEW_WIDTH, DEFAULT_PREVIEW_HEIGHT, Bitmap.Config.RGB_565);
    private Button mOpenButton;
    private TextView mAreaView;
    private TextView mVolumeView;
    private TextView mLengthView;
    private TextView mWidthView;
    private TextView mColorView;
    private TextView mDeepView;
    private int mMeasureStat = 0;
    private ImageView mWoundRgbView;
    private ImageView mAreaMeasureView;
    private ImageView mLengthMeasureView;
    private ImageView mWidthMeasureView;
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


    public static RecordFragmentWoundImage createInstance() {
        RecordFragmentWoundImage fragment = new RecordFragmentWoundImage();

        Bundle bundle = new Bundle();
        //bundle.putLong(ARGUMENT_USER_ID, userId);
        //bundle.putString(ARGUMENT_USER_NAME, userName);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) RecordFragmentWoundImage.this.getActivity();
        //userDao = new UserDao(mActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.wound_image, container, false);
        configView(rootView);
        return rootView;
    }

    private void configView(View rootView) {
        deepCameraInfo = mActivity.getDeepCameraInfo();
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mWoundRgbView = (ImageView) rootView.findViewById(R.id.rgb_view);

        //  手势相关
        mOpenButton = (Button) rootView.findViewById(R.id.measure_button);
        mOpenButton.setOnClickListener(mMeasureListener);
        mOpenButton = (Button) rootView.findViewById(R.id.save_button);
        mOpenButton.setOnClickListener(mSaveDataListener);
        mOpenButton = (Button) rootView.findViewById(R.id.back_button);
        mOpenButton.setOnClickListener(mBackListener);

        mWoundRgbView.setImageBitmap(deepCameraInfo.getRgbBitmap());
    }

    private final OnClickListener mMeasureListener = new OnClickListener() {
        @Override
        public void onClick(final View view) {
            RecordFragmentWoundMeasure fragment = RecordFragmentWoundMeasure.createInstance();
            mActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, fragment)
                    .commit();
        }

    };

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
            view.setClickable(false);
            AlertDialogUtil.showAlertDialog(mActivity,
                    mActivity.getString(R.string.message_title_tip),
                    mActivity.getString(R.string.message_wait_save));
            mActivity.saveDeepCameraInfo();
            syncWoundNum();
            ToastUtil.showShortToast(mActivity, "保存成功");
            AlertDialogUtil.dismissAlertDialog(mActivity);
            view.setClickable(true);
        }

    };

    private final OnClickListener mBackListener = new OnClickListener() {
        @Override
        public void onClick(final View view) {
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

    };


    @Override
    public void onPause() {
        super.onPause();
    }

    public void onDestroy() {
        super.onDestroy();
    }


}
