package com.iteye.chenwh.wound.web;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import com.iteye.chenwh.wound.native_utils.CommonNativeUtils;
import com.iteye.chenwh.wound.opencv.DeepImageUtils;
import com.iteye.chenwh.wound.opencv.Image;
import com.iteye.chenwh.wound.opencv.ImageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springside.modules.web.Servlets;

import com.iteye.chenwh.wound.entity.ArchivesRecord;
import com.iteye.chenwh.wound.entity.Patient;
import com.iteye.chenwh.wound.entity.RecordImage;
import com.iteye.chenwh.wound.service.ArchivesRecordService;
import com.iteye.chenwh.wound.service.PatientService;
import com.iteye.chenwh.wound.service.RecordImageService;
import com.iteye.chenwh.wound.utils.CommonUtils;
import sun.misc.BASE64Decoder;

@Controller
@RequestMapping(value = "/archivesRecord")
public class ArchivesRecordController {
	
	private static Logger log = LoggerFactory.getLogger(ArchivesRecordController.class);
	
	@Autowired
	private ArchivesRecordService service;
	
	@Autowired
	private PatientService patientService;
	
	@Autowired
	private RecordImageService recordImageService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String list(@RequestParam(value = "page", defaultValue = "1") int pageNumber,
			@RequestParam(value = "page.size", defaultValue = CommonUtils.PAGE_SIZE) int pageSize,
			@RequestParam(value = "sortType", defaultValue = "auto") String sortType, Model model,
			ServletRequest request) {
		
		Map<String, Object> searchParams = Servlets.getParametersStartingWith(request, "search_");
		Page<ArchivesRecord> records = service.getRecordList(searchParams, pageNumber, pageSize, sortType);
		//List<User> users = accountService.getAllUser();
		model.addAttribute("records", records);
		model.addAttribute("menu", 1);
		// 将搜索条件编码成字符串，用于排序，分页的URL
		model.addAttribute("searchParams", Servlets.encodeParameterStringWithPrefix(searchParams, "search_"));
		return "archivesRecord/list";
	}
	
	@RequestMapping(value = "details", method = RequestMethod.GET)
	public String details(ArchivesRecord query, Model model) {
		Patient patient = patientService.findPatient(query.getInpatientNo());
		model.addAttribute("patient", patient);
		
		ArchivesRecord dbRecord = service.findRecord(query.getInpatientNo(), query.getRecordTime());
		String woundDressing = dbRecord.getWoundDressing();
		if (woundDressing != null && woundDressing.contains("@")) {
			String[] dressings = woundDressing.split("@");
			if (dressings.length > 1) {
				dbRecord.setWoundDressing1(dressings[0]);
				dbRecord.setWoundDressing2(dressings[1]);
			} else {
				dbRecord.setWoundDressing1(dressings[0]);
			}
		}
		model.addAttribute("record", dbRecord);
		
		List<RecordImage> images = recordImageService.findByRecordId(dbRecord.getId());
		model.addAttribute("images", images);
		
		model.addAttribute("dict_wound_describe_clean", CommonUtils.dict_wound_describe_clean);
		model.addAttribute("dict_wound_measures", CommonUtils.dict_wound_measures);
		model.addAttribute("dict_wound_type", CommonUtils.dict_wound_type);
		model.addAttribute("dict_sex", CommonUtils.dict_sex);
		model.addAttribute("dict_wound_describe_skin", CommonUtils.dict_wound_describe_skin);
		model.addAttribute("dict_wound_assess_prop", CommonUtils.dict_wound_assess_prop);
		model.addAttribute("dict_wound_assess_infect", CommonUtils.dict_wound_assess_infect);
		model.addAttribute("dict_wound_leachate_volume", CommonUtils.dict_wound_leachate_volume);
		model.addAttribute("dict_wound_leachate_color", CommonUtils.dict_wound_leachate_color);
		model.addAttribute("dict_wound_leachate_smell", CommonUtils.dict_wound_leachate_smell);
		model.addAttribute("dict_wound_heal_all", CommonUtils.dict_wound_heal_all);
		model.addAttribute("dict_wound_heal_position", CommonUtils.dict_wound_heal_position);
		model.addAttribute("dict_wound_ache", CommonUtils.dict_wound_ache);
		model.addAttribute("dict_wound_dressing_type", CommonUtils.dict_wound_dressing_type);
		
		
		return "archivesRecord/details";
	}
	
