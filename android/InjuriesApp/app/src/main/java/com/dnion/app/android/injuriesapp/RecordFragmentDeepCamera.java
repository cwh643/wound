package com.dnion.app.android.injuriesapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.dnion.app.android.injuriesapp.camera_tool.CameraParam;
import com.dnion.app.android.injuriesapp.camera_tool.FocusParam;
import com.dnion.app.android.injuriesapp.camera_tool.camera_help.AbCameraHelper;
import com.dnion.app.android.injuriesapp.camera_tool.CameraSurfaceView;
import com.dnion.app.android.injuriesapp.camera_tool.GlobalDef;
import com.dnion.app.android.injuriesapp.camera_tool.camera_help.AbstractCameraHelper;
import com.dnion.app.android.injuriesapp.camera_tool.camera_help.TYCameraHelper;
import com.dnion.app.android.injuriesapp.camera_tool.camera_help.TYCameraHelper8x;
import com.dnion.app.android.injuriesapp.dao.DeepCameraInfo;
import com.dnion.app.android.injuriesapp.utils.AlertDialogUtil;
import com.dnion.app.android.injuriesapp.utils.CommonUtil;
import com.dnion.app.android.injuriesapp.utils.DateUtils;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * Created by yy on 2017/6/18.
 */

public class RecordFragmentDeepCamera extends Fragment {
    public static final String TAG = "record_fragment_camera";
    public static final int DEFAULT_PREVIEW_WIDTH = 460;
    public static final int DEFAULT_PREVIEW_HEIGHT = 345;
    public static final int CAMERA_DEFAULT_WIDTH = 960;
    public static final int CAMERA_DEFAULT_HEIGHT = 540;
    public static final int DEFAULT_TIME_OUT = 100000;
    public static int camera_src = 2; // 0:图漾4x，1:图漾8x，2:奥比
    public static String camera_size = "640x480"; // 0:图漾4x，1:图漾8x，2:奥比
    private int camera_x = 332;
    private int camera_y = 141;
    private int camera_width = 572;
    private int camera_height = 379;
    // 相对于640 * 480 的偏移
    public int deep_lx = 120;
    public int deep_ly = 90;
    public int deep_rx = 360;
    public int deep_ry = 270;
    public int deep_near = 500;
    public int deep_far = 700;
    public float deep_scale_factor = 0.35f;
    public double deep_center_deep = 0;
    public int deep_max_deep = 0;
    public int dept_factor = 140;
    private MainActivity mActivity;
    private Mat mDepth;
    private Mat mRgb;
    private AbstractCameraHelper cameraHelper;

    // final Bitmap bitmap = Bitmap.createBitmap(1280, 640, Bitmap.Config.RGB_565);
    Bitmap mRgbBitmap;
    //Bitmap mDepthBitmap = Bitmap.createBitmap(DEFAULT_PREVIEW_WIDTH, DEFAULT_PREVIEW_HEIGHT, Bitmap.Config.RGB_565);
    Bitmap mDepthBitmap;
    Bitmap mFocusBm;
    private Button mOpenButton;
    private ImageButton mShotButton;
    private TextView mParamView;
    private int mRun = 0;
    private ImageView mRgbView;
    private ImageView mDepthView;
    private ImageView mFocusView;
    private CameraSurfaceView cameraSurfaceView;
    private GestureDetector scollGestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    Mat mDepthTmpMap;
    Paint focusBackpaint;
    Canvas focusBackCanvas;
    Paint focusPaint;
    Canvas focusCanvas;
    FocusParam focusParam;

    public static void init_camera_param(String src, String size) {
        switch (src) {
            case "ty4x":
                camera_src = 0;
                break;
            case "ty8x":
                camera_src = 1;
                break;
            case "ab":
                camera_src = 2;
                break;
            default:
                camera_src = 2;
        }
        camera_size = size;
    }

    public static RecordFragmentDeepCamera createInstance() {
        RecordFragmentDeepCamera fragment = new RecordFragmentDeepCamera();
        Bundle bundle = new Bundle();
        //bundle.putLong(ARGUMENT_USER_ID, userId);
        //bundle.putString(ARGUMENT_USER_NAME, userName);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = (MainActivity) RecordFragmentDeepCamera.this.getActivity();
        //userDao = new UserDao(mActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.camera_get_deep, container, false);
        configView(rootView);
        return rootView;
    }

