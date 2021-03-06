package com.iteye.chenwh.wound.native_utils;

/**
 * Created by yy on 17-3-2.
 */

public class TyNativeUtils {
    // 相对于640 * 480 的偏移
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
        //System.loadLibrary("usb1.0ty");
        //System.loadLibrary("rooxin_camm");
        System.loadLibrary("wound_dll");
    }

    public native int CloseDevice();

    public native int StopDevice();

    public native int StartDevice();

    public native int OpenDevice(final int width, final int heigth);

    public native int FetchData(final long depthAddr, final long rgbAddr, final long pointAddr);

}
