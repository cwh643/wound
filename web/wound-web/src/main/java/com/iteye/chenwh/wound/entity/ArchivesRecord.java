package com.iteye.chenwh.wound.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "archives_record")
public class ArchivesRecord extends IdEntity {
	private String deviceId;//设备ID
	private Long recordId;//设备上的记录id
	private Long doctorId;//医生ID1
	private String inpatientNo;
	private String recordTime;//采集时间
	private String bed;
	private String diagnosis;
	private String department;
	private Integer isOperation;//是否手术
	private Integer woundType;//伤口类型
	private Double woundHeight;//伤口尺寸-长
	private Double woundWidth;//伤口尺寸-宽
	private Double woundDeep;//伤口尺寸-深
	private Double woundArea;//伤口尺寸-面积
	private Double woundVolume;//伤口尺寸-容积
	private String woundTime;//伤口形成时间
	private Integer woundPostion;//伤口的位置
	private Integer woundPostionx;//伤口的位置x
	private Integer woundPostiony;//伤口的位置y
	private Integer woundMeasures;//目前对伤口采取的措施
	private Integer woundDescribeClean;//伤口的清洁程度
	private Double woundColorRed;//伤口的基底颜色-红
	private Double woundColorYellow;//伤口的基底颜色-黄
	private Double woundColorBlack;//伤口的基底颜色-黑
	private Integer woundDescribeSkin;//伤口周围皮肤情况
	private Integer woundAssessProp;//伤口属性
	private Integer woundAssessInfect;//伤口有无感染
	private String woundAssessInfectDesc;//伤口感染描述
	private Integer woundLeachateVolume;//伤口的渗液量
	private Integer woundLeachateColor;//伤口的渗液的颜色
	private Integer woundLeachateSmell;//伤口的渗液的气味
	private String woundHealAll;//全身因素
	private String woundHealPosition;//局部因素
	private String woundDoppler;
	private String woundCta;//CT结果
	private String woundMr;//核磁成像
	private String woundPetct;//PETCT
	private String woundDressing;//敷料的描述
	private String woundDressing1;
	private String woundDressing2;
	private Integer woundDressingType;//敷料的类型
	private String complains;
	private Integer favorites;
	private Date createTime;
	private Date updateTime;
	
	private String woundTypeDesc;//伤口类型描述
	private String woundExam;//常规检查
	private Integer woundAche;//疼痛等级
	
	private Patient patientInfo;
	
	private List<RecordImage> deepList;
	
	
	private String uuid;//唯一ID
	
	
	public ArchivesRecord() {
	}

	public ArchivesRecord(Long id) {
		this.id = id;
	}

