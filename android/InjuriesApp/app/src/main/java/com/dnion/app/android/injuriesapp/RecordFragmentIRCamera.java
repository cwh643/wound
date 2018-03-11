package com.dnion.app.android.injuriesapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
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
import android.widget.TextView;

import com.dnion.app.android.injuriesapp.camera_tool.*;
import com.dnion.app.android.injuriesapp.camera_tool.camera_help.AbstractCameraHelper;
import com.dnion.app.android.injuriesapp.camera_tool.camera_help.IRCameraHelper;
import com.dnion.app.android.injuriesapp.dao.DeepCameraInfo;
import com.dnion.app.android.injuriesapp.utils.AlertDialogUtil;
import com.dnion.app.android.injuriesapp.utils.DateUtils;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by yuanyuan on 2017/6/18.
 */

public class RecordFragmentIRCamera extends Fragment implements CameraBridgeViewBase.CvCameraViewListener2 {
    public static final String TAG = "record_fragment_camera";
    public static final int DEFAULT_PREVIEW_WIDTH = 460;
    public static final int DEFAULT_PREVIEW_HEIGHT = 345;
    public static final int CAMERA_DEFAULT_WIDTH = 960;
    public static final int CAMERA_DEFAULT_HEIGHT = 540;
    public static final int DEFAULT_TIME_OUT = 100000;
    private int camera_src = 1; // 0:图漾，1:奥比
    private int camera_x = 332;
    private int camera_y = 141;
    private int camera_width = 572;
    private int camera_height = 379;
    // 相对于640 * 480 的偏移
    public int deep_lx = 120;
    public int deep_ly = 90;
    public int deep_rx = 360;
    public int deep_ry = 270;

    public int deep_x_diff = 137;
    public int deep_y_diff = 110;
    public int deep_near = 500;
    public int deep_far = 700;
    public float deep_scale_factor = 0.35f;
    public int deep_min_deep = 0;
    public int deep_max_deep = 0;
    public int dept_factor = 140;
    private MainActivity mActivity;
    private Mat mDepth;
    private Mat mRgb;

    private IRCameraHelper irCamera;
    private Handler theHandler = null;

    // final Bitmap bitmap = Bitmap.createBitmap(1280, 640, Bitmap.Config.RGB_565);
    Bitmap mRgbBitmap = Bitmap.createBitmap(160, 120, Bitmap.Config.ARGB_8888);
    Bitmap mRgbTrasBitmap = Bitmap.createBitmap(120, 160, Bitmap.Config.ARGB_8888);
    //Bitmap mDepthBitmap = Bitmap.createBitmap(DEFAULT_PREVIEW_WIDTH, DEFAULT_PREVIEW_HEIGHT, Bitmap.Config.RGB_565);
    Bitmap mDepthBitmap = Bitmap.createBitmap(GlobalDef.RES_COLOR_WIDTH_320, GlobalDef.RES_COLOR_HEIGHT_240, Bitmap.Config.RGB_565);
    private Button mOpenButton;
    private ImageButton mShotButton;
    private TextView mParamView;
    private int mRun = 0;

    private ImageView mRgbView;
    private ImageView mDepthView;
    private CameraSurfaceView cameraSurfaceView;
    private GestureDetector scollGestureDetector;
    private ScaleGestureDetector scaleGestureDetector;

    public static RecordFragmentIRCamera createInstance() {
        RecordFragmentIRCamera fragment = new RecordFragmentIRCamera();
        Bundle bundle = new Bundle();
        //bundle.putLong(ARGUMENT_USER_ID, userId);
        //bundle.putString(ARGUMENT_USER_NAME, userName);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        irCamera.init(mActivity, "");
        theHandler.postDelayed(mRefreshThread, 1000);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = (MainActivity) RecordFragmentIRCamera.this.getActivity();

        //theHandler.postDelayed(mRefreshThread, 1000);
        //userDao = new UserDao(mActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.camera_get_hot, container, false);
        configView(rootView);
        return rootView;
    }