	@RequestMapping(value = "queryHistroy", method = RequestMethod.GET)
	@ResponseBody
	public ModelMap queryHistroy(String inpatientNo) {
		ModelMap map = new ModelMap();
		try {
			Map<String, Object> lineData = new HashMap<String, Object>();
			Map<String, Object> barData = new HashMap<String, Object>();
			List<ArchivesRecord> histroys = service.findRecordHistroy(inpatientNo);

			if (histroys != null && histroys.size() > 0) {
				List<String> categories = new ArrayList<String>();
				List<Double> widthDatas = new ArrayList<Double>();
				List<Double> heightDatas = new ArrayList<Double>();
				List<Double> redDatas = new ArrayList<Double>();
				List<Double> blackDatas = new ArrayList<Double>();
				List<Double> yellowDatas = new ArrayList<Double>();
				for (ArchivesRecord record : histroys) {
					String recordTime = record.getRecordTime();
					categories.add(recordTime.substring(5, 10));
					widthDatas.add(formatFloatValue(record.getWoundWidth()));
					heightDatas.add(formatFloatValue(record.getWoundHeight()));
					redDatas.add(formatFloatValue(record.getWoundColorRed()));
					blackDatas.add(formatFloatValue(record.getWoundColorBlack()));
					yellowDatas.add(formatFloatValue(record.getWoundColorYellow()));
				}
				
				List<Map<String, Object>> lineSeries = new ArrayList<Map<String, Object>>();
				lineData.put("categories", categories);
				lineData.put("series", lineSeries);
				
				Map<String, Object> widthMap = new HashMap<String, Object>();
				widthMap.put("name", "宽度");
				widthMap.put("data", widthDatas);
				lineSeries.add(widthMap);
				
				Map<String, Object> heigthMap = new HashMap<String, Object>();
				heigthMap.put("name", "长度");
				heigthMap.put("data", heightDatas);
				lineSeries.add(heigthMap);
				
				List<Map<String, Object>> barSeries = new ArrayList<Map<String, Object>>();
				barData.put("categories", categories);
				barData.put("series", barSeries);
				
				Map<String, Object> redMap = new HashMap<String, Object>();
				redMap.put("name", "红色组织");
				redMap.put("data", redDatas);
				barSeries.add(redMap);
				
				Map<String, Object> blackMap = new HashMap<String, Object>();
				blackMap.put("name", "黑色组织");
				blackMap.put("data", blackDatas);
				barSeries.add(blackMap);
				
				Map<String, Object> yellowMap = new HashMap<String, Object>();
				yellowMap.put("name", "黄色组织");
				yellowMap.put("data", yellowDatas);
				barSeries.add(yellowMap);
			}
			
			map.addAttribute("lineData", lineData);
			map.addAttribute("barData", barData);
			map.addAttribute("success", true);
		} catch (Exception e) {
			map.addAttribute("success", false);
			map.addAttribute("message", "查询历史信息出错");
			log.error("查询历史信息出错", e);
		}
		return map;
	}
	
	private static double formatFloatValue(Double f) {
		if (f == null) {
			return 0;
		}
		return f.doubleValue();
	}

	@RequestMapping(value = "takePhoto", method = RequestMethod.GET)
	public String takePhoto(String imageUrl, Model model) {
		//Patient patient = patientService.findPatient(query.getInpatientNo());
		//model.addAttribute("patient", patient);
		String[] imageInfo = imageUrl.split("/");
		if (imageInfo.length >= 6) {
			model.addAttribute("uid", imageInfo[3]);
			model.addAttribute("date", imageInfo[5]);
		}
		model.addAttribute("imageUrl", imageUrl);
		return "archivesRecord/takePhoto";
	}

	@RequestMapping(value = "computerArea", method = RequestMethod.POST)
	@ResponseBody
	public ModelMap computerArea(String uuid, String date, String imageData, HttpServletRequest request) {
		ModelMap map = new ModelMap();
		try {
			String path = request.getSession().getServletContext().getRealPath("/");
			File webRoot = new File(path).getParentFile();
			DeepImageUtils utils = new DeepImageUtils(webRoot, uuid, date);

			BASE64Decoder decoder = new BASE64Decoder();
			byte[] b = decoder.decodeBuffer(imageData);
			ByteArrayInputStream bais = new ByteArrayInputStream(b);
			BufferedImage bi = ImageIO.read(bais);
			//File w2 = new File("D:/tmp/test.png");
			//ImageIO.write(bi, "png", w2);
			Image image = ImageUtils.createImage(bi);
			Map<String, String> areaMap = utils.calcArea(image);
			map.addAttribute("success", true);
			map.addAttribute("areaMap", areaMap);
		} catch (Exception e) {
			map.addAttribute("success", false);
			map.addAttribute("message", "获取面积信息出错");
			log.error("获取面积信息出错", e);
		}
		return map;
	}