    private void configView(View rootView) {
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        String camera_mode = mActivity.queryConfig(CommonUtil.CAMERA_MODE);
        String camera_size = mActivity.queryConfig(CommonUtil.CAMERA_SIZE);
        init_camera_param(camera_mode, camera_size);

        mRgbView = (ImageView) rootView.findViewById(R.id.rgb_view);
        //mAbRgbView = (OpenGL2DView) rootView.findViewById(R.id.ab_rgb_view);
        mDepthView = (ImageView) rootView.findViewById(R.id.deep_camera_view);
        mFocusView = (ImageView) rootView.findViewById(R.id.focus_view);
        //mOpenButton = (Button) rootView.findViewById(R.id.open_button);
        //mOpenButton.setOnClickListener(mOpenListener);
        mOpenButton = (Button) rootView.findViewById(R.id.back_button);
        mOpenButton.setOnClickListener(mBackListener);
        mShotButton = (ImageButton) rootView.findViewById(R.id.camera_shot);
        mShotButton.setOnClickListener(mShotListener);
        mParamView = (TextView) rootView.findViewById(R.id.rgb_param);

        initCameraHelper();
        // mImageSwitch = (Switch) rootView.findViewById(R.id.image_swith);
        //  手势相关
        scollGestureDetector = new GestureDetector(this.getContext(), onScollGestureListener);
        scaleGestureDetector = new ScaleGestureDetector(this.getContext(), onScaleGestureListener);
        // mDepthView.setClickable(true);
        // mDepthView.setOnTouchListener(onTouchListener);


        focusBackpaint = new Paint();
        focusBackpaint.setColor(GlobalDef.FOCUS_BACK_COLOR);

        focusPaint = new Paint();
        focusPaint.setColor(GlobalDef.DEEP_COLOR);
    }

    void initCameraHelper() {
        switch (camera_src) {
            case 0:
                cameraHelper = new TYCameraHelper();
                break;
            case 1:
                cameraHelper = new TYCameraHelper8x();
                break;
            case 2:
                cameraHelper = new AbCameraHelper();
                break;
        }
        cameraHelper.init(this.getContext(), camera_size);
        mRgbBitmap = cameraHelper.getRgbBitmap();
        mDepthBitmap = cameraHelper.getDepthBitmap();
        mDepthTmpMap = new Mat(mDepthBitmap.getHeight(), mDepthBitmap.getWidth(), CvType.CV_8UC1);
        mDepth = new Mat(mDepthBitmap.getHeight(), mDepthBitmap.getWidth(), CvType.CV_16UC1);
        mRgb = new Mat(mRgbBitmap.getHeight(), mRgbBitmap.getWidth(), CvType.CV_8UC3);
        initFocusParam();

    }

