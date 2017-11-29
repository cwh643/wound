package com.dnion.app.android.injuriesapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.dnion.app.android.injuriesapp.dao.UserDao;
import com.dnion.app.android.injuriesapp.dao.UserInfo;
import com.dnion.app.android.injuriesapp.utils.AlertDialogUtil;
import com.dnion.app.android.injuriesapp.utils.CommonUtil;
import com.dnion.app.android.injuriesapp.utils.MD5;
import com.dnion.app.android.injuriesapp.utils.SharedPreferenceUtil;
import com.dnion.app.android.injuriesapp.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A fragment representing a single Item detail screen.
 * on handsets.
 */
public class ModifyFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String TAG = "modify_fragment";

    private EditText login_old_password;

    private EditText login_new_password;

    private EditText login_confirm_password;

    private Button btn_save;

    private MainActivity mActivity;

    private UserDao userDao;

    public static ModifyFragment createInstance() {
        ModifyFragment fragment = new ModifyFragment();

        Bundle bundle = new Bundle();
        //bundle.putLong(ARGUMENT_USER_ID, userId);
        //bundle.putString(ARGUMENT_USER_NAME, userName);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity)ModifyFragment.this.getActivity();
        userDao = new UserDao(mActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.modify_pw_fragment, container, false);
        configView(rootView);
        return rootView;
    }

    private void configView(View rootView) {
        login_old_password = (EditText)rootView.findViewById(R.id.login_old_password);
        login_new_password = (EditText)rootView.findViewById(R.id.login_new_password);
        login_confirm_password = (EditText)rootView.findViewById(R.id.login_confirm_password);
        btn_save = (Button) rootView.findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = login_old_password.getText().toString().trim();
                if (TextUtils.isEmpty(oldPassword)) {
                    ToastUtil.showLongToast(mActivity, "旧密码必须输入");
                    return;
                }
                String newPassword = login_new_password.getText().toString().trim();
                if (TextUtils.isEmpty(newPassword)) {
                    ToastUtil.showLongToast(mActivity, "新密码必须输入");
                    return;
                }
                String confirmPassword = login_confirm_password.getText().toString().trim();
                if (TextUtils.isEmpty(confirmPassword)) {
                    ToastUtil.showLongToast(mActivity, "确认密码必须输入");
                    return;
                }
                if (newPassword.length() < 6) {
                    ToastUtil.showLongToast(mActivity, "新密码长度至少6位");
                    return;
                }
                if (!newPassword.equals(confirmPassword)) {
                    ToastUtil.showLongToast(mActivity, "新密码和确认密码必须相同");
                    return;
                }
                String userName = SharedPreferenceUtil.getSharedPreferenceValue(mActivity, CommonUtil.LOGIN_USER);
                UserInfo user = userDao.queryUserByName(userName);
                if (user == null) {
                    ToastUtil.showLongToast(mActivity, "当前登录用户不存在");
                    return;
                }
                String dbPass = (String) user.getPassword();
                if (dbPass == null || !dbPass.equals(MD5.encode(oldPassword))) {
                    ToastUtil.showLongToast(mActivity, "旧密码错误，不能修改");
                    return;
                }
                user.setPassword(MD5.encode(newPassword));
                boolean success = userDao.updateUserPw(user);
                if (success) {
                    login_old_password.setText("");
                    login_new_password.setText("");
                    login_confirm_password.setText("");
                    ToastUtil.showLongToast(mActivity, "修改密码成功");

                    SharedPreferenceUtil.setSharedPreferenceValue(mActivity, CommonUtil.LOGIN_USER, "");
                    Intent intent = new Intent();
                    intent.setClass(mActivity, LoginActivity.class);
                    startActivity(intent);
                    mActivity.finish();
                } else {
                    ToastUtil.showLongToast(mActivity, "修改密码失败");
                }
            }
        });
    }

}
