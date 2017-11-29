package com.dnion.app.android.injuriesapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dnion.app.android.injuriesapp.dao.UserDao;
import com.dnion.app.android.injuriesapp.utils.AlertDialogUtil;
import com.dnion.app.android.injuriesapp.utils.CommonUtil;
import com.dnion.app.android.injuriesapp.utils.DBHelper;
import com.dnion.app.android.injuriesapp.utils.SharedPreferenceUtil;
import com.dnion.app.android.injuriesapp.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by 卫华 on 2017/4/15.
 */

public class BaseActivity extends AppCompatActivity {

    //protected  DBHelper db;

    //private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //db = ((InjApp)getApplication()).getDb();
        //userDao = new UserDao(BaseActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void setFullscreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    protected  void settingDocTitle() {
        String user = SharedPreferenceUtil.getSharedPreferenceValue(this, CommonUtil.LOGIN_USER);
        try {
            JSONObject json = new JSONObject(user);
            TextView doc_title = (TextView)findViewById(R.id.doc_title);
            doc_title.setText("Dr."+json.getString("name")+"（"+json.getString("department")+"）");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected  void settingPatientTitle(String title, String patientNo) {
        //String userName = SharedPreferenceUtil.getSharedPreferenceValue(this, CommonUtil.PATIENT_USER);
        TextView patient_title = (TextView)findViewById(R.id.patient_title);
        String text = ""+title+"（"+patientNo+"）";
        if (text.length() > 2) {
            patient_title.setText(text);
        }
    }

    protected void hiddenTopButton() {
        TextView btn_quit = (TextView)findViewById(R.id.btn_quit);
        btn_quit.setVisibility(View.GONE);
        TextView btn_home = (TextView)findViewById(R.id.btn_home);
        btn_home.setVisibility(View.GONE);
    }

    protected void hiddenTopBar() {
        RelativeLayout top_bar = (RelativeLayout)findViewById(R.id.top_bar);
        top_bar.setVisibility(View.GONE);
    }

    protected void showTopBar() {
        RelativeLayout top_bar = (RelativeLayout)findViewById(R.id.top_bar);
        top_bar.setVisibility(View.VISIBLE);
    }

    protected void hiddenSubMenuBar() {
        RelativeLayout sub_menu_bar = (RelativeLayout)findViewById(R.id.sub_menu_bar);
        sub_menu_bar.setVisibility(View.GONE);
    }

    protected void showSubMenuBar() {
        RelativeLayout sub_menu_bar = (RelativeLayout)findViewById(R.id.sub_menu_bar);
        sub_menu_bar.setVisibility(View.VISIBLE);
    }

    protected void selectMenuButton(int id) {
        Button btn_base_info = (Button)findViewById(R.id.btn_base_info);
        btn_base_info.setBackgroundResource(R.mipmap.menu_btn_bg);

        Button btn_archives = (Button)findViewById(R.id.btn_archives);
        btn_archives.setBackgroundResource(R.mipmap.menu_btn_bg);

        Button btn_photo = (Button)findViewById(R.id.btn_photo);
        btn_photo.setBackgroundResource(R.mipmap.menu_btn_bg);

        Button btn_my_favorites = (Button)findViewById(R.id.btn_my_favorites);
        btn_my_favorites.setBackgroundResource(R.mipmap.menu_btn_bg);

        final View v = findViewById(id);
        v.setBackgroundResource(R.mipmap.menu_btn_select);
    }

    protected void addTopButtonEvent() {
        TextView btn_quit = (TextView)findViewById(R.id.btn_quit);
        btn_quit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                quit();
            }
        });
        TextView btn_home = (TextView)findViewById(R.id.btn_home);
        btn_home.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                home();
            }
        });
    }

    protected void quit() {
        AlertDialogUtil.showConfirmDialog(BaseActivity.this, "提示", "确定退出系统？", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferenceUtil.setSharedPreferenceValue(BaseActivity.this, CommonUtil.LOGIN_USER, "");
                BaseActivity.this.finish();
            }
        });
    }

    protected void home() {
        TopMenuFragment fragment = TopMenuFragment.createInstance();
        BaseActivity.this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, fragment)
                .commit();
    }


    protected void showLongToast(String pMsg) {
        ToastUtil.showLongToast(this, pMsg);
    }
}
