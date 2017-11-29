package com.dnion.app.android.injuriesapp.utils;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 卫华 on 2017/4/23.
 */

public class DBHelper extends DataBaseHelper {

    private static DBHelper mTestDBHelper;

    private DBHelper(Context context){
        super(context);
    }

    public static DBHelper getInstance(Context context){
        if (mTestDBHelper==null){
            synchronized (DataBaseHelper.class){
                if (mTestDBHelper==null){
                    mTestDBHelper = new DBHelper(context);
                    if (mTestDBHelper.getDB()==null||!mTestDBHelper.getDB().isOpen()){
                        mTestDBHelper.open();
                    }
                }
            }
        }
        return mTestDBHelper;
    }

    @Override
    protected int getMDbVersion(Context context) {
        return 6;
    }

    @Override
    protected String getDbName(Context context) {
        return "record.db";
    }

    @Override
    protected String[] getDbCreateSql(Context context) {
        String pass = MD5.encode("admin123321");
        String[] sqlArray =  new String[]{
                " CREATE TABLE user_info ( "
                        + " id  INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , "
                        + " login_name  TEXT(64) NOT NULL , "
                        + " name  TEXT(64) NOT NULL , "
                        + " password  TEXT, "
                        + " type  INTEGER, "
                        + " department  TEXT  , "
                        + " hospital  TEXT  , "
                        + " create_time  TEXT(64) , "
                        + " update_time  TEXT(64)  , "
                        + " sync_flag  INTEGER  NOT NULL , "
                        + " sync_time  TEXT(64)  "
                        + " )"
                ,
                " insert into user_info (login_name, name, password, type, department, sync_flag) values ('admin', '管理员', '"+MD5.encode("admin123321")+"', 1, '创伤科', 0) "
                ,
                        " CREATE TABLE patient_info ( "
                        + " id  INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , "
                        + " inpatient_no  TEXT(64) NOT NULL , "
                        + " name  TEXT(64) NOT NULL , "
                        + " sex  int(1), "
                        + " birthday  TEXT(64), "
                        + " age  TEXT(5)  , "
                        + " department  TEXT(10)  , "
                        + " bed_no  TEXT(5)  , "
                        + " diagnosis  TEXT(1024)  , "
                        + " doctor_id  int(11) NOT NULL , "
                        + " doctor  TEXT(64) , "
                        + " create_time  TEXT(64) , "
                        + " update_time  TEXT(64)  , "
                        + " sync_flag  INTEGER  NOT NULL , "
                        + " sync_time  TEXT(64)  "
                        + " )"
                        ,
                        " CREATE TABLE archives_record ("
                        + " id  INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT ,"
                        + " doctor_id  INTEGER NOT NULL ,"
                        + " inpatient_no  TEXT(64),"
                        + " record_time  TEXT(64) ,"
                        + " bed  TEXT(45) ,"
                        + " diagnosis  TEXT(45) ,"
                        + " department  TEXT(45)  ,"
                        + " is_operation  INTEGER ,"
                        + " wound_type  INTEGER  ,"
                        + " wound_height  REAL(10,2) ,"
                        + " wound_width  REAL(10,2)  ,"
                        + " wound_deep  REAL(10,2)  ,"
                        + " wound_area  REAL(10,2) ,"
                        + " wound_volume  REAL(10,2)  ,"
                        + " wound_time  TEXT(64)  ,"
                        + " wound_postion  INTEGER  ,"
                        + " wound_postionx  INTEGER ,"
                        + " wound_postiony  INTEGER  ,"
                        + " wound_measures  INTEGER  ,"
                        + " wound_describe_clean  INTEGER  ,"
                        + " wound_color_red  REAL(10,2)  ,"
                        + " wound_color_yellow  REAL(10,2)  ,"
                        + " wound_color_black  REAL(10,2)  ,"
                        + " wound_describe_color  INTEGER ,"
                        + " wound_describe_skin  INTEGER  ,"
                        + " wound_assess_prop  INTEGER ,"
                        + " wound_assess_infect  INTEGER ,"
                        + " wound_assess_infect_desc  TEXT(255) ,"
                        + " wound_leachate_volume  INTEGER ,"
                        + " wound_leachate_color  INTEGER ,"
                        + " wound_leachate_smell  INTEGER ,"
                        + " wound_heal_all  TEXT(255) ,"
                        + " wound_heal_position  TEXT(255) ,"
                        + " wound_doppler  TEXT(255) ,"
                        + " wound_cta  TEXT(255) ,"
                        + " wound_mr  TEXT(255) ,"
                        + " wound_petct  TEXT(255) ,"
                        + " wound_dressing  TEXT(255) ,"
                        + " create_time  TEXT(64) ,"
                        + " update_time  TEXT(64)  ,"
                        + " sync_flag  INTEGER  NOT NULL ,"
                        + " sync_time  TEXT(64) "
                        + " )"
                        ,
                " ALTER TABLE `archives_record` ADD `complains` VARCHAR(255) "
                ,
                        " CREATE TABLE record_image ("
                                + " id  INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT ,"
                                + " record_id  INTEGER NOT NULL ,"
                                + " image_type  TEXT(32),"
                                + " image_path  TEXT(255) ,"
                                + " create_time  TEXT(64) ,"
                                + " update_time  TEXT(64)  ,"
                                + " sync_flag  INTEGER  NOT NULL ,"
                                + " sync_time  TEXT(64) "
                                + " )"
                ,
                " ALTER TABLE `archives_record` ADD `wound_dressing_type` INTEGER "
                ,
                " ALTER TABLE `archives_record` ADD `favorites` INTEGER "
                ,
                " CREATE TABLE config ("
                        + " my_key  TEXT(32)  NOT NULL,"
                        + " my_value  TEXT(255) NOT NULL )"
                ,
                " insert into config (my_value, my_key) values ('"+CommonUtil.DEVICE_ID+"','"+CommonUtil.getDeviceId()+"') "
                ,
                " insert into config (my_value, my_key) values ('"+CommonUtil.REMOTE_URL+"','"+CommonUtil.APP_URL+"') "
                ,
                " ALTER TABLE `archives_record` ADD `uuid` TEXT(32) "
                ,
                " ALTER TABLE `patient_info` ADD `uuid` TEXT(32) "
                ,
                " update archives_record set uuid = id "
                ,
                " update patient_info set uuid = id "
                ,
                " ALTER TABLE `archives_record` ADD `wound_exam`  TEXT(255) "
                ,
                " ALTER TABLE `archives_record` ADD `wound_ache` INTEGER "
                ,
                " ALTER TABLE `archives_record` ADD `wound_type_desc` TEXT(32) "
        };
        return sqlArray;
    }

