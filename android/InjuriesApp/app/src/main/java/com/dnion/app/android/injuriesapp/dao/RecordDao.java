package com.dnion.app.android.injuriesapp.dao;

import android.app.Activity;
import android.text.format.DateFormat;

import com.dnion.app.android.injuriesapp.InjApp;
import com.dnion.app.android.injuriesapp.utils.CommonUtil;
import com.dnion.app.android.injuriesapp.utils.DBHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by 卫华 on 2017/4/29.
 */

public class RecordDao {

    private DBHelper db;

    public RecordDao(Activity context) {
        db = ((InjApp)context.getApplication()).getDb();
    }

    public RecordInfo queryRecordInfo(String inpatientNo, String recordTime) {
        String sql = " select * from archives_record where inpatient_no = ? and record_time = ?";
        Map map  = db.queryItemMap(sql, new String[] {inpatientNo, recordTime});
        if (map != null && map.get("inpatient_no") != null) {
            RecordInfo recordInfo = warpRecordInfo(map);
            return recordInfo;
        }
        return null;
    }

    public void updateSyncFlag(int recordId) {
        String sql = "update archives_record set sync_flag = 1 and sync_time = ? where id = ?";
        String dateString = DateFormat.format("yyyy-MM-dd HH:mm:ss", Calendar.getInstance()).toString();
        db.execSQL(sql, new Object[]{dateString, recordId});
    }

    public boolean insertRecordInfo(RecordInfo recordInfo) {
        String uuid = CommonUtil.createUUID();
        boolean success = db.insert("archives_record",
                new String[]{"doctor_id", "inpatient_no", "record_time", "is_operation", "wound_type", "wound_width", "wound_height",
                        "wound_deep", "wound_area", "wound_volume", "wound_time", "wound_postion",
                        "wound_postionx", "wound_postiony", "wound_measures", "wound_describe_clean",
                        "wound_color_red", "wound_color_yellow", "wound_color_black",
                        "wound_describe_skin", "wound_assess_prop", "wound_assess_infect", "wound_assess_infect_desc",
                        "wound_leachate_volume", "wound_leachate_color", "wound_leachate_smell", "wound_heal_all",
                        "wound_heal_position", "wound_doppler",  "wound_cta",  "wound_mr", "wound_petct", "wound_dressing",
                        "wound_dressing_type", "favorites", "uuid",
                        "create_time", "sync_flag", "complains", "wound_type_desc", "wound_exam", "wound_ache"},
                new Object[]{recordInfo.getDoctorId(), recordInfo.getInpatientNo(), recordInfo.getRecordTime(), recordInfo.getIsOperation(), recordInfo.getWoundType(),
                        recordInfo.getWoundWidth(), recordInfo.getWoundHeight(),
                        recordInfo.getWoundDeep(), recordInfo.getWoundArea(), recordInfo.getWoundVolume(), recordInfo.getWoundTime(), recordInfo.getWoundPosition(),
                        recordInfo.getWoundPositionx(), recordInfo.getWoundPositiony(), recordInfo.getWoundMeasures(), recordInfo.getWoundDescribeClean(),
                        recordInfo.getWoundColorRed(), recordInfo.getWoundColorYellow(), recordInfo.getWoundColorBlack(),
                        recordInfo.getWoundDescribeSkin(), recordInfo.getWoundAssessProp(), recordInfo.getWoundAssessInfect(), recordInfo.getWoundAssessInfectDesc(),
                        recordInfo.getWoundLeachateVolume(), recordInfo.getWoundLeachateColor(), recordInfo.getWoundLeachateSmell(), recordInfo.getWoundHealAll(),
                        recordInfo.getWoundHealPosition(), recordInfo.getWoundDoppler(), recordInfo.getWoundCta(), recordInfo.getWoundMr(), recordInfo.getWoundPetct(),
                        recordInfo.getWoundDressing(), recordInfo.getWoundDressingType(), recordInfo.getFavorites(), uuid,
                        DateFormat.format("yyyy-MM-dd HH:mm:ss", Calendar.getInstance()), 0, recordInfo.getComplains(),
                        recordInfo.getWoundTypeDesc(), recordInfo.getWoundExam(), recordInfo.getWoundAche()});
        return success;
    }

    public List<RecordInfo> queryRecordList(String inpatientNo) {
        List<RecordInfo> list = new ArrayList<RecordInfo>();
        String sql = " select * from archives_record where inpatient_no = ? order by create_time desc ";
        List<Map> mapList = db.queryListMap(sql, new String[] {inpatientNo});
        if (mapList == null || mapList.size() == 0) {
            return list;
        }
        for (Map map : mapList) {
            list.add(warpRecordInfo(map));
        }
        return list;
    }

