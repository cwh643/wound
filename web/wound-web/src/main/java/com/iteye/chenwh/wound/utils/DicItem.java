package com.iteye.chenwh.wound.utils;

public class DicItem {
	
	private String code;//字典项目code
	
	private String name;//字典项目值
	
	public DicItem() {
		
	}
	
	public DicItem(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