	@RequestMapping(value = "computerDeep", method = RequestMethod.POST)
	@ResponseBody
	public ModelMap computerDeep(String uuid, String date, String points, HttpServletRequest request) {
		ModelMap map = new ModelMap();
		try {
			ArchivesRecord patient = service.findRecord(uuid);
			if (patient == null) {
				map.addAttribute("success", false);
				map.addAttribute("message", "创伤记录不存在");
				return map;
			}
			double result = 0;
			String path = request.getSession().getServletContext().getRealPath("/");
			File webRoot = new File(path).getParentFile();
			DeepImageUtils utils = new DeepImageUtils(webRoot, uuid, date);
			String[] pointList = points.split(",");
			if (pointList != null && pointList.length == 4) {
				result = utils.calcDeep(Integer.parseInt(pointList[0]),Integer.parseInt(pointList[1]),
						Integer.parseInt(pointList[2]),Integer.parseInt(pointList[3]));
			}
			//System.out.println( "chenwh:" + System.getProperty("java.library.path"));
			//int r = CommonNativeUtils.cvFitPlane(50, 20);

			map.addAttribute("success", true);
			map.addAttribute("length", new DecimalFormat("#.00").format(result));
		} catch (Exception e) {
			map.addAttribute("success", false);
			map.addAttribute("message", "获取长度信息出错");
			log.error("获取长度信息出错", e);
		}
		return map;
	}

	@RequestMapping(value = "computerLength", method = RequestMethod.POST)
	@ResponseBody
	public ModelMap computerLength(String uuid, String date, String points, HttpServletRequest request) {
		ModelMap map = new ModelMap();
		try {
			ArchivesRecord patient = service.findRecord(uuid);
			if (patient == null) {
				map.addAttribute("success", false);
				map.addAttribute("message", "创伤记录不存在");
				return map;
			}
			double result = 0;
			String path = request.getSession().getServletContext().getRealPath("/");
			File webRoot = new File(path).getParentFile();
			DeepImageUtils utils = new DeepImageUtils(webRoot, uuid, date);
			String[] pointList = points.split(",");
			if (pointList != null && pointList.length == 4) {
				result = utils.calcDistince(Integer.parseInt(pointList[0]),Integer.parseInt(pointList[1]),
						Integer.parseInt(pointList[2]),Integer.parseInt(pointList[3]));
			}
			//System.out.println( "chenwh:" + System.getProperty("java.library.path"));
			//int r = CommonNativeUtils.cvFitPlane(50, 20);

			map.addAttribute("success", true);
			map.addAttribute("length", new DecimalFormat("#.00").format(result));
		} catch (Exception e) {
			map.addAttribute("success", false);
			map.addAttribute("message", "获取长度信息出错");
			log.error("获取长度信息出错", e);
		}
		return map;
	}
	
	@RequestMapping(value = "updatePatient", method = RequestMethod.POST)
	@ResponseBody
	public ModelMap updatePatient(Patient patient) {
		ModelMap map = new ModelMap();
		try {
			patientService.savePatient(patient);
			map.addAttribute("success", true);
		} catch (Exception e) {
			map.addAttribute("success", false);
			map.addAttribute("message", "更新信息出错");
			log.error("更新信息出错", e);
		}
		return map;
	}
	
	@RequestMapping(value = "updateRecord1", method = RequestMethod.POST)
	@ResponseBody
	public ModelMap updateRecord1(ArchivesRecord record) {
		ModelMap map = new ModelMap();
		try {
			ArchivesRecord dbRecord = service.findRecordById(record.getId());
			if (dbRecord == null) {
				map.addAttribute("success", false);
				map.addAttribute("message", "要更新的记录不存在");
				return map;
			}
			dbRecord.setWoundType(record.getWoundType());
			dbRecord.setWoundHeight(record.getWoundHeight());
			dbRecord.setWoundWidth(record.getWoundWidth());
			dbRecord.setWoundDeep(record.getWoundDeep());
			dbRecord.setWoundArea(record.getWoundArea());
			dbRecord.setWoundVolume(record.getWoundVolume());
			dbRecord.setWoundTime(record.getWoundTime());
			dbRecord.setWoundMeasures(record.getWoundMeasures());
			service.saveRecord(dbRecord);
			map.addAttribute("success", true);
		} catch (Exception e) {
			map.addAttribute("success", false);
			map.addAttribute("message", "更新信息出错");
			log.error("更新信息出错", e);
		}
		return map;
	}
	