    @Override
    protected String[] getDbUpdateSql(int oldVersion, int newVersion) {
        List<String> sqlArray =  new ArrayList<String>();
        if (oldVersion <= 1) {
            sqlArray.add(" ALTER TABLE `archives_record` ADD `wound_dressing_type` INTEGER ");
            //Log.i("onUpgrade","你在没有卸载的情况下，在线更新了版本2.0,同时列表增加了一个列sex");
        }
        if (oldVersion <= 2) {
            sqlArray.add(" ALTER TABLE `archives_record` ADD `favorites` INTEGER ");
        }
        if (oldVersion <= 3) {
            sqlArray.add(" CREATE TABLE config ("
                    + " my_key  TEXT(32)  NOT NULL,"
                    + " my_value  TEXT(255) NOT NULL )");
            sqlArray.add(" insert into config (my_value, my_key) values ('"+CommonUtil.DEVICE_ID+"','"+CommonUtil.getDeviceId()+"') ");
            sqlArray.add(" insert into config (my_value, my_key) values ('"+CommonUtil.REMOTE_URL+"','"+CommonUtil.APP_URL+"') ");
        }
        if (oldVersion <= 4) {
            sqlArray.add(" ALTER TABLE `archives_record` ADD `uuid` TEXT(32) ");
            sqlArray.add(" ALTER TABLE `patient_info` ADD `uuid` TEXT(32) ");
            sqlArray.add(" update archives_record set uuid = id ");
            sqlArray.add(" update patient_info set uuid = id ");
        }
        if (oldVersion <= 5) {
            sqlArray.add(" ALTER TABLE `archives_record` ADD `wound_type_desc` TEXT(32) ");//伤口类型描述
            sqlArray.add(" ALTER TABLE `archives_record` ADD `wound_exam` TEXT(255) ");//常规检查
            sqlArray.add(" ALTER TABLE `archives_record` ADD `wound_ache` INTEGER ");//疼痛等级
        }
        return sqlArray.toArray(new String [sqlArray.size()]);
    }
}
