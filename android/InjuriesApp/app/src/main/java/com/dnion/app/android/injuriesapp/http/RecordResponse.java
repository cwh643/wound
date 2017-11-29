package com.dnion.app.android.injuriesapp.http;

import com.dnion.app.android.injuriesapp.dao.PatientInfo;
import com.dnion.app.android.injuriesapp.dao.RecordInfo;

/**
 * Created by 卫华 on 2017/6/17.
 */

public class RecordResponse extends BaseResponse {

    private RecordInfo record;

    public RecordInfo getRecord() {
        return record;
    }

    public void setRecord(RecordInfo record) {
        this.record = record;
    }
}
