/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.iteye.chenwh.wound.rest;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springside.modules.security.utils.Digests;
import org.springside.modules.utils.Encodes;

import com.alibaba.fastjson.JSON;
import com.iteye.chenwh.wound.entity.ArchivesRecord;
import com.iteye.chenwh.wound.entity.Patient;
import com.iteye.chenwh.wound.entity.RecordImage;
import com.iteye.chenwh.wound.entity.User;
import com.iteye.chenwh.wound.repository.RecordImageDao;
import com.iteye.chenwh.wound.service.ArchivesRecordService;
import com.iteye.chenwh.wound.service.PatientService;
import com.iteye.chenwh.wound.service.RecordImageService;
import com.iteye.chenwh.wound.service.account.AccountService;
import com.iteye.chenwh.wound.utils.PropertiesUtils;
import com.iteye.chenwh.wound.utils.XZip;

/**
 * Task的Restful API的Controller.
 * 
 * @author calvin
 */
@Controller
@RequestMapping(value = "/api/wap")
public class WapController {

	private static Logger log = LoggerFactory.getLogger(WapController.class);

	@Autowired
	private AccountService accountService;
	
	@Autowired
	private PatientService patientService;
	
	@Autowired
	private ArchivesRecordService recordService;
	
	@Autowired
	private RecordImageService recordImageService;
	
	private String PATH = PropertiesUtils.getProp("application", "record.file.path", "D:/temp/");

	/**
	 *登录
	 */
	@RequestMapping(value="/login", method = RequestMethod.POST)
	@ResponseBody
	public ModelMap login(String username, String password) {
		log.debug("login param:"+ username + "," + password );
		ModelMap map = new ModelMap();
		try {
			User user = accountService.findUserByLoginName(username);
			if (user == null) {
				map.addAttribute("success", false);
				map.addAttribute("message", "用户不存在");
				return map;
			}
			byte[] salt = Encodes.decodeHex(user.getSalt());
			byte[] hashPassword = Digests.sha1(password.getBytes(), salt, AccountService.HASH_INTERATIONS);
			String passwd = Encodes.encodeHex(hashPassword);
			if (user.getPassword().equals(passwd)) {
				map.addAttribute("success", true);
				map.addAttribute("user", user);
				map.addAttribute("message", "登录成功");
			} else {
				map.addAttribute("success", false);
				map.addAttribute("message", "用户名，密码不正确");
			}
		} catch (Exception e) {
			map.addAttribute("success", false);
			map.addAttribute("message", "登录出错");
			log.error("登录出错", e);
		}
		return map;
	}
	
	/**
	 * 修改密码
	 */
	@RequestMapping(value="/modifyPw", method = RequestMethod.POST)
	@ResponseBody
	public ModelMap modifyPw(Long docId, String password, String newpassword) {
		log.debug("modifyPw param:"+ password + "," + newpassword );
		ModelMap map = new ModelMap();
		try {
			User user = accountService.getUser(docId);
			if (user == null) {
				map.addAttribute("success", false);
				map.addAttribute("message", "用户不存在");
				return map;
			}
			byte[] salt = Encodes.decodeHex(user.getSalt());
			byte[] hashPassword = Digests.sha1(password.getBytes(), salt, AccountService.HASH_INTERATIONS);
			String passwd = Encodes.encodeHex(hashPassword);
			if (!user.getPassword().equals(passwd)) {
				map.addAttribute("success", false);
				map.addAttribute("message", "旧密码不正确");
				return map;
			}
			
			user.setPlainPassword(newpassword);
			accountService.updateUser(user);
			map.addAttribute("success", true);
			map.addAttribute("message", "密码修改成功");
		} catch (Exception e) {
			map.addAttribute("success", false);
			map.addAttribute("message", "登录出错");
			log.error("登录出错", e);
		}
		return map;
	}

	/**
	 *医生列表
	 */
	@RequestMapping(value="/docList", method = RequestMethod.GET)
	@ResponseBody
	public ModelMap docList() {
		ModelMap map = new ModelMap();
		try {
			List<User> list = accountService.getDocList();
			map.addAttribute("list", list);
			map.addAttribute("success", true);
		} catch (Exception e) {
			map.addAttribute("success", false);
			map.addAttribute("message", "查询医生列表出错");
			log.error("查询医生列表出错", e);
		}
		return map;
	}
	
	/**
	 *患者列表
	 */
	@RequestMapping(value="/patientList", method = RequestMethod.GET)
	@ResponseBody
	public ModelMap patientList(Integer docId) {
		ModelMap map = new ModelMap();
		try {
			List<Patient> list = patientService.findPtientList(docId);
			map.addAttribute("list", list);
			map.addAttribute("success", true);
		} catch (Exception e) {
			map.addAttribute("success", false);
			map.addAttribute("message", "查询患者列表信息出错");
			log.error("查询患者列表信息出错", e);
		}
		return map;
	}

