package com.dnion.app.android.injuriesapp;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.dnion.app.android.injuriesapp.dao.PatientInfo;
import com.dnion.app.android.injuriesapp.dao.RecordInfo;
import com.dnion.app.android.injuriesapp.utils.BaseTextWatcher;
import com.dnion.app.android.injuriesapp.utils.CommonUtil;

/**
 * Created by 卫华 on 2017/4/15.
 */

public class RecordFragment2 extends Fragment {

    public static final String TAG = "record_fragment2";

    private MainActivity mActivity;

    public static RecordFragment2 createInstance() {
        RecordFragment2 fragment = new RecordFragment2();

        Bundle bundle = new Bundle();
        //bundle.putLong(ARGUMENT_USER_ID, userId);
        //bundle.putString(ARGUMENT_USER_NAME, userName);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity)RecordFragment2.this.getActivity();
        //userDao = new UserDao(mActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.record2_fragment, container, false);
        configView(rootView);
        return rootView;
    }

    private void configView(View rootView) {
        final RecordInfo patientInfo = mActivity.getRecordInfo();
        //伤口的清洁程度
        RadioGroup group_wound_clear = (RadioGroup)rootView.findViewById(R.id.group_wound_clear);
        CommonUtil.initRadioGroup(group_wound_clear, patientInfo.getWoundDescribeClean());
        group_wound_clear.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton item = (RadioButton)group.findViewById(checkedId);
                patientInfo.setWoundDescribeClean(Integer.parseInt("" + item.getTag()));
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
        //伤口的基底颜色-红
        EditText wound_color_red = (EditText)rootView.findViewById(R.id.wound_color_red);
        if (patientInfo.getWoundColorRed() != null) {
            wound_color_red.setText("" + patientInfo.getWoundColorRed());
        }
        wound_color_red.addTextChangedListener(new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                patientInfo.setWoundColorRed(Float.parseFloat(s.toString()));
            }
        });

        //伤口的基底颜色-黄
        EditText wound_color_yellow = (EditText)rootView.findViewById(R.id.wound_color_yellow);
        if (patientInfo.getWoundColorYellow() != null) {
            wound_color_yellow.setText("" + patientInfo.getWoundColorYellow());
        }
        wound_color_yellow.addTextChangedListener(new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                patientInfo.setWoundColorYellow(Float.parseFloat(s.toString()));
            }
        });

        //伤口的基底颜色-黑
        EditText wound_color_black = (EditText)rootView.findViewById(R.id.wound_color_black);
        if (patientInfo.getWoundColorBlack() != null) {
            wound_color_black.setText("" + patientInfo.getWoundColorBlack());
        }
        wound_color_black.addTextChangedListener(new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                patientInfo.setWoundColorBlack(Float.parseFloat(s.toString()));
            }
        });

        //伤口周围皮肤情况
        /*
        RadioGroup group_wound_skin = (RadioGroup)rootView.findViewById(R.id.group_wound_skin);
        CommonUtil.initRadioGroup(group_wound_skin, patientInfo.getWoundDescribeSkin());
        group_wound_skin.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton item = (RadioButton)group.findViewById(checkedId);
                patientInfo.setWoundDescribeSkin(Integer.parseInt("" + item.getTag()));
            }
        });
        */
        Spinner group_wound_skin = (Spinner)rootView.findViewById(R.id.group_wound_skin);
        final TypeArrayAdapter typeAdapter = new TypeArrayAdapter(mActivity, android.R.layout.simple_spinner_item, ArchivesData.woundSkinDict);
        group_wound_skin.setAdapter(typeAdapter);
        CommonUtil.setSpinnerItemSelectedByValue(group_wound_skin, patientInfo.getWoundDescribeSkin());
        group_wound_skin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view;
                //tv.setTextColor(Color.WHITE);
                tv.setTextSize(CommonUtil.SELECT_TEXT_SIZE);
                Pair pair = (Pair)typeAdapter.getItem(position);
                if (pair != null) {
                    //ToastUtil.showLongToast(mActivity, "" + pair.second);
                    patientInfo.setWoundDescribeSkin((Integer)pair.first);
                    return;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //下一步
        /*Button btn_step_next = (Button)rootView.findViewById(R.id.btn_step_next);
        btn_step_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.saveRecordInfo();
                RecordFragment3 fragment = RecordFragment3.createInstance();
                mActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, fragment)
                        .commit();
            }
        });*/
    }

}
