package com.dnion.app.android.injuriesapp.camera_tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.ByteArrayOutputStream;


/**
 * Created by yuanyuan06 on 2015/12/4.
 */
public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    static String TAG = "CAMERA";

    private SurfaceHolder mHolder;
    private Camera mCamera;
    byte[] rawImage;


    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    private Bitmap mBitmap;

    public void takePicture(Camera.PictureCallback jpeg) {
        mCamera.takePicture(shutterCallback, null, jpeg);
    }
    //快门按下的时候onShutter()被回调
    private Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback(){
        public void onShutter() {
        }
    };

    public void setPrewViewCallBack (Camera.PreviewCallback cb) {
        if (mCamera != null) {
            mCamera.setPreviewCallback(cb);
        }
    }


    public boolean init() {
        /**
         * 安全获取Camera对象实例的方法
         */
        if (mCamera == null) {
            try {
                mCamera = Camera.open(); // 试图获取Camera实例
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
                return false;
            }
            // 安装一个SurfaceHolder.Callback，
            // 这样创建和销毁底层surface时能够获得通知。
            mHolder = getHolder();
            mHolder.addCallback(this);
            //mCamera.setPreviewCallback(mPreviewCallback);
        }
        super.setDrawingCacheEnabled(true);
        return true;
    }

    public void stop() {
        try {
            if (mCamera != null) {
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
            }
        } catch (Exception e) {
            showMessage("camera Release error");
        }
    }

    public CameraSurfaceView(Context context) {
        //开启相机
        //将本surface对象加入到相机图层中
        //初始化画箭头的对象
        super(context);

    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // surface已被创建，现在把预览画面的位置通知摄像头
        showMessage("created");
        try {
            if (!init()) {
                return;
            }
            mCamera.setDisplayOrientation(0);
            mCamera.setPreviewDisplay(holder);
            Camera.Parameters parameters = mCamera.getParameters();
            //设置图片格式
            parameters.setPictureFormat(PixelFormat.JPEG);
            //设置预览的帧数,受硬件影响.
            parameters.setPreviewFrameRate(10);
            //设置焦距
            //parameters.setZoom(10);
            mCamera.setParameters(parameters);
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stop();
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

    }

    private void showMessage(String message) {
        try {
            Log.i(TAG, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
