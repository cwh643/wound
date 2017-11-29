package com.dnion.app.android.injuriesapp.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by 卫华 on 2017/7/3.
 */

public class DatabaseContext extends ContextWrapper {

    private String appSdPath = "";

    /**
     * 构造函数
     * @param    base 上下文环境
     */
    public DatabaseContext(Context base){
        super(base);
        //appSdPath = SDCardHelper.getSDCardPrivateFilesDir(base, "wound");
        appSdPath = SDCardHelper.getSDCardBaseDir() + File.separator + "wound";
    }

    /**
     * 获得数据库路径，如果不存在，则创建对象对象
     * @param    name
     */
    @Override
    public File getDatabasePath(String name) {
        //判断是否存在sd卡
        if(!SDCardHelper.isSDCardMounted()){//如果不存在,
            Log.e("SD卡管理：", "SD卡不存在，请加载SD卡");
            return null;
        }
        else{//如果存在
            //获取sd卡路径
            //String dbDir=SDCardHelper.getSDCardBaseDir();
            String dbDir=appSdPath;
            //dbDir += "../wound";//数据库所在目录
            String dbPath = dbDir+"/"+name;//数据库路径
            boolean isFileCreateSuccess = false;
            //判断目录是否存在，不存在则创建该目录
            File dirFile = new File(dbDir);
            if(!dirFile.exists()) {
                isFileCreateSuccess = dirFile.mkdirs();
            } else {
                isFileCreateSuccess = true;
            }
            //判断文件是否存在，不存在则创建该文件
            File dbFile = new File(dbPath);
            if(isFileCreateSuccess && !dbFile.exists()){
                try {
                    isFileCreateSuccess = dbFile.createNewFile();//创建文件
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.d("DatabaseContext", "dbFile=" + dbFile);
            //返回数据库文件对象
            if(isFileCreateSuccess)
                return dbFile;
            else
                return null;
        }
    }

    /**
     * 重载这个方法，是用来打开SD卡上的数据库的，android 2.3及以下会调用这个方法。
     *
     * @param    name
     * @param    mode
     * @param    factory
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
        return result;
    }

    /**
     * Android 4.0会调用此方法获取数据库。
     *
     * @see android.content.ContextWrapper#openOrCreateDatabase(java.lang.String, int,
     *              android.database.sqlite.SQLiteDatabase.CursorFactory,
     *              android.database.DatabaseErrorHandler)
     * @param    name
     * @param    mode
     * @param    factory
     * @param     errorHandler
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory,
                                               DatabaseErrorHandler errorHandler) {
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
        return result;
    }
}
