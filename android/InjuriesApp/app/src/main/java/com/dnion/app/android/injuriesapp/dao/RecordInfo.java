package com.dnion.app.android.injuriesapp.dao;

import android.text.format.DateFormat;
import android.widget.ImageView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by 卫华 on 2017/5/26.
 */

public class RecordInfo {
    private Integer id;
    private Integer doctorId;//医生ID
    private String inpatientNo;//住院号
    private String recordTime = (String) DateFormat.format("yyyy-MM-dd HH:mm:ss", Calendar.getInstance());//采集时间
    private Integer isOperation = 0;//是否手术
    private Integer woundType = 1;//伤口类型

    private Float woundWidth;//伤口尺寸-长
    private Float woundHeight;//伤口尺寸-宽
    private Float woundDeep;//伤口尺寸-深
    private Float woundArea;//伤口尺寸-面积
    private Float woundVolume;//伤口尺寸-容积
    private String woundTime;//伤口形成时间
    private Integer woundMeasures;//目前对伤口采取的措施
    private Integer woundPosition;//伤口的位置
    private Integer woundPositionx;//伤口的位置X
    private Integer woundPositiony;//伤口的位置Y
    private String woundPositionDesc;//伤口位置描述
    private Integer woundDescribeClean;//伤口的清洁程度
    private Float woundColorRed;//伤口的基底颜色-红
    private Float woundColorYellow;//伤口的基底颜色-黄
    private Float woundColorBlack;//伤口的基底颜色-黑
    //private Integer woundDescribeColor;
    private Integer woundDescribeSkin;//伤口周围皮肤情况
    private Integer woundAssessProp;//伤口属性
    private Integer woundAssessInfect;//伤口有无感染
    private String woundAssessInfectDesc;//感染的细菌种类
    private Integer woundLeachateVolume;//伤口的渗液量
    private Integer woundLeachateColor;//伤口的渗液的颜色
    private Integer woundLeachateSmell;//伤口的渗液的气味
    private String woundHealAll;//全身因素
    private String woundHealPosition;//局部因素
    private String woundDoppler;//多普勒
    private String woundCta;//CT检查
    private String woundMr;//MR检查
    private String woundPetct;
    private String woundDressing;//辅料
    private Integer woundDressingType;//辅料类型
    private Integer favorites;//是否收藏
    private String uuid;

    private String woundTypeDesc;//伤口类型描述
    private String woundExam;//常规检查
    private Integer woundAche;//疼痛等级

    public String getWoundPositionDesc() {
        return woundPositionDesc;
    }

    public void setWoundPositionDesc(String woundPositionDesc) {
        this.woundPositionDesc = woundPositionDesc;
    }

    public String getWoundTypeDesc() {
        return woundTypeDesc;
    }

    public void setWoundTypeDesc(String woundTypeDesc) {
        this.woundTypeDesc = woundTypeDesc;
    }

    public String getWoundExam() {
        return woundExam;
    }

    public void setWoundExam(String woundExam) {
        this.woundExam = woundExam;
    }

    public Integer getWoundAche() {
        return woundAche;
    }

    public void setWoundAche(Integer woundAche) {
        this.woundAche = woundAche;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    private PatientInfo patientInfo;

    private List<RecordImage> deepList;

    public PatientInfo getPatientInfo() {
        return patientInfo;
    }

    public void setPatientInfo(PatientInfo patientInfo) {
        this.patientInfo = patientInfo;
    }

    public List<RecordImage> getDeepList() {
        return deepList;
    }

    public void setDeepList(List<RecordImage> deepList) {
        this.deepList = deepList;
    }

    public Integer getFavorites() {
        return favorites;
    }

    public void setFavorites(Integer favorites) {
        this.favorites = favorites;
    }

    public String getComplains() {
        return complains;
    }

    public void setComplains(String complains) {
        this.complains = complains;
    }

    private String complains;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public String getInpatientNo() {
        return inpatientNo;
    }

    public void setInpatientNo(String inpatientNo) {
        this.inpatientNo = inpatientNo;
    }

    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }

    public Integer getIsOperation() {
        return isOperation;
    }

    public void setIsOperation(Integer isOperation) {
        this.isOperation = isOperation;
    }

    public Integer getWoundType() {
        return woundType;
    }

    public void setWoundType(Integer woundType) {
        this.woundType = woundType;
    }

    public Float getWoundWidth() {
        return woundWidth;
    }

    public void setWoundWidth(Float woundWidth) {
        this.woundWidth = woundWidth;
    }

    public Float getWoundHeight() {
        return woundHeight;
    }

    public void setWoundHeight(Float woundHeight) {
        this.woundHeight = woundHeight;
    }

    public Float getWoundDeep() {
        return woundDeep;
    }

    public void setWoundDeep(Float woundDeep) {
        this.woundDeep = woundDeep;
    }

    public Float getWoundArea() {
        return woundArea;
    }