	/**
	 *记录列表
	 */
	@RequestMapping(value="/recordList", method = RequestMethod.GET)
	@ResponseBody
	public ModelMap recordList(String inpatientNo) {
		ModelMap map = new ModelMap();
		try {
			List<ArchivesRecord> list = recordService.findByInpatientNo(inpatientNo);
			map.addAttribute("list", list);
			map.addAttribute("success", true);
		} catch (Exception e) {
			map.addAttribute("success", false);
			map.addAttribute("message", "查询创伤记录列表信息出错");
			log.error("查询创伤记录列表信息出错", e);
		}
		return map;
	}
	
	/**
	 *患者查询
	 */
	@RequestMapping(value="/queryPatient", method = RequestMethod.GET)
	@ResponseBody
	public ModelMap queryPatient(String inpatientNo) {
		ModelMap map = new ModelMap();
		try {
			Patient patient = patientService.findPatient(inpatientNo);
			map.addAttribute("patient", patient);
			map.addAttribute("success", true);
			return map;
		} catch (Exception e) {
			map.addAttribute("success", false);
			map.addAttribute("message", "查询患者信息出错");
			log.error("查询患者记录出错", e);
		}
		return map;
	}
	
	/**
	 *患者创建
	 */
	@RequestMapping(value="/createPatient", method = RequestMethod.POST)
	@ResponseBody
	public ModelMap createPatient(Patient patient) {
		ModelMap map = new ModelMap();
		try {
			String inpatientNo = patient.getInpatientNo();
			if (StringUtils.isEmpty(inpatientNo)) {
				map.addAttribute("message", "患者就诊号不能为空");
				map.addAttribute("success", false);
				return map;
			}
			String name = patient.getName();
			if (StringUtils.isEmpty(name)) {
				map.addAttribute("message", "患者姓名不能为空");
				map.addAttribute("success", false);
				return map;
			}
			Patient dbPatient = patientService.findPatient(inpatientNo);
			if (dbPatient != null) {
				map.addAttribute("message", "患者信息已经存在");
				map.addAttribute("success", false);
				return map;
			}
			
			patientService.savePatient(patient);
			map.addAttribute("success", true);
			map.addAttribute("message", "患者信息创建成功");
		} catch (Exception e) {
			map.addAttribute("success", false);
			map.addAttribute("message", "查询创伤记录信息出错");
			log.error("查询患者列表信息", e);
		}
		return map;
	}
	
	/**
	 *患者更新
	 */
	@RequestMapping(value="/updatePatient", method = RequestMethod.POST)
	@ResponseBody
	public ModelMap updateRecord(Patient patient) {
		ModelMap map = new ModelMap();
		try {
			String inpatientNo = patient.getInpatientNo();
			if (StringUtils.isEmpty(inpatientNo)) {
				map.addAttribute("message", "患者就诊号不能为空");
				map.addAttribute("success", false);
				return map;
			}
			String name = patient.getName();
			if (StringUtils.isEmpty(name)) {
				map.addAttribute("message", "患者姓名不能为空");
				map.addAttribute("success", false);
				return map;
			}
			Patient dbPatient = patientService.findPatient(inpatientNo);
			if (dbPatient == null) {
				map.addAttribute("message", "患者信息不存在");
				map.addAttribute("success", false);
				return map;
			}
			patient.setId(dbPatient.getId());
			patientService.savePatient(patient);
			map.addAttribute("success", true);
			map.addAttribute("message", "患者信息创建成功");
		} catch (Exception e) {
			map.addAttribute("success", false);
			map.addAttribute("message", "患者信息创建出错");
			log.error("患者信息创建出错", e);
		}
		return map;
	}
	
	/**
	 *记录查询
	 */
	@RequestMapping(value="/queryRecord", method = RequestMethod.GET)
	@ResponseBody
	public ModelMap queryRecord(String inpatientNo, String recordTime) {
		ModelMap map = new ModelMap();
		try {
			ArchivesRecord dbRecord = recordService.findRecord(inpatientNo, recordTime);
			map.addAttribute("record", dbRecord);
			map.addAttribute("success", true);
			return map;
		} catch (Exception e) {
			map.addAttribute("success", false);
			map.addAttribute("message", "查询创伤记录信息出错");
			log.error("查询创伤记录出错", e);
		}
		return map;
	}
	
