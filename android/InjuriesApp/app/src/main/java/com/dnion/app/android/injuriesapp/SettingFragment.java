package com.dnion.app.android.injuriesapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.dnion.app.android.injuriesapp.dao.ConfigDao;
import com.dnion.app.android.injuriesapp.dao.RecordInfo;
import com.dnion.app.android.injuriesapp.http.OkHttpClientManager;
import com.dnion.app.android.injuriesapp.http.PatientResponse;
import com.dnion.app.android.injuriesapp.ui.CustomerButton;
import com.dnion.app.android.injuriesapp.utils.AlertDialogUtil;
import com.dnion.app.android.injuriesapp.utils.CommonUtil;
import com.dnion.app.android.injuriesapp.utils.MapUtils;
import com.dnion.app.android.injuriesapp.utils.SharedPreferenceUtil;
import com.dnion.app.android.injuriesapp.utils.ToastUtil;
import com.dnion.app.android.injuriesapp.utils.XZip;
import com.squareup.okhttp.Request;

import java.io.File;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class SettingFragment extends Fragment {

    public static final String TAG = "setting_fragment";

    private MainActivity mActivity;

    private EditText txt_device_id;

    private EditText txt_server_url;

    private RadioGroup select_camera_mode;

    private RadioGroup select_camera_size;

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
        mActivity = (MainActivity) SettingFragment.this.getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.setting_fragment, container, false);
        configView(rootView);
        return rootView;
    }

    private void configView(View rootView) {
        //设备ID
        txt_device_id = (EditText) rootView.findViewById(R.id.txt_device_id);
        txt_device_id.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        String deviceId = mActivity.queryConfig(CommonUtil.DEVICE_ID);
        if (deviceId == null || deviceId.length() == 0) {
            String uid = CommonUtil.getDeviceId();
            mActivity.saveConfig(CommonUtil.DEVICE_ID, uid);
            txt_device_id.setText(uid);
        } else {
            txt_device_id.setText(deviceId);
        }

        //服务器URL
        txt_server_url = (EditText) rootView.findViewById(R.id.txt_server_url);
        txt_server_url.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        String url = mActivity.queryConfig(CommonUtil.REMOTE_URL);
        if (url == null || url.length() == 0) {
            mActivity.saveConfig(CommonUtil.REMOTE_URL, CommonUtil.APP_URL);
            //SharedPreferenceUtil.setSharedPreferenceValue(mActivity, CommonUtil.REMOTE_URL, CommonUtil.APP_URL);
            txt_server_url.setText(CommonUtil.APP_URL);
        } else {
            txt_server_url.setText(url);
        }

        // 深度摄像头厂家
        select_camera_mode = (RadioGroup) rootView.findViewById(R.id.select_camera_mode);
        String str_select_came_mode = mActivity.queryConfig(CommonUtil.CAMERA_MODE);
        for (int i = 0; i < select_camera_mode.getChildCount(); i++) {
            RadioButton rb = (RadioButton)select_camera_mode.getChildAt(i);
            if (rb.getText().toString().equals(str_select_came_mode)) {
                rb.setChecked(true);
                break;
            }
        }

        // 深度摄像头图像尺寸
        select_camera_size = (RadioGroup) rootView.findViewById(R.id.select_camera_size);
        String str_select_came_size = mActivity.queryConfig(CommonUtil.CAMERA_SIZE);
        for (int i = 0; i < select_camera_size.getChildCount(); i++) {
            RadioButton rb = (RadioButton)select_camera_size.getChildAt(i);
            if (rb.getText().toString().equals(str_select_came_size)) {
                rb.setChecked(true);
                break;
            }
        }

        //保存
        Button btn_save = (Button) rootView.findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deviceId = txt_device_id.getText().toString();
                if (deviceId != null && deviceId.trim().length() > 0) {
                    mActivity.saveConfig(CommonUtil.DEVICE_ID, deviceId);
                }

                String url = txt_server_url.getText().toString();
                if (url != null && url.trim().length() > 0) {
                    mActivity.saveConfig(CommonUtil.REMOTE_URL, url);
                }

                String camera_mode = ((RadioButton) mActivity.
                        findViewById(select_camera_mode.getCheckedRadioButtonId())).getText().toString();
                if (camera_mode != null && camera_mode.trim().length() > 0) {
                    mActivity.saveConfig(CommonUtil.CAMERA_MODE, camera_mode);
                }

                String camera_size = ((RadioButton) mActivity.
                        findViewById(select_camera_size.getCheckedRadioButtonId())).getText().toString();
                if (camera_size != null && camera_size.trim().length() > 0) {
                    mActivity.saveConfig(CommonUtil.CAMERA_SIZE, camera_size);
                }

                RecordFragmentDeepCamera.init_camera_param(camera_mode, camera_size);
                ToastUtil.showLongToast(mActivity, "修改成功");
            }
        });

        Button btn_export = (Button) rootView.findViewById(R.id.btn_export);
        btn_export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat df =  new SimpleDateFormat("yyyyMMdd");
                String fileName = df.format(new Date());
                String srcFolder = mActivity.getBaseDir();
                String zipFile = new File(mActivity.getBaseDir()).getParent() + File.separator + "wound_"+fileName+".zip";
                try {
                    XZip.ZipFolder(srcFolder, zipFile);
                    ToastUtil.showLongToast(mActivity, "导出创伤信息成功，路径在：" + zipFile);
                } catch (Exception e) {
                    Log.e(TAG, "导出创伤信息失败", e);
                    //ToastUtil.showLongToast(mActivity, "同步创伤信息失败！");
                }
            }
        });
    }

}
