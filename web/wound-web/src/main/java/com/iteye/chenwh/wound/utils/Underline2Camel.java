package com.iteye.chenwh.wound.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Underline2Camel {
	
	   /**
     * 下划线转驼峰法
     * @param line 源字符串
     * @param smallCamel 大小驼峰,是否为小驼峰
     * @return 转换后的字符串
     */
    public static String underline2Camel(String line,boolean smallCamel){
        if(line==null||"".equals(line)){
            return "";
        }
        StringBuffer sb=new StringBuffer();
        Pattern pattern=Pattern.compile("([A-Za-z\\d]+)(_)?");
        Matcher matcher=pattern.matcher(line);
        while(matcher.find()){
            String word=matcher.group();
            sb.append(smallCamel&&matcher.start()==0?Character.toLowerCase(word.charAt(0)):Character.toUpperCase(word.charAt(0)));
            int index=word.lastIndexOf('_');
            if(index>0){
                sb.append(word.substring(1, index).toLowerCase());
            }else{
                sb.append(word.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }
    /**
     * 驼峰法转下划线
     * @param line 源字符串
     * @return 转换后的字符串
     */
    public static String camel2Underline(String line){
        if(line==null||"".equals(line)){
            return "";
        }
        line=String.valueOf(line.charAt(0)).toUpperCase().concat(line.substring(1));
        StringBuffer sb=new StringBuffer();
        Pattern pattern=Pattern.compile("[A-Z]([a-z\\d]+)?");
        Matcher matcher=pattern.matcher(line);
        while(matcher.find()){
            String word=matcher.group();
            sb.append(word.toUpperCase());
            sb.append(matcher.end()==line.length()?"":"_");
        }
        return sb.toString();
    }
    public static void main(String[] args) {
        String line="patient_id,doctor_id,record_name,bed,diagnosis,department,is_operation,wound_type,wound_height,wound_width,wound_deep,wound_area,wound_volume,wound_time,wound_postion,wound_postion_x,wound_postion_y,wound_measures,wound_describe_clean,wound_describe_color,wound_describe_skin,wound_assess_prop,wound_assess_infect,wound_assess_infect_desc,wound_leachate_volume,wound_leachate_color,wound_leachate_smell,wound_heal_all,wound_heal_position,wound_doppler,wound_cta,wound_mr,wound_petct,wound_dressing,create_time,update_time,sync_time";
        String[] lines = line.split(",");
        for (String s :  lines) {
        	System.out.println(underline2Camel(s,true));
        }
    }

}
