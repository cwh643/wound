package com.dnion.app.android.injuriesapp.camera_tool.native_utils;

/**
 * Created by Administrator on 2018-10-24.
 */

public class GPIOControl {

    static {
        System.loadLibrary("GPIOControl");
    }

    public static native int open();

    public static native int read(int gpio);//读取GPIO口转态

    public static native int write(int gpio, int value);//控制GPIO口高低转态

    public static native void close();

}
