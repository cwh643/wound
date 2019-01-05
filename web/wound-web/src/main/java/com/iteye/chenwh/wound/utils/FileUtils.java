package com.iteye.chenwh.wound.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {
    public static void writeFile(String data, String path, String name, boolean rewrite) {
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
