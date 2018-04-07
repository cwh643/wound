package com.dnion.app.android.injuriesapp.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yy on 2017/6/24.
 */

public class BitmapUtils {
    public static String TAG = "BitmapUtils";

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
                mat.put(i, j, new Double(items[j]));
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

    public static Bitmap mergeBitmap(Bitmap backBitmap, Bitmap... frontBitmaps) {
        if (backBitmap == null || backBitmap.isRecycled()) {
            Log.e(TAG, "backBitmap error:" + backBitmap + ";");
            return null;
        }
        Canvas canvas = new Canvas(backBitmap);
        Paint paint = new Paint();
        Rect baseRect = new Rect(0, 0, backBitmap.getWidth(), backBitmap.getHeight());
        for (Bitmap frontBitmap : frontBitmaps) {
            if (backBitmap == null || backBitmap.isRecycled()
                    || frontBitmap == null || frontBitmap.isRecycled()) {
                Log.e(TAG, "frontBitmap error:" + frontBitmap);
                continue;
            }
            paint.setAlpha(125);
            Rect frontRect = new Rect(0, 0, frontBitmap.getWidth(), frontBitmap.getHeight());
            canvas.drawBitmap(frontBitmap, frontRect, baseRect, paint);
        }
        return backBitmap;
    }

    public static Bitmap getViewBitmap(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap tBitmap = view.getDrawingCache();
        if (tBitmap == null) {
            return null;
        }
        // 拷贝图片，否则在setDrawingCacheEnabled(false)以后该图片会被释放掉
        tBitmap = Bitmap.createBitmap(tBitmap);
        view.setDrawingCacheEnabled(false);
        return tBitmap;
    }

    public static Bitmap mergeBitmap(Bitmap backgroud, View... views) {
        Canvas canvas = new Canvas(backgroud);
        Paint paint = new Paint();
        for (View view : views) {
            view.setDrawingCacheEnabled(true);
            Bitmap tBitmap = view.getDrawingCache();
            // 拷贝图片，否则在setDrawingCacheEnabled(false)以后该图片会被释放掉
            if (tBitmap == null || backgroud == null) {
                return null;
            }
            paint.setAlpha(125);
            // 需要计算位置和大小
            int x = new Float(view.getX()).intValue();
            int y = new Float(view.getY()).intValue();
            Rect baseRect = new Rect(x, y
                    , x + tBitmap.getWidth(), y + tBitmap.getHeight());
            Rect frontRect = new Rect(0, 0, tBitmap.getWidth(), tBitmap.getHeight());
            canvas.drawBitmap(tBitmap, frontRect, baseRect, paint);
            view.setDrawingCacheEnabled(false);
        }
        return backgroud;
    }
}
