package com.dnion.app.android.injuriesapp;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.dnion.app.android.injuriesapp.dao.PatientInfo;
import com.dnion.app.android.injuriesapp.dao.RecordInfo;
import com.dnion.app.android.injuriesapp.utils.CommonUtil;

/**
 * Created by 卫华 on 2017/4/15.
 */

public class RecordFragment4 extends Fragment {

    public static final String TAG = "record_fragment4";

    private MainActivity mActivity;

    public static RecordFragment4 createInstance() {
        RecordFragment4 fragment = new RecordFragment4();

        Bundle bundle = new Bundle();
        //bundle.putLong(ARGUMENT_USER_ID, userId);
        //bundle.putString(ARGUMENT_USER_NAME, userName);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity)RecordFragment4.this.getActivity();
        //userDao = new UserDao(mActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.record4_fragment, container, false);
        configView(rootView);
        return rootView;
    }

    private void configView(View rootView) {
        final RecordInfo patientInfo = mActivity.getRecordInfo();
        //伤口的渗液量
        RadioGroup group_wound_leachate_volume = (RadioGroup) rootView.findViewById(R.id.group_wound_leachate_volume);
        CommonUtil.initRadioGroup(group_wound_leachate_volume, patientInfo.getWoundLeachateVolume());
        group_wound_leachate_volume.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton item = (RadioButton) group.findViewById(checkedId);
                patientInfo.setWoundLeachateVolume(Integer.parseInt("" + item.getTag()));
            }
        });
        //伤口的颜色
        RadioGroup group_wound_leachate_color = (RadioGroup) rootView.findViewById(R.id.group_wound_leachate_color);
        CommonUtil.initRadioGroup(group_wound_leachate_color, patientInfo.getWoundLeachateColor());
        group_wound_leachate_color.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton item = (RadioButton) group.findViewById(checkedId);
                patientInfo.setWoundLeachateColor(Integer.parseInt("" + item.getTag()));
            }
        });
        //伤口的气味
        RadioGroup group_wound_leachate_smell = (RadioGroup) rootView.findViewById(R.id.group_wound_leachate_smell);
        CommonUtil.initRadioGroup(group_wound_leachate_smell, patientInfo.getWoundLeachateSmell());
        group_wound_leachate_smell.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton item = (RadioButton) group.findViewById(checkedId);
                patientInfo.setWoundLeachateSmell(Integer.parseInt("" + item.getTag()));
            }
        });

        //下一步
        Button btn_step_next = (Button)rootView.findViewById(R.id.btn_step_next);
        btn_step_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.saveRecordInfo();
                RecordFragment5 fragment = RecordFragment5.createInstance();
                mActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, fragment)
                        .commit();
            }
        });
    }

}
