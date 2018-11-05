package com.dnion.app.android.injuriesapp.camera_tool.camera_help;

import com.dnion.app.android.injuriesapp.camera_tool.GlobalDef;

import com.iteye.chenwh.wound.native_utils.TyNativeUtils;
import com.iteye.chenwh.wound.opencv.Image;
import com.iteye.chenwh.wound.opencv.ImageUtils;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created by yy on 17/10/14.
 */

public class TYCameraHelper8x extends AbstractCameraHelper {
    String TAG = "tyColor";
    boolean mInit_Ok = false;
    protected TyNativeUtils nativeUtils;

    private Mat mPointMat;
    private Mat[] mPointMatArr;
    private static Logger logger = LoggerFactory.getLogger(TYCameraHelper8x.class);
    @Override
    public void init(String size) {
        super.init(size);
        nativeUtils = new TyNativeUtils();
        nativeUtils.deep_lx = param.deep_lx;
        nativeUtils.deep_rx = param.deep_rx;
        nativeUtils.deep_ly = param.deep_ly;
        nativeUtils.deep_ry = param.deep_ry;
        nativeUtils.deep_near = GlobalDef.CALC_MIN_DEEP;
        nativeUtils.deep_far = GlobalDef.CALC_MAX_DEEP;
        //nativeUtils.deep_x_diff = transIntParam(-20);
        //nativeUtils.deep_y_diff = transIntParam(45);
        nativeUtils.deep_x_diff = transIntParam(0);
        nativeUtils.deep_y_diff = transIntParam(0);
        nativeUtils.deep_center_dis = transIntParam(GlobalDef.DEPTH_CENTER_DIS);
        mPointMat = new Mat(mHeight, mWidth, CvType.CV_16UC1);
        // 初始化数组，为保存时候取帧做准备
        mPointMatArr = new Mat[FINAL_DEPTH_NUM];
        for (int i = 0; i < FINAL_DEPTH_NUM; i++) {
            mPointMatArr[i] = new Mat(mHeight, mWidth, CvType.CV_16UC1);
        }
    }

    @Override
    protected void initSize(String size) {
        super.initSize(size);
        //mWidth = GlobalDef.RES_COLOR_WIDTH_1280;
        //mHeight = GlobalDef.RES_COLOR_HEIGHT_960;
    }

    public void onResume(Callback callback) {
        mLoadCallback = callback;
        if (OpenCVLoader.initDebug()) { //默认加载opencv_java.so库
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
            //加载依赖opencv_java.so的jni库
            System.loadLibrary("opencv_java300");
        }
        // OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback() {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    logger.info("OpenCV loaded successfully");
                    // Load native library after(!) OpenCV initialization
                    int err = nativeUtils.OpenDevice(mWidth, mHeight);
					if (err == 0) {
                        mInit_Ok = true;
                    }
                    mLoadCallback.onInited(err);
                     
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    public BufferedImage FetchData(Mat depthMat) {
        if (!mInit_Ok) {
            return null;
        }
        logger.debug("fatch data....");

        nativeUtils.FetchData(mPointMat.getNativeObjAddr(), mRgbMat.getNativeObjAddr(), depthMat.getNativeObjAddr());
        BufferedImage bi = Utils.matToBitmap(mRgbMat);
        centerDeep = nativeUtils.deep_center_deep;
        logger.debug("fatch data....0");
        return bi;
    }

    @Override
    public int FetchFinalData(Mat depthMat, Image image) {
        if (!mInit_Ok) {
            return GlobalDef.NOT_READY;
        }
        logger.debug("fatch final data....");
        for (int i = 0; i < FINAL_DEPTH_NUM; i++) {
            nativeUtils.FetchData(mPointMat.getNativeObjAddr(), mRgbMat.getNativeObjAddr(), mPointMatArr[i].getNativeObjAddr());
            //暂时注释，不知道PC上怎么对应处理
            //Utils.matToBitmap(mRgbMat, image.getAsBufferedImage());
            centerDeep = nativeUtils.deep_center_deep;
        }

        // 补全点集
        double maxNumValue = 0;
        int maxNum = 0;
        Map<Double, Integer> cacheMap = new HashMap<>();
        for (int w = 0; w < depthMat.cols(); w++) {
            for (int h = 0; h < depthMat.height(); h++) {
                for (int i = 0; i < FINAL_DEPTH_NUM; i++) {
                    double deep = mPointMatArr[i].get(h, w)[0];
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

    public void onStop() {
        nativeUtils.CloseDevice();
    }

    /*
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
    */

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
