package com.dnion.app.android.injuriesapp.camera_tool;

import android.graphics.Color;

/**
 * Created by yy on 2015/8/2.
 */
public class GlobalDef {

    public static final boolean FPS_ON = false;
    public static String PACKAGE_NAME;

    // Resolution
    public static final int RES_COLOR_WIDTH_1280 = 1280;//彩色图显示宽度，在非UVC下设置为与RES_COLOR_WIDTH大小一致，否则与RES_UVC_WIDTH一致
    public static final int RES_COLOR_HEIGHT_960 = 960;//彩色图显示高度，在非UVC下设置为与RES_COLOR_HEIGHT大小一致，否则与RES_UVC_HEIGHT一致
    public static final int RES_COLOR_WIDTH_640 = 640;//彩色图显示宽度，在非UVC下设置为与RES_COLOR_WIDTH大小一致，否则与RES_UVC_WIDTH一致
    public static final int RES_COLOR_HEIGHT_480 = 480;//彩色图显示高度，在非UVC下设置为与RES_COLOR_HEIGHT大小一致，否则与RES_UVC_HEIGHT一致
    public static final int RES_COLOR_WIDTH_480 = 480;//彩色图显示宽度，在非UVC下设置为与RES_COLOR_WIDTH大小一致，否则与RES_UVC_WIDTH一致
    public static final int RES_COLOR_HEIGHT_360 = 360;//彩色图显示高度，在非UVC下设置为与RES_COLOR_HEIGHT大小一致，否则与RES_UVC_HEIGHT一致
    public static final int RES_COLOR_WIDTH_320 = 320;//普通摄像头分辨率
    public static final int RES_COLOR_HEIGHT_240 = 240;
    public static final int RES_CALC_WIDTH = 1920;//为了没有毛边，设置的比较大
    public static final int RES_CALC_HEIGHT = 1440;//为了没有毛边，设置的比较大
    public static final int RES_DEPTH_WIDTH = 640;//深度图分辨率
    public static final int RES_DEPTH_HEIGHT = 480;
    //public static final int RES_IR_RGB_WIDTH = 320;//深度图分辨率
    //public static final int RES_IR_RGB_HEIGHT = 240;
    public static final int RES_IR_RGB_WIDTH = 160;//深度图分辨率
    public static final int RES_IR_RGB_HEIGHT = 120;

    public static final int CALC_MIN_DEEP = 300;
    public static final int CALC_MAX_DEEP = 700;
    public static final float MODEL_BACK_MIN_DEEP = 10;
    public static final float MODEL_FRONT_MIN_DEEP = 50;
    public static final int MODEL_DEEP_CENTER_DIS = 5;

    public static final int DEPTH_CENTER_DIS = 3;

    public static final int AREA_COLOR = 0xFF1BB2B1;
    public static final int AREA_EDGE_COLOR = 0xFF1BB2B2;
    public static final int LENGTH_COLOR = Color.GREEN;
    public static final int WIDTH_COLOR = Color.BLUE;
    public static final int DEEP_COLOR = Color.RED;
    public static final int FOCUS_BACK_COLOR = Color.YELLOW;
    public static final int FOCUS_STROKE_WIDTH = 2;

    public static final int SUCC = 0;
    public static final int NOT_READY = -1;
    public static final int TIME_OUT = 2;

    public static final int measure_time_delay = 3000;

    public static final int R2_GPIO_LED = 14;
    public static final int R2_GPIO_ON = 1;
    public static final int R2_GPIO_OFF = 0;
}
