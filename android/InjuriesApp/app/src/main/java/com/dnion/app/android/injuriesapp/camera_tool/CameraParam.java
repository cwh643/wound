package com.dnion.app.android.injuriesapp.camera_tool;

/**
 * Created by yy on 17/11/4.
 */

public class CameraParam {
    public int deep_lx;
    public int deep_ly;
    public int deep_rx;
    public int deep_ry;
    public float camera_size_factor;

    public int getValidWidth() {
        return deep_rx - deep_lx;
    }

    public int getValidHeight() {
        return deep_ry - deep_ly;
    }

    public int transIntParam(int p_value) {
        return new Float(p_value * camera_size_factor).intValue();
    }

    public float transFloatParam(float p_value) {
        return p_value * camera_size_factor;
    }

}