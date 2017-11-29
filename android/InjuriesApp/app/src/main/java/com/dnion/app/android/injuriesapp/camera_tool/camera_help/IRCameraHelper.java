package com.dnion.app.android.injuriesapp.camera_tool.camera_help;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.Log;

import com.dnion.app.android.injuriesapp.camera_tool.GlobalDef;
import com.dnion.app.android.injuriesapp.camera_tool.OpenGL2DView;
import com.dnion.app.android.injuriesapp.camera_tool.camera_help.AbstractCameraHelper;


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
 * Created by baidu on 17/10/14.
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
    private OpenGL2DView mIRView;
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

    private int mWidth = GlobalDef.RES_COLOR_WIDTH;
    private int mHeight = GlobalDef.RES_COLOR_HEIGHT;
    private IrSdk irHandle;

    public void init(Context context) {
        super.init(context);
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
    protected void initSize() {
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
        if (ret == irHandle.captureImage(mImageBuffer, 160 * 120 * Integer.SIZE,
                mTempBuffer, 320 * 240 * Double.SIZE)) {
            rgbBitmap.setPixels(mImageBuffer, 0, 120, 0, 0, 120, 160);
            if (depthMat != null) {
                for (int i = 0; i < mTempBuffer.length; i++) {
                    int y = 319 - i / 240;
                    int x = i % 240;
                    double temp = mTempBuffer[i] * 100;
                    depthMat.put(x, y, temp);
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
