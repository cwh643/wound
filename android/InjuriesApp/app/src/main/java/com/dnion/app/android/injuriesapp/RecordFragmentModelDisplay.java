package com.dnion.app.android.injuriesapp;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.dnion.app.android.injuriesapp.camera_tool.DeepModelDisplayView;
import com.dnion.app.android.injuriesapp.camera_tool.DeepModelDisplayViewV2;
import com.dnion.app.android.injuriesapp.dao.DeepCameraInfo;

import org.opencv.core.Mat;

/**
 * Created by yy on 2017/6/18.
 */

public class RecordFragmentModelDisplay extends Fragment {
    public static final String TAG = "record_fragment_camera";
    public static final int DEFAULT_PREVIEW_WIDTH = 640;
    public static final int DEFAULT_PREVIEW_HEIGHT = 480;
    public static final int DEFAULT_TIME_OUT = 100000;
    private MainActivity mActivity;

    private Mat mDepth;
    private Mat mRgb;

    // final Bitmap bitmap = Bitmap.createBitmap(1280, 640, Bitmap.Config.RGB_565);
    Bitmap mBitmap = Bitmap.createBitmap(DEFAULT_PREVIEW_WIDTH, DEFAULT_PREVIEW_HEIGHT, Bitmap.Config.RGB_565);
    private Button mOpenButton;
    private Switch mImageSwitch;
    private TextView mAreaView;
    private TextView mVolumeView;
    private int mRun = 0;

    private DeepModelDisplayViewV2 mModelView;
    private GestureDetector scollGestureDetector;
    private ScaleGestureDetector scaleGestureDetector;

    public static RecordFragmentModelDisplay createInstance() {
        RecordFragmentModelDisplay fragment = new RecordFragmentModelDisplay();

        Bundle bundle = new Bundle();
        //bundle.putLong(ARGUMENT_USER_ID, userId);
        //bundle.putString(ARGUMENT_USER_NAME, userName);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) RecordFragmentModelDisplay.this.getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.camera_model, container, false);
        configView(rootView);
        return rootView;
    }

    private void configView(View rootView) {
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mModelView = (DeepModelDisplayViewV2) rootView.findViewById(R.id.deep_model_view);
        mOpenButton = (Button) rootView.findViewById(R.id.reset_button);
        mOpenButton.setOnClickListener(mResetListener);
        mOpenButton = (Button) rootView.findViewById(R.id.back_button);
        mOpenButton.setOnClickListener(mBackListener);

        mImageSwitch = (Switch) rootView.findViewById(R.id.image_swith);
        //  手势相关
        scollGestureDetector = new GestureDetector(this.getContext(), onScollGestureListener);
        scaleGestureDetector = new ScaleGestureDetector(this.getContext(), onScaleGestureListener);
        mModelView.setClickable(true);
        mModelView.setOnTouchListener(onTouchListener);
        mModelView.updateDeepCameraInfo(mActivity.getDeepCameraInfo());
        mAreaView = (TextView) rootView.findViewById(R.id.area_view);
        mVolumeView = (TextView) rootView.findViewById(R.id.volume_view);
        //mAreaView.setText("面积：\n" + mActivity.getDeepCameraInfo().getWoundArea() + "cm²");
        //mVolumeView.setText("体积：\n" + mActivity.getDeepCameraInfo().getWoundVolume() + "cm³");
    }


    private GestureDetector.OnGestureListener onScollGestureListener =
            new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    //Log.i("touch", "log scoll");
                    mModelView.scoll(distanceX, distanceY);
                    return true;
                }

            };
    private ScaleGestureDetector.OnScaleGestureListener onScaleGestureListener = new ScaleGestureDetector.OnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            //缩放比例
            float scale = detector.getScaleFactor();
            //Log.i("touch", "scale factor" + scale);
            mModelView.scale(scale);
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            //一定要返回true才会进入onScale()这个函数
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {

        }
    };

    public View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (scaleGestureDetector.onTouchEvent(event) == true) {
                return scollGestureDetector.onTouchEvent(event);
            }
            return false;
        }
    };


    private final OnClickListener mResetListener = new OnClickListener() {
        @Override
        public void onClick(final View view) {
            mModelView.reset_position();
        }
    };
    private final OnClickListener mBackListener = new OnClickListener() {
        @Override
        public void onClick(final View view) {
            RecordFragmentWoundMeasure fragment = RecordFragmentWoundMeasure.createInstance();
            mActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, fragment)
                    .commit();
        }

    };

}
