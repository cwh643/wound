package com.dnion.app.android.injuriesapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
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

import com.dnion.app.android.injuriesapp.dao.ConfigDao;
import com.dnion.app.android.injuriesapp.dao.PatientDao;
import com.dnion.app.android.injuriesapp.dao.PatientInfo;
import com.dnion.app.android.injuriesapp.dao.RecordDao;
import com.dnion.app.android.injuriesapp.dao.RecordImage;
import com.dnion.app.android.injuriesapp.dao.RecordImageDao;
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
import com.dnion.app.android.injuriesapp.utils.XZip;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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

    private RecordImageDao recordImageDao;

    private ConfigDao configDao;

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
        configDao = new ConfigDao(mActivity);
        recordImageDao = new RecordImageDao(mActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.query_fragment, container, false);
        rootView.setClickable(true);
        configView(rootView);
        return rootView;
    }

    private void configView(View rootView) {
        btn_add = (Button)rootView.findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectInpatientNo != null && selectInpatientNo.length() > 0) {
                    HomeFragment fragment = HomeFragment.createInstance(selectInpatientNo, "", "1");
                    mActivity.getSupportFragmentManager().beginTransaction()
                            .add(R.id.main_container, fragment)
                            //.replace(R.id.main_container, fragment)
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
                HomeFragment fragment = HomeFragment.createInstance(selectInpatientNo, recordTime, "1");
                mActivity.getSupportFragmentManager().beginTransaction()
                        .add(R.id.main_container, fragment)
                        //.replace(R.id.main_container, fragment)
                        .commit();
            }
        });

        Button btn_sync = (Button)rootView.findViewById(R.id.btn_sync);
        btn_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //uploadRecordInfo(mActivity.getPatientInfo(), mActivity.getRecordInfo());
                syncRecordInfo();
            }
        });
    }

    private class RecordSyncProcessTask extends AsyncTask<String, Object, String> {
        //private RecordImage image;

        @Override
        protected void onPreExecute() {
            AlertDialogUtil.showAlertDialog(mActivity,
                    mActivity.getString(R.string.message_title_tip),
                    mActivity.getString(R.string.message_wait));
        }

        @Override
        protected String doInBackground(String... params) {
            //获取未同步的数据
            List<RecordInfo> syncRecordList = recordDao.queryListBySyncStatus(0);
            if (syncRecordList == null || syncRecordList.size() == 0) {
                return "未找到需要同步的记录！";
            }
            int allCnt = syncRecordList.size();
            int successCnt = 0;
            int failCnt = 0;
            StringBuilder err = new StringBuilder();
            //同步数据
            for (RecordInfo recordInfo : syncRecordList) {
                boolean flag = uploadRecordInfo(recordInfo, err);
                if (flag) {
                    //recordDao.updateSyncFlag(recordInfo.getId());
                    successCnt++;
                } else {
                    failCnt++;
                }
            }
            return "同步创伤记录总数:" + allCnt + "，成功:" + successCnt + "条，失败" + failCnt+ "条！\n" + err;
        }

        @Override
        protected void onPostExecute(String msg) {
            AlertDialogUtil.dismissAlertDialog(mActivity);
            AlertDialogUtil.showAlertDialog(mActivity, mActivity.getString(R.string.message_title_tip),msg, null);
        }

    }

    private void syncRecordInfo() {
        new RecordSyncProcessTask().execute();
   };


    private boolean uploadRecordInfo(RecordInfo recordInfo, StringBuilder msg) {
        boolean sync_flag = false;
        try {
            final String deviceId = configDao.queryValue(CommonUtil.DEVICE_ID);
            final String remoteUrl = configDao.queryValue(CommonUtil.REMOTE_URL);

            //AlertDialogUtil.showAlertDialog(mActivity, mActivity.getString(R.string.message_title_tip), mActivity.getString(R.string.message_wait));

            final int recordId = recordInfo.getId();
            String recordInfoJson = MapUtils.mGson.toJson(recordInfo);
            Log.d(TAG, "sync record: " + recordInfoJson);
            String uuid = recordInfo.getUuid();
            String srcFolder = mActivity.getRecordPath(uuid);
            if (new File(srcFolder).exists()) {
                String zipFile = mActivity.getBaseDir() + File.separator + "recordInfo.zip";
                XZip.ZipFolder(srcFolder, zipFile);
                String url = CommonUtil.initUrl(remoteUrl, "syncRecordInfoHasFile");
                Log.d(TAG, "syncRecordInfoHasFile,url=" + url);
                Response response = OkHttpClientManager.post(url,new File(zipFile), "record",
                        new OkHttpClientManager.Param("recordInfo", recordInfoJson),
                        new OkHttpClientManager.Param("deviceId", deviceId));
                String resultStr = response.body().string();
                Log.d(TAG, "syncRecordInfoHasFile return: " + resultStr);
                PatientResponse res = MapUtils.mGson.fromJson(resultStr, PatientResponse.class);
                if (!res.isSuccess()) {
                    String message = res.getMessage();
                    msg.append("fail on " + uuid + " by " + message + "\n");
                    //ToastUtil.showLongToast(mActivity, "同步创伤信息失败！原因是：" + message);
                } else {
                    //ToastUtil.showLongToast(mActivity, "同步创伤信息成功！");
                    sync_flag = true;
                    int id = Integer.parseInt(res.getMessage());
                    mActivity.updateSyncFlag(id);
                }
            } else {
                String url = CommonUtil.initUrl(remoteUrl, "syncRecordInfo");
                Log.d(TAG, "syncRecordInfo,url=" + url);
                Response response = OkHttpClientManager.post(url,
                        new OkHttpClientManager.Param("recordInfo", recordInfoJson),
                        new OkHttpClientManager.Param("deviceId", deviceId));
                String resultStr = response.body().string();
                Log.d(TAG, "syncRecordInfoHasFile return: " + resultStr);
                PatientResponse res = MapUtils.mGson.fromJson(resultStr, PatientResponse.class);
                if (!res.isSuccess()) {
                    String message = res.getMessage();
                    msg.append("fail on " + uuid + " by " + message + "\n");
                    //ToastUtil.showLongToast(mActivity, "同步创伤信息失败！原因是：" + message);
                } else {
                    //ToastUtil.showLongToast(mActivity, "同步创伤信息成功！");
                    sync_flag = true;
                    int id = Integer.parseInt(res.getMessage());
                    mActivity.updateSyncFlag(id);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "同步创伤信息失败", e);
            msg.append("Error by " + e.getMessage() + "\n");
            //ToastUtil.showLongToast(mActivity, "同步创伤信息失败！");
        }
        return sync_flag;

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
