package com.dnion.app.android.injuriesapp;

import android.os.Bundle;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.dnion.app.android.injuriesapp.dao.RecordInfo;
import com.dnion.app.android.injuriesapp.utils.BaseTextWatcher;
import com.dnion.app.android.injuriesapp.utils.CommonUtil;
import com.dnion.app.android.injuriesapp.utils.ToastUtil;

/**
 * Created by 卫华 on 2017/4/15.
 */

public class RecordFragment8 extends Fragment {

    public static final String TAG = "record_fragment8";

    //private RecordAdapter adapter;

    private MainActivity mActivity;

    private TypeArrayAdapter typeAdapter;

    public static RecordFragment8 createInstance() {
        RecordFragment8 fragment = new RecordFragment8();

        Bundle bundle = new Bundle();
        //bundle.putLong(ARGUMENT_USER_ID, userId);
        //bundle.putString(ARGUMENT_USER_NAME, userName);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity)RecordFragment8.this.getActivity();
        //userDao = new UserDao(mActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.record8_fragment, container, false);
        configView(rootView);
        return rootView;
    }

    private void configView(final View rootView) {
        final RecordInfo patientInfo = mActivity.getRecordInfo();

        final TextView wound_dressing_desc = (TextView)rootView.findViewById(R.id.wound_dressing_desc);
        Spinner wound_dressing_type = (Spinner)rootView.findViewById(R.id.wound_dressing_type);
        if (CommonUtil.isEn(mActivity)) {
            typeAdapter = new TypeArrayAdapter(mActivity, android.R.layout.simple_spinner_item, ArchivesData.woundDressingTypeEn);
        } else {
            typeAdapter = new TypeArrayAdapter(mActivity, android.R.layout.simple_spinner_item, ArchivesData.woundDressingType);
        }

        wound_dressing_type.setAdapter(typeAdapter);
        CommonUtil.setSpinnerItemSelectedByValue(wound_dressing_type, patientInfo.getWoundDressingType());
        wound_dressing_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view;
                //tv.setTextColor(Color.WHITE);
                tv.setTextSize(CommonUtil.SELECT_TEXT_SIZE);
                Pair pair = (Pair)typeAdapter.getItem(position);
                if (pair != null) {
                    //ToastUtil.showLongToast(mActivity, "" + pair.second);
                    patientInfo.setWoundDressingType((Integer)pair.first);
                    if (CommonUtil.isEn(mActivity)) {
                        wound_dressing_desc.setText(ArchivesData.woundDressingEnDisc.get((Integer)pair.first));
                    } else {
                        wound_dressing_desc.setText(ArchivesData.woundDressingDisc.get((Integer)pair.first));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final EditText wound_dressing1 = (EditText)rootView.findViewById(R.id.wound_dressing1);
        final EditText wound_dressing2 = (EditText)rootView.findViewById(R.id.wound_dressing2);
        String woundDressing = patientInfo.getWoundDressing();
        if (woundDressing != null) {
            String[] dressingInfo = woundDressing.split("@");
            if (dressingInfo.length == 2) {
                wound_dressing1.setText(dressingInfo[0]);
                wound_dressing2.setText(dressingInfo[1]);
            }
        }
        wound_dressing1.addTextChangedListener(new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                patientInfo.setWoundDressing(s.toString() + "@" + wound_dressing2.getText());
            }
        });
        wound_dressing2.addTextChangedListener(new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                patientInfo.setWoundDressing(wound_dressing1.getText() + "@" + s.toString());
            }
        });

        //下一步
        /*Button btn_step_next = (Button)rootView.findViewById(R.id.btn_step_next);
        btn_step_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.saveRecordInfo();
                ToastUtil.showLongToast(mActivity, "保存创伤记录信息成功！");
                mActivity.clearInfo();
                BaseInfoFragment fragment = BaseInfoFragment.createInstance();
                mActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, fragment)
                        .commit();
            }
        });*/
    }

}
