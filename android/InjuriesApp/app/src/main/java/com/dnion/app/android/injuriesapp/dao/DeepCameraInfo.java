package com.dnion.app.android.injuriesapp.dao;

import android.graphics.Bitmap;

import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yy on 17/7/16.
 */

public class DeepCameraInfo {
    private String filepath;
    private transient boolean isNew = true;
    private transient Bitmap rgbBitmap;
    private transient Mat depthMat;
    private int depthMatWidth = 640;
    private int depthMatHeight = 480;
    private List<Point> areaPointList = new ArrayList<>();
    private List<Point>  lengthPointList = new ArrayList<>();
    private List<Point>  widthPointList = new ArrayList<>();
    private float woundArea;
    private float woundWidth;
    private float woundHeight;
    private float woundVolume;
    private float woundDeep;
    private float woundRedRate;
    private float woundYellowRate;
    private float woundBlackRate;
    private double minDeep;
    private double maxDeep;
    private double centerDeep;
    private int deep_lx;
    private int deep_ly;
    private int deep_rx;
    private int deep_ry;
    private float deep_scale_factor;
    private int deep_near;
    private int deep_far;
    private List<Float> vertexList = new ArrayList<>();
    private List<Float> colorList = new ArrayList<>();
    private Point modelCenter;
    private int[] cameraParamArr = new int[4];

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public Bitmap getRgbBitmap() {
        return rgbBitmap;
    }

    public void setRgbBitmap(Bitmap rgbBitmap) {
        this.rgbBitmap = rgbBitmap;
    }

    public double getMinDeep() {
        return minDeep;
    }

    public void setMinDeep(double minDeep) {
        this.minDeep = minDeep;
    }

    public double getMaxDeep() {
        return maxDeep;
    }

    public void setMaxDeep(double maxDeep) {
        this.maxDeep = maxDeep;
    }

    public Mat getDepthMat() {
        return depthMat;
    }

    public void setDepthMat(Mat depthMat) {
        this.depthMat = depthMat;
        this.depthMatWidth = depthMat.cols();
        this.depthMatHeight = depthMat.rows();
    }

    public List<Point> getAreaPointList() {
        if (areaPointList == null) {
            areaPointList = new ArrayList<>();
        }
        return areaPointList;
    }

    public void setAreaPointList(List<Point> areaPointList) {
        this.areaPointList = areaPointList;
    }

    public List<Point> getLengthPointList() {
        return lengthPointList;
    }

    public void setLengthPointList(List<Point> lengthPointList) {
        this.lengthPointList = lengthPointList;
    }

    public List<Point> getWidthPointList() {
        return widthPointList;
    }

    public void setWidthPointList(List<Point> widthPointList) {
        this.widthPointList = widthPointList;
    }

    public Float getWoundArea() {
        return woundArea;
    }

    public void setWoundArea(Float woundArea) {
        this.woundArea = woundArea;
    }

    public Float getWoundWidth() {
        return woundWidth;
    }

    public void setWoundWidth(Float woundWidth) {
        this.woundWidth = woundWidth;
    }

    public Float getWoundHeight() {
        return woundHeight;
    }

    public void setWoundHeight(Float woundHeight) {
        this.woundHeight = woundHeight;
    }

    public Float getWoundVolume() {
        return woundVolume;
    }

    public void setWoundVolume(Float woundVolume) {
        this.woundVolume = woundVolume;
    }

    public List<Float> getVertexList() {
        if (vertexList == null) {
            vertexList = new ArrayList<>();
        }
        return vertexList;
    }

    public void setVertexList(List<Float> vertexList) {
        this.vertexList = vertexList;
    }

    public List<Float> getColorList() {
        if (colorList == null) {

        }
        return colorList;
    }

    public void setColorList(List<Float> colorList) {
        this.colorList = colorList;
    }

    public int[] getCameraParamArr() {
        return cameraParamArr;
    }

    public void setCameraParamArr(int[] cameraParamArr) {
        this.cameraParamArr = cameraParamArr;
    }

    public int getDeep_lx() {
        return deep_lx;
    }

    public void setDeep_lx(int deep_lx) {
        this.deep_lx = deep_lx;
    }

    public int getDeep_ly() {
        return deep_ly;
    }

    public void setDeep_ly(int deep_ly) {
        this.deep_ly = deep_ly;
    }

    public int getDeep_rx() {
        return deep_rx;
    }

    public void setDeep_rx(int deep_rx) {
        this.deep_rx = deep_rx;
    }

    public int getDeep_ry() {
        return deep_ry;
    }

    public void setDeep_ry(int deep_ry) {
        this.deep_ry = deep_ry;
    }

    public float getDeep_scale_factor() {
        return deep_scale_factor;
    }

    public void setDeep_scale_factor(float deep_scale_factor) {
        this.deep_scale_factor = deep_scale_factor;
    }

    public int getDeep_near() {
        return deep_near;
    }

    public void setDeep_near(int deep_near) {
        this.deep_near = deep_near;
    }

    public int getDeep_far() {
        return deep_far;
    }

    public void setDeep_far(int deep_far) {
        this.deep_far = deep_far;
    }

    public Point getModelCenter() {
        return modelCenter;
    }

    public void setModelCenter(Point modelCenter) {
        this.modelCenter = modelCenter;
    }

    public float getWoundRedRate() {
        return woundRedRate;
    }

    public void setWoundRedRate(float woundRedRate) {
        this.woundRedRate = woundRedRate;
    }

    public float getWoundYellowRate() {
        return woundYellowRate;
    }

    public void setWoundYellowRate(float woundYellowRate) {
        this.woundYellowRate = woundYellowRate;
    }

    public float getWoundBlackRate() {
        return woundBlackRate;
    }

    public void setWoundBlackRate(float woundBlackRate) {
        this.woundBlackRate = woundBlackRate;
    }

    public float getWoundDeep() {
        return woundDeep;
    }

    public void setWoundDeep(float woundDeep) {
        this.woundDeep = woundDeep;
    }

    public int getDepthMatWidth() {
        return depthMatWidth;
    }

    public void setDepthMatWidth(int depthMatWidth) {
        this.depthMatWidth = depthMatWidth;
    }

    public int getDepthMatHeight() {
        return depthMatHeight;
    }

    public void setDepthMatHeight(int depthMatHeight) {
        this.depthMatHeight = depthMatHeight;
    }

    public double getCenterDeep() {
        return centerDeep;
    }

    public void setCenterDeep(double centerDeep) {
        this.centerDeep = centerDeep;
    }
}
