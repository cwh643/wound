package com.dnion.app.android.injuriesapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.dnion.app.android.injuriesapp.camera_tool.camera_help.AbCameraHelper;
import com.dnion.app.android.injuriesapp.camera_tool.camera_help.AbstractCameraHelper;
import com.dnion.app.android.injuriesapp.camera_tool.camera_help.TYCameraHelper8x;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.util.Hashtable;

/**
 * Created by Administrator on 2018-10-24.
 */

public class QrCodeUtils {

    public static Result scanningImage(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        Bitmap scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);
        return scanningImage(scanBitmap);
    }

    public static Bitmap fetchImage(Context context) {
        Bitmap rgb = Bitmap.createBitmap(1280, 960, Bitmap.Config.ARGB_8888);
        // 1. 创建个cameraHelper，并初始化图片参数
        AbstractCameraHelper cameraHelper = new TYCameraHelper8x();
        cameraHelper.init(context, "1280x960");

        // 2. 创建个回调，用看打开摄像头之后的操作
        AbstractCameraHelper.Callback mLoadCallback = new AbCameraHelper.Callback() {
            @Override
            public void onInited(int status) {
                if (status != 0) {
                    // 错误
                    return;
                }
                // TODO 这里表示打开成功，可以在这里直接轮询图片
            }
        };
        // 3. 打开设备
        cameraHelper.onResume(mLoadCallback);
        // 4.读读片的具体方法
        cameraHelper.FetchImage(rgb);
        // 4.注销设备
        cameraHelper.onStop();
        return rgb;
    }


    public static Result scanningImage(Bitmap scanBitmap) {
        if (scanBitmap == null) {
            return null;
        }
        // DecodeHintType 和EncodeHintType
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码
        int width = scanBitmap.getWidth();
        int height = scanBitmap.getHeight();
        int[] pixels = new int[width * height];
        scanBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        Result result = null;
        try {
            result = reader.decode(bitmap, hints);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return result;
    }
}
