package com.dnion.app.android.injuriesapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.dnion.app.android.injuriesapp.InjApp;
import com.dnion.app.android.injuriesapp.LoginActivity;
import com.dnion.app.android.injuriesapp.RecordFragment1;
import com.dnion.app.android.injuriesapp.TypeArrayAdapter;
import com.dnion.app.android.injuriesapp.dao.ConfigDao;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by 卫华 on 2017/4/23.
 */

public class CommonUtil {

    public static final float SELECT_TEXT_SIZE = 25;

    public static final String LOGIN_USER = "login_user_name";
    public static final String LOGIN_TYPE = "login_user_type";
    public static final String PATIENT_USER = "patient_name";
    public static final String LOGIN_ID = "login_id";
    public static final String REMOTE_URL = "remote_url";
    public static final String DEVICE_ID = "device_id";

    //public static final String APP_URL = "http://139.196.171.110:8080/wound-web/api/";
    public static final String APP_URL = "http://192.168.1.8:8080/wound-web";

    public static String getDeviceId() {
        int serialNumber = (int)(1000 * Math.random() + 1);
        String uid = "wound-" + String.format("%03d", serialNumber);
        return uid;
    }

    public static void hiddenKeybord(Activity activity, View v) {
        InputMethodManager im = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(v.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void initRadioGroup(RadioGroup group, Integer id) {
        int count = group.getChildCount();
        for (int i = 0; i < count; i++) {
            RadioButton item = (RadioButton)group.getChildAt(i);
            if (id != null && id.equals(Integer.parseInt("" + item.getTag()))) {
                item.setChecked(true);
            } else {
                item.setChecked(false);
            }
        }
    }

    public static void initRadioGroup(RadioGroup group, String val) {
        int count = group.getChildCount();
        for (int i = 0; i < count; i++) {
            RadioButton item = (RadioButton)group.getChildAt(i);
            if (val != null && val.equals("" + item.getTag())) {
                item.setChecked(true);
            } else {
                item.setChecked(false);
            }
        }
    }

    /**
     * 根据值, 设置spinner默认选中:
     * @param spinner
     * @param value
     */
    public static void setSpinnerItemSelectedByValue(Spinner spinner, Integer value){
        TypeArrayAdapter apsAdapter= (TypeArrayAdapter)spinner.getAdapter(); //得到SpinnerAdapter对象
        int k= apsAdapter.getCount();
        for(int i=0;i<k;i++){
            if(value != null && value.equals(apsAdapter.getItem(i).first)){
                //spinner.setSelection(i,true);// 默认选中项
                spinner.setSelection(i);// 默认选中项
                break;
            }
        }
    }

    public static Bitmap readBitMap(String path){
        BitmapFactory.Options options = new  BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        int scalw = 1;
        if (options.outWidth > 300) {
            scalw = options.outWidth / 300;
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = scalw;
        bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    public static String initUrl(Context context, String req) {
        String remote_url = SharedPreferenceUtil.getSharedPreferenceValue(context, CommonUtil.REMOTE_URL, CommonUtil.APP_URL);
        return remote_url + "/api/wap/" + req;
    }

    public static String initUrl(String remoteUrl, String req) {
        //String remote_url = SharedPreferenceUtil.getSharedPreferenceValue(context, CommonUtil.REMOTE_URL, CommonUtil.APP_URL);
        return remoteUrl + "/api/wap/" + req;
    }

    /**
     * 返回当前程序版本名
     */
    public static int getAppVersionCode(Context context) {
        int versioncode = 0;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            //versionName = pi.versionName;
            versioncode = pi.versionCode;
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versioncode;
    }

    public static void datasync3(Activity context) {
        //ConfigDao configDao = new ConfigDao(context);
        //String deviceId = configDao.queryValue(CommonUtil.DEVICE_ID).replace("-", "");
        DBHelper db = ((InjApp)context.getApplication()).getDb();
        String sql = " update archives_record set uuid = id ";
        db.execSQL(sql);
        sql = " update patient_info set uuid = id ";
        db.execSQL(sql);
    }

    public static void datasync(Context context) {
        String pathNew = SDCardHelper.getSDCardBaseDir() + File.separator + "wound";
        String pathOld = SDCardHelper.getSDCardPrivateFilesDir(context, "wound");
        File fileNew =  new File(pathNew);
        if (!fileNew.exists()) {
            fileNew.mkdirs();
        }
        File fileOld =  new File(pathOld);
        try {
            copyFile(fileOld, pathNew, pathOld);
        } catch (Exception e) {
            Log.e("woundApp", " Error in copy file ", e);
        }
    }

    public static String createUUID() {
        String uuid = UUID.randomUUID().toString(); //获取UUID并转化为String对象
        uuid = uuid.replace("-", "");          //因为UUID本身为32位只是生成时多了“-”，所以将它们去点就可
        return uuid;
    }

    public static boolean isEn(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("en"))
            return true;
        else
            return false;
    }

    private static void copyFile(File inFile, String outPath, String pathOld) throws Exception {
        File[] files = inFile.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    copyFile(f, outPath, pathOld);
                } else {
                    String path = f.getPath();
                    String newPath = outPath + path.replace(pathOld, "");
                    File fileNew = new File(newPath);
                    if (!fileNew.getParentFile().exists()) {
                        fileNew.getParentFile().mkdirs();
                    }
                    FileUtils.copyFile(f, fileNew);
                    Log.i("woundApp", "copy file " + path + " -> " + newPath);
                    f.delete();
                }
            }
        }
    }



}
