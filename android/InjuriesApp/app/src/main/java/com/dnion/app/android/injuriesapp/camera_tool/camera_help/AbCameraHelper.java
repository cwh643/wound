package com.dnion.app.android.injuriesapp.camera_tool.camera_help;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.dnion.app.android.injuriesapp.camera_tool.native_utils.AbNativeUtils;
import com.dnion.app.android.injuriesapp.camera_tool.GlobalDef;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.openni.Device;
import org.openni.DeviceInfo;
import org.openni.ImageRegistrationMode;
import org.openni.OpenNI;
import org.openni.PixelFormat;
import org.openni.SensorType;
import org.openni.VideoFrameRef;
import org.openni.VideoMode;
import org.openni.VideoStream;
import org.openni.android.OpenNIHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created by yy on 17/10/14.
 */

public class AbCameraHelper extends AbstractCameraHelper implements OpenNIHelper.DeviceOpenListener {
    String TAG = "obColor";

    boolean mInit_Ok = false;
    private Device mDevice;
    private OpenNIHelper mOpenNIHelper;
    private VideoStream mRgbStream;
    private VideoStream mDepthStream;
    private List<VideoMode> mVideoModes;
    private Callback mLoadCallback;
    List<VideoStream> rgbStreams = new ArrayList<VideoStream>();
    List<VideoStream> depthStreams = new ArrayList<VideoStream>();
    private AbNativeUtils nativeUtils;
    private boolean rgbStarted = false;
    private boolean depthStarted = false;
    boolean usbPermissonGrant = false;


    public void init(Context context) {
        super.init(context);

        nativeUtils = new AbNativeUtils();
        nativeUtils.deep_lx = param.deep_lx;
        nativeUtils.deep_rx = param.deep_rx;
        nativeUtils.deep_ly = param.deep_ly;
        nativeUtils.deep_ry = param.deep_ry;
        nativeUtils.deep_near = GlobalDef.CALC_MIN_DEEP;
        nativeUtils.deep_far = GlobalDef.CALC_MAX_DEEP;
        nativeUtils.deep_x_diff = transIntParam(0);
        nativeUtils.deep_y_diff = transIntParam(0);
        nativeUtils.deep_center_dis = GlobalDef.DEPTH_CENTER_DIS;
        mOpenNIHelper = new OpenNIHelper(context);
        OpenNI.setLogAndroidOutput(true);
        OpenNI.setLogMinSeverity(0);
        OpenNI.initialize();
    }

    @Override
    protected void initSize() {
        mWidth = GlobalDef.RES_COLOR_WIDTH;
        mHeight = GlobalDef.RES_COLOR_HEIGHT;
    }

    public void onResume(Callback callback) {
        this.mLoadCallback = callback;
        String uri;

        if (getDevList().isEmpty()) {
            showAlertAndExit("未找到摄像头设备，请确认连接正常");
            return;
        }
        if (!usbPermissonGrant) {
            List<DeviceInfo> devices = OpenNI.enumerateDevices();
            if (devices.isEmpty()) {
                showAlertAndExit("未找到摄像头设备，请确认连接正常");
            } else {
                int vid = devices.get(0).getUsbVendorId();
                int pid = devices.get(0).getUsbProductId();
                Log.v(TAG, "devices: " + Integer.toHexString(vid) + ", " + Integer.toHexString(pid));

                uri = devices.get(devices.size() - 1).getUri();
                Log.e(TAG, "device, uri " + uri);
                mOpenNIHelper.requestDeviceOpen(uri, this);
            }

            usbPermissonGrant = true;
        }

    }

    @Override
    public void onDeviceOpened(Device device) {
        mInit_Ok = true;
        mDevice = device;
        mRgbStream = VideoStream.create(mDevice, SensorType.COLOR);
        mVideoModes = mRgbStream.getSensorInfo().getSupportedVideoModes();

        for (VideoMode mode : mVideoModes) {
            int X = mode.getResolutionX();
            int Y = mode.getResolutionY();
            int fps = mode.getFps();
            Log.d(TAG, " color support resolution: " + X + " x " + Y + " fps: " + fps + ", (" + mode.getPixelFormat() + ")");
            if (X == mWidth && Y == mHeight && mode.getPixelFormat() == PixelFormat.RGB888 && fps == 30) {
                mRgbStream.setVideoMode(mode);
                Log.v(TAG, " setmode");
            }

        }

        mDepthStream = VideoStream.create(mDevice, SensorType.DEPTH);
        mVideoModes = mDepthStream.getSensorInfo().getSupportedVideoModes();

        for (VideoMode mode : mVideoModes) {
            int X = mode.getResolutionX();
            int Y = mode.getResolutionY();
            int fps = mode.getFps();

            Log.d(TAG, " depth support resolution: " + X + " x " + Y + " fps: " + fps + ", (" + mode.getPixelFormat() + ")");
            if (X == mWidth && Y == mHeight && mode.getPixelFormat() == PixelFormat.DEPTH_1_MM) {
                mDepthStream.setVideoMode(mode);
                Log.v(TAG, " setmode");
            }

        }
        if (device.isImageRegistrationModeSupported(ImageRegistrationMode.DEPTH_TO_COLOR)) {
            device.setDepthColorSyncEnabled(true);
            device.setImageRegistrationMode(ImageRegistrationMode.DEPTH_TO_COLOR);
        }
        mLoadCallback.onInited(0);
        rgbStreams.add(mRgbStream);
        depthStreams.add(mDepthStream);

    }

    @Override
    public void onDeviceOpenFailed(String uri) {
        mInit_Ok = false;

    }

