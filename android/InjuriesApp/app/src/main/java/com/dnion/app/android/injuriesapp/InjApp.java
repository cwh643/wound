package com.dnion.app.android.injuriesapp;

import android.app.Application;

import com.dnion.app.android.injuriesapp.utils.DBHelper;
import com.dnion.app.android.injuriesapp.utils.DatabaseContext;

/**
 * Created by 卫华 on 2017/4/24.
 */

public class InjApp extends Application {

    protected DBHelper db;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void initDb() {
        db = DBHelper.getInstance(new DatabaseContext(this));
    }


    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        super.onTerminate();
        if (db != null) {
            db.close();
        }
    }
    @Override
    public void onLowMemory() {
        // 低内存的时候执行
        super.onLowMemory();
    }
    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        super.onTrimMemory(level);
    }

    public DBHelper getDb() {
        if (db == null) {
            initDb();
        }
        return db;
    }


}