    @RequestMapping(value="/syncRecordInfo",method=RequestMethod.POST)
    @ResponseBody
    private ModelMap syncRecordInfo(String recordInfo , String deviceId, @RequestParam("record") MultipartFile file, HttpServletRequest request) { 
		ModelMap map = new ModelMap();
		try {
	    	ArchivesRecord record = JSON.parseObject(recordInfo, ArchivesRecord.class);
	    	//保存患者信息
	    	Patient patient = record.getPatientInfo();
	    	//String inpatientNo = patient.getInpatientNo();
	    	patient.setDeviceId(deviceId);
	    	String patientUuid = patient.getUuid();
	    	if (patientUuid.equals("" + patient.getId())) {
	    		patientUuid = deviceId + "-" + patientUuid;
	    		patient.setUuid(patientUuid);
	    	}
	    	Patient dbPatient = patientService.findPatientByUuid(patientUuid);
	    	if (dbPatient != null) {
	    		patient.setId(dbPatient.getId());
	    		patient.setUpdateTime(new Date());
	    	} else {
	    		patient.setId(null);
	    		patient.setCreateTime(new Date());
	    	}
	    	patientService.savePatient(patient);
	    	
	    	//保存伤口档案
	    	Long deviceRecordId = record.getId();
	    	record.setDeviceId(deviceId);
	    	record.setRecordId(deviceRecordId);
	    	String recordUuid = record.getUuid();
	    	if (recordUuid.equals("" + record.getId())) {
	    		recordUuid = deviceId + "-" + recordUuid;
	    		record.setUuid(recordUuid);
	    	}
	    	ArchivesRecord dbRecord = recordService.findRecord(recordUuid);
	    	if (dbRecord != null) {
	    		record.setId(dbRecord.getId());
	    		record.setUpdateTime(new Date());
	    	} else {
	    		record.setId(null);
	    		record.setCreateTime(new Date());
	    	}
	    	recordService.saveRecord(record);
	    	
	    	Long recordId = record.getId();
	    	List<RecordImage> images = record.getDeepList();
	    	if (images != null && images.size() > 0) {
	    		recordImageService.deleteByRecordId(recordId);
	    		for (RecordImage image : images) {
	    			image.setId(null);
	    			image.setRecordId(recordId);
	    			String path = image.getImagePath();
	    			if (path != null) {
	    				path = path.replace("/storage/emulated/0/wound/", "");
	    				image.setImagePath(path);
	    			}
	    			recordImageService.save(image);
	    		}
	    	}
	    	
	    	
	    	//保存伤口图片文件
	    	
	        //获得物理路径webapp所在路径  
	    	//Long recordId = record.getId();
	    	//String path = PATH+ deviceId;
	    	String path = PATH;
	        if (!file.isEmpty()) {
	            //获得文件类型  
	            String contentType=file.getContentType();  
	            //获得文件后缀名称  
	            String suffix=contentType.substring(contentType.indexOf("/")+1);  
	            String fileName = recordUuid+"." + suffix;
	            
	            File parent = new File(path);
	            if (!parent.exists()) {
	            	parent.mkdirs();
	            }
	            File tofile = new File(path, fileName);
	            file.transferTo(tofile);
	            
	            if (tofile.exists()) {
	            	//String uuid = record.getUuid();
	            	File unZipfile = new File(path);
	            	if (recordUuid.contains(deviceId)) {
	            		unZipfile = new File(path, deviceId);
	            	}
	            	XZip.UnZipFolder(tofile.getAbsolutePath(), unZipfile.getAbsolutePath());
	            	tofile.delete();
	            }
	        }
	        map.addAttribute("message", "同步上传创伤记录信息出错");
			map.addAttribute("success", true);
			return map;
	} catch (Exception e) {
		map.addAttribute("success", false);
		map.addAttribute("message", "同步上传创伤记录信息出错");
		log.error("同步上传创伤记录出错", e);
	}
	return map;
    } 
	
	/**
	 *记录保存
	 */
	@RequestMapping(value="/saveRecord", method = RequestMethod.POST)
	@ResponseBody
	public ModelMap saveRecord(ArchivesRecord record) {
		ModelMap map = new ModelMap();
		try {
			String inpatientNo = record.getInpatientNo();
			if (StringUtils.isEmpty(inpatientNo)) {
				map.addAttribute("message", "患者就诊号不能为空");
				map.addAttribute("success", false);
				return map;
			}
			String recordTime = record.getRecordTime();
			if (StringUtils.isEmpty(recordTime)) {
				map.addAttribute("message", "就诊日期不能为空");
				map.addAttribute("success", false);
				return map;
			}
			ArchivesRecord dbRecord = recordService.findRecord(inpatientNo, recordTime);
			if (dbRecord != null) {
				record.setId(dbRecord.getId());
			}
			recordService.saveRecord(record);
			map.addAttribute("success", true);
		} catch (Exception e) {
			map.addAttribute("success", false);
			map.addAttribute("message", "保存创伤记录信息出错");
			log.error("保存创伤记录信息出错", e);
		}
		return map;
	}
	
}
