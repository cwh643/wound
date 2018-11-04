package com.iteye.chenwh.wound.native_utils;

import java.nio.ByteBuffer;

/**
 * Created by yy on 17-3-2.
 */

public class AbNativeUtils {
    // 这里的参数只是为了参考，会被camerahelper覆盖
    public int deep_lx = 120;
    public int deep_ly = 90;
    public int deep_rx = 360;
    public int deep_ry = 270;
    public int deep_near = 500;
    public int deep_far = 700;
    public int deep_min_deep = 0;
    public int deep_max_deep = 0;
    public int deep_center_deep = 0;
    public int deep_x_diff = 130;
    public int deep_y_diff = 60;
    public int deep_center_dis = 3;

    static {
        System.loadLibrary("opencv_java");
        System.loadLibrary("rooxin_camm");
    }

    public  native int ConvertTORGBA(ByteBuffer src, ByteBuffer dst, int w, int h, int strideInBytes);
    public  native int RGB888TORGBA(ByteBuffer src, ByteBuffer dst, int w, int h, int strideInBytes);
    public  native int rgb2mat(ByteBuffer src, final long mat, int strideInBytes);
    public  native int depth2mat(ByteBuffer src, final long mat, int strideInBytes);


}
