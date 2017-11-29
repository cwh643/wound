package com.dnion.app.android.injuriesapp.camera_tool.native_utils;

import org.opencv.core.Mat;

import java.nio.ByteBuffer;

/**
 * Created by xlj on 17-3-2.
 */

public class AbNativeUtils {

    static {
        System.loadLibrary("opencv_java");
        System.loadLibrary("rooxin_camm");
    }

    public  native static int ConvertTORGBA(ByteBuffer src, ByteBuffer dst, int w, int h, int strideInBytes);
    public  native static int RGB888TORGBA(ByteBuffer src, ByteBuffer dst, int w, int h, int strideInBytes);
    public  native static int rgb2mat(ByteBuffer src, final long mat, int strideInBytes);
    public  native static int depth2mat(ByteBuffer src, final long mat, int strideInBytes);


}
