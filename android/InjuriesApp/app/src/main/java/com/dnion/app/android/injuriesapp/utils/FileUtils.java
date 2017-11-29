package com.dnion.app.android.injuriesapp.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

/**
 * Tools for handler picture
 *
 * @author yuanyuan
 */
public final class FileUtils {

    /**
     * Check the SD card
     *
     * @return
     */
    public static boolean checkSDCardAvailable() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    /**
     * Save image to the SD card
     *
     * @param data
     * @param path
     * @param name
     * @param rewrite
     */
    public static void writeFile(String data, String path, String name, boolean rewrite) {
        if (checkSDCardAvailable()) {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }


            File dataFile = new File(path, name);
            if (rewrite && dataFile.exists()) {
                dataFile.delete();
            }
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(dataFile);
                if (data != null) {
                    fileOutputStream.write(data.getBytes());

                    fileOutputStream.flush();
                }
            } catch (FileNotFoundException e) {
                dataFile.delete();
                e.printStackTrace();
            } catch (IOException e) {
                dataFile.delete();
                e.printStackTrace();
            } finally {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static String readFile(String path, String name) {
        StringBuffer text = new StringBuffer();
        if (checkSDCardAvailable()) {

            //Get the text file
            File file = new File(path, name);

            //Read text from file

            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
        return text.toString();
    }

    public static void copyFile(File inf,File outf) throws Exception{
        int length=2048;
        FileInputStream in=new FileInputStream(inf);
        FileOutputStream out=new FileOutputStream(outf);
        byte[] buffer=new byte[length];
        try {
            while(true){
                int ins=in.read(buffer);
                if(ins==-1){
                    out.flush();
                    return ;
                }else
                    out.write(buffer,0,ins);
            }
        } finally {
            in.close();
            out.close();
        }


    }

}

