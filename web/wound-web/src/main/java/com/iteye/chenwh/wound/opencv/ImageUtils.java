package com.iteye.chenwh.wound.opencv;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
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

    public static BufferedImage conver2Image(Mat mat) {
        int width = mat.cols();
        int height = mat.rows();
        int dims = mat.channels();
        int[] pixels = new int[width*height];
        byte[] rgbdata = new byte[width*height*dims];
        mat.get(0, 0, rgbdata);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int index = 0;
        int r=0, g=0, b=0;
        for(int row=0; row<height; row++) {
            for(int col=0; col<width; col++) {
                if(dims == 3) {
                    index = row*width*dims + col*dims;
                    b = rgbdata[index]&0xff;
                    g = rgbdata[index+1]&0xff;
                    r = rgbdata[index+2]&0xff;
                    pixels[row*width+col] = ((255&0xff)<<24) | ((r&0xff)<<16) | ((g&0xff)<<8) | b&0xff;
                }
                if(dims == 1) {
                    index = row*width + col;
                    b = rgbdata[index]&0xff;
                    pixels[row*width+col] = ((255&0xff)<<24) | ((b&0xff)<<16) | ((b&0xff)<<8) | b&0xff;
                }
            }
        }
        setRGB( image, 0, 0, width, height, pixels);
        return image;
    }

    /**
     * A convenience method for setting ARGB pixels in an image. This tries to avoid the performance
     * penalty of BufferedImage.setRGB unmanaging the image.
     */
    public static void setRGB( BufferedImage image, int x, int y, int width, int height, int[] pixels ) {
        int type = image.getType();
        if ( type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB )
            image.getRaster().setDataElements( x, y, width, height, pixels );
        else
            image.setRGB( x, y, width, height, pixels, 0, width );
    }

    /**
     * BufferedImage转换成Mat
     *
     * @param original
     *            要转换的BufferedImage
     * @param imgType
     *            bufferedImage的类型 如 BufferedImage.TYPE_3BYTE_BGR
     * @param matType
     *            转换成mat的type 如 CvType.CV_8UC3
     */
    public static Mat BufImg2Mat (BufferedImage original, int imgType, int matType) {
        if (original == null) {
            throw new IllegalArgumentException("original == null");
        }

        // Don't convert if it already has correct type
        if (original.getType() != imgType) {

            // Create a buffered image
            BufferedImage image = new BufferedImage(original.getWidth(), original.getHeight(), imgType);

            // Draw the image onto the new buffer
            Graphics2D g = image.createGraphics();
            try {
                g.setComposite(AlphaComposite.Src);
                g.drawImage(original, 0, 0, null);
            } finally {
                g.dispose();
            }
        }

        int[] pixels = ((DataBufferInt) original.getRaster().getDataBuffer()).getData();
        Mat mat = Mat.eye(original.getHeight(), original.getWidth(), matType);
        int width = mat.cols();
        int height = mat.rows();
        int dims = mat.channels();
        byte[] rgbData = new byte[width*height*dims];
        int index = 0, index2 = 0;
        int r = 0, g = 0, b = 0;
        for(int row=0; row<height; row++) {
            for(int col=0; col<width; col++) {
                index = row*width + col;
                index2 = row*width*dims + col*dims;
                int color = pixels[index];
                r = (color&0xff0000)>>16;
                g = (color&0x00ff00)>>8;
                b = (color&0x0000ff);
                rgbData[index2] = (byte)b;
                rgbData[index2 + 1] = (byte)g;
                rgbData[index2 + 2] = (byte)r;
            }
        }

        mat.put(0, 0, rgbData);
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