    public int FetchData(Mat depthMat, Bitmap rgbBitmap) {
        if (!mInit_Ok) {
            return GlobalDef.NOT_READY;
        }
        if (!rgbStarted) {
            mDepthStream.start();
            mRgbStream.start();
            rgbStarted = true;
        }

        try {
            //OpenNI.waitForAnyStream(rgbStreams, 1000);
            OpenNI.waitForAnyStream(depthStreams, 1000);
        } catch (TimeoutException e) {
            e.printStackTrace();
            return GlobalDef.TIME_OUT;
        }
        VideoFrameRef rgbFrame = mRgbStream.readFrame();
        nativeUtils.rgb2mat(rgbFrame.getData(), mRgbMat.getNativeObjAddr(), rgbFrame.getStrideInBytes());
        Utils.matToBitmap(mRgbMat, rgbBitmap);

        VideoFrameRef depthFrame = mDepthStream.readFrame();
        nativeUtils.depth2mat(depthFrame.getData(), depthMat.getNativeObjAddr(), depthFrame.getStrideInBytes());
        //计算中心点距离
        centerDeep = nativeUtils.deep_center_deep;
        return GlobalDef.SUCC;
    }

    public int FetchFinalData(Mat depthMat, Bitmap rgbBitmap) {
        if (!mInit_Ok) {
            return GlobalDef.NOT_READY;
        }
        if (!rgbStarted) {
            mDepthStream.start();
            mRgbStream.start();
            rgbStarted = true;
        }

        Mat[] mats = new Mat[FINAL_DEPTH_NUM];
        for (int i = 0; i < FINAL_DEPTH_NUM; i++) {
            try {
                //OpenNI.waitForAnyStream(rgbStreams, 1000);
                OpenNI.waitForAnyStream(depthStreams, 1000);
            } catch (TimeoutException e) {
                e.printStackTrace();
                return GlobalDef.TIME_OUT;
            }
            VideoFrameRef depthFrame = mDepthStream.readFrame();
            mats[i] = new Mat(depthMat.rows(), depthMat.cols(), depthMat.type());
            nativeUtils.depth2mat(depthFrame.getData(), mats[i].getNativeObjAddr(), depthFrame.getStrideInBytes());
        }

        VideoFrameRef rgbFrame = mRgbStream.readFrame();
        nativeUtils.rgb2mat(rgbFrame.getData(), mRgbMat.getNativeObjAddr(), rgbFrame.getStrideInBytes());
        Utils.matToBitmap(mRgbMat, rgbBitmap);

        // 补全点击
        double maxNumValue = 0;
        int maxNum = 0;
        Map<Double, Integer> cacheMap = new HashMap<>();
        for (int w = 0; w < depthMat.cols(); w++) {
            for (int h = 0; h < depthMat.height(); h++) {
                for (int i = 0; i < FINAL_DEPTH_NUM; i++) {
                    double deep = mats[i].get(h, w)[0];
                    if (deep < GlobalDef.CALC_MIN_DEEP || deep > GlobalDef.CALC_MAX_DEEP) {
                        continue;
                    }
                    int num = 1;
                    if (cacheMap.containsKey(deep)) {
                        num = cacheMap.get(deep);
                        num++;
                    }
                    cacheMap.put(deep, num);
                    if (Math.max(num, maxNum) == num) {
                        maxNumValue = deep;
                        maxNum = num;
                    }
                }
                depthMat.put(h, w, maxNumValue);
                cacheMap.clear();
                maxNumValue = 0;
                maxNum = 0;
            }
        }
        return GlobalDef.SUCC;
    }

    public void FetchData_1(Mat depthMat, Bitmap rgbBitmap) {
        List<VideoStream> rgbStreams = new ArrayList<VideoStream>();
        rgbStreams.add(mRgbStream);
        List<VideoStream> depthStreams = new ArrayList<VideoStream>();
        depthStreams.add(mDepthStream);
        mDepthStream.start();
        try {
            OpenNI.waitForAnyStream(depthStreams, 1000);
        } catch (TimeoutException e) {
            e.printStackTrace();
            return;
        }
        VideoFrameRef depthFrame = mDepthStream.readFrame();
        nativeUtils.depth2mat(depthFrame.getData(), depthMat.getNativeObjAddr(), depthFrame.getStrideInBytes());
        mDepthStream.stop();
        mRgbStream.start();
        try {
            OpenNI.waitForAnyStream(rgbStreams, 1000);
        } catch (TimeoutException e) {
            e.printStackTrace();
            return;
        }
        VideoFrameRef rgbFrame = mRgbStream.readFrame();
        nativeUtils.rgb2mat(rgbFrame.getData(), mRgbMat.getNativeObjAddr(), rgbFrame.getStrideInBytes());
        Utils.matToBitmap(mRgbMat, rgbBitmap);
        mRgbStream.stop();
    }

    public void onStop() {
        if (mInit_Ok) {
            if (mRgbStream != null) {
                mRgbStream.stop();

            }

            if (mDepthStream != null) {
                mDepthStream.stop();
            }

            if (mDevice != null) {
                mDevice.close();
            }

            rgbStarted = false;
            usbPermissonGrant = false;

            Log.i(TAG, "ab camera closed!");
        }
    }

    private void showAlertAndExit(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(message);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }


    private HashMap<String, UsbDevice> getDevList() {
        UsbManager manager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        Iterator<UsbDevice> iterator = deviceList.values().iterator();
        while (iterator.hasNext()) {
            UsbDevice device = (UsbDevice) iterator.next();
            int vendorId = device.getVendorId();
            int productId = device.getProductId();

            if ((vendorId == 0x1D27 && (productId == 0x05FC || productId != 0x0601)) ||
                    (vendorId == 0x2BC5 && (productId >= 0x0401 && productId <= 0x04FF))
                    ) {
                continue;
            } else {
                iterator.remove();
            }
        }

        return deviceList;
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
