package com.iteye.chenwh.wound.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesUtils {
	
	private static Logger log = LoggerFactory.getLogger(PropertiesUtils.class);
	
	private static Map<String, Properties> properties = new HashMap<String, Properties>();
	
	public static String getProp(String fileName, String key, String defaultValue) {
		Properties prop = properties.get(fileName);
		if (prop == null) {
			prop = loadProp(fileName);
			if (prop != null) {
				properties.put(fileName, prop);
			}
		}
		if (prop == null) {
			return "";
		}
		return prop.getProperty(key, defaultValue);
	}
	
	private static Properties loadProp(String fileName) {
		try {
			InputStream in = PropertiesUtils.class.getClassLoader().getResourceAsStream(fileName + ".properties");
			try {
				if (in != null) {
					Properties prop = new Properties();
					prop.load(in);
					return prop;
				}
			} finally {
				in.close();
			}
		} catch (IOException e) {
			log.error("读取资源文件"+fileName+".properties出错：", e);
		}
		return null;
	}

}
