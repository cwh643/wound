package com.dnion.app.android.injuriesapp.http;

import com.dnion.app.android.injuriesapp.dao.PatientInfo;

/**
 * Created by 卫华 on 2017/6/17.
 */

public class PatientResponse extends BaseResponse {

    private PatientInfo patient;

    public PatientInfo getPatient() {
        return patient;
    }

    public void setPatient(PatientInfo patient) {
        this.patient = patient;
    }
}
