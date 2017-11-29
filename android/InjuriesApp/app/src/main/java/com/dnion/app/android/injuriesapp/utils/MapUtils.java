package com.dnion.app.android.injuriesapp.utils;

import com.dnion.app.android.injuriesapp.http.OkHttpClientManager;
import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by 卫华 on 2017/6/17.
 */

public class MapUtils {

    public static Gson mGson = new Gson();

    public static Map bean2Map(Object bean) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (bean != null) {
            Class cls = bean.getClass();
            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    Object o = field.get(bean);
                    map.put(field.getName(), o);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }

    public static OkHttpClientManager.Param[] bean2Params(Object bean) {
        List<OkHttpClientManager.Param> list = new ArrayList<OkHttpClientManager.Param>();
        Map<String, Object> map = bean2Map(bean);
        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            Object value = map.get(key);
            if (value != null) {
                list.add(new OkHttpClientManager.Param(key, "" + value));
            }
        }
        return list.toArray(new OkHttpClientManager.Param[list.size()]);
    }

}
