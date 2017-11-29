package com.dnion.app.android.injuriesapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.dnion.app.android.injuriesapp.dao.PatientDao;
import com.dnion.app.android.injuriesapp.dao.PatientInfo;
import com.dnion.app.android.injuriesapp.dao.RecordDao;
import com.dnion.app.android.injuriesapp.dao.RecordInfo;
import com.dnion.app.android.injuriesapp.dao.UserDao;
import com.dnion.app.android.injuriesapp.dao.UserInfo;
import com.dnion.app.android.injuriesapp.http.OkHttpClientManager;
import com.dnion.app.android.injuriesapp.http.PatientListResponse;
import com.dnion.app.android.injuriesapp.http.PatientResponse;
import com.dnion.app.android.injuriesapp.http.RecordListResponse;
import com.dnion.app.android.injuriesapp.http.UserListResponse;
import com.dnion.app.android.injuriesapp.utils.AlertDialogUtil;
import com.dnion.app.android.injuriesapp.utils.CommonUtil;
import com.dnion.app.android.injuriesapp.utils.MapUtils;
import com.dnion.app.android.injuriesapp.utils.SharedPreferenceUtil;
import com.dnion.app.android.injuriesapp.utils.ToastUtil;
import com.squareup.okhttp.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A fragment representing a single Item detail screen.
 * on handsets.
 */
