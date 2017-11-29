package com.dnion.app.android.injuriesapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import com.dnion.app.android.injuriesapp.dao.ConfigDao;
import com.dnion.app.android.injuriesapp.ui.CustomerButton;
import com.dnion.app.android.injuriesapp.utils.AlertDialogUtil;
import com.dnion.app.android.injuriesapp.utils.CommonUtil;
import com.dnion.app.android.injuriesapp.utils.SharedPreferenceUtil;
import com.dnion.app.android.injuriesapp.utils.ToastUtil;

import java.text.NumberFormat;
import java.util.UUID;

public class SettingFragment extends Fragment {

    public static final String TAG = "setting_fragment";

    private MainActivity mActivity;

    private EditText txt_device_id;

    private EditText txt_server_url;

    private ConfigDao configDao;

    public static SettingFragment createInstance() {
        SettingFragment fragment = new SettingFragment();

        Bundle bundle = new Bundle();
        //bundle.putLong(ARGUMENT_USER_ID, userId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity)SettingFragment.this.getActivity();
        configDao = new ConfigDao(mActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.setting_fragment, container, false);
        configView(rootView);
        return rootView;
    }

    private void configView(View rootView) {
        //设备ID
        txt_device_id = (EditText)rootView.findViewById(R.id.txt_device_id);
        txt_device_id.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        String deviceId = configDao.queryValue(CommonUtil.DEVICE_ID);
        if (deviceId == null || deviceId.length() == 0) {
            String uid = CommonUtil.getDeviceId();
            configDao.saveValue(CommonUtil.DEVICE_ID, uid);
            txt_device_id.setText(uid);
        } else {
            txt_device_id.setText(deviceId);
        }

        //服务器URL
        txt_server_url = (EditText)rootView.findViewById(R.id.txt_server_url);
        txt_server_url.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        String url = configDao.queryValue(CommonUtil.REMOTE_URL);
        if (url == null || url.length() == 0) {
            configDao.saveValue(CommonUtil.REMOTE_URL, CommonUtil.APP_URL);
            //SharedPreferenceUtil.setSharedPreferenceValue(mActivity, CommonUtil.REMOTE_URL, CommonUtil.APP_URL);
            txt_server_url.setText(CommonUtil.APP_URL);
        } else {
            txt_server_url.setText(url);
        }

        //保存
        Button btn_save = (Button)rootView.findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deviceId = txt_device_id.getText().toString();
                if (deviceId != null && deviceId.trim().length() > 0) {
                    configDao.saveValue(CommonUtil.DEVICE_ID, deviceId);
                }

                String url = txt_server_url.getText().toString();
                if (url != null && url.trim().length() > 0) {
                    configDao.saveValue(CommonUtil.REMOTE_URL, url);
                }

                ToastUtil.showLongToast(mActivity, "修改成功");
            }
        });
    }

}
