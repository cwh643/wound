package com.iteye.chenwh.wound.opencv;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.awt.image.BufferedImage;
import java.io.File;

public class ImageUtils {

    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }


    public static Image createImage(BufferedImage bi) {
        return new Image(bi);
    }

    public static Image createImage(String path) {
        return new Image(path);
    }

    public static Image createImage(File file) {
        return new Image(file);
    }

    public static Image createImage(int w, int h) {
        return createImage(w, h, BufferedImage.TYPE_INT_ARGB);
    }

    public static Image createImage(int w, int h, int t) {
        BufferedImage bufferedImage = new BufferedImage(w, h, t);
        return new Image(bufferedImage);
    }

    public static Image createImage(Image image) {
        int w = image.getWidth();
        int h = image.getHeight();
        int t = image.getImageType();
        return createImage(w, h, t);
    }

    public static Image scaleImage(Image image, int nw, int nh) {
        return image.resize(nw, nh);
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

    public static int alpha(int color) {
        return color >>> 24;
    }

    public static int red(int color) {
        return (color >> 16) & 0xFF;
    }

    public static int green(int color) {
        return (color >> 8) & 0xFF;
    }

    public static int blue(int color) {
        return color & 0xFF;
    }

}
