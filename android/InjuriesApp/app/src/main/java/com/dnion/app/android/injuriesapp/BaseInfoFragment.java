package com.dnion.app.android.injuriesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.dnion.app.android.injuriesapp.dao.PatientDao;
import com.dnion.app.android.injuriesapp.dao.PatientInfo;
import com.dnion.app.android.injuriesapp.dao.RecordInfo;
import com.dnion.app.android.injuriesapp.utils.BaseTextWatcher;
import com.dnion.app.android.injuriesapp.utils.CommonUtil;
import com.dnion.app.android.injuriesapp.utils.SharedPreferenceUtil;
import com.dnion.app.android.injuriesapp.utils.ToastUtil;

/**
 * A fragment representing a single Item detail screen.
 * on handsets.
 */
public class BaseInfoFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String TAG = "base_info_fragment";

    private MainActivity mActivity;

    private EditText txt_patient_name;

    private EditText txt_inpatient_no;

    private PatientDao patientDao;

    public static BaseInfoFragment createInstance() {
        BaseInfoFragment fragment = new BaseInfoFragment();

        Bundle bundle = new Bundle();
        //bundle.putLong(ARGUMENT_USER_ID, userId);
        //bundle.putString(ARGUMENT_USER_NAME, userName);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) BaseInfoFragment.this.getActivity();
        patientDao = new PatientDao(mActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.base_info_fragment, container, false);
        configView(rootView);
        return rootView;
    }

    private void configView(View rootView) {
        final PatientInfo patientInfo = mActivity.getPatientInfo();

        txt_patient_name = (EditText) rootView.findViewById(R.id.txt_patient_name);
        txt_patient_name.setText(patientInfo.getName());
        txt_patient_name.addTextChangedListener(new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                patientInfo.setName(s.toString());
            }
        });

        EditText txt_patient_age = (EditText) rootView.findViewById(R.id.txt_patient_age);
        txt_patient_age.setText(patientInfo.getAge());
        txt_patient_age.addTextChangedListener(new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                patientInfo.setAge(s.toString());
            }
        });

        EditText txt_patient_bed = (EditText) rootView.findViewById(R.id.txt_patient_bed);
        txt_patient_bed.setText(patientInfo.getBedNo());
        txt_patient_bed.addTextChangedListener(new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                patientInfo.setBedNo(s.toString());
            }
        });

        txt_inpatient_no = (EditText) rootView.findViewById(R.id.txt_inpatient_no);
        txt_inpatient_no.setText(patientInfo.getInpatientNo());
        txt_inpatient_no.addTextChangedListener(new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                patientInfo.setInpatientNo(s.toString());
            }
        });

        Button btn_scan = (Button) rootView.findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开扫码画面
                Intent intent = new Intent(mActivity, CaptureActivity.class);
                //intent.putExtra("isCustomerCode", "" + isCustomerCode);
                //intent.putExtra("products", getProducts());
                startActivityForResult(intent, 1001);
            }
        });

        EditText txt_doctor = (EditText) rootView.findViewById(R.id.txt_doctor);
        txt_doctor.setText(patientInfo.getDoctor());
        txt_doctor.addTextChangedListener(new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                patientInfo.setDoctor(s.toString());
            }
        });

        EditText txt_diagnosis = (EditText) rootView.findViewById(R.id.txt_diagnosis);
        txt_diagnosis.setText(patientInfo.getDiagnosis());
        txt_diagnosis.addTextChangedListener(new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                patientInfo.setDiagnosis(s.toString());
            }
        });


        RadioGroup radio_sex_group = (RadioGroup) rootView.findViewById(R.id.radio_sex_group);
        CommonUtil.initRadioGroup(radio_sex_group, patientInfo.getSex());
        radio_sex_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton item = (RadioButton) group.findViewById(checkedId);
                patientInfo.setSex(Integer.parseInt("" + item.getTag()));
            }
        });

        Button btn_step_next = (Button) rootView.findViewById(R.id.btn_step_next);
        btn_step_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.requestFocus();
                savePatientInfo();
            }
        });
    }

    public boolean savePatientInfo() {
        PatientInfo patientInfo = mActivity.getPatientInfo();
        String patient_name = patientInfo.getName();
        if (TextUtils.isEmpty(patient_name)) {
            ToastUtil.showLongToast(mActivity, "患者姓名不能为空");
            return false;
        }
        String age = patientInfo.getAge();
        if (TextUtils.isEmpty(age)) {
            ToastUtil.showLongToast(mActivity, "年龄不能为空");
            return false;
        }

        String inpatient_no = patientInfo.getInpatientNo();
        if (TextUtils.isEmpty(inpatient_no)) {
            //ToastUtil.showLongToast(mActivity, "住院号不能为空");
            //return false;
            patientInfo.setInpatientNo("" + System.currentTimeMillis());
        }
        return savePatient(patientInfo);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            if (data != null) {
                String code = data.getStringExtra("result");
                //Log.d("DatabaseContext", "code="+code);
                txt_inpatient_no.setText(code);
            }
        }


    }

    private boolean savePatient(final PatientInfo patientInfo) {
        RecordInfo recordInfo = mActivity.getRecordInfo();
        if (recordInfo == null) {
            String inpatient_no = patientInfo.getInpatientNo();
            PatientInfo dbPatientInfo = patientDao.queryPatientInfo(inpatient_no);
            if (dbPatientInfo != null) {
                ToastUtil.showLongToast(mActivity, "患者信息已经存在");
                return false;
            }
            String docId = SharedPreferenceUtil.getSharedPreferenceValue(mActivity, CommonUtil.LOGIN_ID);
            patientInfo.setDoctorId(Integer.parseInt(docId));
            boolean success = patientDao.insertPatient(patientInfo);
            if (success) {
                //ToastUtil.showLongToast(mActivity, "患者信息保存成功");
                dbPatientInfo = patientDao.queryPatientInfo(patientInfo.getInpatientNo());
                mActivity.setPatientInfo(dbPatientInfo);
                recordInfo = new RecordInfo();
                recordInfo.setInpatientNo(dbPatientInfo.getInpatientNo());
                mActivity.setRecordInfo(recordInfo);
                mActivity.saveRecordInfo();
            } else {
                ToastUtil.showLongToast(mActivity, "患者信息保存失败");
                return false;
            }
        } else {
            patientDao.updatePatient(patientInfo);
        }
        return true;
        //档案
        //RecordFragment1 fragment = RecordFragment1.createInstance();
        //mActivity.getSupportFragmentManager().beginTransaction()
        //        .replace(R.id.detail_container, fragment)
        //        .commit();
    }

        /*
        String inpatient_no = patientInfo.getInpatientNo();
        PatientInfo dbPatientInfo = patientDao.queryPatientInfo(inpatient_no);
        if (dbPatientInfo != null) {
            ToastUtil.showLongToast(mActivity, "患者信息已经存在");
            return false;
        } else {
            String login_user = SharedPreferenceUtil.getSharedPreferenceValue(mActivity, CommonUtil.LOGIN_USER);
            boolean success = patientDao.insertPatient(patientInfo, login_user);
            if (success) {
                ToastUtil.showLongToast(mActivity, "患者信息保存成功");
            } else {
                ToastUtil.showLongToast(mActivity, "患者信息保存失败");
            }
            return success;
        }
        */

}
