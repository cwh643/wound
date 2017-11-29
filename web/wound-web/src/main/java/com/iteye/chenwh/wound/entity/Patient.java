package com.iteye.chenwh.wound.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

//JPA标识
@Entity
@Table(name = "patient_info")
public class Patient extends IdEntity {
	
	private String deviceId;//设备ID
	
    private String name;//姓名

    private String sex;//性别

    private Date birthday;//出生日期

    private String age;//年龄

    private String department;//科室

    private String bedNo;//床号

    private String inpatientNo;//住院号

    private String diagnosis;//诊断

    private String doctor;//医生姓名

    private Integer doctorId;//医生Id
    
	private Date createTime;
	
	private Date updateTime;
	
	private String uuid;//唯一ID

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getBedNo() {
		return bedNo;
	}

	public void setBedNo(String bedNo) {
		this.bedNo = bedNo;
	}

	public String getInpatientNo() {
		return inpatientNo;
	}

	public void setInpatientNo(String inpatientNo) {
		this.inpatientNo = inpatientNo;
	}

	public String getDoctor() {
		return doctor;
	}

	public void setDoctor(String doctor) {
		this.doctor = doctor;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}

	public Integer getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Integer doctorId) {
		this.doctorId = doctorId;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
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

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	
}