    public boolean updateRecordInfo(RecordInfo recordInfo) {
        boolean success = db.update("archives_record",
                new String[]{"wound_type", "wound_width", "wound_height",
                        "wound_deep", "wound_area", "wound_volume", "wound_time", "wound_postion",
                        "wound_postionx", "wound_postiony", "wound_measures", "wound_describe_clean",
                        "wound_color_red", "wound_color_yellow", "wound_color_black",
                        "wound_describe_skin", "wound_assess_prop", "wound_assess_infect", "wound_assess_infect_desc",
                        "wound_leachate_volume", "wound_leachate_color", "wound_leachate_smell", "wound_heal_all",
                        "wound_heal_position", "wound_doppler",  "wound_cta",  "wound_mr", "wound_petct", "wound_dressing",
                        "wound_dressing_type", "favorites",
                        "update_time", "sync_flag", "complains", "wound_type_desc", "wound_exam", "wound_ache"},
                new Object[]{recordInfo.getWoundType(), recordInfo.getWoundWidth(), recordInfo.getWoundHeight(),
                        recordInfo.getWoundDeep(), recordInfo.getWoundArea(), recordInfo.getWoundVolume(), recordInfo.getWoundTime(), recordInfo.getWoundPosition(),
                        recordInfo.getWoundPositionx(), recordInfo.getWoundPositiony(), recordInfo.getWoundMeasures(), recordInfo.getWoundDescribeClean(),
                        recordInfo.getWoundColorRed(), recordInfo.getWoundColorYellow(), recordInfo.getWoundColorBlack(),
                        recordInfo.getWoundDescribeSkin(), recordInfo.getWoundAssessProp(), recordInfo.getWoundAssessInfect(), recordInfo.getWoundAssessInfectDesc(),
                        recordInfo.getWoundLeachateVolume(), recordInfo.getWoundLeachateColor(), recordInfo.getWoundLeachateSmell(), recordInfo.getWoundHealAll(),
                        recordInfo.getWoundHealPosition(), recordInfo.getWoundDoppler(), recordInfo.getWoundCta(), recordInfo.getWoundMr(), recordInfo.getWoundPetct(),
                        recordInfo.getWoundDressing(), recordInfo.getWoundDressingType(), recordInfo.getFavorites(),
                        DateFormat.format("yyyy-MM-dd HH:mm:ss", Calendar.getInstance()), 0, recordInfo.getComplains(),
                        recordInfo.getWoundTypeDesc(), recordInfo.getWoundExam(), recordInfo.getWoundAche()},
                new String[]{"id"},
                new String[]{""+ recordInfo.getId()});
        return success;
    }

    private RecordInfo warpRecordInfo(Map map) {
        RecordInfo recordInfo = new RecordInfo();
        recordInfo.setId((Integer)map.get("id"));
        recordInfo.setInpatientNo((String)map.get("inpatient_no"));
        recordInfo.setRecordTime((String)map.get("record_time"));
        recordInfo.setIsOperation(getIntValue(map,"is_operation"));
        recordInfo.setWoundType(getIntValue(map,"wound_type"));
        recordInfo.setWoundWidth(getFloatValue(map,"wound_width"));
        recordInfo.setWoundHeight(getFloatValue(map,"wound_height"));
        recordInfo.setWoundDeep(getFloatValue(map,"wound_deep"));
        recordInfo.setWoundArea(getFloatValue(map,"wound_area"));
        recordInfo.setWoundVolume(getFloatValue(map,"wound_volume"));
        recordInfo.setWoundTime((String)map.get("wound_time"));
        recordInfo.setWoundPosition(getIntValue(map,"wound_postion"));
        recordInfo.setWoundPositionx(getIntValue(map,"wound_postionx"));
        recordInfo.setWoundPositiony(getIntValue(map,"wound_postiony"));
        recordInfo.setWoundMeasures(getIntValue(map,"wound_measures"));
        recordInfo.setWoundDescribeClean(getIntValue(map,"wound_describe_clean"));
        recordInfo.setWoundColorRed(getFloatValue(map,"wound_color_red"));
        recordInfo.setWoundColorYellow(getFloatValue(map,"wound_color_yellow"));
        recordInfo.setWoundColorBlack(getFloatValue(map,"wound_color_black"));
        recordInfo.setWoundDescribeSkin(getIntValue(map,"wound_describe_skin"));
        recordInfo.setWoundAssessProp(getIntValue(map,"wound_assess_prop"));
        recordInfo.setWoundAssessInfect(getIntValue(map,"wound_assess_infect"));
        recordInfo.setWoundAssessInfectDesc((String)map.get("wound_assess_infect_desc"));
        recordInfo.setWoundLeachateVolume(getIntValue(map,"wound_leachate_volume"));
        recordInfo.setWoundLeachateColor(getIntValue(map,"wound_leachate_color"));
        recordInfo.setWoundLeachateSmell(getIntValue(map,"wound_leachate_smell"));
        recordInfo.setWoundHealAll((String)map.get("wound_heal_all"));
        recordInfo.setWoundHealPosition((String)map.get("wound_heal_position"));
        recordInfo.setWoundDoppler((String)map.get("wound_doppler"));
        recordInfo.setWoundCta((String)map.get("wound_cta"));
        recordInfo.setWoundMr((String)map.get("wound_mr"));
        recordInfo.setWoundPetct((String)map.get("wound_petct"));
        recordInfo.setWoundDressing((String)map.get("wound_dressing"));
        recordInfo.setWoundDressingType(getIntValue(map,"wound_dressing_type"));
        recordInfo.setComplains((String)map.get("complains"));
        recordInfo.setFavorites(getIntValue(map,"favorites"));
        recordInfo.setUuid((String)map.get("uuid"));
        recordInfo.setWoundTypeDesc((String)map.get("wound_type_desc"));
        recordInfo.setWoundExam((String)map.get("wound_exam"));
        recordInfo.setWoundAche(getIntValue(map, "wound_ache"));
        return recordInfo;
    }

