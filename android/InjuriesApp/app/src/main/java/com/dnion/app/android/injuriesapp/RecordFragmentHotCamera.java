package com.dnion.app.android.injuriesapp;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ThermalExpert.ThermalExpert;
import com.dnion.app.android.injuriesapp.utils.DateUtils;
import com.dnion.app.android.injuriesapp.utils.ImageTools;

import java.io.File;
import java.util.Date;

/**
 * Created by yy on 2017/6/18.
 */

public class RecordFragmentHotCamera extends Fragment {

    public static final String TAG = "record_fragment_hot_camera";

    private MainActivity mActivity;

    private ImageView mTeImageView;

    private Button calibrationButton;

    private Button playButton;

    private Button colorButton;

    private TextView temperatureText;

    private TextView temperatureCoorText;

    private TextView temperatureRectMax;

    private TextView temperatureRectAvg;

    private TextView temperatureRectMin;

    private TextView temperatureRectCoor;

    private Rect mRectTemperature = new Rect();

    private ThermalExpert te;

    private boolean mIsPlay =  true;

    private int mColorMode = 1;

    private TextView rgb_param;

    public static RecordFragmentHotCamera createInstance() {
        RecordFragmentHotCamera fragment = new RecordFragmentHotCamera();

        Bundle bundle = new Bundle();
        //bundle.putLong(ARGUMENT_USER_ID, userId);
        //bundle.putString(ARGUMENT_USER_NAME, userName);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) RecordFragmentHotCamera.this.getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.camera_get_hot_test, container, false);
        configView(rootView);
        mActivity.registerMyTouchListener(myTouchListener);
        return rootView;
    }

    private void configView(View rootView) {
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mTeImageView = ((ImageView)rootView.findViewById(R.id.image_preview));
        playButton = ((Button)rootView.findViewById(R.id.play_button));
        playButton.setOnClickListener(mClickListener);
        calibrationButton = ((Button)rootView.findViewById(R.id.calibration_button));
        calibrationButton.setOnClickListener(mClickListener);
        colorButton = ((Button)rootView.findViewById(R.id.color_button));
        colorButton.setOnClickListener(mClickListener);
        rgb_param = ((TextView)rootView.findViewById(R.id.textview));
        temperatureText = ((TextView)rootView.findViewById(R.id.temperature_textview));
        temperatureCoorText = ((TextView)rootView.findViewById(R.id.temperature_coor_textview));
        temperatureRectMax = ((TextView)rootView.findViewById(R.id.temperature_rect_max));
        temperatureRectAvg = ((TextView)rootView.findViewById(R.id.temperature_rect_avg));
        temperatureRectMin = ((TextView)rootView.findViewById(R.id.temperature_rect_min));
        temperatureRectCoor = ((TextView)rootView.findViewById(R.id.temperature_rect_coor));
        te = new ThermalExpert(thermalExpertListener, mTeImageView);
        te.Initial(mActivity.getApplicationContext());
    }


    private final OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(final View view) {
            //view.setClickable(false);
            switch (view.getId()) {
                case R.id.calibration_button:
                    calibrationButton.setEnabled(false);
                    te.CalibrationImage();
                    break;
                case R.id.color_button:
                    te.SetColorMap((++mColorMode)%4);
                    break;
                case R.id.play_button:
                    if (mIsPlay) {
                        mIsPlay = false;
                        playButton.setText("打开");
                        te.StopReceive();

                    } else {
                        mIsPlay = true;
                        playButton.setText("关闭");
                        te.StartReceive();
                    }
            }
            return;
        }
    };

    private ThermalExpert.ThermalExpertListener thermalExpertListener = new ThermalExpert.ThermalExpertListener(){
        @Override
        public void onFlashReadFinished() {
            rgb_param.setText("onFlashReadFinished is run");
            mTeImageView.setScaleType(ImageView.ScaleType.CENTER);
            te.StartReceive();
        }

        @Override
        public void onUsbConnected() {
            rgb_param.setText("onUsbConnected is run");
        }

        @Override
        public void onUsbDisconnected() {
            rgb_param.setText("onUsbDisconnected is run");
            if (te != null) {
                //finish();
                //Process.killProcess(Process.myPid());
            }
        }

        @Override
        public void onOneFrameFinished() {
            rgb_param.setText("onOneFrameFinished is run");
            Bitmap bitmap = te.GetImage();
            mTeImageView.setImageDrawable(new BitmapDrawable(bitmap));
            te.GetData();
        }

        @Override
        public void onCalibrationFinished() {
            rgb_param.setText("onCalibrationFinished is run");
            calibrationButton.setEnabled(true);
            Bitmap bitmap = te.GetImage();
            saveDataAndJumpPage(bitmap);
        }

        @Override
        public void onRecogNumber() {
            rgb_param.setText("onRecogNumber is run");
        }
    };

    private MainActivity.MyTouchListener myTouchListener = new MainActivity.MyTouchListener() {
        @Override
        public void onTouchEvent(MotionEvent event) {
            // 处理手势事件
            int i = (int)event.getX();
            int j = (int)event.getY();
            if (event.getAction() == 0)
                mRectTemperature.set(i, j, i, j);
            while (true) {
                if (event.getAction() != 1)
                    continue;
                if ((Math.abs(mRectTemperature.left - i) >= 10) && (Math.abs(mRectTemperature.top - j) >= 10)) {
                    mRectTemperature.set(mRectTemperature.left, mRectTemperature.top, i, j);
                    double[] arrayOfDouble = te.GetRectTemperature(mRectTemperature);
                    temperatureRectAvg.setText(String.format("%.2f", new Object[] { Double.valueOf(arrayOfDouble[0]) }));
                    temperatureRectMax.setText(String.format("%.2f", new Object[] { Double.valueOf(arrayOfDouble[1]) }));
                    temperatureRectMin.setText(String.format("%.2f", new Object[] { Double.valueOf(arrayOfDouble[2]) }));
                    temperatureRectCoor.setText(String.format("(%d, %d),\n(%d, %d)", new Object[] { Integer.valueOf(te.GetTemperatureRect().left), Integer.valueOf(te.GetTemperatureRect().top), Integer.valueOf(te.GetTemperatureRect().right), Integer.valueOf(te.GetTemperatureRect().bottom) }));
                    continue;
                }
                double d = te.GetPointTemperature(i, j);
                temperatureText.setText(String.format("%.2f", new Object[] { Double.valueOf(d) }));
                temperatureCoorText.setText(String.format("(%d,\n%d)", new Object[] { Integer.valueOf(te.GetTemperaturePoint().x), Integer.valueOf(te.GetTemperaturePoint().y) }));
                return;
            }
        }
    };

    public void onDestroyView() {
        super.onDestroyView();
        te.StopReceive();
        mActivity.unRegisterMyTouchListener(myTouchListener);
    }

    public static final int SCALE = 1;

    private void saveDataAndJumpPage(Bitmap bitmap) {

        Bitmap newBitmap = ImageTools.zoomBitmap(bitmap, bitmap.getWidth() / SCALE, bitmap.getHeight() / SCALE);
        //由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
        //bitmap.recycle();

        //将处理过的图片显示在界面上，并保存到本地
        String path = mActivity.getImagePath("hot");
        String name = DateUtils.formateDate(new Date()) +".jpg";
        mActivity.saveImage(newBitmap, path, name);
        newBitmap.recycle();
        mActivity.saveHotCameraInfo(path + File.separator + name);
    }
}
