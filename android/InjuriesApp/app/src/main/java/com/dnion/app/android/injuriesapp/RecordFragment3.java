package com.dnion.app.android.injuriesapp;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dnion.app.android.injuriesapp.dao.PatientInfo;
import com.dnion.app.android.injuriesapp.dao.RecordInfo;
import com.dnion.app.android.injuriesapp.utils.CommonUtil;

/**
 * Created by 卫华 on 2017/4/15.
 */

public class RecordFragment3 extends Fragment {

    public static final String TAG = "record_fragment3";

    private MainActivity mActivity;

    public static RecordFragment3 createInstance() {
        RecordFragment3 fragment = new RecordFragment3();

        Bundle bundle = new Bundle();
        //bundle.putLong(ARGUMENT_USER_ID, userId);
        //bundle.putString(ARGUMENT_USER_NAME, userName);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity)RecordFragment3.this.getActivity();
        //userDao = new UserDao(mActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.record3_fragment, container, false);
        configView(rootView);
        return rootView;
    }


    private void configView(View rootView) {
        final RecordInfo patientInfo = mActivity.getRecordInfo();
        //伤口属性
        RadioGroup group_wound_prop = (RadioGroup)rootView.findViewById(R.id.group_wound_prop);
        CommonUtil.initRadioGroup(group_wound_prop, patientInfo.getWoundAssessProp());
        group_wound_prop.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton item = (RadioButton)group.findViewById(checkedId);
                patientInfo.setWoundAssessProp(Integer.parseInt("" + item.getTag()));
            }
        });
        //疼痛等级
        final TextView wound_ache_title = (TextView)rootView.findViewById(R.id.wound_ache_title);
        final String woundGrade = getString(R.string.record_wound_grade);
        wound_ache_title.setText(woundGrade + ":" + (patientInfo.getWoundAche() == null ? "" : patientInfo.getWoundAche()));
        SeekBar wound_ache = (SeekBar)rootView.findViewById(R.id.wound_ache);
        if (patientInfo.getWoundAche() != null) {
            wound_ache.setProgress(patientInfo.getWoundAche());
        }

        wound_ache.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                wound_ache_title.setText(woundGrade + ":"+ progress);
                patientInfo.setWoundAche(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //伤口有无感染
        /*
        RadioGroup group_wound_infected = (RadioGroup)rootView.findViewById(R.id.group_wound_infected);
        CommonUtil.initRadioGroup(group_wound_infected, patientInfo.getWoundAssessInfect());
        group_wound_infected.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton item = (RadioButton)group.findViewById(checkedId);
                patientInfo.setWoundAssessInfect(Integer.parseInt("" + item.getTag()));
            }
        });
        //感染的细菌种类,药物试验
        EditText wound_germs = (EditText)rootView.findViewById(R.id.wound_germs);
        wound_germs.setText(patientInfo.getWoundAssessInfectDesc());
        wound_germs.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String text = ((EditText)v).getText().toString();
                    patientInfo.setWoundAssessInfectDesc(text);
                }
            }
        });
        */

        //下一步
        /*Button btn_step_next = (Button)rootView.findViewById(R.id.btn_step_next);
        btn_step_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.saveRecordInfo();
                RecordFragment4 fragment = RecordFragment4.createInstance();
                mActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, fragment)
                        .commit();
            }
        });*/
    }

}
