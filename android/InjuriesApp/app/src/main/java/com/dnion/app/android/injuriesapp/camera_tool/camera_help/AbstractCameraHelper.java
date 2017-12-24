package com.dnion.app.android.injuriesapp.camera_tool.camera_help;

import android.content.Context;
import android.graphics.Bitmap;

import com.dnion.app.android.injuriesapp.camera_tool.CameraParam;
import com.dnion.app.android.injuriesapp.camera_tool.GlobalDef;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

/**
 * Created by yy on 17/10/14.
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
    protected double centerDeep;

    protected int mWidth = GlobalDef.RES_COLOR_WIDTH_640;
    protected int mHeight = GlobalDef.RES_COLOR_HEIGHT_480;
    protected CameraParam param = new CameraParam();

    public void init(Context context) {
        mContext = context;
        initSize();
        mRgbMat = new Mat(mHeight, mWidth, CvType.CV_8UC3);
        param.camera_size_factor = new Float(mWidth) / 640;
        param.deep_lx = new Float(160 * param.camera_size_factor).intValue();
        param.deep_ly = new Float(120 * param.camera_size_factor).intValue();
        param.deep_rx = new Float(480 * param.camera_size_factor).intValue();
        param.deep_ry = new Float(360 * param.camera_size_factor).intValue();
    }

    public void init(Context context, String size) {
        mContext = context;
        initSize();
        mRgbMat = new Mat(mHeight, mWidth, CvType.CV_8UC3);
        param.camera_size_factor = new Float(mWidth) / 640;
        param.deep_lx = new Float(160 * param.camera_size_factor).intValue();
        param.deep_ly = new Float(120 * param.camera_size_factor).intValue();
        param.deep_rx = new Float(480 * param.camera_size_factor).intValue();
        param.deep_ry = new Float(360 * param.camera_size_factor).intValue();
    }



    protected void initSize() {
        mWidth = GlobalDef.RES_COLOR_WIDTH_640;
        mHeight = GlobalDef.RES_COLOR_HEIGHT_480;
    }

    protected int transIntParam(int p_value) {
        return param.transIntParam(p_value);
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

    public double getCenterDeep() {
        return centerDeep;
    }

    public void setCenterDeep(double centerDeep) {
        this.centerDeep = centerDeep;
    }

    public int getmWidth() {
        return mWidth;
    }

    public void setmWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    public int getmHeight() {
        return mHeight;
    }

    public void setmHeight(int mHeight) {
        this.mHeight = mHeight;
    }
}