	public Long getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Long doctorId) {
		this.doctorId = doctorId;
	}

	public String getBed() {
		return bed;
	}

	public void setBed(String bed) {
		this.bed = bed;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
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

	public Double getWoundHeight() {
		return woundHeight;
	}

	public void setWoundHeight(Double woundHeight) {
		this.woundHeight = woundHeight;
	}

	public Double getWoundWidth() {
		return woundWidth;
	}

	public void setWoundWidth(Double woundWidth) {
		this.woundWidth = woundWidth;
	}

	public Double getWoundDeep() {
		return woundDeep;
	}

	public void setWoundDeep(Double woundDeep) {
		this.woundDeep = woundDeep;
	}

	public Double getWoundArea() {
		return woundArea;
	}

	public void setWoundArea(Double woundArea) {
		this.woundArea = woundArea;
	}

	public Double getWoundVolume() {
		return woundVolume;
	}

	public void setWoundVolume(Double woundVolume) {
		this.woundVolume = woundVolume;
	}

	public String getWoundTime() {
		return woundTime;
	}

	public void setWoundTime(String woundTime) {
		this.woundTime = woundTime;
	}

	public Integer getWoundPostion() {
		return woundPostion;
	}

	public void setWoundPostion(Integer woundPostion) {
		this.woundPostion = woundPostion;
	}

	public Integer getWoundMeasures() {
		return woundMeasures;
	}

	public void setWoundMeasures(Integer woundMeasures) {
		this.woundMeasures = woundMeasures;
	}

	public Integer getWoundDescribeClean() {
		return woundDescribeClean;
	}

	public void setWoundDescribeClean(Integer woundDescribeClean) {
		this.woundDescribeClean = woundDescribeClean;
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

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(String recordTime) {
		this.recordTime = recordTime;
	}

	public String getInpatientNo() {
		return inpatientNo;
	}

	public void setInpatientNo(String inpatientNo) {
		this.inpatientNo = inpatientNo;
	}

	public Integer getWoundPostionx() {
		return woundPostionx;
	}

	public void setWoundPostionx(Integer woundPostionx) {
		this.woundPostionx = woundPostionx;
	}

	public Integer getWoundPostiony() {
		return woundPostiony;
	}

	public void setWoundPostiony(Integer woundPostiony) {
		this.woundPostiony = woundPostiony;
	}

	public Double getWoundColorRed() {
		return woundColorRed;
	}

	public void setWoundColorRed(Double woundColorRed) {
		this.woundColorRed = woundColorRed;
	}

	public Double getWoundColorYellow() {
		return woundColorYellow;
	}

	public void setWoundColorYellow(Double woundColorYellow) {
		this.woundColorYellow = woundColorYellow;
	}

	public Double getWoundColorBlack() {
		return woundColorBlack;
	}

	public void setWoundColorBlack(Double woundColorBlack) {
		this.woundColorBlack = woundColorBlack;
	}

	public String getWoundAssessInfectDesc() {
		return woundAssessInfectDesc;
	}

	public void setWoundAssessInfectDesc(String woundAssessInfectDesc) {
		this.woundAssessInfectDesc = woundAssessInfectDesc;
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

	public Integer getWoundDressingType() {
		return woundDressingType;
	}

	public void setWoundDressingType(Integer woundDressingType) {
		this.woundDressingType = woundDressingType;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public Long getRecordId() {
		return recordId;
	}

	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}

	@Transient
	public Patient getPatientInfo() {
		return patientInfo;
	}

	public void setPatientInfo(Patient patientInfo) {
		this.patientInfo = patientInfo;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/*  
     * @OneToMany: 指明Order 与OrderItem关联关系为一对多关系 
     *  
     * mappedBy: 定义类之间的双向关系。如果类之间是单向关系，不需要提供定义，如果类和类之间形成双向关系，我们就需要使用这个属性进行定义， 
     * 否则可能引起数据一致性的问题。 
     *  
     * cascade: CascadeType[]类型。该属性定义类和类之间的级联关系。定义的级联关系将被容器视为对当前类对象及其关联类对象采取相同的操作， 
     * 而且这种关系是递归调用的。举个例子：Order 和OrderItem有级联关系，那么删除Order 时将同时删除它所对应的OrderItem对象。 
     * 而如果OrderItem还和其他的对象之间有级联关系，那么这样的操作会一直递归执行下去。cascade的值只能从CascadeType.PERSIST（级联新建）、 
     * CascadeType.REMOVE（级联删除）、CascadeType.REFRESH（级联刷新）、CascadeType.MERGE（级联更新）中选择一个或多个。 
     * 还有一个选择是使用CascadeType.ALL，表示选择全部四项。 
     *  
     * fatch: 可选择项包括：FetchType.EAGER 和FetchType.LAZY。前者表示关系类(本例是OrderItem类)在主类(本例是Order类)加载的时候 
     * 同时加载;后者表示关系类在被访问时才加载,默认值是FetchType. LAZY。 
     *  
     */
	//@OneToMany(mappedBy="record",cascade=CascadeType.ALL,fetch=FetchType.LAZY)
	@Transient
	public List<RecordImage> getDeepList() {
		return deepList;
	}

	public void setDeepList(List<RecordImage> deepList) {
		this.deepList = deepList;
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

	@Transient
	public String getWoundDressing1() {
		return woundDressing1;
	}

	public void setWoundDressing1(String woundDressing1) {
		this.woundDressing1 = woundDressing1;
	}

	@Transient
	public String getWoundDressing2() {
		return woundDressing2;
	}

	public void setWoundDressing2(String woundDressing2) {
		this.woundDressing2 = woundDressing2;
	}

}
