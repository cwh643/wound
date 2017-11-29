package com.dnion.app.android.injuriesapp.camera_tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.util.Log;

import com.dnion.app.android.injuriesapp.dao.DeepCameraInfo;

import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by yuanyuan06 on 2017/5/4.
 */
public class DeepModelDisplayView extends GLSurfaceView implements GLSurfaceView.Renderer {

    public static double AREA_PER_POINT = Math.tan(Math.toRadians(30)) / 320 * Math.tan(Math.toRadians(23)) / 240 * 1000;
    public static float MAX_DISTINCE = 150;
    public static float MIN_DISTINCE = 0;
    private Opengl3DHelper mHelper;
    /*private Arrows mArrows;*/
    private float[] mVertex;
    private float[] mColor;
    private float[] mRotationMatrix = new float[16];
    private float mView_distince = 200;
    private Bitmap rgbBitmap;
    private Mat mDepthMat;
    private DeepCameraInfo deepCameraInfo;

    public void init() {
        this.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setRenderer(this);
        Matrix.setIdentityM(mRotationMatrix, 0);
    }

    public void reset() {
        if (null == mHelper) return;
        mHelper.reset();
        requestRender();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (null == mHelper) return;
        mHelper.draw();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
//        gl.glViewport(0, 0, width, height);
    }

