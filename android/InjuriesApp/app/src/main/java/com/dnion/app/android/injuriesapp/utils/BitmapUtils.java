package com.dnion.app.android.injuriesapp.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanyuan on 2017/6/24.
 */

public class BitmapUtils {
    public static Bitmap scale_image(Bitmap bm, int x, int y, int width, int height, int newWidth, int newHeight) {

        // 计算缩放比例
        float scaleWidth = 1;
        float scaleHeight = 1;
        scaleWidth = ((float) newWidth) / width;
        scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, x, y, width, height, matrix,
                true);
        // if (bm != null && !bm.equals(newbm) && !bm.isRecycled()) {
        //     bm.recycle();
        //     bm = null;
        // }
        return newbm;
    }

    public static Mat get_mat_from_string(String str, int width, int height) {
        Mat mat = new Mat(height, width, CvType.CV_16UC1);
        String[] lines = str.split("\n");
        for (int i = lines.length - 1; i >= 0; i--) {
            String[] items = lines[i].split(",");
            for (int j = items.length - 1; j >= 0; j--) {
                mat.put(i,j, new Double(items[j]));
            }
        }
        return mat;
    }

    public static String save_mat_to_string(Mat mat) {
        StringBuffer sb = new StringBuffer();
        for (int j = 0; j < mat.height(); j++) {
            for (int i = 0; i < mat.width(); i++) {
                double deep = 0;
                double[] depth = mat.get(j, i);
                if (null != depth) {
                    deep = depth[0];
                }
                sb.append(deep).append(",");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static List<Point> get_path_from_string(String str) {
        return new ArrayList<Point>();
    }

    public static String save_path_to_string(List<Point> pointList) {
        StringBuffer sb = new StringBuffer();
        Point p;
        for (int i = 0; i < pointList.size(); i++) {
            p = pointList.get(i);
            double x = p.x;
            double y = p.y;
            sb.append(x).append("|");
            sb.append(y).append(",");
        }
        return sb.toString();
    }
}
