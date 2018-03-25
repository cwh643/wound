package com.dnion.app.android.injuriesapp.dao;

import android.graphics.Bitmap;
import android.util.Log;

import com.dnion.app.android.injuriesapp.utils.BitmapUtils;
import com.dnion.app.android.injuriesapp.utils.FileUtils;
import com.dnion.app.android.injuriesapp.utils.ImageTools;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.opencv.core.Mat;

import java.io.File;

/**
 * Created by 卫华 on 2017/4/29.
 */

public class DeepCameraInfoDao {
    public static final String TAG = "deep_camera_info_dao";
    public final static String RGB_FILE_NAME = "rgb.jpeg";
    public final static String PDF_IMAGE_FILE_NAME = "pdf.jpeg";
    public final static String DEEP_FILE_NAME = "deep.data";
    public final static String LIST_IMAGE_FILE_NAME = "list_rgb.jpeg";
    public final static String MODEL_FILE_NAME = "model.data";


    public DeepCameraInfo getDeepCameraInfo(String path) {
        String data_json = FileUtils.readFile(path, MODEL_FILE_NAME);
        Gson gson = new Gson();
        DeepCameraInfo dci = gson.fromJson(data_json, DeepCameraInfo.class);
        if (dci == null) {
            dci = new DeepCameraInfo();
        }
        dci.setNew(false);
        dci.setFilepath(path);
        // 加载rgb
        Bitmap rgb = ImageTools.getPhotoFromSDCard(path, RGB_FILE_NAME);
        dci.setRgbBitmap(rgb);
        // 加载deep
        String deepStr = FileUtils.readFile(path, DEEP_FILE_NAME);
        Mat depth = BitmapUtils.get_mat_from_string(deepStr, dci.getDepthMatWidth(), dci.getDepthMatHeight());
        dci.setDepthMat(depth);
        // 加载pdf图片
        Bitmap pdf = ImageTools.getPhotoFromSDCard(path, PDF_IMAGE_FILE_NAME);
        dci.setPdfBitmap(pdf);
        return dci;
    }

    public boolean save(DeepCameraInfo dci) {
        try {
            String path = dci.getFilepath();
            if (dci.isNew()) {
                // 保存rgb图片
                String rgbPath = path + File.separator + RGB_FILE_NAME;
                Log.i(TAG, "rgb_path:" + rgbPath);
                ImageTools.savePhotoToSDCard(dci.getRgbBitmap(), path, RGB_FILE_NAME);

                // 保存pdf图片
                String pdfImagePath = path + File.separator + PDF_IMAGE_FILE_NAME;
                ImageTools.savePhotoToSDCard(dci.getPdfBitmap(), path, PDF_IMAGE_FILE_NAME);
                Log.i(TAG, "pdf_path:" + pdfImagePath);

                // 保存rgb略缩图片
                String listRgbPath = path + File.separator + LIST_IMAGE_FILE_NAME;
                Bitmap listBitmap = BitmapUtils.scale_image(dci.getRgbBitmap(), 0, 0, dci.getRgbBitmap().getWidth(), dci.getRgbBitmap().getHeight(), 320, 240);
                Log.i(TAG, "List_rgb_path:" + listRgbPath);

                ImageTools.savePhotoToSDCard(listBitmap, listRgbPath, LIST_IMAGE_FILE_NAME);
                // 保存deep点云
                String deepPath = path + File.separator + DEEP_FILE_NAME;
                Log.i(TAG, "deep_path:" + deepPath);
                FileUtils.writeFile(BitmapUtils.save_mat_to_string(dci.getDepthMat()), path, DEEP_FILE_NAME, true);
                // 其他属性保存到json中
            }
            Gson gson = new Gson();
            String data_json = gson.toJson(dci);
            FileUtils.writeFile(data_json, path, MODEL_FILE_NAME, true);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


}