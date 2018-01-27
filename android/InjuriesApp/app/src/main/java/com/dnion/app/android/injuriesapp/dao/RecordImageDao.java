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

public class RecordImageDao {

    private DBHelper db;

    public RecordImageDao(Activity context) {
        db = ((InjApp) context.getApplication()).getDb();
    }

    public List<RecordImage> queryRecordImage(String recordId) {
        List<RecordImage> list = new ArrayList<RecordImage>();
        String sql = " select * from record_image where record_id = ? order by create_time desc ";
        List<Map> mapList = db.queryListMap(sql, new String[]{recordId});
        if (mapList == null || mapList.size() == 0) {
            return list;
        }
        for (Map map : mapList) {
            RecordImage info = warpRecordImage(map);
            list.add(info);
        }
        return list;
    }

    public List<RecordImage> queryRecordImage(String recordId, String type) {
        List<RecordImage> list = new ArrayList<RecordImage>();
        String sql = " select * from record_image where record_id = ? and image_type in ("
                + type + ") order by create_time desc ";
        List<Map> mapList = db.queryListMap(sql, new String[]{recordId});
        if (mapList == null || mapList.size() == 0) {
            return list;
        }
        for (Map map : mapList) {
            RecordImage info = warpRecordImage(map);
            list.add(info);
        }
        return list;
    }

    public boolean insertRecordImage(RecordImage recordImage) {
        boolean success = db.insert("record_image",
                new String[]{"record_id", "image_type", "image_path", "create_time", "sync_flag"},
                new Object[]{recordImage.getRecordId(), recordImage.getImageType(), recordImage.getImagePath(),
                        DateFormat.format("yyyy-MM-dd HH:mm:ss", Calendar.getInstance()), 0});
        return success;
    }

    public static RecordImage warpRecordImage(Map map) {
        RecordImage recordImage = new RecordImage();
        recordImage.setId((Integer) map.get("id"));
        recordImage.setImageType((String) map.get("image_type"));
        recordImage.setImagePath((String) map.get("image_path"));
        recordImage.setCreateTime((String) map.get("create_time"));
        return recordImage;
    }

}