    public void setWoundArea(Float woundArea) {
        this.woundArea = woundArea;
    }

    public Float getWoundVolume() {
        return woundVolume;
    }

    public void setWoundVolume(Float woundVolume) {
        this.woundVolume = woundVolume;
    }

    public String getWoundTime() {
        return woundTime;
    }

    public void setWoundTime(String woundTime) {
        this.woundTime = woundTime;
    }

    public Integer getWoundMeasures() {
        return woundMeasures;
    }

    public void setWoundMeasures(Integer woundMeasures) {
        this.woundMeasures = woundMeasures;
    }

    public Integer getWoundPosition() {
        return woundPosition;
    }

    public void setWoundPosition(Integer woundPosition) {
        this.woundPosition = woundPosition;
    }

    public Integer getWoundPositionx() {
        return woundPositionx;
    }

    public void setWoundPositionx(Integer woundPositionx) {
        this.woundPositionx = woundPositionx;
    }

    public Integer getWoundPositiony() {
        return woundPositiony;
    }

    public void setWoundPositiony(Integer woundPositiony) {
        this.woundPositiony = woundPositiony;
    }

    public Integer getWoundDescribeClean() {
        return woundDescribeClean;
    }

    public void setWoundDescribeClean(Integer woundDescribeClean) {
        this.woundDescribeClean = woundDescribeClean;
    }

    public Float getWoundColorRed() {
        return woundColorRed;
    }

    public void setWoundColorRed(Float woundColorRed) {
        this.woundColorRed = woundColorRed;
    }

    public Float getWoundColorYellow() {
        return woundColorYellow;
    }

    public void setWoundColorYellow(Float woundColorYellow) {
        this.woundColorYellow = woundColorYellow;
    }

    public Float getWoundColorBlack() {
        return woundColorBlack;
    }

    public void setWoundColorBlack(Float woundColorBlack) {
        this.woundColorBlack = woundColorBlack;
    }

    public Integer getWoundDescribeSkin() {
        return woundDescribeSkin;
    }

    public void setWoundDescribeSkin(Integer woundDescribeSkin) {
        this.woundDescribeSkin = woundDescribeSkin;
    }

    public Integer getWoundAssessProp() {
        return woundAssessProp;
    }

    public void setWoundAssessProp(Integer woundAssessProp) {
        this.woundAssessProp = woundAssessProp;
    }

    public Integer getWoundAssessInfect() {
        return woundAssessInfect;
    }

    public void setWoundAssessInfect(Integer woundAssessInfect) {
        this.woundAssessInfect = woundAssessInfect;
    }

    public String getWoundAssessInfectDesc() {
        return woundAssessInfectDesc;
    }

    public void setWoundAssessInfectDesc(String woundAssessInfectDesc) {
        this.woundAssessInfectDesc = woundAssessInfectDesc;
    }

    public Integer getWoundLeachateVolume() {
        return woundLeachateVolume;
    }

    public void setWoundLeachateVolume(Integer woundLeachateVolume) {
        this.woundLeachateVolume = woundLeachateVolume;
    }

    public Integer getWoundLeachateColor() {
        return woundLeachateColor;
    }

    public void setWoundLeachateColor(Integer woundLeachateColor) {
        this.woundLeachateColor = woundLeachateColor;
    }

    public Integer getWoundLeachateSmell() {
        return woundLeachateSmell;
    }

    public void setWoundLeachateSmell(Integer woundLeachateSmell) {
        this.woundLeachateSmell = woundLeachateSmell;
    }

    public String getWoundHealAll() {
        return woundHealAll;
    }

    public void setWoundHealAll(String woundHealAll) {
        this.woundHealAll = woundHealAll;
    }

    public String getWoundHealPosition() {
        return woundHealPosition;
    }

    public void setWoundHealPosition(String woundHealPosition) {
        this.woundHealPosition = woundHealPosition;
    }

    public String getWoundDoppler() {
        return woundDoppler;
    }

    public void setWoundDoppler(String woundDoppler) {
        this.woundDoppler = woundDoppler;
    }

    public String getWoundCta() {
        return woundCta;
    }

    public void setWoundCta(String woundCta) {
        this.woundCta = woundCta;
    }

    public String getWoundMr() {
        return woundMr;
    }

    public void setWoundMr(String woundMr) {
        this.woundMr = woundMr;
    }

    public String getWoundPetct() {
        return woundPetct;
    }

    public void setWoundPetct(String woundPetct) {
        this.woundPetct = woundPetct;
    }

    public String getWoundDressing() {
        return woundDressing;
    }

    public void setWoundDressing(String woundDressing) {
        this.woundDressing = woundDressing;
    }

    public Integer getWoundDressingType() {
        return woundDressingType;
    }

    public void setWoundDressingType(Integer woundDressingType) {
        this.woundDressingType = woundDressingType;
    }
}
