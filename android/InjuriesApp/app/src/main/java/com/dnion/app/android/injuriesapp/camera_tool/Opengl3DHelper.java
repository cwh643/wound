package com.dnion.app.android.injuriesapp.camera_tool;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by yuanyuan06 on 2015/12/13.
 */
public class Opengl3DHelper {
    protected final String vertexShaderCode =
            "uniform mat4 u_MVPMatrix;      \n"        // A constant representing the combined model/view/projection matrix.

                    + "attribute vec4 a_Position;     \n"        // Per-vertex position information we will pass in.
                    + "attribute vec4 a_Color;        \n"        // Per-vertex color information we will pass in.
                    + "varying vec4 v_Color;          \n"        // This will be passed into the fragment shader.

                    + "void main()                    \n"        // The entry point for our vertex shader.
                    + "{                              \n"
                    + "   v_Color = a_Color;          \n"        // Pass the color through to the fragment shader.
                    // It will be interpolated across the triangle.
                    + "   gl_Position = u_MVPMatrix   \n"     // gl_Position is a special variable used to store the final position.
                    + "               * a_Position;   \n"     // Multiply the vertex by the matrix to get the final point in
                    + "}                              \n";    // normalized screen coordinates.

    protected final String fragmentShaderCode =
            "precision mediump float;       \n"        // Set the default precision to medium. We don't need as high of a
                    // precision in the fragment shader.
                    + "varying vec4 v_Color;          \n"        // This is the color from the vertex shader interpolated across the
//                    + "uniform vec4 v_Color;          \n"        // This is the color from the vertex shader interpolated across the
                    // triangle per fragment.
                    + "void main()                    \n"        // The entry point for our fragment shader.
                    + "{                              \n"
                    + "   gl_FragColor = v_Color;     \n"        // Pass the color directly through the pipeline.
                    + "}                              \n";

    protected final int POSITION_DATA_SIZE = 3;
    protected final int COLOR_DATA_SIZE = 4;
    protected static final int BYTES_PER_FLOAT = 4;
    /**
     * 点集的间隔，0代表紧密联系在一起
     */
    protected final int vertexStride = 0;

    protected int vertexCount = 0;
    protected int mMVPMatrixHandle;
    protected int vertexShaderHandle;
    protected int fragmentShaderHandle;
    protected int mProgram;
    protected int mPositionHandle;
    protected int mColorHandle;
    protected float[] mMVPMatrix = new float[16];
    protected float[] mViewMatrix = new float[16];
    protected float[] mModelMatrix = new float[16];
    protected float[] mProjectionMatrix = new float[16];
    protected float[] mBackGroudColor = new float[4];
    protected FloatBuffer positionBuffer;
    protected FloatBuffer colorBuffer;

    public Opengl3DHelper() {
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.setIdentityM(mViewMatrix, 0);
        Matrix.setIdentityM(mProjectionMatrix, 0);
        setViewMatrix(0, 0, 200f);
        initProgram();
        GLES20.glUseProgram(mProgram);
    }

    public int initProgram() {
        vertexShaderHandle = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        fragmentShaderHandle = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        linkProgram();
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix");
        return this.mProgram;
    }

    /**
     * @param type       GLES20.GL_VERTEX_SHADER | GLES20.GL_FRAGMENT_SHADER
     * @param shaderCode code String
     * @return shaderHanlder
     */
    protected int loadShader(int type, String shaderCode) {

        // 创建一个vertex shader类型(GLES20.GL_VERTEX_SHADER)
        // 或fragment shader类型(GLES20.GL_FRAGMENT_SHADER)
        int shaderHandle = GLES20.glCreateShader(type);
        if (shaderHandle != 0) {
            // 将源码添加到shader并编译之
            GLES20.glShaderSource(shaderHandle, shaderCode);
            GLES20.glCompileShader(shaderHandle);
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            if (compileStatus[0] == 0) {
                GLES20.glDeleteShader(shaderHandle);
                shaderHandle = 0;
            }
        }
        if (shaderHandle == 0) {
            throw new RuntimeException("failed to create shader:" + type);
        }
        return shaderHandle;
    }

