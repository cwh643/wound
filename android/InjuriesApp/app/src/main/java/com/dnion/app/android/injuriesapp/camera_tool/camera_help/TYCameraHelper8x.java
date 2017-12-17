package com.dnion.app.android.injuriesapp.camera_tool.camera_help;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;

import com.dnion.app.android.injuriesapp.camera_tool.GlobalDef;
import com.dnion.app.android.injuriesapp.camera_tool.native_utils.TyNativeUtils;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;

/**
 * Created by yy on 17/10/14.
 */

public class TYCameraHelper8x extends AbstractCameraHelper {
    String TAG = "tyColor";
    boolean mInit_Ok = false;
    protected TyNativeUtils nativeUtils;

    public void init(Context context) {
        super.init(context);
        nativeUtils = new TyNativeUtils();
        nativeUtils.deep_lx = param.deep_lx;
        nativeUtils.deep_rx = param.deep_rx;
        nativeUtils.deep_ly = param.deep_ly;
        nativeUtils.deep_ry = param.deep_ry;
        nativeUtils.deep_near = GlobalDef.CALC_MIN_DEEP;
        nativeUtils.deep_far = GlobalDef.CALC_MAX_DEEP;
        nativeUtils.deep_x_diff = transIntParam(-20);
        nativeUtils.deep_y_diff = transIntParam(45);
        //nativeUtils.deep_x_diff = transIntParam(0);
        //nativeUtils.deep_y_diff = transIntParam(0);
        nativeUtils.deep_center_dis = transIntParam(GlobalDef.DEPTH_CENTER_DIS);
        //cameraSurfaceView = (CameraSurfaceView) rootView.findViewById(R.id.camera_surface_view);
        //cameraSurfaceView.init();
        //cameraSurfaceView.setPrewViewCallBack(mPreviewCallback);
    }

    @Override
    protected void initSize() {
        mWidth = GlobalDef.RES_COLOR_WIDTH_1280;
        mHeight = GlobalDef.RES_COLOR_HEIGHT_960;
    }

    public void onResume(Callback callback) {
        mLoadCallback = callback;
        if (OpenCVLoader.initDebug()) { //默认加载opencv_java.so库
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
            //加载依赖opencv_java.so的jni库
            System.loadLibrary("opencv_java");
            //System.loadLibrary("mixed_sample");
        }
        // OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this.mContext) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    // Load native library after(!) OpenCV initialization
                    int err = nativeUtils.OpenDevice(mWidth, mHeight);
                    mLoadCallback.onInited(err);
                    if (err == 0) {
                        mInit_Ok = true;
                    }
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    public int FetchData(Mat depthMat, Bitmap rgbBitmap) {
        if (!mInit_Ok) {
            return GlobalDef.NOT_READY;
        }
        Log.d(TAG, "fatch data....");

        nativeUtils.FetchData(depthMat.getNativeObjAddr(), mRgbMat.getNativeObjAddr());
        Utils.matToBitmap(mRgbMat, rgbBitmap);
        centerDeep = nativeUtils.deep_center_deep;
        Log.d(TAG, "fatch data....0");
        //Mat tmpMap = new Mat(mDepthBitmap.getHeight(), mDepthBitmap.getWidth(), CvType.CV_8UC1);
        //mRgb.convertTo(tmpMap, tmpMap.type());
        //Utils.matToBitmap(mRgbMat, rgbBitmap);
        //Log.d(TAG, "fatch data....1");
        //mDepthView.post(mUpdateDepthTask);
        //Log.d(TAG, "fatch data....3");
        return GlobalDef.SUCC;
    }

    public void onStop() {
        nativeUtils.CloseDevice();
    }

    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            //camera.setOneShotPreviewCallback(null);
            Log.d(TAG, "camera preview image start 0 ");
            //处理data
            Camera.Size previewSize = camera.getParameters().getPreviewSize();//获取尺寸,格式转换的时候要用到
//            BitmapFactory.Options newOpts = new BitmapFactory.Options();
//            newOpts.inJustDecodeBounds = true;
            YuvImage yuvimage = new YuvImage(
                    data,
                    ImageFormat.NV21,
                    previewSize.width,
                    previewSize.height,
                    null);
            Log.d(TAG, "camera preview image  1 ");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (!yuvimage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 100, baos)) {
                // 80--JPG图片的质量[0-100],100最高
                Log.e(TAG, "compress error");
                return;
            }
            Log.d(TAG, "camera preview image  2 ");
            byte[] tmp = baos.toByteArray();
            //将rawImage转换成bitmap
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inSampleSize = 1;
            Bitmap bitmap = BitmapFactory.decodeByteArray(tmp, 0, tmp.length, options);
            Log.d(TAG, "camera preview image  3 ");
            //mRgbBitmap = BitmapUtils.scale_image(bitmap, camera_x, camera_y, camera_width, camera_height, DEFAULT_PREVIEW_WIDTH, DEFAULT_PREVIEW_HEIGHT);
            //mRgbBitmap = BitmapUtils.scale_image(bitmap, camera_x * 4, camera_y * 4, camera_width * 4, camera_height * 4, 1920, 1280);
            //mRgbBitmap = BitmapUtils.scale_image(bitmap, camera_x * 2, camera_y * 2, camera_width * 2, camera_height * 2, 960, 690);
            //mRgbView.post(mUpdateRgbTask);
            Log.d(TAG, "camera preview image end");
        }
    };

    public Mat getmRgbMat() {
        return mRgbMat;
    }

    public void setmRgbMat(Mat mRgbMat) {
        this.mRgbMat = mRgbMat;
    }

    public Mat getmDepthMat() {
        return mDepthMat;
    }

    public void setmDepthMat(Mat mDepthMat) {
        this.mDepthMat = mDepthMat;
    }

}
