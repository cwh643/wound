package com.dnion.app.android.injuriesapp;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dnion.app.android.injuriesapp.dao.UserDao;
import com.dnion.app.android.injuriesapp.dao.UserInfo;
import com.dnion.app.android.injuriesapp.http.OkHttpClientManager;
import com.dnion.app.android.injuriesapp.utils.AlertDialogUtil;
import com.dnion.app.android.injuriesapp.utils.CommonUtil;
import com.dnion.app.android.injuriesapp.utils.MD5;
import com.dnion.app.android.injuriesapp.utils.SharedPreferenceUtil;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by 卫华 on 2017/4/23.
 */

public class LoginActivity extends BaseActivity {

    private Button btn_longin;

    private EditText login_name;

    private EditText login_password;

    private UserDao userDao;

    private static final String TAG = "LoginActivity";

    private LoginActivity mActivity;

    private static final int VOICE_REQUEST_CODE = 66;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏虚拟键
        View main = getLayoutInflater().from(this).inflate(R.layout.activity_login , null);
        main.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(main);
        //setContentView(R.layout.activity_login);
        mActivity = this;

        //6.0以上需要权限申请
        requestPermissions(mActivity);

        login_name = (EditText)findViewById(R.id.login_name);
        login_password = (EditText)findViewById(R.id.login_password);
        btn_longin = (Button)findViewById(R.id.btn_login);
        btn_longin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = login_name.getText().toString().trim();
                if (TextUtils.isEmpty(userName)) {
                    showLongToast("用户名不能为空");
                    return;
                }
                String password = login_password.getText().toString().trim();
                if (TextUtils.isEmpty(password)) {
                    showLongToast("密码不能为空");
                    return;
                }
                login(userName, password);
                //remoteLogin(userName, password);

            }
        });
/*
        Button btn_server = (Button)findViewById(R.id.btn_server);
        btn_server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText serverUrl = new EditText(LoginActivity.this);
                serverUrl.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
                String url = SharedPreferenceUtil.getSharedPreferenceValue(LoginActivity.this, CommonUtil.REMOTE_URL);
                if (url == null || url.length() == 0) {
                    SharedPreferenceUtil.setSharedPreferenceValue(LoginActivity.this, CommonUtil.REMOTE_URL, CommonUtil.APP_URL);
                    serverUrl.setText(CommonUtil.APP_URL);
                } else {
                    serverUrl.setText(url);
                }
                AlertDialogUtil.showInputDialog(LoginActivity.this, serverUrl, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = serverUrl.getText().toString();
                        if (url != null && url.trim().length() > 0) {
                            SharedPreferenceUtil.setSharedPreferenceValue(LoginActivity.this, CommonUtil.REMOTE_URL, url);
                            showLongToast("服务器地址修改成功");
                        }
                    }
                });

            }
        });
*/
        //String loginName = SharedPreferenceUtil.getSharedPreferenceValue(LoginActivity.this, CommonUtil.LOGIN_USER);
        //if(loginName != null && loginName.length() > 0) {
            //Intent intent = new Intent();
            //intent.setClass(LoginActivity.this, MainActivity.class);
            //startActivity(intent);
            //LoginActivity.this.finish();
        //}

    }

    private void login(String userName, String password) {
        UserInfo userInfo = userDao.queryUserByName(userName);
        if (userInfo == null) {
            showLongToast("用户名，密码错误");
            return;
        }
        String dbPass = userInfo.getPassword();
        if (dbPass != null && dbPass.equals(MD5.encode(password))) {
            int userId = userInfo.getId();
            SharedPreferenceUtil.setSharedPreferenceValue(LoginActivity.this, CommonUtil.LOGIN_USER, "" + userInfo);
            SharedPreferenceUtil.setSharedPreferenceValue(LoginActivity.this, CommonUtil.LOGIN_ID, "" + userId);
            //SharedPreferenceUtil.setSharedPreferenceValue(LoginActivity.this, CommonUtil.LOGIN_USER, (String)rowMap.get("user_name"));
            //SharedPreferenceUtil.setSharedPreferenceValue(LoginActivity.this, CommonUtil.LOGIN_TYPE, "" + rowMap.get("user_type"));
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            LoginActivity.this.finish();
        } else {
            showLongToast("用户名，密码错误");
        }
    }

    private void remoteLogin(String userName, String password) {
        String url = CommonUtil.initUrl(LoginActivity.this, "login");
        Log.d(TAG, "登录,url="+url);

        AlertDialogUtil.showAlertDialog(LoginActivity.this,
                LoginActivity.this.getString(R.string.message_title_tip),
                LoginActivity.this.getString(R.string.message_wait));
        OkHttpClientManager.postAsyn(url, new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                AlertDialogUtil.dismissAlertDialog(LoginActivity.this);
                showLongToast("登录失败,请检查服务器地址是否正确");
                Log.e(TAG, "登录失败", e);
            }
            @Override
            public void onResponse(String resultStr) {
                AlertDialogUtil.dismissAlertDialog(LoginActivity.this);
                try {
                    JSONObject json = new JSONObject(resultStr);
                    boolean success = json.getBoolean("success");
                    if (!success) {
                        String message = json.getString("message");
                        showLongToast(message);
                        Log.i(TAG, message);
                    }

                    JSONObject user = json.getJSONObject("user");
                    //String userName = user.getString("loginName");
                    int userId = user.getInt("id");
                    SharedPreferenceUtil.setSharedPreferenceValue(LoginActivity.this, CommonUtil.LOGIN_USER, "" + user);
                    SharedPreferenceUtil.setSharedPreferenceValue(LoginActivity.this, CommonUtil.LOGIN_ID, "" + userId);
                    Intent intent = new Intent();
                    intent.setClass(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                } catch (JSONException e) {
                    showLongToast("登录失败");
                    Log.e(TAG, "登录失败", e);
                }
            }
        },
        //file,//
        //"mFile",//
        new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("username", userName),
                new OkHttpClientManager.Param("password", password)}
        );
    }

    /**
     * 开启扫描之前判断权限是否打开
     */
    private void requestPermissions(Activity mainActivity) {
        //判断是否开麦克风权限
        if ((ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
            //StartListener();
            //数据迁移，版本2
            if (2 == CommonUtil.getAppVersionCode(mActivity)) {
                CommonUtil.datasync(mActivity);
            }
            //if (3 <= CommonUtil.getAppVersionCode(mActivity)) {
            //    CommonUtil.datasync3(mActivity);
            //}
            //判断是否开启语音权限
            userDao = new UserDao(this);
        } else {
            //请求获取麦克风权限
            ActivityCompat.requestPermissions((Activity) mainActivity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA}, VOICE_REQUEST_CODE);
        }

    }

    /**
     * 请求权限回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == VOICE_REQUEST_CODE) {
            if ((grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                //StartListener();
                //数据迁移，版本2
                if (2 == CommonUtil.getAppVersionCode(mActivity)) {
                    CommonUtil.datasync(mActivity);
                }
                if (3 <= CommonUtil.getAppVersionCode(mActivity)) {
                    CommonUtil.datasync3(mActivity);
                }
                Toast.makeText(mActivity, "已获得权限！", Toast.LENGTH_SHORT).show();
                userDao = new UserDao(this);
            } else {
                Toast.makeText(mActivity, "已拒绝权限！", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
