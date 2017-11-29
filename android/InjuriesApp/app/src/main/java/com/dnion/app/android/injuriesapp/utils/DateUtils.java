package com.dnion.app.android.injuriesapp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 卫华 on 2017/5/28.
 */

public class DateUtils {
    public final static String DATE_FORMATE = "yyyyMMddHHmmss";

    public static Date parseDate(String fromat, String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(fromat);//小写的mm表示的是分钟
        try {
            Date date = sdf.parse(dateStr);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String formateDate(Date datetime, String formate) {
        SimpleDateFormat formatter = new SimpleDateFormat(formate);
        String dateString = formatter.format(datetime);
        return dateString;
    }

    public static String formateDate(Date datetime) {
        return formateDate(datetime, DATE_FORMATE);
    }
}
