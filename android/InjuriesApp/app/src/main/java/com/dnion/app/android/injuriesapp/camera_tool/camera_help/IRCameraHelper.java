package com.dnion.app.android.injuriesapp.camera_tool.camera_help;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.Log;

import com.dnion.app.android.injuriesapp.camera_tool.GlobalDef;


import net.launchdigital.irsdk.IrSdk;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.openni.Device;
import org.openni.VideoFrameRef;
import org.openni.VideoMode;
import org.openni.VideoStream;
import org.openni.android.OpenNIHelper;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by yy on 17/10/14.
 */

public class IRCameraHelper extends AbstractCameraHelper {
    String TAG = "IR";

    boolean mInit_Ok = false;
    private Device mDevice;
    private OpenNIHelper mOpenNIHelper;
    private VideoStream mRgbStream;
    private VideoStream mDepthStream;
    private List<VideoMode> mVideoModes;
    private VideoFrameRef mLastFrame;
    private ByteBuffer mByteBuf;
    private Mat mRgbMat;
    private Mat mDepthMat;
    /**
     * rgb888 数据缓冲区
     */
    private int[] mImageBuffer;

    /**
     * 温度数据缓冲区
     */
    private double[] mTempBuffer;

    private int mWidth = GlobalDef.RES_COLOR_WIDTH_320;
    private int mHeight = GlobalDef.RES_COLOR_HEIGHT_240;
    private IrSdk irHandle;

    @Override
    public void init(Context context, String size) {
        super.init(context, size);
        try {
            irHandle = new IrSdk(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        irHandle.register();
        mInit_Ok = true;
        mRgbMat = new Mat(mHeight, mWidth, CvType.CV_8UC3);
        mImageBuffer = new int[160 * 120];
        mTempBuffer = new double[240 * 320];
    }

    @Override
    protected void initSize(String size) {
        mWidth = GlobalDef.RES_IR_RGB_WIDTH;
        mHeight = GlobalDef.RES_IR_RGB_HEIGHT;
    }

    public void onResume(Callback callback) {
        super.onResume(callback);
        mLoadCallback.onInited(0);
    }

    public int FetchData(Mat depthMat, Bitmap rgbBitmap) {
        /**
         * capture 读取 RGB 数据和温度数据
         */
        int ret = 0;
        if (ret == irHandle.readImage(mImageBuffer, mImageBuffer.length * Integer.SIZE)) {
            rgbBitmap.setPixels(mImageBuffer, 0, 120, 0, 0, 120, 160);
            return 0;
        }
        return ret;
    }

    public int FetchDataTest(Mat depthMat, Bitmap rgbBitmap) {
        int ret = 0;
        if (ret == irHandle.captureImage(mImageBuffer, mImageBuffer.length * Integer.SIZE,
                mTempBuffer, mTempBuffer.length * Double.SIZE)) {
            rgbBitmap.setPixels(mImageBuffer, 0, 120, 0, 0, 120, 160);
        }
        double max_temp = 0;
        for (int i = 0; i < mTempBuffer.length; i++) {
            max_temp = Math.max(max_temp, mTempBuffer[i]);
        }
        irHandle.setTempRange(100);
        return ret;
    }

    public int FetchFinalData(Mat depthMat, Bitmap rgbBitmap) {
        int ret = 0;
        if (ret == irHandle.captureImage(mImageBuffer, mImageBuffer.length * Integer.SIZE,
                mTempBuffer, mTempBuffer.length * Double.SIZE)) {
            rgbBitmap.setPixels(mImageBuffer, 0, 120, 0, 0, 120, 160);
            if (depthMat != null) {
                for (int i = 0; i < mTempBuffer.length; i++) {
                    int y = i % 240;
                    int x = 319 - i / 240;
                    double temp = mTempBuffer[i];
                    depthMat.put(y, x, temp);
                }
            }
            return 0;
        }
        return ret;
    }

    public void onStop() {
        if (mInit_Ok) {
            irHandle.unregister();

            rgbStarted = false;

            Log.i(TAG, "ab camera closed!");
        }
    }

    public void onDestroy() {
        if (mInit_Ok && irHandle.isIrSdkReady()) {
            irHandle.destroy();
        }
    }

    private void showAlertAndExit(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(message);
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
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
}
