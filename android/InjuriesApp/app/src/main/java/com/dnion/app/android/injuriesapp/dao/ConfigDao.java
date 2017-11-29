package com.dnion.app.android.injuriesapp.dao;

import android.app.Activity;
import android.text.format.DateFormat;

import com.dnion.app.android.injuriesapp.InjApp;
import com.dnion.app.android.injuriesapp.utils.DBHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by 卫华 on 2017/4/29.
 */

public class ConfigDao {

    private DBHelper db;

    public ConfigDao(Activity context) {
        db = ((InjApp)context.getApplication()).getDb();
    }

    public String queryValue(String key) {
        String sql = " select my_value from config where my_key = ? ";
        Map map  = db.queryItemMap(sql, new String[] {key});
        if (map != null && map.get("my_value") != null) {
            return (String)map.get("my_value");
        }
        return null;
    }

    public boolean saveValue(String key, String value) {
        String sql = " select my_value from config where my_key = ? ";
        Map map  = db.queryItemMap(sql, new String[] {key});
        if (map != null && map.get("my_value") != null) {
            sql = " update config set my_value = ? where my_key = ? ";
            db.execSQL(sql, new Object[]{value, key});
        } else {
            sql = " insert into config (my_value, my_key) values (?,?) ";
            db.execSQL(sql, new Object[]{value, key});
        }
        return true;
    }
}