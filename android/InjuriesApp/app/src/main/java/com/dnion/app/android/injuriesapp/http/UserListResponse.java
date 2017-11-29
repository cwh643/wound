package com.dnion.app.android.injuriesapp.http;

import com.dnion.app.android.injuriesapp.dao.UserInfo;

import java.util.List;

/**
 * Created by 卫华 on 2017/6/17.
 */

public class UserListResponse extends BaseResponse {

    private List<UserInfo> list;

    public List<UserInfo> getList() {
        return list;
    }

    public void setList(List<UserInfo> list) {
        this.list = list;
    }
}
