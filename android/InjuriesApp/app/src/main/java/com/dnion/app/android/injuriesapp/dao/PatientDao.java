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

public class PatientDao {

    private DBHelper db;

    public PatientDao(Activity context) {
        db = ((InjApp)context.getApplication()).getDb();
    }

    public PatientInfo queryPatientInfo(String inpatientNo) {
        String sql = " select * from patient_info where inpatient_no = ? ";
        Map map  = db.queryItemMap(sql, new String[] {inpatientNo});
        if (map != null && map.get("inpatient_no") != null) {
            PatientInfo info = warpPatientInfo(map);
            return info;
        }
        return null;
    }

    public boolean insertPatient(PatientInfo patientInfo) {
        String uuid = CommonUtil.createUUID();
        boolean success = db.insert("patient_info",
                new String[]{"name", "sex", "age", "bed_no", "inpatient_no", "diagnosis", "doctor", "doctor_id", "uuid", "create_time", "sync_flag"},
                new Object[]{patientInfo.getName(), patientInfo.getSex(), patientInfo.getAge(), patientInfo.getBedNo(), patientInfo.getInpatientNo(), patientInfo.getDiagnosis(),
                        patientInfo.getDoctor(), patientInfo.getDoctorId(), uuid, DateFormat.format("yyyy-MM-dd HH:mm:ss", Calendar.getInstance()), 0});
        return success;
    }


    public boolean updatePatient(PatientInfo patientInfo) {
        boolean success = db.update("patient_info",
                new String[]{"name", "sex", "age", "bed_no", "inpatient_no", "diagnosis", "doctor", "doctor_id", "update_time"},
                new Object[]{patientInfo.getName(), patientInfo.getSex(), patientInfo.getAge(), patientInfo.getBedNo(), patientInfo.getInpatientNo(), patientInfo.getDiagnosis(),
                        patientInfo.getDoctor(), patientInfo.getDoctorId(), DateFormat.format("yyyy-MM-dd HH:mm:ss", Calendar.getInstance())},
                new String[]{"id"},
                new String[]{""+ patientInfo.getId()});
        return success;
    }

    public List<PatientInfo> queryPatientList(String docId) {
        List<PatientInfo> list = new ArrayList<PatientInfo>();
        String sql = " select * from patient_info where doctor_id = ? order by create_time desc ";
        List<Map> mapList = db.queryListMap(sql, new String[] {docId});
        if (mapList == null || mapList.size() == 0) {
            return list;
        }
        for (Map map : mapList) {
            list.add(warpPatientInfo(map));
        }
        return list;
    }

    public static PatientInfo warpPatientInfo(Map map) {
        PatientInfo patientInfo = new PatientInfo();
        patientInfo.setId(((Integer)map.get("id")).longValue());
        patientInfo.setName((String)map.get("name"));
        patientInfo.setSex(getIntValue(map, "sex"));
        patientInfo.setAge((String)map.get("age"));
        patientInfo.setBedNo((String)map.get("bed_no"));
        patientInfo.setInpatientNo((String)map.get("inpatient_no"));
        patientInfo.setDiagnosis((String)map.get("diagnosis"));
        patientInfo.setDoctor((String)map.get("doctor"));
        patientInfo.setDoctorId(getIntValue(map, "doctor_id"));
        patientInfo.setUuid((String)map.get("uuid"));
        return patientInfo;
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

}