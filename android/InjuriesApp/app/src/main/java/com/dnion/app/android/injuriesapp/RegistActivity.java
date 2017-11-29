package com.dnion.app.android.injuriesapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.dnion.app.android.injuriesapp.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A fragment representing a single Item detail screen.
 * on handsets.
 */
public class RegistActivity extends BaseActivity {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String TAG = "regist_activity";

    private ListView user_list_view;

    private EditText txt_query_name;

    private EditText txt_register_name;

    private Button btn_query;

    private Button btn_register;

    private Button btn_delete;

    private UserAdapter adapter;

    private UserDao userDao;

    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist_fragment);
        userDao = new UserDao(this);
        mActivity = this;

        user_list_view = (ListView)this.findViewById(R.id.user_list_view);
        List<UserInfo> userList = userDao.queryUserList();
        adapter = new UserAdapter(userList);
        user_list_view.setAdapter(adapter);
        user_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserTag viewHolder = (UserTag) view.getTag();
                if (viewHolder != null) {
                    viewHolder.item_check.toggle();
                    if(viewHolder.item_check.isChecked()) {
                        adapter.set(position, true);
                    }else{
                        adapter.set(position, false);
                    }
                }
            }
        });

        txt_query_name = (EditText)this.findViewById(R.id.txt_query_name);
        txt_register_name = (EditText)this.findViewById(R.id.txt_register_name);
        btn_query = (Button)this.findViewById(R.id.btn_query);
        btn_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = txt_query_name.getText().toString();
                List<UserInfo> userList = userDao.queryUserList(query);
                adapter.setUserList(userList);
            }
        });

        btn_register = (Button)this.findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = txt_register_name.getText().toString().trim();
                if (name == null || name.length() == 0) {
                    ToastUtil.showLongToast(mActivity, "用户名必须输入");
                    return;
                }
                UserInfo user = userDao.queryUserByName(name);
                if (user != null && name.equals(user.getName())) {
                    ToastUtil.showLongToast(mActivity, "用户名已经存在");
                    return;
                }
                boolean success = userDao.insertUser(user);
                if (success) {
                    txt_query_name.setText("");
                    List<UserInfo> userList = userDao.queryUserList();
                    adapter.setUserList(userList);
                    ToastUtil.showLongToast(mActivity, "注册成功");
                } else {
                    ToastUtil.showLongToast(mActivity, "注册失败");
                }
            }
        });

        btn_delete = (Button)this.findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String ids = adapter.getCheckUserIds();
                //Log.e("UserDao", ids);
                if (ids.length() == 0) {
                    ToastUtil.showLongToast(mActivity, "请选择需要删除的用户");
                    return;
                }
                AlertDialogUtil.showConfirmDialog(mActivity, "提示", "确定删除这些用户吗？", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userDao.deleteUser(ids);
                        List<UserInfo> userList = userDao.queryUserList();
                        adapter.setUserList(userList);
                    }
                });

            }
        });

    }

    public class UserAdapter extends BaseAdapter {

        private List<UserInfo> userList = new ArrayList();

        public UserAdapter(List<UserInfo> userList) {
            this.userList = userList;
        }

        @Override
        public int getCount() {
            return userList.size();
        }

        @Override
        public Object getItem(int position) {
            return userList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item_view, null);
                UserTag tag = new UserTag();
                convertView.setTag(tag);
                tag.item_username = (TextView)convertView.findViewById(R.id.item_username);
                tag.item_check = (CheckBox)convertView.findViewById(R.id.item_check);
                tag.item_check.setEnabled(false);
            }
            UserInfo user = userList.get(position);
            UserTag tag = (UserTag)convertView.getTag();
            tag.item_username.setText(user.getName());
            boolean isCheck = user.isItemCheck();
            tag.item_check.setChecked(isCheck);
            settingDocTitle();
            addTopButtonEvent();
            return convertView;
        }

        public void setUserList(List<UserInfo> userList) {
            this.userList = userList;
            this.notifyDataSetChanged();
        }

        public void set(int position, boolean checked) {
            UserInfo user = userList.get(position);
            //Log.e("UserDao", "position=" + position + ",checked="+checked +",user=" + user.get("user_name"));
            if (user != null) {
                user.setItemCheck(checked);
            }
        }

        public String getCheckUserIds() {
            String ids = "";
            for (UserInfo user : userList) {
                boolean check = user.isItemCheck();
                if (check) {
                    if (ids.length() > 0) {
                        ids += ",";
                    }
                    ids += user.getId();
                }
            }
            return ids;
        }
    }

    private class UserTag {

        TextView item_username;
        CheckBox item_check;

    }
}