public class QueryFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String TAG = "query_fragment";

    private MainActivity mActivity;

    private UserDao userDao;

    private ListView doc_list;

    private QueryAdapter docAdapter;

    private ListView patient_list;

    private QueryAdapter patientAdapter;

    private PatientDao patientDao;

    private ListView record_list;

    private QueryAdapter recordAdapter;

    private RecordDao recordDao;

    private Button btn_add;

    private String selectInpatientNo = "";

    public static QueryFragment createInstance() {
        QueryFragment fragment = new QueryFragment();
        Bundle bundle = new Bundle();
        //bundle.putLong(ARGUMENT_USER_ID, userId);
        //bundle.putString(ARGUMENT_USER_NAME, userName);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity)QueryFragment.this.getActivity();
        recordDao = new RecordDao(mActivity);
        patientDao = new PatientDao(mActivity);
        userDao = new UserDao(mActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.query_fragment, container, false);
        configView(rootView);
        return rootView;
    }

    private void configView(View rootView) {
        btn_add = (Button)rootView.findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectInpatientNo != null && selectInpatientNo.length() > 0) {
                    HomeFragment fragment = HomeFragment.createInstance(selectInpatientNo, "");
                    mActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_container, fragment)
                            .commit();
                } else {
                    ToastUtil.showLongToast(mActivity, "请选择患者");
                }
            }
        });

        docAdapter = new QueryAdapter(1);
        doc_list = (ListView)rootView.findViewById(R.id.doc_list);
        doc_list.setAdapter(docAdapter);
        doc_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserInfo user = (UserInfo)docAdapter.getItem(position);
                int docId = user.getId();
                queryPatientList(docId);
            }
        });
        queryDocList();

        patientAdapter = new QueryAdapter(2);
        patient_list = (ListView)rootView.findViewById(R.id.patient_list);
        patient_list.setAdapter(patientAdapter);
        patient_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PatientInfo patientInfo = (PatientInfo)patientAdapter.getItem(position);
                if (patientInfo != null) {
                    String inpatientNo = patientInfo.getInpatientNo();
                    selectInpatientNo = inpatientNo;
                    queryRecordList(inpatientNo);
                }

            }
        });

        recordAdapter = new QueryAdapter(3);
        record_list = (ListView)rootView.findViewById(R.id.record_list);
        record_list.setAdapter(recordAdapter);
        record_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RecordInfo record = (RecordInfo)recordAdapter.getItem(position);
                String recordTime = record.getRecordTime();
                HomeFragment fragment = HomeFragment.createInstance(selectInpatientNo, recordTime);
                mActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container, fragment)
                        .commit();
            }
        });
    }

    private void queryRecordList(String inpatientNo) {
        List<RecordInfo> list = recordDao.queryRecordList(inpatientNo);
        recordAdapter.setDataList(list);
    }

    private void queryRemoteRecordList(String inpatientNo) {
        String url = CommonUtil.initUrl(mActivity, "recordList?inpatientNo="+inpatientNo);
        Log.d(TAG, "获取患者就诊列表,url="+url);

        AlertDialogUtil.showAlertDialog(mActivity,
                mActivity.getString(R.string.message_title_tip),
                mActivity.getString(R.string.message_wait));
        OkHttpClientManager.getAsyn(url, new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                AlertDialogUtil.dismissAlertDialog(mActivity);
                ToastUtil.showLongToast(mActivity, "获取患者就诊列表失败！");
                Log.e(TAG, "获取患者就诊列表失败", e);
            }
            @Override
            public void onResponse(String resultStr) {
                AlertDialogUtil.dismissAlertDialog(mActivity);
                RecordListResponse res = MapUtils.mGson.fromJson(resultStr, RecordListResponse.class);
                if (!res.isSuccess()) {
                    String message = res.getMessage();
                    ToastUtil.showLongToast(mActivity, "获取患者就诊列表失败！原因是：" + message);
                    return;
                }
                List<RecordInfo> list = res.getList();
                recordAdapter.setDataList(list);
            }
        });
    }

    private void queryPatientList(int docId) {
        List<PatientInfo> list = patientDao.queryPatientList("" + docId);
        patientAdapter.setDataList(list);
    }

    private void queryRemotePatientList(int docId) {
        String url = CommonUtil.initUrl(mActivity, "patientList?docId="+docId);
        Log.d(TAG, "获取患者列表,url="+url);

        AlertDialogUtil.showAlertDialog(mActivity,
                mActivity.getString(R.string.message_title_tip),
                mActivity.getString(R.string.message_wait));
        OkHttpClientManager.getAsyn(url, new OkHttpClientManager.ResultCallback<String>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        AlertDialogUtil.dismissAlertDialog(mActivity);
                        ToastUtil.showLongToast(mActivity, "获取患者列表失败！");
                        Log.e(TAG, "获取患者列表失败", e);
                    }
                    @Override
                    public void onResponse(String resultStr) {
                        AlertDialogUtil.dismissAlertDialog(mActivity);
                        PatientListResponse res = MapUtils.mGson.fromJson(resultStr, PatientListResponse.class);
                        if (!res.isSuccess()) {
                            String message = res.getMessage();
                            ToastUtil.showLongToast(mActivity, "获取患者列表失败！原因是：" + message);
                            return;
                        }
                        List<PatientInfo> list = res.getList();
                        patientAdapter.setDataList(list);
                    }
                });
    }

    private void queryDocList() {
        List<UserInfo> list = new ArrayList<UserInfo>();
        String user = SharedPreferenceUtil.getSharedPreferenceValue(mActivity, CommonUtil.LOGIN_USER);
        UserInfo userInfo = MapUtils.mGson.fromJson(user, UserInfo.class);
        list.add(userInfo);
        docAdapter.setDataList(list);
    }

    private void queryRemoteDocList() {
        String url = CommonUtil.initUrl(mActivity, "docList");
        Log.d(TAG, "获取医生列表,url="+url);

        AlertDialogUtil.showAlertDialog(mActivity,
                mActivity.getString(R.string.message_title_tip),
                mActivity.getString(R.string.message_wait));
        OkHttpClientManager.getAsyn(url, new OkHttpClientManager.ResultCallback<String>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        AlertDialogUtil.dismissAlertDialog(mActivity);
                        ToastUtil.showLongToast(mActivity, "获取医生列表失败！");
                        Log.e(TAG, "获取医生列表失败", e);
                    }
                    @Override
                    public void onResponse(String resultStr) {
                        AlertDialogUtil.dismissAlertDialog(mActivity);
                        UserListResponse res = MapUtils.mGson.fromJson(resultStr, UserListResponse.class);
                        if (!res.isSuccess()) {
                            String message = res.getMessage();
                            ToastUtil.showLongToast(mActivity, "获取医生列表失败！原因是：" + message);
                            return;
                        }
                        List<UserInfo> list = res.getList();
                        docAdapter.setDataList(list);
                    }
                }
        );
    }

    public class QueryAdapter extends BaseAdapter {

        private int type = 0;

        private List dataList = new ArrayList();

        public QueryAdapter(int type) {
            this(type, null);
        }

        public QueryAdapter(int type, List dataList) {
            this.type = type;
            if (dataList != null) {
                this.dataList = dataList;
            }
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_item_view, null);
                UserTag tag = new UserTag();
                convertView.setTag(tag);
                tag.item_name = (TextView)convertView.findViewById(R.id.item_name);
            }

            UserTag tag = (UserTag)convertView.getTag();
            if (type == 1) {
                UserInfo user = (UserInfo)getItem(position);
                tag.item_name.setText("" + user.getName() + "(" + user.getDepartment() + ")");
            } else if (type == 2) {
                PatientInfo patient = (PatientInfo)getItem(position);
                tag.item_name.setText("" + patient.getName() + "(" + patient.getInpatientNo() + ")");
            } else if (type == 3) {
                RecordInfo record = (RecordInfo)getItem(position);
                int is_operation = record.getIsOperation();
                tag.item_name.setText("" + record.getRecordTime() + "(" + (is_operation == 0 ? "否": "是") + ")");
            } else {
                tag.item_name.setText("");
            }

            return convertView;
        }

        public void setDataList(List dataList) {
            this.dataList = dataList;
            this.notifyDataSetChanged();
        }

        private class UserTag {
            TextView item_name;

        }
    }


}