    private void configView(View rootView) {
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        cameraSurfaceView = (CameraSurfaceView) rootView.findViewById(R.id.camera_surface_view);
        //cameraSurfaceView.init();
        //cameraSurfaceView.setPrewViewCallBack(mPreviewCallback);
        mRgbView = (ImageView) rootView.findViewById(R.id.rgb_view);
        //mAbRgbView = (OpenGL2DView) rootView.findViewById(R.id.ab_rgb_view);
        mDepthView = (ImageView) rootView.findViewById(R.id.deep_camera_view);
        //mOpenButton = (Button) rootView.findViewById(R.id.open_button);
        //mOpenButton.setOnClickListener(mOpenListener);
        mOpenButton = (Button) rootView.findViewById(R.id.back_button);
        mOpenButton.setOnClickListener(mBackListener);
        mShotButton = (ImageButton) rootView.findViewById(R.id.camera_shot);
        mShotButton.setOnClickListener(mShotListener);

        mParamView = (TextView) rootView.findViewById(R.id.rgb_param);

        // mImageSwitch = (Switch) rootView.findViewById(R.id.image_swith);
        //  手势相关

        irCamera = new IRCameraHelper();
        theHandler = new Handler();

        scollGestureDetector = new GestureDetector(this.getContext(), onScollGestureListener);
        scaleGestureDetector = new ScaleGestureDetector(this.getContext(), onScaleGestureListener);
        mDepthView.setClickable(true);
        mDepthView.setOnTouchListener(onTouchListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        resumeirCamera();
    }

    private void resumeirCamera() {
        irCamera.onResume(mAbLoadCallback);
    }

    private AbstractCameraHelper.Callback mAbLoadCallback = new AbstractCameraHelper.Callback() {
        @Override
        public void onInited(int status) {
            mDepth = new Mat(mDepthBitmap.getHeight(), mDepthBitmap.getWidth(), CvType.CV_16UC1);
            startDeepCamera();
        }
    };

    private void displayCameraParam() {
        String sb = new String();
        sb += "x:" + camera_x + "\n";
        sb += "y:" + camera_y + "\n";
        sb += "width:" + camera_width + "\n";
        sb += "height:" + camera_height + "\n";
        mParamView.setText(sb);
    }

    private GestureDetector.OnGestureListener onScollGestureListener =
            new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    //Log.i("touch", "log scoll");
                    int tmp_x = new Float(camera_x + distanceX).intValue();
                    int tmp_y = new Float(camera_y + distanceY).intValue();
                    if (tmp_x < 0 || tmp_x + camera_width >= CAMERA_DEFAULT_WIDTH) {
                        return true;
                    }
                    if (tmp_y < 0 || tmp_y + camera_height >= CAMERA_DEFAULT_HEIGHT) {
                        return true;
                    }
                    camera_x = tmp_x;
                    camera_y = tmp_y;
                    displayCameraParam();
                    return true;
                }

            };
    private ScaleGestureDetector.OnScaleGestureListener onScaleGestureListener = new ScaleGestureDetector.OnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            //缩放比例
            float scale = detector.getScaleFactor();
            //Log.i("touch", "scale factor" + scale);
            int tmp_width = new Float(1 / scale * camera_width).intValue();
            int tmp_height = new Float(1 / scale * camera_height).intValue();
            if (tmp_width >= CAMERA_DEFAULT_WIDTH || tmp_height >= CAMERA_DEFAULT_HEIGHT) {
                return false;
            }
            camera_width = tmp_width;
            camera_height = tmp_height;
            if (camera_width + camera_x >= CAMERA_DEFAULT_WIDTH) {
                camera_x = CAMERA_DEFAULT_WIDTH - tmp_width;
            }
            if (camera_height + camera_y >= CAMERA_DEFAULT_HEIGHT) {
                camera_y = CAMERA_DEFAULT_HEIGHT - tmp_height;
            }
            if (camera_x < 0 || camera_y < 0) {
                Log.e(TAG, camera_x + "," + camera_y);
            }
            displayCameraParam();
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



    private final OnClickListener mOpenListener = new OnClickListener() {
        @Override
        public void onClick(final View view) {
            startDeepCamera();
        }
    };

    private int startDeepCamera() {

        if (mRun == 1) {
            return 0;
        }
        int err = -1;
        if (camera_src == 0) {
            try {
                err = 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (camera_src == 1) {
            err = 0;
        }
        if (err != 0) {
            alert_massage("打开摄像头错误:" + err);
            return err;
        }
        mRun = 1;
        return 0;
    }

    private void stopDeepCamera() {
        if (mRun != 1) {
            return;
        }
        mRun = 2;
        try {

            Thread.sleep(500l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (camera_src == 0) {
            //getDataThread.interrupt();
            //finish();
            //System.exit(0);
        } else if (camera_src == 1) {
            irCamera.onStop();
        }

    }

    public View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (scaleGestureDetector.onTouchEvent(event) == true) {
                return scollGestureDetector.onTouchEvent(event);
            }
            return false;
        }
    };

    private final OnClickListener mBackListener = new OnClickListener() {
        @Override
        public void onClick(final View view) {
            Fragment fragment;
            fragment = PhotoListFragment.createInstance();
            mActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, fragment)
                    .commit();

        }

    };


    private final OnClickListener mShotListener = new OnClickListener() {
        @Override
        public void onClick(final View view) {
//            camerasurfaceview.stop();
//            cameraSurfaceView.takePicture(pictureCallback);
            new ProcessTask(view).execute();
        }
    };

    private class ProcessTask extends AsyncTask<String, Object, Long> {

        private View view;

        public ProcessTask(View view) {
            this.view = view;
        }

        @Override
        protected void onPreExecute() {
            AlertDialogUtil.showAlertDialog(mActivity,
                    mActivity.getString(R.string.message_title_tip),
                    mActivity.getString(R.string.message_wait_save));
            view.setClickable(false);
        }

        @Override
        protected Long doInBackground(String... params) {
            // 复制深度信息
            mActivity.setDeepCameraInfo(new DeepCameraInfo());
            getLastPicture = true;
            while (getLastPicture) {
            }
            saveDataAndJumpPage();
            return 0L;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            view.setClickable(true);
            AlertDialogUtil.dismissAlertDialog(mActivity);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap photo = null;
        Uri uri = data.getData();
        if (uri == null) {
            //use bundle to get data
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                photo = (Bitmap) bundle.get("data"); //get mBitmap
            }
        } else {
            try {
                photo = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    public void onDestroy() {
        super.onDestroy();
        irCamera.onDestroy();

    }

    @Override
    public void onStop() {
        super.onStop();
        stopDeepCamera();
    }

    boolean getLastPicture = false;
    protected Runnable mRefreshThread = new Runnable() {

        @Override
        public void run() {
            if (mRun != 1) {
                return;
            }
            int ret = 0;
            if (getLastPicture) {
                ret = irCamera.FetchFinalData(mDepth, mRgbTrasBitmap);
                getLastPicture = false;
            } else {
                ret = irCamera.FetchData(null, mRgbTrasBitmap);
            }
            if (ret != GlobalDef.SUCC) {
                AlertDialogUtil.showAlertDialog(mActivity,
                        mActivity.getString(R.string.message_title_tip),
                        mActivity.getString(R.string.message_device_error));
                return;
            }
            Matrix matrix = new Matrix();
            // 设置旋转角度
            matrix.setRotate(90);
            // 重新绘制Bitmap
            mRgbBitmap = Bitmap.createBitmap(mRgbTrasBitmap, 0, 0, mRgbTrasBitmap.getWidth(), mRgbTrasBitmap.getHeight(), matrix, true);
            mRgbView.setImageBitmap(mRgbBitmap);
            //Mat tmpMap = new Mat(mDepthBitmap.getHeight(), mDepthBitmap.getWidth(), CvType.CV_8UC1);
            //mDepth.convertTo(tmpMap, tmpMap.type());
            //Utils.matToBitmap(mDepth, mDepthBitmap);
            //mDepthView.setImageBitmap(mDepthBitmap);
            //}
            theHandler.postDelayed(this, 50);
        }

    };

    public void alert_massage(String msg) {
        new AlertDialog.Builder(mActivity).setTitle("系统错误")//设置对话框标题
                .setMessage("请联系管理员！err= " + msg);
    }

    private void saveDataAndJumpPage() {
        String path = mActivity.getImagePath("ir") + File.separator + DateUtils.formateDate(new Date());
        DeepCameraInfo deepCameraInfo = mActivity.getDeepCameraInfo();
        deepCameraInfo.setNew(true);
        deepCameraInfo.setFilepath(path);
        deepCameraInfo.setRgbBitmap(mRgbBitmap);
        mActivity.getDeepCameraInfo().setDepthMat(mDepth);
        RecordFragmentWoundIRMeasure fragment = RecordFragmentWoundIRMeasure.createInstance();
        mActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.detail_container, fragment)
                .commit();
    }



    @Override
    public void onCameraViewStarted(int width, int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCameraViewStopped() {
        // TODO Auto-generated method stub

    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        // TODO Auto-generated method stub
        return null;
    }

}
