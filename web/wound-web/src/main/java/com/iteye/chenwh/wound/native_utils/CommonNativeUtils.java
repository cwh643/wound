package com.iteye.chenwh.wound.native_utils;

import org.opencv.core.Mat;

/**
 * Created by yy on 17-3-2.
 */

public class CommonNativeUtils {

    static {
        System.out.println( "chenwh:" + System.getProperty("java.library.path"));
        System.loadLibrary("tycam");
        System.loadLibrary("wound_dll");
        System.loadLibrary("opencv_world300");
        //System.loadLibrary("opencv_java300");
        //System.loadLibrary("rooxin_camm");

    }

    public static native int cvFitPlane(final long points, final long plane);

}