    private void initFocusParam() {
        CameraParam param = cameraHelper.getParam();
        focusBackpaint = new Paint();
        focusBackpaint.setColor(GlobalDef.FOCUS_BACK_COLOR);

        focusPaint = new Paint();
        focusPaint.setColor(GlobalDef.DEEP_COLOR);

        focusParam = new FocusParam();
        mFocusBm = Bitmap.createBitmap(mDepthBitmap.getWidth(), mDepthBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        focusParam.center_x = mFocusBm.getWidth() / 2;
        focusParam.center_y = mFocusBm.getHeight() / 2;
        focusParam.text_x = focusParam.center_x - param.transIntParam(focusParam.text_length);
        focusParam.text_y = focusParam.center_y - param.transIntParam(focusParam.text_height);
        focusParam.line_length = param.transIntParam(focusParam.line_length);
        //focusPaint.setTextSize(param.transFloatParam(focusPaint.getTextSize()));
        focusBackCanvas = new Canvas(mDepthBitmap);
        focusCanvas = new Canvas(mFocusBm);
    }

    @Override
    public void onResume() {
        super.onResume();
        deep_lx = cameraHelper.getParam().deep_lx;
        deep_ly = cameraHelper.getParam().deep_ly;
        deep_rx = cameraHelper.getParam().deep_rx;
        deep_ry = cameraHelper.getParam().deep_ry;
        cameraHelper.onResume(mLoadCallback);
    }

    private boolean focusValidBack = false;

    private void updataFocusBitmap(String text) {
        CameraParam param = cameraHelper.getParam();
        focusPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        focusCanvas.drawPaint(focusPaint);
        focusPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        if (deep_center_deep != 0) {
            if (deep_center_deep < GlobalDef.CALC_MIN_DEEP + 50 || deep_center_deep > GlobalDef.CALC_MAX_DEEP - 50) {
                focusBackCanvas.drawRect(param.deep_lx, param.deep_ly, param.deep_rx, param.deep_ry, focusBackpaint);
                text = deep_center_deep < GlobalDef.CALC_MIN_DEEP + 50 ? "远一点" : "近一点";
            }
        }
        focusPaint.setStrokeWidth(param.transIntParam(1));
        focusCanvas.drawText(text, focusParam.text_x, focusParam.text_y, focusPaint);
        focusPaint.setStrokeWidth(param.transIntParam(4));
        focusCanvas.drawLine(focusParam.center_x - focusParam.line_length, focusParam.center_y,
                focusParam.center_x + focusParam.line_length, focusParam.center_y, focusPaint);
        focusCanvas.drawLine(focusParam.center_x, focusParam.center_y - focusParam.line_length,
                focusParam.center_x, focusParam.center_y + focusParam.line_length, focusPaint);
        mFocusView.setImageBitmap(mFocusBm);
    }

    private AbstractCameraHelper.Callback mLoadCallback = new AbCameraHelper.Callback() {
        @Override
        public void onInited(int status) {
            if (status != 0) {
                alert_massage("打开摄像头错误:" + status);
                return;
            }
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

    public View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (scaleGestureDetector.onTouchEvent(event) == true) {
                return scollGestureDetector.onTouchEvent(event);
            }
            return false;
        }
    };

    private int startDeepCamera() {
        if (mRun == 1) {
            return 0;
        }
        if (mRun == 0) {
            getDataThread.start();
        } else {
            getDataThread.run();
        }
        mRun = 1;
        return 0;
    }

    private void stopDeepCamera() {
        if (mRun != 1 && mRun != 3) {
            return;
        }
        mRun = 2;
        try {
            Thread.sleep(500l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cameraHelper.onStop();
    }

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

    private final OnClickListener mCloseListener = new OnClickListener() {
        @Override
        public void onClick(final View view) {
            stopDeepCamera();
        }
    };


    private final OnClickListener mPhotoListener = new OnClickListener() {
        @Override
        public void onClick(final View view) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");//相片类型
            startActivityForResult(intent, 1);
        }
    };
    private final OnClickListener mCameraListener = new OnClickListener() {
        @Override
        public void onClick(final View view) {
            Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivityForResult(getImageByCamera, 2);
        }
    };

    private void syncDepthSizeParam() {
        DeepCameraInfo deepCameraInfo = mActivity.getDeepCameraInfo();

        deepCameraInfo.setDeep_lx(deep_lx);
        deepCameraInfo.setDeep_ly(deep_ly);
        deepCameraInfo.setDeep_rx(deep_rx);
        deepCameraInfo.setDeep_ry(deep_ry);
        deepCameraInfo.setCamera_size_factor(cameraHelper.getParam().camera_size_factor);
        deepCameraInfo.setDeep_near(deep_near);
        deepCameraInfo.setDeep_far(deep_far);
    }

    private void saveDataAndJumpPage() {
        mRun = 3;
        String path = mActivity.getImagePath("deep") + File.separator + DateUtils.formateDate(new Date());
        DeepCameraInfo deepCameraInfo = mActivity.getDeepCameraInfo();
        syncDepthSizeParam();
        deepCameraInfo.setNew(true);
        deepCameraInfo.setFilepath(path);
        deepCameraInfo.setCenterDeep(deep_center_deep);
        synchronized (mDepth) {
            cameraHelper.FetchFinalData(mDepth, mRgbBitmap);
            deepCameraInfo.setRgbBitmap(mRgbBitmap);
            Mat depth_data = new Mat(mDepth.rows(), mDepth.cols(), CvType.CV_16UC1);
            mDepth.convertTo(depth_data, depth_data.type());
            mActivity.getDeepCameraInfo().setDepthMat(depth_data);
        }
        RecordFragmentWoundMeasure fragment = RecordFragmentWoundMeasure.createInstance();
        mActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.detail_container, fragment)
                .commit();
    }

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
                    mActivity.getString(R.string.message_wait_shot));
            view.setClickable(false);
        }

        @Override
        protected Long doInBackground(String... params) {
            // 复制深度信息
            mActivity.setDeepCameraInfo(new DeepCameraInfo());
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
        stopDeepCamera();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopDeepCamera();
    }

    private final Runnable mUpdateRgbTask = new Runnable() {
        @Override
        public void run() {
            if (mRgbBitmap != null) {
                mRgbView.setImageBitmap(mRgbBitmap);
            }
        }
    };
    private final Runnable mUpdateDepthTask = new Runnable() {
        @Override
        public void run() {
            //if (deep_center_deep < deep_near) {
            //    ToastUtil.showShortToast(mActivity, "远一点");
            //}
            //if (deep_center_deep > deep_far) {
            //    ToastUtil.showShortToast(mActivity, "近一点");
            //}

            String format = new DecimalFormat("#.00").format(deep_center_deep / 10);
            String msg = format + "厘米";

            updataFocusBitmap(msg);
            //ToastUtil.showShortToast(mActivity, "中心距离：" + format + "厘米");
            //synchronized (mBitmap) {
            //Log.d(TAG, "mIFrameCallback set depth image");
            if (mDepthBitmap != null) {
                mDepthView.setImageBitmap(mDepthBitmap);
                // mFocusView.bringToFront();
            }

            //Log.d(TAG, "mIFrameCallback set depth image end");
            //}
        }
    };

    GetDataThread getDataThread = new GetDataThread();


    protected class GetDataThread extends Thread {

        @Override
        public void run() {
            while (mRun == 1) {
                synchronized (mDepth) {
                    cameraHelper.FetchData(mDepth, mRgbBitmap);
                }
                mRgbView.post(mUpdateRgbTask);
                mDepth.convertTo(mDepthTmpMap, mDepthTmpMap.type());
                Utils.matToBitmap(mDepthTmpMap, mDepthBitmap);

                deep_center_deep = cameraHelper.getCenterDeep();
                mDepthView.post(mUpdateDepthTask);
            }
        }

    }

    public void alert_massage(String msg) {
        new AlertDialog.Builder(mActivity).setTitle("系统错误")//设置对话框标题
                .setMessage("请联系管理员！err= " + msg);
    }

}
