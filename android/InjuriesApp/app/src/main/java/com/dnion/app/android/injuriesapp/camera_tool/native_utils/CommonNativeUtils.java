package com.dnion.app.android.injuriesapp.camera_tool.native_utils;

import org.opencv.core.Mat;

/**
 * Created by yy on 17-3-2.
 */

public class CommonNativeUtils {

    public static native int cvFitPlane(final long points, final long plane);

}