    public static Integer getIntValue(Map map, String key) {
        Object val = map.get(key);
        if (val == null) {
            return null;
        }
        String str = "" + val;
        if (str.length() == 0) {
            return null;
        }
        return Integer.parseInt(str);
    }

    public static Float getFloatValue(Map map, String key) {
        Object val = map.get(key);
        if (val == null) {
            return null;
        }
        String str = "" + val;
        if (str.length() == 0) {
            return null;
        }
        return Float.parseFloat(str);
    }

    public List<RecordInfo> queryFavortiesRecordList() {
        List<RecordInfo> list = new ArrayList<RecordInfo>();
        String sql = " select * from archives_record where favorites = 1 order by create_time desc ";
        List<Map> mapList = db.queryListMap(sql, new String[] {});
        if (mapList == null || mapList.size() == 0) {
            return list;
        }
        for (Map map : mapList) {
            RecordInfo info = warpRecordInfo(map);
            list.add(info);

            String inpatientNo = info.getInpatientNo();
            sql = " select * from patient_info where inpatient_no = ? ";
            Map pmap  = db.queryItemMap(sql, new String[] {inpatientNo});
            if (pmap != null && pmap.get("inpatient_no") != null) {
                PatientInfo pinfo = PatientDao.warpPatientInfo(pmap);
                info.setPatientInfo(pinfo);
            }

            String recordId = "" + info.getId();
            List<RecordImage> imageList = new ArrayList<RecordImage>();
            sql = " select * from record_image where record_id = ? and image_type = ? order by create_time desc ";
            List<Map> imapList  = db.queryListMap(sql, new String[] {recordId, "deep"});
            if (imapList != null && imapList.size() > 0) {
                RecordImage image = RecordImageDao.warpRecordImage(imapList.get(0));
                imageList.add(image);
            }
            info.setDeepList(imageList);
        }
        return list;
    }

    public List<RecordInfo> queryListBySyncStatus(int status) {
        List<RecordInfo> list = new ArrayList<RecordInfo>();
        String sql = " select * from archives_record where sync_flag = ?";
        List<Map> mapList = db.queryListMap(sql, new String[] {"" + status});
        if (mapList == null || mapList.size() == 0) {
            return list;
        }
        for (Map map : mapList) {
            RecordInfo info = warpRecordInfo(map);
            list.add(info);
            String inpatientNo = info.getInpatientNo();
            sql = " select * from patient_info where inpatient_no = ? ";
            Map pmap  = db.queryItemMap(sql, new String[] {inpatientNo});
            if (pmap != null && pmap.get("inpatient_no") != null) {
                PatientInfo pinfo = PatientDao.warpPatientInfo(pmap);
                info.setPatientInfo(pinfo);
            }

            String recordId = "" + info.getId();
            List<RecordImage> imageList = new ArrayList<RecordImage>();
            sql = " select * from record_image where record_id = ? and image_type = ? order by create_time desc ";
            List<Map> imapList  = db.queryListMap(sql, new String[] {recordId, "deep"});
            if (imapList != null && imapList.size() > 0) {
                RecordImage image = RecordImageDao.warpRecordImage(imapList.get(0));
                imageList.add(image);
            }
            info.setDeepList(imageList);
        }
        return list;
    }
}