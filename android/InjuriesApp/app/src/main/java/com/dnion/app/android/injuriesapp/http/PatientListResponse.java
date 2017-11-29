package com.dnion.app.android.injuriesapp.http;

import com.dnion.app.android.injuriesapp.dao.PatientInfo;

import java.util.List;

/**
 * Created by 卫华 on 2017/6/17.
 */

public class PatientListResponse extends BaseResponse {

    private List<PatientInfo> list;

    public List<PatientInfo> getList() {
        return list;
    }

    public void setList(List<PatientInfo> list) {
        this.list = list;
    }
}
