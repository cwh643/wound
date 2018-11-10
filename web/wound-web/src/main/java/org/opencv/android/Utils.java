package org.opencv.android;

import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;

public class Utils {
/*
    public static String exportResource(Context context, int resourceId) {
        return exportResource(context, resourceId, "OpenCV_data");
    }

    public static String exportResource(Context context, int resourceId, String dirname) {
        String fullname = context.getResources().getString(resourceId);
        String resName = fullname.substring(fullname.lastIndexOf("/") + 1);
        try {
            InputStream is = context.getResources().openRawResource(resourceId);
            File resDir = context.getDir(dirname, Context.MODE_PRIVATE);
            File resFile = new File(resDir, resName);

            FileOutputStream os = new FileOutputStream(resFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            return resFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            throw new CvException("Failed to export resource " + resName
                    + ". Exception thrown: " + e);
        }
    }

    public static Mat loadResource(Context context, int resourceId) throws IOException
    {
        return loadResource(context, resourceId, -1);
    }

    public static Mat loadResource(Context context, int resourceId, int flags) throws IOException
    {
        InputStream is = context.getResources().openRawResource(resourceId);
        ByteArrayOutputStream os = new ByteArrayOutputStream(is.available());

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        is.close();

        Mat encoded = new Mat(1, os.size(), CvType.CV_8U);
        encoded.put(0, 0, os.toByteArray());
        os.close();

        Mat decoded = Highgui.imdecode(encoded, flags);
        encoded.release();

        return decoded;
    }
*/
    /**
     * Converts Android Bitmap to OpenCV Mat.
     * <p>
     * This function converts an Android Bitmap image to the OpenCV Mat.
     * <br>'ARGB_8888' and 'RGB_565' input Bitmap formats are supported.
     * <br>The output Mat is always created of the same size as the input Bitmap and of the 'CV_8UC4' type,
     * it keeps the image in RGBA format.
     * <br>This function throws an exception if the conversion fails.
     * @param bmp is a valid input Bitmap object of the type 'ARGB_8888' or 'RGB_565'.
     * @param mat is a valid output Mat object, it will be reallocated if needed, so it may be empty.
     * @param unPremultiplyAlpha is a flag, that determines, whether the bitmap needs to be converted from alpha premultiplied format (like Android keeps 'ARGB_8888' ones) to regular one; this flag is ignored for 'RGB_565' bitmaps.
     */
    public static void bitmapToMat(BufferedImage bmp, Mat mat, boolean unPremultiplyAlpha) {
        if (bmp == null)
            throw new IllegalArgumentException("bmp == null");
        if (mat == null)
            throw new IllegalArgumentException("mat == null");
        //nBitmapToMat2(bmp, mat.nativeObj, unPremultiplyAlpha);
        mat = BufImg2Mat(bmp, BufferedImage.TYPE_4BYTE_ABGR, CvType.CV_16UC1);
    }

    /**
     * Short form of the bitmapToMat(bmp, mat, unPremultiplyAlpha=false).
     * @param bmp is a valid input Bitmap object of the type 'ARGB_8888' or 'RGB_565'.
     * @param mat is a valid output Mat object, it will be reallocated if needed, so Mat may be empty.
     */
    public static void bitmapToMat(BufferedImage bmp, Mat mat) {
        bitmapToMat(bmp, mat, false);
    }


    /**
     * Converts OpenCV Mat to Android Bitmap.
     * <p>
     * <br>This function converts an image in the OpenCV Mat representation to the Android Bitmap.
     * <br>The input Mat object has to be of the types 'CV_8UC1' (gray-scale), 'CV_8UC3' (RGB) or 'CV_8UC4' (RGBA).
     * <br>The output Bitmap object has to be of the same size as the input Mat and of the types 'ARGB_8888' or 'RGB_565'.
     * <br>This function throws an exception if the conversion fails.
     *
     * @param mat is a valid input Mat object of types 'CV_8UC1', 'CV_8UC3' or 'CV_8UC4'.
     * @param bmp is a valid Bitmap object of the same size as the Mat and of type 'ARGB_8888' or 'RGB_565'.
     * @param premultiplyAlpha is a flag, that determines, whether the Mat needs to be converted to alpha premultiplied format (like Android keeps 'ARGB_8888' bitmaps); the flag is ignored for 'RGB_565' bitmaps.
     */
    public static BufferedImage matToBitmap(Mat mat, boolean premultiplyAlpha) {
        if (mat == null)
            throw new IllegalArgumentException("mat == null");
        //if (bmp == null)
        //    throw new IllegalArgumentException("bmp == null");
        //nMatToBitmap2(mat.nativeObj, bmp, premultiplyAlpha);
        return Mat2BufImg(mat, ".jpg");
    }

    /**
     * Short form of the <b>matToBitmap(mat, bmp, premultiplyAlpha=false)</b>
     * @param mat is a valid input Mat object of the types 'CV_8UC1', 'CV_8UC3' or 'CV_8UC4'.
     * @param bmp is a valid Bitmap object of the same size as the Mat and of type 'ARGB_8888' or 'RGB_565'.
     */
    public static BufferedImage matToBitmap(Mat mat) {
        return matToBitmap(mat, false);
    }

    /**
     * Mat转换成BufferedImage
     *
     * @param matrix
     *            要转换的Mat
     * @param fileExtension
     *            格式为 ".jpg", ".png", etc
     * @return
     */
    public static BufferedImage Mat2BufImg (Mat matrix, String fileExtension) {
        // convert the matrix into a matrix of bytes appropriate for
        // this file extension
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(fileExtension, matrix, mob);
        // convert the "matrix of bytes" into a byte array
        byte[] byteArray = mob.toArray();
        BufferedImage bufImage = null;
        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);
            System.out.println("Image Type:" + bufImage.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bufImage;
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

        byte[] pixels = ((DataBufferByte) original.getRaster().getDataBuffer()).getData();
        Mat mat = Mat.eye(original.getHeight(), original.getWidth(), matType);
        mat.put(0, 0, pixels);
        return mat;
    }


    //private static native void nBitmapToMat2(Bitmap b, long m_addr, boolean unPremultiplyAlpha);

    //private static native void nMatToBitmap2(long m_addr, Bitmap b, boolean premultiplyAlpha);
}
