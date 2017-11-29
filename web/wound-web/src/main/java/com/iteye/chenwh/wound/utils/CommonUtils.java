package com.iteye.chenwh.wound.utils;

import java.util.ArrayList;
import java.util.List;

public class CommonUtils {
	
	public static final String PAGE_SIZE = "10";
	
	public static List<DicItem> dict_sex = new ArrayList<DicItem>();
	
	public static List<DicItem> dict_wound_type = new ArrayList<DicItem>();
	
	public static List<DicItem> dict_wound_measures = new ArrayList<DicItem>();
	
	public static List<DicItem> dict_wound_describe_clean = new ArrayList<DicItem>();
	
	public static List<DicItem> dict_wound_describe_skin = new ArrayList<DicItem>();
	
	public static List<DicItem> dict_wound_assess_prop = new ArrayList<DicItem>();
	
	public static List<DicItem> dict_wound_assess_infect = new ArrayList<DicItem>();
	
	public static List<DicItem> dict_wound_leachate_volume = new ArrayList<DicItem>();
	
	public static List<DicItem> dict_wound_leachate_color = new ArrayList<DicItem>();
	
	public static List<DicItem> dict_wound_leachate_smell = new ArrayList<DicItem>();
	
	public static List<DicItem> dict_wound_heal_all = new ArrayList<DicItem>();
	
	public static List<DicItem> dict_wound_heal_position = new ArrayList<DicItem>();
	
	public static List<DicItem> dict_wound_ache = new ArrayList<DicItem>();
	
	public static List<DicItem> dict_wound_dressing_type = new ArrayList<DicItem>();
	
	static {
		dict_sex.add(new DicItem("1", "男"));
		dict_sex.add(new DicItem("2", "女"));
		
		dict_wound_type.add(new DicItem("1", "外科伤口"));
		dict_wound_type.add(new DicItem("2", "压疮"));
		dict_wound_type.add(new DicItem("3", "糖尿病溃疡"));
		dict_wound_type.add(new DicItem("4", "静脉性溃疡"));
		dict_wound_type.add(new DicItem("5", "动脉性溃疡"));
		dict_wound_type.add(new DicItem("6", "癌性伤口"));
		dict_wound_type.add(new DicItem("7", "放射性损伤"));
		dict_wound_type.add(new DicItem("8", "瘘管"));
		dict_wound_type.add(new DicItem("0", "其他"));
		
		dict_wound_measures.add(new DicItem("1", "社区换药"));
		dict_wound_measures.add(new DicItem("2", "门诊换药"));
		dict_wound_measures.add(new DicItem("3", "住院治疗（内科）"));
		dict_wound_measures.add(new DicItem("4", "住院治疗（外科）"));
		
		dict_wound_describe_clean.add(new DicItem("1", "清洁伤口"));
		dict_wound_describe_clean.add(new DicItem("2", "污染伤口"));
		dict_wound_describe_clean.add(new DicItem("3", "感染伤口"));
		
		dict_wound_describe_skin.add(new DicItem("7", "完好无损"));
		dict_wound_describe_skin.add(new DicItem("1", "苍白"));
		dict_wound_describe_skin.add(new DicItem("2", "红斑"));
		dict_wound_describe_skin.add(new DicItem("3", "浸渍"));
		dict_wound_describe_skin.add(new DicItem("4", "色素沉着"));
		dict_wound_describe_skin.add(new DicItem("5", "水肿"));
		dict_wound_describe_skin.add(new DicItem("6", "坏死"));
		dict_wound_describe_skin.add(new DicItem("8", "青灰色"));
		dict_wound_describe_skin.add(new DicItem("9", "干燥起皮"));
        dict_wound_describe_skin.add(new DicItem("10", "硬化结茧"));
        dict_wound_describe_skin.add(new DicItem("11", "角化过度"));
		
		dict_wound_assess_prop.add(new DicItem("1", "急性伤口"));
		dict_wound_assess_prop.add(new DicItem("2", "慢性伤口"));
		
		dict_wound_assess_infect.add(new DicItem("1", "无"));
		dict_wound_assess_infect.add(new DicItem("2", "有"));
		
		dict_wound_leachate_volume.add(new DicItem("1", "少量"));
		dict_wound_leachate_volume.add(new DicItem("2", "中量"));
		dict_wound_leachate_volume.add(new DicItem("3", "大量"));
		
		dict_wound_leachate_color.add(new DicItem("1", "清澈"));
		dict_wound_leachate_color.add(new DicItem("2", "血水样"));
		dict_wound_leachate_color.add(new DicItem("3", "黄脓"));
		dict_wound_leachate_color.add(new DicItem("4", "绿黄脓"));
		dict_wound_leachate_color.add(new DicItem("5", "褐色"));
		
		dict_wound_leachate_smell.add(new DicItem("1", "无味"));
		dict_wound_leachate_smell.add(new DicItem("2", "有异味"));
		dict_wound_leachate_smell.add(new DicItem("3", "有臭味"));
		
		dict_wound_heal_all.add(new DicItem("01", "年龄"));
		dict_wound_heal_all.add(new DicItem("02", "营养不良"));
		dict_wound_heal_all.add(new DicItem("03", "糖尿病"));
		dict_wound_heal_all.add(new DicItem("04", "神经系统疾病"));
		dict_wound_heal_all.add(new DicItem("05", "免疫系统疾病"));
		dict_wound_heal_all.add(new DicItem("06", "凝血制剂不全"));
		dict_wound_heal_all.add(new DicItem("07", "药物"));
		dict_wound_heal_all.add(new DicItem("08", "激素"));
		
		dict_wound_heal_position.add(new DicItem("01", "感染"));
		dict_wound_heal_position.add(new DicItem("02", "结痂"));
		dict_wound_heal_position.add(new DicItem("03", "异物"));
		dict_wound_heal_position.add(new DicItem("04", "水肿"));
		dict_wound_heal_position.add(new DicItem("05", "干燥"));
		dict_wound_heal_position.add(new DicItem("06", "渗液过多"));
		
		dict_wound_ache.add(new DicItem("1", "1级"));
		dict_wound_ache.add(new DicItem("2", "2级"));
		dict_wound_ache.add(new DicItem("3", "3级"));
		dict_wound_ache.add(new DicItem("4", "4级"));
		dict_wound_ache.add(new DicItem("5", "5级"));
		dict_wound_ache.add(new DicItem("6", "6级"));
		dict_wound_ache.add(new DicItem("7", "7级"));
		dict_wound_ache.add(new DicItem("8", "8级"));
		dict_wound_ache.add(new DicItem("9", "9级"));
		dict_wound_ache.add(new DicItem("10", "10级"));
		
		dict_wound_dressing_type.add(new DicItem("1", "薄膜类敷料"));
		dict_wound_dressing_type.add(new DicItem("2", "水凝胶敷料"));
		dict_wound_dressing_type.add(new DicItem("3", "水胶体敷料"));
		dict_wound_dressing_type.add(new DicItem("4", "泡沫敷料"));
		dict_wound_dressing_type.add(new DicItem("5", "新型藻酸盐类敷料"));
		dict_wound_dressing_type.add(new DicItem("6", "药用类敷料"));
		dict_wound_dressing_type.add(new DicItem("7", "其他"));
	}

}
