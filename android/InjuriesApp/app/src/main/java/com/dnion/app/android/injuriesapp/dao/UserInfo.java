package com.dnion.app.android.injuriesapp.dao;

import com.dnion.app.android.injuriesapp.utils.MapUtils;

/**
 * Created by 卫华 on 2017/6/18.
 */

public class UserInfo {

    private Integer id;

    private String loginName;//登录名

    private String name;//姓名

    private String password;//密码

    private String department;//科室

    public String toString() {
        return MapUtils.mGson.toJson(this);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String hospital;//医院

    private Integer type;//类型

    private boolean itemCheck;

    public boolean isItemCheck() {
        return itemCheck;
    }

    public void setItemCheck(boolean itemCheck) {
        this.itemCheck = itemCheck;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