    protected void linkProgram() {
        mProgram = GLES20.glCreateProgram();
        if (mProgram != 0) {
            GLES20.glAttachShader(mProgram, vertexShaderHandle);
            GLES20.glAttachShader(mProgram, fragmentShaderHandle);

//            GLES20.glBindAttribLocation(mProgram, 0, "a_Position");
//            GLES20.glBindAttribLocation(mProgram, 1, "a_Color");

            GLES20.glLinkProgram(mProgram);

            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, linkStatus, 0);

            if (linkStatus[0] == 0) {
                GLES20.glDeleteProgram(mProgram);
                mProgram = 0;
            }
        }

        if (mProgram == 0) {
            throw new RuntimeException("failed to create program");
        }
    }


    public void setVertex(float[] data) {
        if (null == data) {
            return;
        }
        positionBuffer = ByteBuffer.allocateDirect(data.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        positionBuffer.put(data).position(0);
    }

    public void setColor(final float[] data) {
        if (null == data) {
            return;
        }
        colorBuffer = ByteBuffer.allocateDirect(data.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        colorBuffer.put(data).position(0);
    }

    public void setBackGroudColor(float[] color) {
        mBackGroudColor = color;
    }

    protected void renderData(FloatBuffer dataBuffer, int handle, int vertexSize, int vertexStride) {
        dataBuffer.position(0);
        GLES20.glEnableVertexAttribArray(handle);
        GLES20.glVertexAttribPointer(handle, vertexSize, GLES20.GL_FLOAT, false, vertexStride, dataBuffer);
    }

    public void setModelMatrix(float[] modelMatrix) {
        // 获取指向fragment shader的成员vColor的handle
        mModelMatrix = modelMatrix;
//        Matrix.setIdentityM(mModelMatrix, 0);
//        Matrix.multiplyMM(mModelMatrix, 0, modelMatrix, 0, mModelMatrix, 0);
    }

    private float rotationVector;

    public void rotate(float[] rotationVector) {
        rotationVector = rotationVector;
    }

    public void setProjectionMatrix(int width, int height) {
        final float ratio = (float) height / width;
        final float left = -1.0f;
        final float right = 1.0f;
        final float bottom = -ratio;
        final float top = ratio;
        final float near = 1f;
        final float far = 2000000f;
//        Matrix.orthoM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
        GLES20.glViewport(0, 0, width, height);
    }

    public void reset() {
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.setIdentityM(mMVPMatrix, 0);
    }

    public void setViewMatrix(float x, float y, float z) {
        // Position the eye behind the origin.
        final float eyeX = x; //0.0f;
        final float eyeY = y; //0.0f;
        final float eyeZ = z; //500f;

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = 0.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
    }

    public void draw() {
        if (null == positionBuffer || null == colorBuffer) {
            //showMessage("顶点为空");
            return;
        }
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        //在draw方法里设置点的原因是因为javagc会回收openGl的内存，导致顶点数据不可用
        if (null != mBackGroudColor) {
            GLES20.glClearColor(mBackGroudColor[0], mBackGroudColor[1], mBackGroudColor[2], mBackGroudColor[3]);
        }
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "a_Color");
        renderData(positionBuffer, mPositionHandle, POSITION_DATA_SIZE, vertexStride);
        renderData(colorBuffer, mColorHandle, COLOR_DATA_SIZE, vertexStride);
        Matrix.setIdentityM(mMVPMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        //GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, positionBuffer.remaining() / POSITION_DATA_SIZE);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, positionBuffer.remaining() / POSITION_DATA_SIZE);
        //GLES20.glDrawArrays(GLES20.GL_POINTS, 0, positionBuffer.remaining() / POSITION_DATA_SIZE);
    }

    public void showMessage(String msg) {
        Log.e("OPENGL", msg);
    }

}