    public DeepModelDisplayView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        init();
    }

    public DeepModelDisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        init();
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        mHelper = new Opengl3DHelper();
        //mHelper.setVertex(mVertex);
        //mHelper.setColor(mColor);
        mHelper.setProjectionMatrix(getWidth(), getHeight());


        mHelper.setVertex(mVertex);
        mHelper.setColor(mColor);
        float[] backgroud = new float[4];
        backgroud[0] = 0.5f;
        backgroud[1] = 0.5f;
        backgroud[2] = 0.5f;
        backgroud[3] = 1f;
        mHelper.setBackGroudColor(backgroud);
        Log.d("model", "model_process 1");
        requestRender();
        Log.d("model", "model_process 1 end");
    }

    public void updateDeepCameraInfo(DeepCameraInfo deepCameraInfo) {
        this.deepCameraInfo = deepCameraInfo;
        Log.d("model", "model_process 0");
        getVertexArr();
        Log.d("model", "model_process 0 end");
    }


    private void getVertexArr() {
        List<Float> vertexList = deepCameraInfo.getVertexList();
        List<Float> colorList = deepCameraInfo.getColorList();
        float min_deep = new Float(deepCameraInfo.getMinDeep()).floatValue();
        int vertex_size = vertexList.size();
        int color_size = colorList.size();
        int fix_width = 320;
        int fix_height = 240;
        if (deepCameraInfo.getModelCenter() != null) {
            fix_width = (int) deepCameraInfo.getModelCenter().x;
            fix_height = (int) deepCameraInfo.getModelCenter().y;
        } else {
            fix_width = deepCameraInfo.getDepthMat().width() / 2;
            fix_height = deepCameraInfo.getDepthMat().height() / 2;
        }
        mVertex = new float[vertex_size];
        mColor = new float[color_size];
        for (int i = 0; i < vertex_size; i += 3) {
            mVertex[i] = vertexList.get(i).floatValue() - fix_width;
            mVertex[i + 1] = vertexList.get(i + 1).floatValue() - fix_height;
            mVertex[i + 2] = min_deep - vertexList.get(i + 2).floatValue();
        }

        for (int i = 0; i < color_size; i++) {
            mColor[i] = colorList.get(i).floatValue();
        }
    }

    public void scoll(float x, float y) {
        Log.d("3dmodel", String.format("scoll x:%f,y:%f", x, y));
        float distance = new Double(Math.sqrt((x * x + y * y))).floatValue();
        if (Math.abs(x) > Math.abs(y)) {
            y = 0;
        } else {
            x = 0;
        }
        Matrix.rotateM(mRotationMatrix, 0, distance, -y, -x, 0);
        mHelper.setModelMatrix(mRotationMatrix);
        requestRender();
    }

    public void scale(float factor) {
        if (factor == 1.0f || (mView_distince >= MAX_DISTINCE && factor > 1.0f) || (mView_distince <= MIN_DISTINCE && factor < 1.0f)) {
            return;
        }

        float distince = factor * 3;
        distince = factor > 1.0f ? -distince : distince;
        mView_distince = mView_distince - distince;
        set_position();
    }

    public void reset_position() {
        mView_distince = 0;
        set_position();
    }

    public void set_position() {
        //mView_distince = 200f;
        Matrix.setIdentityM(mRotationMatrix, 0);
        Matrix.translateM(mRotationMatrix, 0, 0, 0, mView_distince);
        mHelper.setModelMatrix(mRotationMatrix);
        requestRender();
    }

    public void mat2vertex(Mat mDepth) {
        List<Float> vertexList = new ArrayList<>();
        List<Float> colorList = new ArrayList<>();
        double max_deep = 0;
        double min_deep = Double.MAX_VALUE;
        int rgbWidth, rgbHeight;
        int fix_width = mDepth.width() / 2;
        int fix_height = mDepth.height() / 2;
        float xRatio = 0, yRatio = 0;
        if (rgbBitmap != null) {
            rgbWidth = rgbBitmap.getWidth();
            rgbHeight = rgbBitmap.getHeight();
            xRatio = mDepth.width() / rgbWidth;
            yRatio = (float) mDepth.height() / rgbHeight;
        }
        for (int j = 0; j < mDepth.height() - 1; j++) {
            float last_x = 0;
            float last_y = 0;
            float last_z = 0;
            boolean first = true;
            for (int i = 0; i < mDepth.width(); i++) {
                for (int k = 0; k < 2; k++) {
                    double[] depth = mDepth.get(j + k, i);
                    if (null == depth || depth[0] < 400 || depth[0] > 20000) {
                        // Log.d("point", "deep:" + depth[0]);
                        continue;
                    }
                    double deep;
                    deep = depth[0];
                    max_deep = Math.max(deep, max_deep);
                    min_deep = Math.min(deep, min_deep);
                    deep = deep - 600;
                    last_x = i - fix_width;
                    last_y = -j + fix_height;
                    last_z = (float) -deep;
                    int times = first ? 2 : 1;
                    first = false;
                    for (int n = 0; n < times; n++) {
                        vertexList.add(last_x);
                        vertexList.add(last_y);
                        vertexList.add(last_z);
                        if (rgbBitmap != null) {
                            int rgb_i = new Float(i / xRatio).intValue();
                            int rgb_j = new Float(j / yRatio).intValue();
                            int color = rgbBitmap.getPixel(rgb_i, rgb_j);
                            colorList.add((float) Color.red(color) / 255);
                            colorList.add((float) Color.green(color) / 255);
                            colorList.add((float) Color.blue(color) / 255);
                            colorList.add((float) Color.alpha(color));
                        } else {
                            colorList.add((float) (2000 - deep) / 2000);
                            colorList.add((float) (2000 - deep) / 2000);
                            colorList.add((float) (2000 - deep) / 2000);
                            colorList.add(1.0f);
                        }
                    }
                }
            }
        }
        Log.d("", String.format("max deep:%a, min deep:%a, size:%d", max_deep, min_deep, vertexList.size()));
//        mView_distince = (float) ((max_deep - min_deep) / 2 + min_deep);
        // mView_distince = 1500f;

        // mock_point(vertexList, colorList);
        int size = vertexList.size();
        mVertex = new float[size];
        mColor = new float[size];

//
//        Log.d("model", "model_process 2");
//        get_arr(vertexList, colorList);
//        Log.d("model", "model_process 2 end");
    }

    private void mock_point(List<Float> vertexList, List<Float> colorList) {
        vertexList.clear();
        colorList.clear();
        vertexList.add(0.0f);
        vertexList.add(0.0f);
        vertexList.add(1.0f);

        vertexList.add(0.0f);
        vertexList.add(-1.0f);
        vertexList.add(0.0f);

        vertexList.add(1.0f);
        vertexList.add(0.0f);
        vertexList.add(3.0f);


        vertexList.add(1.0f);
        vertexList.add(-1.0f);
        vertexList.add(5.0f);

        vertexList.add(2.0f);
        vertexList.add(0.0f);
        vertexList.add(6.0f);

        vertexList.add(2.0f);
        vertexList.add(-1.0f);
        vertexList.add(1.0f);

        vertexList.add(0.0f);
        vertexList.add(-1.0f);
        vertexList.add(20.0f);

        vertexList.add(0.0f);
        vertexList.add(-1.0f);
        vertexList.add(8.0f);

        vertexList.add(0.0f);
        vertexList.add(-2.0f);
        vertexList.add(0.0f);

        vertexList.add(1.0f);
        vertexList.add(-1.0f);
        vertexList.add(1.0f);

        vertexList.add(1.0f);
        vertexList.add(-2.0f);
        vertexList.add(0.0f);

        vertexList.add(2.0f);
        vertexList.add(-1.0f);
        vertexList.add(0.0f);

        vertexList.add(2.0f);
        vertexList.add(-2.0f);
        vertexList.add(0.0f);

        for (int i = vertexList.size(); i > 0; i--) {
            colorList.add(1.0f);
            colorList.add(1.0f);
            colorList.add(1.0f);
            colorList.add(1.0f);
        }

    }


    public void setDistince(float distince) {
        mView_distince = 1000 - distince;
        set_position();
    }

    public void updateRGB(Bitmap RGB) {
        this.rgbBitmap = RGB;
    }

}
