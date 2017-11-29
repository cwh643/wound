package com.dnion.app.android.injuriesapp.camera_tool.camera_help;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;

import com.dnion.app.android.injuriesapp.R;
import com.dnion.app.android.injuriesapp.camera_tool.CameraSurfaceView;
import com.dnion.app.android.injuriesapp.camera_tool.GlobalDef;
import com.dnion.app.android.injuriesapp.camera_tool.OpenGL2DView;
import com.dnion.app.android.injuriesapp.camera_tool.native_utils.TyNativeUtils;
import com.dnion.app.android.injuriesapp.utils.BitmapUtils;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.openni.Device;
import org.openni.OpenNI;
import org.openni.VideoFrameRef;
import org.openni.VideoMode;
import org.openni.VideoStream;
import org.openni.android.OpenNIHelper;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by baidu on 17/10/14.
 */

public class TYCameraHelper extends TYCameraHelper8x {
    String TAG = "obColor";

    @Override
    protected void initSize() {
        mWidth = GlobalDef.RES_COLOR_WIDTH_480;
        mHeight = GlobalDef.RES_COLOR_HEIGHT_360;
    }

}
