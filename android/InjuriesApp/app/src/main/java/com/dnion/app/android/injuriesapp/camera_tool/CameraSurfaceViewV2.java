//package com.dnion.app.android.injuriesapp.camera_tool;
//
//import android.content.Context;
//import android.graphics.ImageFormat;
//import android.hardware.camera2.CameraAccessException;
//import android.hardware.camera2.CameraCharacteristics;
//import android.hardware.camera2.CameraManager;
//import android.media.ImageReader;
//import android.os.Handler;
//import android.os.HandlerThread;
//import android.util.AttributeSet;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//
//import android.util.Log;
//
///**
// * Created by yuanyuan06 on 2015/12/4.
// */
//public class CameraSurfaceViewV2 extends SurfaceView implements SurfaceHolder.Callback {
//    static String TAG = "camera surface";
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder holder) {
//        try {
//            if (mCamera != null) {
//                mCamera.stopPreview();
//                mCamera.release();
//                mCamera = null;
//            }
//        } catch (Exception e) {
//            Log.e()
//
//        }
//
//    }
//
//    private SurfaceHolder mHolder;
//    private CameraManager mCameraManager;
//
//    public boolean init() {
//        /**
//         * 安全获取Camera对象实例的方法
//         */
//        mCameraManager = (CameraManager) this.getContext().getSystemService(Context.CAMERA_SERVICE);
//        // 安装一个SurfaceHolder.Callback，
//        // 这样创建和销毁底层surface时能够获得通知。
//         mHolder = getHolder();
//        mHolder.addCallback(this);
//        return true;
//
//    }
//
//
//
//
//    public CameraSurfaceViewV2(Context context) {
//        //开启相机
//        //将本surface对象加入到相机图层中
//        //初始化画箭头的对象
//        super(context);
//
//    }
//
//    public CameraSurfaceViewV2(Context context, AttributeSet attrs) {
//        super(context, attrs);
//
//    }
//
//    @Override
//    public void surfaceCreated(SurfaceHolder holder) {
//        // surface已被创建，现在把预览画面的位置通知摄像头
//        initCameraAndPreview();
//    }
//
//    private Handler mHandler;
//    private String mCameraId;
//    private ImageReader mImageReader;
//    private void initCameraAndPreview() {
//        Log.d("linc", "init camera and preview");
//        HandlerThread handlerThread = new HandlerThread("Camera2");
//        handlerThread.start();
//        mHandler = new Handler(handlerThread.getLooper());
//        try {
//            mCameraId = "" + CameraCharacteristics.LENS_FACING_FRONT;
//            mImageReader = ImageReader.newInstance(this.getWidth(), this.getHeight(),
//                    ImageFormat.JPEG,/*maxImages*/7);
//            mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mHandler);
//
//            mCameraManager.openCamera(mCameraId, DeviceStateCallback, mHandler);
//        } catch (CameraAccessException e) {
//            Log.e("linc", "open camera failed." + e.getMessage());
//        }
//    }
//
//    @Override
//    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
//
//    }
//
//}