	@RequestMapping(value = "updateRecord2", method = RequestMethod.POST)
	@ResponseBody
	public ModelMap updateRecord2(ArchivesRecord record) {
		ModelMap map = new ModelMap();
		try {
			ArchivesRecord dbRecord = service.findRecordById(record.getId());
			if (dbRecord == null) {
				map.addAttribute("success", false);
				map.addAttribute("message", "要更新的记录不存在");
				return map;
			}
			dbRecord.setWoundDescribeClean(record.getWoundDescribeClean());
			dbRecord.setWoundColorRed(record.getWoundColorRed());
			dbRecord.setWoundColorYellow(record.getWoundColorYellow());
			dbRecord.setWoundColorBlack(record.getWoundColorBlack());
			dbRecord.setWoundDescribeSkin(record.getWoundDescribeSkin());
			service.saveRecord(dbRecord);
			map.addAttribute("success", true);
		} catch (Exception e) {
			map.addAttribute("success", false);
			map.addAttribute("message", "更新信息出错");
			log.error("更新信息出错", e);
		}
		return map;
	}
	
	@RequestMapping(value = "updateRecord3", method = RequestMethod.POST)
	@ResponseBody
	public ModelMap updateRecord3(ArchivesRecord record) {
		ModelMap map = new ModelMap();
		try {
			ArchivesRecord dbRecord = service.findRecordById(record.getId());
			if (dbRecord == null) {
				map.addAttribute("success", false);
				map.addAttribute("message", "要更新的记录不存在");
				return map;
			}
			dbRecord.setWoundAssessProp(record.getWoundAssessProp());
			dbRecord.setWoundAssessInfect(record.getWoundAssessInfect());
			dbRecord.setWoundAssessInfectDesc(record.getWoundAssessInfectDesc());
			service.saveRecord(dbRecord);
			map.addAttribute("success", true);
		} catch (Exception e) {
			map.addAttribute("success", false);
			map.addAttribute("message", "更新信息出错");
			log.error("更新信息出错", e);
		}
		return map;
	}
	
	@RequestMapping(value = "updateRecord4", method = RequestMethod.POST)
	@ResponseBody
	public ModelMap updateRecord4(ArchivesRecord record) {
		ModelMap map = new ModelMap();
		try {
			ArchivesRecord dbRecord = service.findRecordById(record.getId());
			if (dbRecord == null) {
				map.addAttribute("success", false);
				map.addAttribute("message", "要更新的记录不存在");
				return map;
			}
			dbRecord.setWoundLeachateVolume(record.getWoundLeachateVolume());
			dbRecord.setWoundLeachateColor(record.getWoundLeachateColor());
			dbRecord.setWoundLeachateSmell(record.getWoundLeachateSmell());
			service.saveRecord(dbRecord);
			map.addAttribute("success", true);
		} catch (Exception e) {
			map.addAttribute("success", false);
			map.addAttribute("message", "更新信息出错");
			log.error("更新信息出错", e);
		}
		return map;
	}
	
	@RequestMapping(value = "updateRecord5", method = RequestMethod.POST)
	@ResponseBody
	public ModelMap updateRecord5(ArchivesRecord record) {
		ModelMap map = new ModelMap();
		try {
			ArchivesRecord dbRecord = service.findRecordById(record.getId());
			if (dbRecord == null) {
				map.addAttribute("success", false);
				map.addAttribute("message", "要更新的记录不存在");
				return map;
			}
			dbRecord.setWoundHealAll(record.getWoundHealAll());
			dbRecord.setWoundHealPosition(record.getWoundHealPosition());
			service.saveRecord(dbRecord);
			map.addAttribute("success", true);
		} catch (Exception e) {
			map.addAttribute("success", false);
			map.addAttribute("message", "更新信息出错");
			log.error("更新信息出错", e);
		}
		return map;
	}
	
	@RequestMapping(value = "updateRecord8", method = RequestMethod.POST)
	@ResponseBody
	public ModelMap updateRecord8(ArchivesRecord record) {
		ModelMap map = new ModelMap();
		try {
			record.setWoundDressing(record.getWoundDressing1() + "@" + record.getWoundDressing2());
			ArchivesRecord dbRecord = service.findRecordById(record.getId());
			if (dbRecord == null) {
				map.addAttribute("success", false);
				map.addAttribute("message", "要更新的记录不存在");
				return map;
			}
			dbRecord.setWoundDressing(record.getWoundDressing());
			dbRecord.setWoundDressingType(record.getWoundDressingType());
			service.saveRecord(dbRecord);
			map.addAttribute("success", true);
		} catch (Exception e) {
			map.addAttribute("success", false);
			map.addAttribute("message", "更新信息出错");
			log.error("更新信息出错", e);
		}
		return map;
	}
	
}
