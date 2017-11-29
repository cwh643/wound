package com.dnion.app.android.injuriesapp.dao;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.dnion.app.android.injuriesapp.InjApp;
import com.dnion.app.android.injuriesapp.utils.DBHelper;
import com.dnion.app.android.injuriesapp.utils.MD5;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by 卫华 on 2017/4/29.
 */

public class UserDao {

    private DBHelper db;

    public UserDao(Activity context) {
        db = ((InjApp)context.getApplication()).getDb();
    }

    public List<UserInfo> queryUserList(String name) {
        List<Map> userList = null;
        if (name == null || name.length() == 0) {
            String sql = " select * from user_info where type = 2  ";
            userList = db.queryListMap(sql, null);
        } else {
            String sql = " select * from user_info where user_type = 2 user_name like ? ";
            userList = db.queryListMap(sql, new String[] {"%" + name + "%"});
        }
        List<UserInfo> list = new ArrayList<>();
        if (userList != null) {
            for (Map map : userList) {
                UserInfo userInfo = warpObject(map);
                list.add(userInfo);
            }
        }
        return list;
    }

    public UserInfo queryUserByName(String name) {
        Map user = db.queryItemMap(" select * from user_info where login_name = ? and type >= 0 ", new String[] {name});
        UserInfo userInfo = warpObject(user);
        return userInfo;
    }

    private UserInfo warpObject(Map map) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId((Integer) map.get("id"));
        userInfo.setLoginName((String) map.get("login_name"));
        userInfo.setName((String) map.get("name"));
        userInfo.setPassword((String) map.get("password"));
        userInfo.setType((Integer) map.get("type"));
        userInfo.setDepartment((String) map.get("department"));
        userInfo.setHospital((String) map.get("hospital"));
        return userInfo;
    }

    public boolean insertUser(UserInfo userInfo) {
        String pass = MD5.encode("123456");
        boolean success = db.insert("user_info",
                new String[]{"login_name", "name", "password", "department", "hospital", "type", "sync_flag"},
                new String[]{userInfo.getLoginName(), userInfo.getName(), pass, userInfo.getDepartment(), userInfo.getHospital(), "2", "0"});
        return success;
    }

    public boolean deleteUser(Long id) {
        boolean success = db.delete("user_info", new String[]{"id"}, new String[]{"" + id});
        return success;
    }


    public List<UserInfo> queryUserList() {
        return queryUserList(null);
    }

    public boolean deleteUser(String ids) {
        String sql = "delete from user_info where user_type = 2 and id in (" + ids + ")";
        //Log.e("UserDao", sql);
        db.execSQL(sql);
        return true;
    }

    public boolean updateUserPw(UserInfo user) {
        String sql = " update user_info set user_pass = ? where id = ? ";
        db.execSQL(sql, new Object[]{user.getPassword(), user.getId()});
        return true;
    }
}