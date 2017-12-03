package com.dnion.app.android.injuriesapp.camera_tool.camera_help;

import android.content.Context;
import android.graphics.Bitmap;

import com.dnion.app.android.injuriesapp.camera_tool.CameraParam;
import com.dnion.app.android.injuriesapp.camera_tool.GlobalDef;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

/**
 * Created by baidu on 17/10/14.
 */

public abstract class AbstractCameraHelper {
    String TAG = "cameraHelper";

    protected int CENTER_DIS = 3;
    protected int FINAL_DEPTH_NUM = 3;
    boolean mInit_Ok = false;
    protected Context mContext;
    protected Mat mRgbMat;
    protected Mat mDepthMat;
    protected Callback mLoadCallback;
    protected boolean rgbStarted = false;
    protected boolean depthStarted = false;
    protected boolean mExit = false;
    protected double minDeep;

    protected int mWidth = GlobalDef.RES_COLOR_WIDTH_640;
    protected int mHeight = GlobalDef.RES_COLOR_HEIGHT_480;
    protected CameraParam param = new CameraParam();

    public void init(Context context) {
        mContext = context;
        initSize();
        mRgbMat = new Mat(mHeight, mWidth, CvType.CV_8UC3);
    }

    protected void initSize() {
        mWidth = GlobalDef.RES_COLOR_WIDTH_640;
        mHeight = GlobalDef.RES_COLOR_HEIGHT_480;
    }

    public Bitmap getRgbBitmap() {
        return Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
    }

    public Bitmap getDepthBitmap() {
        return Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
    }

    public void onResume(Callback callback) {
        this.mLoadCallback = callback;
    }

    public int FetchData(Mat depthMat, Bitmap rgbBitmap) {
        return 0;
    }

    public int FetchFinalData(Mat depthMat, Bitmap rgbBitmap) {
        return 0;
    }

    public void onStop() {
    }

    public static abstract class Callback {
        public abstract void onInited(int status);
    }

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

    public CameraParam getParam() {
        return param;
    }

    public void setParam(CameraParam param) {
        this.param = param;
    }

    public double getMinDeep() {
        return minDeep;
    }

    public void setMinDeep(double minDeep) {
        this.minDeep = minDeep;
    }
}
