package com.dnion.app.android.injuriesapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.dnion.app.android.injuriesapp.utils.AlertDialogUtil;
import com.dnion.app.android.injuriesapp.utils.CommonUtil;
import com.dnion.app.android.injuriesapp.ui.CustomerButton;
import com.dnion.app.android.injuriesapp.utils.SharedPreferenceUtil;
import com.dnion.app.android.injuriesapp.utils.ToastUtil;

public class TopMenuFragment extends Fragment {

    public static final String TAG = "top_menu_fragment";

    private MainActivity mActivity;

    public static TopMenuFragment createInstance() {
        TopMenuFragment fragment = new TopMenuFragment();

        Bundle bundle = new Bundle();
        //bundle.putLong(ARGUMENT_USER_ID, userId);
        //bundle.putString(ARGUMENT_USER_NAME, userName);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity)TopMenuFragment.this.getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.top_menu_fragment, container, false);
        configView(rootView);
        return rootView;
    }

    private void configView(View rootView) {
        //新建
        CustomerButton btn_new = (CustomerButton)rootView.findViewById(R.id.btn_new);
        btn_new.setText("新建");
        btn_new.setImage(R.mipmap.home_new);
        btn_new.setSelectImage(R.mipmap.home_new_s);
        btn_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment fragment = HomeFragment.createInstance("", "");
                mActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container, fragment)
                        .commit();
            }
        });

        //查询
        CustomerButton btn_query = (CustomerButton)rootView.findViewById(R.id.btn_query);
        btn_query.setText("查询");
        btn_query.setImage(R.mipmap.home_search);
        btn_query.setSelectImage(R.mipmap.home_search_s);
        btn_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueryFragment fragment = QueryFragment.createInstance();
                mActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container, fragment)
                        .commit();
            }
        });

        //注册
        CustomerButton btn_regist = (CustomerButton)rootView.findViewById(R.id.btn_regist);
        btn_regist.setText("设置");
        btn_regist.setImage(R.mipmap.home_setting);
        btn_regist.setSelectImage(R.mipmap.home_setting_s);
        btn_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingFragment fragment = SettingFragment.createInstance();
                mActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container, fragment)
                        .commit();
                /*

                final EditText serverUrl = new EditText(mActivity);
                serverUrl.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
                String url = SharedPreferenceUtil.getSharedPreferenceValue(mActivity, CommonUtil.REMOTE_URL);
                if (url == null || url.length() == 0) {
                    SharedPreferenceUtil.setSharedPreferenceValue(mActivity, CommonUtil.REMOTE_URL, CommonUtil.APP_URL);
                    serverUrl.setText(CommonUtil.APP_URL);
                } else {
                    serverUrl.setText(url);
                }
                AlertDialogUtil.showInputDialog(mActivity, serverUrl, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = serverUrl.getText().toString();
                        if (url != null && url.trim().length() > 0) {
                            SharedPreferenceUtil.setSharedPreferenceValue(mActivity, CommonUtil.REMOTE_URL, url);
                            ToastUtil.showLongToast(mActivity, "服务器地址修改成功");
                        }
                    }
                });*/
            }
        });

        //修改密码
        CustomerButton btn_modify_pw = (CustomerButton)rootView.findViewById(R.id.btn_modify_pw);
        btn_modify_pw.setText("修改密码");
        btn_modify_pw.setImage(R.mipmap.home_modify_pw);
        btn_modify_pw.setSelectImage(R.mipmap.home_modify_pw_s);
        btn_modify_pw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModifyFragment fragment = ModifyFragment.createInstance();
                mActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container, fragment)
                        .commit();
            }
        });

        //收藏夹
        CustomerButton btn_favorites = (CustomerButton)rootView.findViewById(R.id.btn_favorites);
        btn_favorites.setText("收藏夹");
        btn_favorites.setImage(R.mipmap.home_favorites);
        btn_favorites.setSelectImage(R.mipmap.home_favorites_s);
        btn_favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyFavoritesFragment fragment = MyFavoritesFragment.createInstance();
                mActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container, fragment)
                        .commit();
            }
        });
    }

}
