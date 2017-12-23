package com.dnion.app.android.injuriesapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.dnion.app.android.injuriesapp.dao.PatientInfo;
import com.dnion.app.android.injuriesapp.dao.RecordInfo;

import java.util.List;

/**
 * Created by 卫华 on 2017/4/15.
 */

public class RecordFragment5 extends Fragment {

    public static final String TAG = "record_fragment5";

    private MainActivity mActivity;

    private Integer[] itemsList;

    private Integer[] localItemsList;

    public static RecordFragment5 createInstance() {
        RecordFragment5 fragment = new RecordFragment5();

        Bundle bundle = new Bundle();
        //bundle.putLong(ARGUMENT_USER_ID, userId);
        //bundle.putString(ARGUMENT_USER_NAME, userName);

        fragment.setArguments(bundle);
        return fragment;
    }

    public RecordFragment5() {
        itemsList = new Integer[] {
                R.id.effect_element_1
                ,R.id.effect_element_2
                ,R.id.effect_element_3
                ,R.id.effect_element_4
                ,R.id.effect_element_5
                ,R.id.effect_element_6
                ,R.id.effect_element_7
                ,R.id.effect_element_8
        };
        localItemsList = new Integer[] {
                R.id.effect_element_local_1
                ,R.id.effect_element_local_2
                ,R.id.effect_element_local_3
                ,R.id.effect_element_local_4
                ,R.id.effect_element_local_5
                ,R.id.effect_element_local_6
        };
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity)RecordFragment5.this.getActivity();
        //userDao = new UserDao(mActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.record5_fragment, container, false);
        configView(rootView);
        return rootView;
    }

    private void configView(final View rootView) {
        final RecordInfo patientInfo = mActivity.getRecordInfo();

        //影响伤口愈合的因素-全身因素
        String woundEffectElement = patientInfo.getWoundHealAll();
        if (woundEffectElement != null && woundEffectElement.length() > 0) {
            String[] items = woundEffectElement.split(",");
            for (String item : items) {
                for (int id: itemsList) {
                    CheckBox checkBox = (CheckBox)rootView.findViewById(id);
                    if (item.equals("" + checkBox.getTag())) {
                        checkBox.setChecked(true);
                    }
                }
            }
        }


        for (int id: itemsList) {
            CheckBox checkBox = (CheckBox)rootView.findViewById(id);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String values = getItemCheck(itemsList, rootView);
                    patientInfo.setWoundHealAll(values);
                }
            });
        }

        //影响伤口愈合的因素-局部因素
        String woundEffectLocalElement = patientInfo.getWoundHealPosition();
        if (woundEffectLocalElement != null && woundEffectLocalElement.length() > 0) {
            String[] localItems = woundEffectLocalElement.split(",");
            for (String item : localItems) {
                for (int id: localItemsList) {
                    CheckBox checkBox = (CheckBox)rootView.findViewById(id);
                    if (item.equals("" + checkBox.getTag())) {
                        checkBox.setChecked(true);
                    }
                }
            }
        }

        for (int id: localItemsList) {
            CheckBox checkBox = (CheckBox)rootView.findViewById(id);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String values = getItemCheck(localItemsList, rootView);
                    patientInfo.setWoundHealPosition(values);
                }
            });
        }
        //下一步
        /*Button btn_step_next = (Button)rootView.findViewById(R.id.btn_step_next);
        btn_step_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.saveRecordInfo();
                RecordFragment6 fragment = RecordFragment6.createInstance();
                mActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, fragment)
                        .commit();
            }
        });*/
    }

    private String getItemCheck(Integer[] ids, View rootView) {
        String itemValues = "";
        for (int id: ids) {
            CheckBox checkBox = (CheckBox)rootView.findViewById(id);
            if (checkBox.isChecked()) {
                if (itemValues.length() > 0) {
                    itemValues += ",";
                }
                itemValues += checkBox.getTag();
            }
        }
        return itemValues;
    }


}
