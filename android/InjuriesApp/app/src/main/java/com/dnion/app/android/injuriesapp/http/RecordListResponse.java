package com.dnion.app.android.injuriesapp.http;

import com.dnion.app.android.injuriesapp.dao.RecordInfo;
import com.dnion.app.android.injuriesapp.dao.UserInfo;

import java.util.List;

/**
 * Created by 卫华 on 2017/6/17.
 */

public class RecordListResponse extends BaseResponse {

    private List<RecordInfo> list;

    public List<RecordInfo> getList() {
        return list;
    }

    public void setList(List<RecordInfo> list) {
        this.list = list;
    }
}
