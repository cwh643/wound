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
import com.github.mikephil.charting.matrix.Vector3;

import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat6;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Rect;
import org.opencv.imgproc.Subdiv2D;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by yuanyuan06 on 2017/5/4.
 */
public class DeepModelDisplayViewV2 extends GLSurfaceView implements GLSurfaceView.Renderer {
    public static String TAG = "model";

    public static double AREA_PER_POINT = Math.tan(Math.toRadians(30)) / 320 * Math.tan(Math.toRadians(23)) / 240 * 1000;
    public static float MAX_DISTINCE = 100;
    public static float MIN_DISTINCE = 0;
    private Opengl3DHelper mHelper;
    /*private Arrows mArrows;*/
    private float[] mVertex;
    private float[] mColor;
    private float[] mRotationMatrix = new float[16];
    private float mView_distince = 100;
    private Bitmap rgbBitmap;
    private Mat mDepthMat;
    private DeepCameraInfo deepCameraInfo;
    private Subdiv2D subdiv2D;

    public void init() {
        this.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setRenderer(this);
        Matrix.setIdentityM(mRotationMatrix, 0);
        subdiv2D = new Subdiv2D();
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

    public DeepModelDisplayViewV2(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        init();
    }

    public DeepModelDisplayViewV2(Context context, AttributeSet attrs) {
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
        //getVertexArr();
        getDelaunay();
        Log.d("model", "model_process 0 end");
    }

    private void getDelaunay() {
        List<Float> vertexList = deepCameraInfo.getVertexList();
        List<Float> colorList = deepCameraInfo.getColorList();
        //List<Float> vertexList = new ArrayList<>();
        //List<Float> colorList = new ArrayList<>();
        //mock_point_squar(vertexList, colorList);

        List<Float> transVertex = new ArrayList<>();
        List<Float> transColor = new ArrayList<>();
        int matWidth = deepCameraInfo.getDepthMat().cols();
        int matHeight = deepCameraInfo.getDepthMat().rows();
        Rect rect = new Rect(0, 0, matWidth, matHeight);
        int fix_width = 180;
        int fix_height = 120;
        if (deepCameraInfo.getModelCenter() != null) {
            fix_width = (int) deepCameraInfo.getModelCenter().x;
            fix_height = matHeight - (int) deepCameraInfo.getModelCenter().y;
        } else {
            fix_width = deepCameraInfo.getDepthMat().width() / 2;
            fix_height = deepCameraInfo.getDepthMat().height() / 2;
        }
        float min_deep = new Float(deepCameraInfo.getMinDeep()).floatValue();
        // 构造delaunay三角
        subdiv2D.initDelaunay(rect);
        HashMap<Point, PointInfo3D> deepMap = new HashMap<>();
        for (int i = 0; i < vertexList.size(); i += 3) {
            Point pt = new Point(vertexList.get(i), matHeight - vertexList.get(i + 1));
            subdiv2D.insert(pt);
            if (vertexList.get(i + 2) > min_deep + 200) {
                int a = 1;
            }
            PointInfo3D pinfo = new PointInfo3D();
            pinfo.x = vertexList.get(i);
            pinfo.y = vertexList.get(i + 1);
            pinfo.z = vertexList.get(i + 2);
            int colorIdx = i / 3 * 4;
            pinfo.colorR = colorList.get(colorIdx);
            pinfo.colorG = colorList.get(colorIdx + 1);
            pinfo.colorB = colorList.get(colorIdx + 2);
            pinfo.colorA = colorList.get(colorIdx + 3);
            deepMap.put(pt, pinfo);
        }
        MatOfFloat6 mat6 = new MatOfFloat6();
        subdiv2D.getTriangleList(mat6);
        for (int i = 0; i < mat6.cols(); i++) {
            for (int j = 0; j < mat6.rows(); j++) {
                double[] ary = mat6.get(j, i);
                Point pt1 = new Point(new Double(ary[0]), new Double(ary[1]));
                Point pt2 = new Point(new Double(ary[2]), new Double(ary[3]));
                Point pt3 = new Point(new Double(ary[4]), new Double(ary[5]));
                if (!deepMap.containsKey(pt1) || !deepMap.containsKey(pt2) || !deepMap.containsKey(pt3)) {
                    continue;
                }
                for (int k = 0; k < 6; k += 2) {
                    Point pt = new Point(new Double(ary[k]), new Double(ary[k + 1]));
                    transVertex.add(new Float(ary[k]) - fix_width);
                    transVertex.add(new Float(ary[k + 1]) - fix_height);

                    if (deepMap.containsKey(pt)) {
                        PointInfo3D pinfo = deepMap.get(pt);
                        Float deep = new Float(min_deep - pinfo.z);
                        //if (deep < GlobalDef.MODEL_MIN_DEEP) {
                        //    deep = GlobalDef.MODEL_MIN_DEEP;
                        //}
                        transVertex.add(deep);
                        transColor.add(pinfo.colorR);
                        transColor.add(pinfo.colorG);
                        transColor.add(pinfo.colorB);
                        transColor.add(pinfo.colorA);
                    } else {
                        transVertex.add(new Float(0));
                        transColor.add(0f);
                        transColor.add(0f);
                        transColor.add(0f);
                        transColor.add(0f);
                    }
                }


            }

        }
        mVertex = new float[transVertex.size()];
        mColor = new float[transColor.size()];
        for (int i = 0; i < mVertex.length; i++) {
            mVertex[i] = transVertex.get(i);
        }
        for (int i = 0; i < transColor.size(); i++) {
            mColor[i] = transColor.get(i);
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
        vertexList.add(100.0f);
        vertexList.add(100.0f);
        vertexList.add(400.0f);

        vertexList.add(110.0f);
        vertexList.add(100.0f);
        vertexList.add(501.0f);

        vertexList.add(120.0f);
        vertexList.add(100.0f);
        vertexList.add(300.0f);


        vertexList.add(130.0f);
        vertexList.add(100.0f);
        vertexList.add(550.0f);

        vertexList.add(90.0f);
        vertexList.add(110.0f);
        vertexList.add(600.0f);

        vertexList.add(100.0f);
        vertexList.add(110.0f);
        vertexList.add(400.0f);

        vertexList.add(110.0f);
        vertexList.add(110.0f);
        vertexList.add(300.0f);

        vertexList.add(120.0f);
        vertexList.add(110.0f);
        vertexList.add(545.0f);

        vertexList.add(100.0f);
        vertexList.add(120.0f);
        vertexList.add(500.0f);

        vertexList.add(110.0f);
        vertexList.add(120.0f);
        vertexList.add(349.0f);

        vertexList.add(120.0f);
        vertexList.add(120.0f);
        vertexList.add(500.0f);

        vertexList.add(130.0f);
        vertexList.add(120.0f);
        vertexList.add(450.0f);

        vertexList.add(120.0f);
        vertexList.add(130.0f);
        vertexList.add(500.0f);

        for (int i = vertexList.size(); i > 0; i--) {
            colorList.add(1.0f * (new Float(i) / vertexList.size()));
            colorList.add(1.0f);
            colorList.add(1.0f);
            colorList.add(1.0f);
        }

    }

    private void mock_point_squar(List<Float> vertexList, List<Float> colorList) {
        vertexList.clear();
        colorList.clear();
        vertexList.add(100.0f);
        vertexList.add(100.0f);
        vertexList.add(500.0f);

        vertexList.add(110.0f);
        vertexList.add(100.0f);
        vertexList.add(500.0f);

        vertexList.add(120.0f);
        vertexList.add(100.0f);
        vertexList.add(500.0f);

        vertexList.add(100.0f);
        vertexList.add(110.0f);
        vertexList.add(500.0f);

        vertexList.add(110.0f);
        vertexList.add(110.0f);
        vertexList.add(500.0f);

        vertexList.add(120.0f);
        vertexList.add(110.0f);
        vertexList.add(500.0f);

        vertexList.add(100.0f);
        vertexList.add(120.0f);
        vertexList.add(500.0f);

        vertexList.add(110.0f);
        vertexList.add(120.0f);
        vertexList.add(500.0f);

        vertexList.add(120.0f);
        vertexList.add(120.0f);
        vertexList.add(500.0f);


        for (int i = vertexList.size(); i > 0; i--) {
            colorList.add(1.0f * (new Float(i) / vertexList.size()));
            colorList.add(1.0f * (new Float(i) / vertexList.size()));
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
