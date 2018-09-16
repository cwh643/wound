package com.iteye.chenwh.wound.opencv;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeepImageUtils {

    public static final double length_factor = 1; // 2.2
    public static final double WIDTH_PER_PIX = (Math.tan(0.5236) / 320) * 1000 / length_factor;
    public static final double HEIGHT_PER_PIX = (Math.tan(0.4014) / 240) * 1000 / length_factor;
    public static final double AREA_PER_PIX = WIDTH_PER_PIX * WIDTH_PER_PIX / length_factor;

    private DeepCameraInfo deepCameraInfo;
    private double displayFactor;
    private double measureDeepFactor;
    private int deepValidWidth;
    private int deepValidHeight;

    private Mat mFilterDepth;

    private Image mAreaMeasureBitmap;

    public DeepImageUtils(File webRoot, String uuid, String recordDate) {
        //String path = webRoot.getPath() + "/upload/wound/" + uuid + "/deep/" + recordDate + "/model.data";
        String path = "D:/work/tool/apache-tomcat-7.0.86/webapps/upload/wound/64a9b96201bb4312b27ef163cbc2f177/deep/20180701172557/model.data";
        String deepPath = "D:/work/tool/apache-tomcat-7.0.86/webapps/upload/wound/64a9b96201bb4312b27ef163cbc2f177/deep/20180701172557/deep.data";
        String rgbPath = "D:/work/tool/apache-tomcat-7.0.86/webapps/upload/wound/64a9b96201bb4312b27ef163cbc2f177/deep/20180701172557/rgb.jpeg";
        try {
            //mAreaMeasureBitmap = ImageUtils.createImage(GlobalDef.RES_CALC_WIDTH, GlobalDef.RES_CALC_HEIGHT);
            String json = FileUtils.readFileToString(new File(path));
            deepCameraInfo = JSONObject.parseObject(json, DeepCameraInfo.class);
            String deepData = FileUtils.readFileToString(new File(deepPath));
            deepCameraInfo.setDepthMat(ImageUtils.get_mat_from_string(deepData, deepCameraInfo.getDepthMatWidth(), deepCameraInfo.getDepthMatHeight()));
            deepCameraInfo.setRgbBitmap(ImageUtils.createImage(rgbPath));
            initConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initConfig() {
        //Mat tmpMap = new Mat(mBitmap.getHeight(), mBitmap.getWidth(), CvType.CV_8UC1);
        //deepCameraInfo.getDepthMat().convertTo(tmpMap, tmpMap.type());
        Mat mDepth = deepCameraInfo.getDepthMat();
        deepValidWidth = deepCameraInfo.getDeep_rx() - deepCameraInfo.getDeep_lx();
        deepValidHeight = deepCameraInfo.getDeep_ry() - deepCameraInfo.getDeep_ly();
        measureDeepFactor = deepValidWidth / new Double(GlobalDef.RES_CALC_WIDTH);

        // 在此加入中值滤波器，
        mFilterDepth = new Mat(mDepth.rows(), mDepth.cols(), mDepth.type());
        Imgproc.medianBlur(mDepth, mFilterDepth, GlobalDef.MODEL_DEEP_CENTER_DIS);
        //mFilterDepth = mDepth;
        Image rgb = deepCameraInfo.getRgbBitmap();
        int lx = deepCameraInfo.getDeep_lx();
        int ly = deepCameraInfo.getDeep_ly();
        int rx = deepCameraInfo.getDeep_rx();
        int ry = deepCameraInfo.getDeep_ry();
        int bitmapWidth = rx - lx;
        int bitmapHeight = ry - ly;

        /*
        mWoundOrgRgbView.setVisibility(View.INVISIBLE);
        if (displayMode == 1) {
            mWoundRgbBitmap = Bitmap.createBitmap(rgb, lx, ly, bitmapWidth, bitmapHeight);
            mWoundRgbView.setImageBitmap(mWoundRgbBitmap);
        } else if (displayMode == 2) {
            Mat tmpMap = new Mat(mDepth.rows(), mDepth.cols(), CvType.CV_8UC1);
            mDepth.convertTo(tmpMap, tmpMap.type());
            Bitmap orgDepth = Bitmap.createBitmap(mDepth.cols(), mDepth.rows(), Bitmap.Config.RGB_565);
            Utils.matToBitmap(tmpMap, orgDepth);
            mWoundCalcBitmap = Bitmap.createBitmap(orgDepth, lx, ly, bitmapWidth, bitmapHeight);
            mWoundRgbView.setImageBitmap(mWoundCalcBitmap);
        } else if (displayMode == 3) {
            mWoundOrgRgbView.setImageBitmap(rgb);
            mWoundOrgRgbView.setVisibility(View.VISIBLE);
        }
        */
    }

    public double calcDistince(int x1, int y1, int x2, int y2) {
        Point lp = new Point(x1, y1);
        Point rp = new Point(x2, y2);
        return calcDistince(lp, rp);
    }

    private double calcDistince(Point lp, Point rp) {
        Mat depth = deepCameraInfo.getDepthMat();
        Point3[] validPoints = getValidLinePoint(depth, lp, rp);
        Point3 v_lp = validPoints[0];
        Point3 v_rp = validPoints[1];

        double pre_deep = Math.min(v_lp.z, v_rp.z);
        double after_deep = Math.max(v_lp.z, v_rp.z);
        float camera_size_factor = deepCameraInfo.getCamera_size_factor();
        double d_with = (v_rp.x - v_lp.x) * WIDTH_PER_PIX / camera_size_factor;
        double d_height = (v_rp.y - v_lp.y) * WIDTH_PER_PIX / camera_size_factor;
        double d_deep = (after_deep - pre_deep);
        double pre_length = Math.sqrt(Math.pow(d_with, 2) + Math.pow(d_height, 2)) * pre_deep / 1000;
        double after_length = Math.sqrt(Math.pow(d_with, 2) + Math.pow(d_height, 2)) * after_deep / 1000;
        double length = (pre_length + after_length) / 2;
        double line = Math.sqrt(Math.pow(length, 2) + Math.pow(d_deep, 2));
        return line;
    }

    private Point3[] getValidLinePoint(Mat depth, Point lp_org, Point rp_org) {
        // 需要先校准深度数据的参数，包括边界起点和缩放比例，以640为基准
        int deep_lx = deepCameraInfo.getDeep_lx();
        int deep_ly = deepCameraInfo.getDeep_ly();
        Point lp = lp_org;
        Point rp = rp_org;
        boolean transAxis = false;
        double lineRate = 0;
        double dx = rp.x - lp.x;
        double dy = rp.y - lp.y;
        if (dx == 0 || Math.abs(dy) > Math.abs(dx)) {
            // x轴差距为0, 或者y轴斜率大于1，反转坐标系
            transAxis = true;
        }

        if (transAxis) {
            if (lp_org.y > rp_org.y) {
                rp = lp_org;
                lp = rp_org;
            }
            lineRate = dx / dy;
        } else {
            if (lp_org.x > rp_org.x) {
                rp = lp_org;
                lp = rp_org;
            }
            lineRate = dy / dx;
        }

        double lx = lp.x * measureDeepFactor + deep_lx;
        double ly = lp.y * measureDeepFactor + deep_ly;
        double rx = rp.x * measureDeepFactor + deep_lx;
        double ry = rp.y * measureDeepFactor + deep_ly;

        double default_deep = (deepCameraInfo.getDeep_far() + deepCameraInfo.getDeep_near()) / 2;
        double[] ldeeps = null;
        Point3[] ret = new Point3[2];
        ret[0] = new Point3();
        ret[1] = new Point3();

        ret[0].x = rx;
        ret[0].y = ry;
        ret[0].z = default_deep;
        ldeeps = depth.get(new Double(ly).intValue(), new Double(lx).intValue());
        if (transAxis) {
            for (; ly < ry; ly++) {
                if (ldeeps != null && ldeeps.length > 0 && ldeeps[0] > 0) {
                    ret[0].x = lx;
                    ret[0].y = ly;
                    ret[0].z = ldeeps[0];
                    break;
                }
                lx += lineRate;
                ldeeps = depth.get(new Double(ly).intValue(), new Double(lx).intValue());
            }
        } else {
            for (; lx < rx; lx++) {
                if (ldeeps != null && ldeeps.length > 0 && ldeeps[0] > 0) {
                    ret[0].x = lx;
                    ret[0].y = ly;
                    ret[0].z = ldeeps[0];
                    break;
                }
                ly += lineRate;
                ldeeps = depth.get(new Double(ly).intValue(), new Double(lx).intValue());
            }
        }

        ret[1].x = rx;
        ret[1].y = ry;
        ret[1].z = default_deep;
        ldeeps = depth.get(new Double(ry).intValue(), new Double(rx).intValue());
        if (transAxis) {
            for (; ry > ly; ry--) {
                if (ldeeps != null && ldeeps.length > 0 && ldeeps[0] > 0) {
                    ret[1].x = rx;
                    ret[1].y = ry;
                    ret[1].z = ldeeps[0];
                    break;
                }
                rx -= lineRate;
                ldeeps = depth.get(new Double(ry).intValue(), new Double(rx).intValue());
            }
        } else {
            for (; rx > lx; rx--) {
                if (ldeeps != null && ldeeps.length > 0 && ldeeps[0] > 0) {
                    ret[1].x = rx;
                    ret[1].y = ry;
                    ret[1].z = ldeeps[0];
                    break;
                }
                ry -= lineRate;
                ldeeps = depth.get(new Double(ry).intValue(), new Double(rx).intValue());
            }
        }
        // 更新最开始的lp和rp
        lp.x = (lx - deep_lx) / measureDeepFactor;
        lp.y = (ly - deep_ly) / measureDeepFactor;
        rp.x = (rx - deep_lx) / measureDeepFactor;
        rp.y = (ry - deep_ly) / measureDeepFactor;
        return ret;
    }

    private PointInfo3D getDeepPoint(int viewWidth, int bWidth, int pointX, int pointY) {
        Mat depth = deepCameraInfo.getDepthMat();
        //int viewWidth = mDeepMeasureView.getWidth();
        //int bWidth = mDeepMeasureBitmap.getWidth();
        //int bHeight = mDeepMeasureBitmap.getHeight();
        int lx = deepCameraInfo.getDeep_lx();
        int ly = deepCameraInfo.getDeep_ly();
        int rx = deepCameraInfo.getDeep_rx();
        int ry = deepCameraInfo.getDeep_ry();
        int bitmapWidth = rx - lx;
        int bitmapHeight = ry - ly;
        float viewFactor = new Float(bWidth) / viewWidth;
        float deepFactor = new Float(bitmapWidth) / viewWidth;

        float t_x = new Float(pointX * viewFactor);
        int d_x = new Float(pointX * deepFactor).intValue() + lx;
        float t_y = new Float(pointY * viewFactor);
        int d_y = new Float(pointY * deepFactor).intValue() + ly;
        // 取温度数据
        double deep = depth.get(d_y, d_x)[0];
        PointInfo3D point = new PointInfo3D();
        point.x = t_x;
        point.y = t_y;
        point.z = new Float(deep);
        return point;
    }

    /*
    private void drawDeepPoint(PointInfo3D point, boolean is_first) {
        String temp;
        float deep = point.z;
        float t_x = point.x;
        float t_y = point.y;
        if (deep == 0) {
            temp = mActivity.getString(R.string.measure_tip_deep_no_data);
        } else {
            if (is_first) {
                temp = mActivity.getString(R.string.measure_tip_deep_base);
            } else {
                temp = new DecimalFormat("#0").format(deep) + "mm";
            }
        }
        int text_witdh_diff = 150;
        int text_heigth_diff = 80;
        int tc_diff = 50;
        float text_x = t_x < text_witdh_diff ? t_x : t_x - text_witdh_diff;
        float text_y = (t_y < text_heigth_diff ? t_y + text_heigth_diff : t_y) - 15;
        float bolb = paint.getStrokeWidth();
        paint.setStrokeWidth(GlobalDef.FOCUS_STROKE_WIDTH);
        deepCanvas.drawText(temp, text_x, text_y, paint);
        deepCanvas.drawLine(t_x - tc_diff, t_y, t_x + tc_diff, t_y, paint);
        deepCanvas.drawLine(t_x, t_y - tc_diff, t_x, t_y + tc_diff, paint);
        paint.setStrokeWidth(bolb);
        mDeepMeasureView.setImageBitmap(mDeepMeasureBitmap);
    }

    private void getEventDeep(MotionEvent e) {
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        deepCanvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        PointInfo3D point = getDeepPoint(e);
        if (point.z == 0) {
            drawDeepPoint(point, false);
            return;
        }
        deep_mode_first = !deep_mode_first;
        if (!deep_mode_first) {
            wound_deep_first = point;
            drawDeepPoint(point, false);
            return;
        }
        float distince = Math.abs(point.z - wound_deep_first.z);
        String format = new DecimalFormat("#0").format(distince);
        point.z = distince;
        drawDeepPoint(point, false);
        drawDeepPoint(wound_deep_first, true);

        deepCameraInfo.setWoundDeep(new Float(format));
        setDeep();

    }
    private void setDeep() {
        mDeepView.setText(deepCameraInfo.getWoundDeep() + "");
    }
    */



    public Map<String, String> calcArea(Image measureBitmap ) {
        Map<String, String> map = new HashMap<>();
        if (measureBitmap == null) {
            return map;
        }
        this.mAreaMeasureBitmap = measureBitmap;
        List<Float> vertexList = deepCameraInfo.getVertexList();
        List<Float> colorList = deepCameraInfo.getColorList();
        List<Point3> areaEdgePointList = new ArrayList<>();
        Mat mDepth = deepCameraInfo.getDepthMat();
        //Mat mDepth = mFilterDepth;
        Image rgbBitmap = deepCameraInfo.getRgbBitmap();
        vertexList.clear();
        colorList.clear();
        deepValidWidth = deepCameraInfo.getDeep_rx() - deepCameraInfo.getDeep_lx();
        deepValidHeight = deepCameraInfo.getDeep_ry() - deepCameraInfo.getDeep_ly();
        Image areaMeasureBitmap = ImageUtils.scaleImage(mAreaMeasureBitmap, deepValidWidth, deepValidHeight);
        // 测量页面与depth的缩放比例，用于还原测量点的坐标
        int width = areaMeasureBitmap.getWidth();
        int height = areaMeasureBitmap.getHeight();
        // rgb和depth的缩放比例
        float rgbFactor = (float) rgbBitmap.getWidth() / mDepth.cols();
        // 存放每个轴上面最大和最小值
        double[][] lengthMap = new double[width][2];
        double[][] widthMap = new double[height][2];
        int lx = deepCameraInfo.getDeep_lx();
        int ly = deepCameraInfo.getDeep_ly();
        double area = 0;
        double volume = 0;
        Point3 max_deep = new Point3(0, 0, 0);
        Point3 min_deep = new Point3(0, 0, Integer.MAX_VALUE);
        ModelPointinfo mi = new ModelPointinfo();

        mi.left_x = Integer.MAX_VALUE;
        mi.top_y = Integer.MAX_VALUE;
        mi.right_x = 0;
        mi.bottom_y = 0;
        mi.last_deep = 0;
        // 红、黄、黑比例
        mi.redNum = 0;
        mi.yellowNum = 0;
        mi.blackNum = 0;
        mi.pixSize = 1;
        Image areaBitmap = ImageUtils.createImage(rgbBitmap.getWidth(), rgbBitmap.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int i = 0; i < width; i++) {
            mi.last_deep = 0;
            for (int j = 0; j < height; j++) {
                if (areaMeasureBitmap.getRGB(i, j) == GlobalDef.AREA_EDGE_COLOR) {
                    int depth_j = j + ly;
                    int depth_i = i + lx;
                    mi.last_deep = filterPoint(mFilterDepth, depth_i, depth_j);
                    if (mi.last_deep != 0) {
                        areaEdgePointList.add(new Point3(depth_i, depth_j, mi.last_deep));
                    }
                }
                //System.out.println("RGB("+i+","+j+") = 0x" + Integer.toHexString(areaMeasureBitmap.getRGB(i, j)) + "," +  GlobalDef.AREA_COLOR);
                if (areaMeasureBitmap.getRGB(i, j) == GlobalDef.AREA_COLOR) {
                    int depth_j = j + ly;
                    int depth_i = i + lx;
                    mi.last_deep = filterPoint(mFilterDepth, depth_i, depth_j);
                    if (mi.last_deep != 0) {
                        // 获取xy上最大的点和最小的点
                        if (lengthMap[i][0] == 0) {
                            lengthMap[i][0] = mi.last_deep;
                            lengthMap[i][1] = mi.last_deep;
                        } else {
                            lengthMap[i][0] = Math.min(mi.last_deep, lengthMap[i][0]);
                            lengthMap[i][1] = Math.max(mi.last_deep, lengthMap[i][1]);
                        }
                        if (widthMap[j][0] == 0) {
                            widthMap[j][0] = mi.last_deep;
                            widthMap[j][1] = mi.last_deep;
                        } else {
                            widthMap[j][0] = Math.min(mi.last_deep, widthMap[j][0]);
                            widthMap[j][1] = Math.max(mi.last_deep, widthMap[j][1]);
                        }
                        // 找到最近的点
                        if (mi.last_deep > max_deep.z) {
                            max_deep.z = mi.last_deep;
                            max_deep.x = i;
                            max_deep.y = j;
                        }
                        if (mi.last_deep < min_deep.z) {
                            min_deep.z = mi.last_deep;
                            min_deep.x = i;
                            min_deep.y = j;
                        }
                        mi.left_x = Math.min(depth_i, mi.left_x);
                        mi.right_x = Math.max(depth_i, mi.right_x);
                        mi.top_y = Math.min(depth_j, mi.top_y);
                        mi.bottom_y = Math.max(depth_j, mi.bottom_y);

                        int rgb_i = new Float(rgbFactor * depth_i).intValue();
                        int rgb_j = new Float(rgbFactor * depth_j).intValue();
                        int color = rgbBitmap.getRGB(rgb_i, rgb_j);
                        // testBm.setPixel(depth_i, depth_j, color);
                        areaBitmap.setRGB(rgb_i, rgb_j, color);
                        int red = ImageUtils.red(color);
                        int green = ImageUtils.green(color);
                        int blue = ImageUtils.blue(color);


                        colorList.add((float) red);
                        colorList.add((float) green);
                        colorList.add((float) blue);
                        colorList.add((float) ImageUtils.alpha(1));
                    } else {
//                        continue;
                        colorList.add((float) 0);
                        colorList.add((float) 0);
                        colorList.add((float) 0);
                        colorList.add((float) ImageUtils.alpha(1));
                    }

                    // 计算模型的点集
                    vertexList.add((float) depth_i);
                    vertexList.add((float) depth_j);
                    vertexList.add((float) mi.last_deep);
                }
            }
        }
        //mAreaMeasureView.setImageBitmap(testBm);
        deepCameraInfo.setMinDeepPoint(min_deep);
        deepCameraInfo.setMaxDeepPoint(max_deep);

        clacColorRate(areaBitmap, mi);

        // 计算拟合平面
        float[] plane = new float[4];
        calcPlane(areaEdgePointList, plane);
        // 计算平面和 z = 0 平面的夹角cos值
        float[] zPlane = new float[]{0, 0, 1, 0};
        double cosAngle = getCosAngle(zPlane, plane);
        double tanAngle = Math.tan(Math.acos(cosAngle));
        double triangleHeight = WIDTH_PER_PIX * tanAngle;

        //  中间点
        int test = 1;
        Point center_p = new Point((mi.left_x + mi.right_x) / 2, (mi.top_y + mi.bottom_y) / 2);
        deepCameraInfo.setModelCenter(center_p);
        float camera_size_factor = deepCameraInfo.getCamera_size_factor();

        for (int i = vertexList.size() - 1; i >= 0; i -= 3) {
            float deep = vertexList.get(i);
            int x = new Float(vertexList.get(i - 2)).intValue();
            int y = new Float(vertexList.get(i - 1)).intValue();
            float planDeep = getPlaneDeep(x, y, plane);

            // 先计算长和宽，所以需要乘以两次deep，而deep的比例是以米为单位，所以要除以2次1000
            double upArea = planDeep * planDeep * AREA_PER_PIX / 1000000 / camera_size_factor / camera_size_factor;
            // 根据平面夹余弦值算出最终的面积
            double areaPlane = upArea / cosAngle;
            area += areaPlane;
            // 深度先计算平行z轴的体积，再加上因为平面倾斜少计算的体积
            // 因为z轴是发散的，所以是个梯形，计算梯形的面积
            double downArea = deep * deep * AREA_PER_PIX / 1000000 / camera_size_factor / camera_size_factor;
            double deep_dis = Math.abs(deep - planDeep);
            deep_dis = deep_dis > 2 ? deep_dis : 0;
            double rectVolum = (upArea + downArea) * deep_dis / 2;
            double triangleVolumn = triangleHeight * upArea * deep_dis / 2;

            double volum_per = rectVolum + triangleVolumn;
            //double volum_per = (deep - planDeep) / 10 * areaPlane;
            volume += volum_per;
            if (volum_per > 0) {
                test = 1;
            }
        }
        volume = Math.abs(volume) / 1000;
        //deepCameraInfo.setWoundWidth(new Double(test).floatValue());
        String format_area = new DecimalFormat("#.00").format(area / 100);
        String format_volume = new DecimalFormat("#.00").format(volume);
        String format_red = new DecimalFormat("#.00").format((float) mi.redNum / mi.pixSize * 100);
        String format_yellow = new DecimalFormat("#.00").format((float) mi.yellowNum / mi.pixSize * 100);
        String format_black = new DecimalFormat("#.00").format((float) mi.blackNum / mi.pixSize * 100);
        double woundDeep = (deepCameraInfo.getMaxDeep() - deepCameraInfo.getMinDeep()) / 10;
        String format_deep = new DecimalFormat("#.00").format(woundDeep);
        map.put("area", format_area);
        map.put("volume", format_volume);
        map.put("deep", format_deep);
        map.put("red", format_red);
        map.put("yellow", format_yellow);
        map.put("black", format_black);

        return map;
    }

    private double filterPoint(Mat depth, int x, int y) {
        double deep = getDeep(depth.get(y, x));
        return deep;//* 0.5;
    }

    private void calcPlane(List<Point3> vertexList, float[] plane) {
        Mat points = new Mat(vertexList.size(), 3, CvType.CV_32FC1);
        for (int i = 0; i < vertexList.size(); i++) {
            Point3 p = vertexList.get(i);
            points.put(i, 0, p.x);
            points.put(i, 1, p.y);
            points.put(i, 2, p.z);
        }
        Mat planeMat = new Mat(1, 4, CvType.CV_32FC1);
        //未找到PC对应方法
        //CommonNativeUtils.cvFitPlane(points.getNativeObjAddr(), planeMat.getNativeObjAddr());
        plane[0] = new Double(planeMat.get(0, 0)[0]).floatValue();
        plane[1] = new Double(planeMat.get(0, 1)[0]).floatValue();
        plane[2] = new Double(planeMat.get(0, 2)[0]).floatValue();
        plane[3] = new Double(planeMat.get(0, 3)[0]).floatValue();
    }

    private void clacColorRate(Image rgbBitmap, ModelPointinfo mi) {
        Mat srcRgbMat = new Mat(rgbBitmap.getHeight(), rgbBitmap.getWidth(), CvType.CV_8UC3);
        Mat dstRgbMat = new Mat(rgbBitmap.getHeight(), rgbBitmap.getWidth(), CvType.CV_8UC3);
        //未找到PC对应方法
        //Utils.bitmapToMat(rgbBitmap, srcRgbMat);
        Imgproc.cvtColor(srcRgbMat, dstRgbMat, Imgproc.COLOR_RGB2HSV);
        //Bitmap dstBitmap = Bitmap.createBitmap(rgbBitmap);
        //Utils.matToBitmap(dstRgbMat, dstBitmap);
        //mWoundOrgRgbView.setImageBitmap(dstBitmap);
        double minH = Integer.MAX_VALUE;
        double maxH = 0;
        double minS = Integer.MAX_VALUE;
        double maxS = 0;
        double minV = Integer.MAX_VALUE;
        double maxV = 0;
        for (int i = 0; i < dstRgbMat.cols(); i++) {
            for (int j = 0; j < dstRgbMat.rows(); j++) {
                double[] color = dstRgbMat.get(j, i);
                if (color[0] != 0 && color[1] != 0 && color[2] != 0) {
                    mi.pixSize++;
                    double val_H = color[0];
                    double val_S = color[1];
                    double val_V = color[2];
                    minH = Math.min(minH, val_H);
                    maxH = Math.max(maxH, val_H);
                    minS = Math.min(minS, val_S);
                    maxS = Math.max(maxS, val_S);
                    minV = Math.min(minV, val_V);
                    maxV = Math.max(maxV, val_V);
                    if (((val_H > 0 && val_H < 4) || (val_H > 156 && val_H < 180)) && val_S > 7 && val_V > 46) {
                        mi.redNum++;
                    } else if (val_H >= 4 && val_H < 48 && val_S > 7 && val_V > 46) {
                        mi.yellowNum++;
                    } else if (val_V > 0 && val_V < 46) {
                        mi.blackNum++;
                    }

                }
            }
        }

        //Log.i(TAG, "HSV minH:" + minH + "maxH:" + maxH);
        //Log.i(TAG, "HSV minS:" + minS + "maxS:" + maxS);
        //Log.i(TAG, "HSV minV:" + minV + "maxV:" + maxV);
    }

    private double getDeep(double[] deep_arr) {
        if (deep_arr == null) {
            return 0;
        }
        double deep = deep_arr[0];
        if (deep == 0 || deep < GlobalDef.CALC_MIN_DEEP || deep > GlobalDef.CALC_MAX_DEEP) {
            return 0;
        }
        return deep;
    }

    private float getPlaneDeep(float x, float y, float[] plane) {
        float a = plane[0];
        float b = plane[1];
        float c = plane[2];
        float d = plane[3];
        return (d - (a * x) - (b * y)) / c;
    }

    private double getCosAngle(float[] plane1, float[] plane2) {
        float a1 = plane1[0];
        float b1 = plane1[1];
        float c1 = plane1[2];
        float d1 = plane1[3];
        float a2 = plane2[0];
        float b2 = plane2[1];
        float c2 = plane2[2];
        float d2 = plane2[3];
        return Math.abs((a1 * a2 + b1 * b2 + c1 * c2) / (Math.sqrt(a1 * a1 + b1 * b1 + c1 * c1) * Math.sqrt(a2 * a2 + b2 * b2 + c2 * c2)));
    }
}
