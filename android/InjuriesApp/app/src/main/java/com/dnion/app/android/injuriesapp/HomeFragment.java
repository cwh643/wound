package com.dnion.app.android.injuriesapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.dnion.app.android.injuriesapp.dao.ConfigDao;
import com.dnion.app.android.injuriesapp.dao.PatientDao;
import com.dnion.app.android.injuriesapp.dao.PatientInfo;
import com.dnion.app.android.injuriesapp.dao.RecordDao;
import com.dnion.app.android.injuriesapp.dao.RecordImage;
import com.dnion.app.android.injuriesapp.dao.RecordImageDao;
import com.dnion.app.android.injuriesapp.dao.RecordInfo;
import com.dnion.app.android.injuriesapp.http.OkHttpClientManager;
import com.dnion.app.android.injuriesapp.http.PatientResponse;
import com.dnion.app.android.injuriesapp.http.RecordResponse;
import com.dnion.app.android.injuriesapp.utils.AlertDialogUtil;
import com.dnion.app.android.injuriesapp.utils.CommonUtil;
import com.dnion.app.android.injuriesapp.utils.MapUtils;
import com.dnion.app.android.injuriesapp.utils.PdfViewer;
import com.dnion.app.android.injuriesapp.utils.SharedPreferenceUtil;
import com.dnion.app.android.injuriesapp.utils.ToastUtil;
import com.dnion.app.android.injuriesapp.utils.XZip;
import com.itextpdf.text.DocumentException;
import com.squareup.okhttp.Request;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by 卫华 on 2017/4/15.
 */

public class HomeFragment extends Fragment {

    public static final String TAG = "home_fragment";

    private MainActivity mActivity;

    private Fragment currentFragment;

    private Button btn_base_info;

    private Button btn_archives;

    private Button btn_photo;

    //private Button btn_my_favorites;

    private Button btn_save;

    private Button btn_history;

    //private ViewGroup save_layout;

    private ImageButton btn_favorites;

    private ConfigDao configDao;

    private RecordImageDao recordImageDao;

    public static final String SELECT_INPATIENT_NO = "selectInpatientNo";

    public static final String ACQUISITION_TIME = "acquisitionTime";

    public static final String IS_FROM_QUERY = "isFromQuery";

    private Button btn_save_bi;

    private Button btn_back;

    private Button btn_back_bi;

    private ViewGroup operation_bi;

    public static HomeFragment createInstance(String selectInpatientNo, String acquisitionTime) {
        Bundle bundle = new Bundle();
        HomeFragment fragment = new HomeFragment();
        bundle.putString(SELECT_INPATIENT_NO, selectInpatientNo);
        bundle.putString(ACQUISITION_TIME, acquisitionTime);
        bundle.putString(IS_FROM_QUERY, "");
        fragment.setArguments(bundle);
        return fragment;
    }

    public static HomeFragment createInstance(String selectInpatientNo, String acquisitionTime, String isFromQuery) {
        Bundle bundle = new Bundle();
        HomeFragment fragment = new HomeFragment();
        bundle.putString(SELECT_INPATIENT_NO, selectInpatientNo);
        bundle.putString(ACQUISITION_TIME, acquisitionTime);
        bundle.putString(IS_FROM_QUERY, isFromQuery);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity)HomeFragment.this.getActivity();
        //recordDao = new RecordDao(mActivity);
        //patientDao = new PatientDao(mActivity);
        configDao = new ConfigDao(mActivity);
        recordImageDao = new RecordImageDao(mActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_fragment, container, false);
        rootView.setClickable(true);
        configView(rootView);
        return rootView;
    }

    private void configView(final View rootView) {
        //save_layout = (ViewGroup) rootView.findViewById(R.id.save_layout);
        //detail_container = findViewById(R.id.detail_container);
        final String isFromQuery = getArguments().getString(IS_FROM_QUERY);
        btn_save_bi = (Button)rootView.findViewById(R.id.btn_save_bi);
        operation_bi = (RelativeLayout)rootView.findViewById(R.id.operation_bi);
        btn_back_bi = (Button)rootView.findViewById(R.id.btn_back_bi);
        btn_back = (Button)rootView.findViewById(R.id.btn_back);
        if (isFromQuery != null && "1".equals(isFromQuery)) {
            btn_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.getSupportFragmentManager().beginTransaction()
                            .remove(HomeFragment.this)
                            .commit();
                }
            });
            btn_back_bi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.getSupportFragmentManager().beginTransaction()
                            .remove(HomeFragment.this)
                            .commit();
                }
            });
        } else {
            btn_back.setVisibility(View.GONE);
            btn_back_bi.setVisibility(View.GONE);
        }

        final String selectInpatientNo = getArguments().getString(SELECT_INPATIENT_NO);
        final String acquisitionTime = getArguments().getString(ACQUISITION_TIME);
        //基本信息
        btn_base_info = (Button)rootView.findViewById(R.id.btn_base_info);
        btn_base_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFragment != null && currentFragment instanceof BaseInfoFragment) {
                    return;
                }
                btn_save_bi.setVisibility(View.VISIBLE);
                operation_bi.setVisibility(View.VISIBLE);
                //save_layout.setVisibility(View.GONE);
                selectMenuButton(v);
                BaseInfoFragment fragment = BaseInfoFragment.createInstance();
                currentFragment = fragment;
                mActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, fragment)
                        .commit();
            }
        });

        //档案
        btn_archives = (Button)rootView.findViewById(R.id.btn_archives);
        btn_archives.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFragment != null && currentFragment instanceof RecordFragment) {
                    return;
                }
                String inpatientNo = mActivity.getPatientInfo().getInpatientNo();
                if (inpatientNo == null || inpatientNo.length() == 0) {
                    ToastUtil.showLongToast(mActivity, getString(R.string.message_fill_info));
                    return;
                }
                btn_save_bi.setVisibility(View.VISIBLE);
                operation_bi.setVisibility(View.VISIBLE);
                //save_layout.setVisibility(View.VISIBLE);
                selectMenuButton(v);
                RecordFragment fragment = RecordFragment.createInstance();
                currentFragment = fragment;
                mActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, fragment)
                        .commit();
                /*
                View archives_container = rootView.findViewById(R.id.archives_container);
                if (archives_container.getVisibility() == View.VISIBLE) {
                    //archives_container.setVisibility(View.GONE);
                } else {
                    //archives_container.setVisibility(View.VISIBLE);
                }*/
            }
        });

        //历史数据
        btn_history = (Button)rootView.findViewById(R.id.btn_history);
        btn_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFragment != null && currentFragment instanceof HistoryFragment) {
                    return;
                }
                String inpatientNo = mActivity.getPatientInfo().getInpatientNo();
                if (inpatientNo == null || inpatientNo.length() == 0) {
                    ToastUtil.showLongToast(mActivity, getString(R.string.message_fill_info));
                    return;
                }
                btn_save_bi.setVisibility(View.GONE);
                operation_bi.setVisibility(View.GONE);
                //save_layout.setVisibility(View.GONE);
                selectMenuButton(v);
                HistoryFragment fragment = HistoryFragment.createInstance();
                currentFragment = fragment;
                mActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, fragment)
                        .commit();
            }
        });

        //照片
        btn_photo = (Button)rootView.findViewById(R.id.btn_photo);
        btn_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFragment != null && currentFragment instanceof PhotoListFragment) {
                    return;
                }
                String inpatientNo = mActivity.getPatientInfo().getInpatientNo();
                if (inpatientNo == null || inpatientNo.length() == 0) {
                    ToastUtil.showLongToast(mActivity, getString(R.string.message_fill_info));
                    return;
                }
                btn_save_bi.setVisibility(View.GONE);
                operation_bi.setVisibility(View.GONE);
                //save_layout.setVisibility(View.GONE);
                selectMenuButton(v);
                PhotoListFragment fragment = PhotoListFragment.createInstance();
                currentFragment = fragment;
                mActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, fragment)
                        .commit();
            }
        });

        //保存
        //btn_save = (Button)rootView.findViewById(R.id.btn_save);
        //Log.e("chenwh", "" + btn_save.getText());
        btn_save_bi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFragment != null && currentFragment instanceof BaseInfoFragment) {
                    boolean success = ((BaseInfoFragment)currentFragment).savePatientInfo();
                    if (success) {
                        PhotoListFragment fragment = PhotoListFragment.createInstance();
                        currentFragment = fragment;
                        //save_layout.setVisibility(View.GONE);
                        btn_save_bi.setVisibility(View.GONE);
                        operation_bi.setVisibility(View.GONE);
                        mActivity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.detail_container, fragment)
                                .commit();
                        selectMenuButton(btn_photo);
                    }

                } else if (currentFragment != null && currentFragment instanceof PhotoListFragment) {
                    RecordFragment fragment = RecordFragment.createInstance();
                    currentFragment = fragment;
                    //save_layout.setVisibility(View.VISIBLE);
                    btn_save_bi.setVisibility(View.VISIBLE);
                    operation_bi.setVisibility(View.VISIBLE);
                    mActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.detail_container, fragment)
                            .commit();
                    selectMenuButton(btn_archives);
                } else {
                    if (selectInpatientNo != null && selectInpatientNo.length() > 0) {
                        saveRecordInfo(mActivity.getRecordInfo(), false);
                    } else {
                        saveRecordInfo(mActivity.getRecordInfo(), true);
                    }
                }

            }
        });
/*
        btn_my_favorites = (Button)rootView.findViewById(R.id.btn_my_favorites);
        btn_my_favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFragment != null && currentFragment instanceof MyFavoritesFragment) {
                    return;
                }
                //save_layout.setVisibility(View.GONE);
                btn_save_bi.setVisibility(View.GONE);
                selectMenuButton(v);
                MyFavoritesFragment fragment = MyFavoritesFragment.createInstance();
                currentFragment = fragment;
                mActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, fragment)
                        .commit();
            }
        });
*/
        //PDF预览
        Button btn_upload = (Button)rootView.findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PDFProcessTask().execute();
                //uploadRecordInfo(mActivity.getPatientInfo(), mActivity.getRecordInfo());
            }
        });

        btn_favorites = (ImageButton)rootView.findViewById(R.id.btn_favorites);
        btn_favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inpatientNo = mActivity.getPatientInfo().getInpatientNo();
                if (inpatientNo == null || inpatientNo.length() == 0) {
                    ToastUtil.showLongToast(mActivity, getString(R.string.message_fill_info));
                    return;
                }

                RecordInfo recordInfo = mActivity.getRecordInfo();
                if (recordInfo == null) {
                    ToastUtil.showLongToast(mActivity, getString(R.string.message_fill_info));
                    return;
                }

                int selected = Integer.parseInt("" + btn_favorites.getTag());
                if (selected == 0) {
                    btn_favorites.setImageResource(R.mipmap.btn_favorites_s);
                    btn_favorites.setTag(1);
                    recordInfo.setFavorites(1);
                } else {
                    btn_favorites.setImageResource(R.mipmap.btn_favorites);
                    btn_favorites.setTag(0);
                    recordInfo.setFavorites(0);
                }
                mActivity.saveRecordInfo();
            }
        });



        if (selectInpatientNo != null && selectInpatientNo.length() > 0) {
            queryPatientInfo(selectInpatientNo, acquisitionTime);
        } else {
            mActivity.clearInfo();
            BaseInfoFragment fragment = BaseInfoFragment.createInstance();
            mActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, fragment)
                    .commit();
            currentFragment = fragment;
            //save_layout.setVisibility(View.GONE);
            btn_save_bi.setVisibility(View.VISIBLE);
            operation_bi.setVisibility(View.VISIBLE);
        }

        btn_favorites.setImageResource(R.mipmap.btn_favorites);
        btn_favorites.setTag(0);
        RecordInfo recordInfo = mActivity.getRecordInfo();
        if (recordInfo != null) {
            Integer selected = recordInfo.getFavorites();
            if (selected != null && selected == 1) {
                btn_favorites.setImageResource(R.mipmap.btn_favorites_s);
                btn_favorites.setTag(1);
            }
        }

    }

    private void selectMenuButton(View v) {
        btn_base_info.setBackgroundResource(R.mipmap.menu_btn_bg);
        btn_archives.setBackgroundResource(R.mipmap.menu_btn_bg);
        btn_photo.setBackgroundResource(R.mipmap.menu_btn_bg);
        //btn_my_favorites.setBackgroundResource(R.mipmap.menu_btn_bg);
        btn_history.setBackgroundResource(R.mipmap.menu_btn_bg);
        v.setBackgroundResource(R.mipmap.menu_btn_select);
    }

    private void uploadRecordInfo(PatientInfo patientInfo, RecordInfo recordInfo) {
        try {
            final int recordId = recordInfo.getId();
            List<RecordImage> images = recordImageDao.queryRecordImage("" + recordId);
            recordInfo.setPatientInfo(patientInfo);
            recordInfo.setDeepList(images);
            String recordInfoJson = MapUtils.mGson.toJson(recordInfo);
            String uuid = recordInfo.getUuid();
            String srcFolder = mActivity.getRecordPath(uuid);
            String zipFile = mActivity.getBaseDir() + File.separator + "recordInfo.zip";
            XZip.ZipFolder(srcFolder, zipFile);
            final String deviceId = configDao.queryValue(CommonUtil.DEVICE_ID);
            final String remoteUrl = configDao.queryValue(CommonUtil.REMOTE_URL);

            AlertDialogUtil.showAlertDialog(mActivity, mActivity.getString(R.string.message_title_tip), mActivity.getString(R.string.message_wait));
            String url = CommonUtil.initUrl(remoteUrl, "syncRecordInfo");
            Log.d(TAG, "同步创伤信息,url=" + url);
            OkHttpClientManager.postAsyn(url, new OkHttpClientManager.ResultCallback<String>() {
                @Override
                public void onError(Request request, Exception e) {
                    AlertDialogUtil.dismissAlertDialog(mActivity);
                    ToastUtil.showLongToast(mActivity, "同步创伤信息失败！");
                    Log.e(TAG, "同步创伤信息失败", e);
                }

                @Override
                public void onResponse(String resultStr) {
                    AlertDialogUtil.dismissAlertDialog(mActivity);
                    PatientResponse res = MapUtils.mGson.fromJson(resultStr, PatientResponse.class);
                    if (!res.isSuccess()) {
                        String message = res.getMessage();
                        ToastUtil.showLongToast(mActivity, "同步创伤信息失败！原因是：" + message);
                    } else {
                        ToastUtil.showLongToast(mActivity, "同步创伤信息成功！");

                        mActivity.updateSyncFlag(recordId);
                    }
                }
            },new File(zipFile), "record",
                    new OkHttpClientManager.Param("recordInfo", recordInfoJson),
                    new OkHttpClientManager.Param("deviceId", deviceId));

        } catch (Exception e) {
            Log.e(TAG, "同步创伤信息失败", e);
            ToastUtil.showLongToast(mActivity, "同步创伤信息失败！");
        }


    }

    private void remoteSavePatient(final PatientInfo patientInfo) {
        String url = CommonUtil.initUrl(mActivity, "createPatient");
        Log.d(TAG, "保存患者信息,url=" + url);
        String docId = SharedPreferenceUtil.getSharedPreferenceValue(mActivity, CommonUtil.LOGIN_ID);
        patientInfo.setDoctorId(Integer.parseInt(docId));

        AlertDialogUtil.showAlertDialog(mActivity,
                mActivity.getString(R.string.message_title_tip),
                mActivity.getString(R.string.message_wait));
        OkHttpClientManager.postAsyn(url, new OkHttpClientManager.ResultCallback<String>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        AlertDialogUtil.dismissAlertDialog(mActivity);
                        ToastUtil.showLongToast(mActivity, "保存患者信息失败！");
                        Log.e(TAG, "保存患者信息失败", e);
                    }

                    @Override
                    public void onResponse(String resultStr) {
                        AlertDialogUtil.dismissAlertDialog(mActivity);
                        PatientResponse res = MapUtils.mGson.fromJson(resultStr, PatientResponse.class);
                        if (!res.isSuccess()) {
                            String message = res.getMessage();
                            ToastUtil.showLongToast(mActivity, "保存患者信息失败！原因是：" + message);
                        } else {

                            //档案
                            RecordFragment1 fragment = RecordFragment1.createInstance();
                            mActivity.setPatientInfo(patientInfo);
                            mActivity.getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.detail_container, fragment)
                                    .commit();
                        }
                    }
                },
                MapUtils.bean2Params(patientInfo)
        );
    }

    private void queryPatientInfo(final String inpatientNo, final String recordTime) {
        PatientDao patientDao = mActivity.getPatientDao();
        PatientInfo patientInfo = patientDao.queryPatientInfo(inpatientNo);
        mActivity.setPatientInfo(patientInfo);

        if (recordTime != null && recordTime.length() > 0) {
            RecordDao recordDao = mActivity.getRecordDao();
            RecordInfo recordInfo = recordDao.queryRecordInfo(inpatientNo, recordTime);
            mActivity.setRecordInfo(recordInfo);
        } else {
            RecordInfo recordInfo = new RecordInfo();
            recordInfo.setInpatientNo(inpatientNo);
            mActivity.setRecordInfo(recordInfo);
            saveRecordInfo(recordInfo, false);
        }
        RecordFragment fragment = RecordFragment.createInstance();
        mActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.detail_container, fragment)
                .commit();
        selectMenuButton(btn_archives);
        //save_layout.setVisibility(View.VISIBLE);
        btn_save_bi.setVisibility(View.VISIBLE);
        operation_bi.setVisibility(View.VISIBLE);
    }

    private void queryRemotePatientInfo(final String inpatientNo, final String acquisitionTime) {
        String url = CommonUtil.initUrl(mActivity, "queryPatient?inpatientNo="+inpatientNo);
        Log.d(TAG, "获取患者信息,url="+url);

        AlertDialogUtil.showAlertDialog(mActivity,
                mActivity.getString(R.string.message_title_tip),
                mActivity.getString(R.string.message_wait));
        OkHttpClientManager.getAsyn(url, new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                AlertDialogUtil.dismissAlertDialog(mActivity);
                ToastUtil.showLongToast(mActivity, "获取患者信息失败！");
                Log.e(TAG, "获取患者信息失败", e);
            }
            @Override
            public void onResponse(String resultStr) {
                AlertDialogUtil.dismissAlertDialog(mActivity);
                PatientResponse patientResponse = MapUtils.mGson.fromJson(resultStr, PatientResponse.class);
                if (!patientResponse.isSuccess()) {
                    String message = patientResponse.getMessage();
                    ToastUtil.showLongToast(mActivity, "获取患者信息失败！原因是：" + message);
                    return;
                }
                mActivity.setPatientInfo(patientResponse.getPatient());

                if (acquisitionTime != null && acquisitionTime.length() > 0) {
                    queryRecordInfo(inpatientNo, acquisitionTime);
                } else {
                    RecordInfo recordInfo = new RecordInfo();
                    recordInfo.setInpatientNo(inpatientNo);
                    mActivity.setRecordInfo(recordInfo);
                    RecordFragment1 fragment = new RecordFragment1();
                    mActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.detail_container, fragment)
                            .commit();
                }



            }
        });
    }

    private void queryRecordInfo(String inpatientNo, String recordTime) {
        String url = CommonUtil.initUrl(mActivity, "queryRecord?inpatientNo="+inpatientNo + "&recordTime=" + recordTime);
        Log.d(TAG, "获取创伤记录信息,url="+url);

        AlertDialogUtil.showAlertDialog(mActivity,
                mActivity.getString(R.string.message_title_tip),
                mActivity.getString(R.string.message_wait));
        OkHttpClientManager.getAsyn(url, new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                AlertDialogUtil.dismissAlertDialog(mActivity);
                ToastUtil.showLongToast(mActivity, "获取创伤记录信息失败！");
                Log.e(TAG, "获取创伤记录信息失败", e);
            }
            @Override
            public void onResponse(String resultStr) {
                AlertDialogUtil.dismissAlertDialog(mActivity);
                RecordResponse patientResponse = MapUtils.mGson.fromJson(resultStr, RecordResponse.class);
                if (!patientResponse.isSuccess()) {
                    String message = patientResponse.getMessage();
                    ToastUtil.showLongToast(mActivity, "获取创伤记录信息失败！原因是：" + message);
                    return;
                }
                mActivity.setRecordInfo(patientResponse.getRecord());
                RecordFragment1 fragment = new RecordFragment1();
                mActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, fragment)
                        .commit();
            }
        });
    }

    private void startFragment(Fragment fragment) {
        String inpatientNo = mActivity.getPatientInfo().getInpatientNo();
        if (inpatientNo == null || inpatientNo.length() == 0) {
            ToastUtil.showLongToast(mActivity, getString(R.string.message_fill_info));
            return;
        }
        mActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.detail_container, fragment)
                .commit();
    }
    private void saveRecordInfo(RecordInfo recordInfo, boolean clearInfo) {
        mActivity.saveRecordInfo();
        ToastUtil.showLongToast(mActivity, "保存创伤记录信息成功！");
        if (clearInfo) {
            //无需清除数据
            //mActivity.clearInfo();
        }

        //BaseInfoFragment fragment = BaseInfoFragment.createInstance();
        //mActivity.getSupportFragmentManager().beginTransaction()
        //        .replace(R.id.detail_container, fragment)
        //        .commit();
    }

    private void saveRemoteRecordInfo(RecordInfo recordInfo) {
        String docId = SharedPreferenceUtil.getSharedPreferenceValue(mActivity, CommonUtil.LOGIN_ID);
        recordInfo.setDoctorId(Integer.parseInt(docId));
        String url = CommonUtil.initUrl(mActivity, "saveRecord");
        Log.d(TAG, "保存创伤记录信息,url=" + url);

        AlertDialogUtil.showAlertDialog(mActivity,
                mActivity.getString(R.string.message_title_tip),
                mActivity.getString(R.string.message_wait));
        OkHttpClientManager.postAsyn(url, new OkHttpClientManager.ResultCallback<String>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        AlertDialogUtil.dismissAlertDialog(mActivity);
                        ToastUtil.showLongToast(mActivity, "保存创伤记录信息失败！");
                        Log.e(TAG, "保存创伤记录信息失败", e);
                    }

                    @Override
                    public void onResponse(String resultStr) {
                        AlertDialogUtil.dismissAlertDialog(mActivity);
                        RecordResponse patientResponse = MapUtils.mGson.fromJson(resultStr, RecordResponse.class);
                        if (!patientResponse.isSuccess()) {
                            String message = patientResponse.getMessage();
                            ToastUtil.showLongToast(mActivity, "保存创伤记录信息失败！原因是：" + message);
                        } else {
                            ToastUtil.showLongToast(mActivity, "保存创伤记录成功");
                        }
                    }
                },
                MapUtils.bean2Params(recordInfo)
        );

/*
        String inpatientNo = recordInfo.getInpatientNo();
        String acquisitionTime = recordInfo.getAcquisitionTime();
        RecordInfo dbRecordInfo = recordDao.queryRecordInfo(inpatientNo, acquisitionTime);
        if (dbRecordInfo != null) {
            recordInfo.setId(dbRecordInfo.getId());
            String login_user = SharedPreferenceUtil.getSharedPreferenceValue(mActivity, CommonUtil.LOGIN_USER);
            boolean success = recordDao.updateRecordInfo(recordInfo, login_user);
            if (success) {
                ToastUtil.showLongToast(mActivity, "采集信息更新成功");
            } else {
                ToastUtil.showLongToast(mActivity, "采集信息更新失败");
            }
            return success;
        } else {
            String login_user = SharedPreferenceUtil.getSharedPreferenceValue(mActivity, CommonUtil.LOGIN_USER);
            boolean success = recordDao.insertRecordInfo(recordInfo, login_user);
            if (success) {
                ToastUtil.showLongToast(mActivity, "采集信息保存成功");
            } else {
                ToastUtil.showLongToast(mActivity, "采集信息保存失败");
            }
            return success;
        }
        */
    }

    private class PDFProcessTask extends AsyncTask<String, Object, String> {
        //private RecordImage image;

        @Override
        protected void onPreExecute() {
            AlertDialogUtil.showAlertDialog(mActivity,
                    mActivity.getString(R.string.message_title_tip),
                    mActivity.getString(R.string.message_wait));
        }

        @Override
        protected String doInBackground(String... params) {
            String filePath = mActivity.getBaseDir() + "/report" + "/chapter_title.pdf";
            try {
                PdfViewer.createPdf(mActivity, filePath, mActivity.getPatientInfo(), mActivity.getRecordInfo());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            }
            return filePath;
        }

        @Override
        protected void onPostExecute(String filePath) {
            AlertDialogUtil.dismissAlertDialog(mActivity);
            //File file = new File(filePath);
            /* 内置PDF打开
            Intent intent = new Intent(mActivity, PdfViewActivity.class);
            intent.putExtra("filePath", filePath);
            startActivityForResult(intent, 1001);
            */
            //外部PDF打开
            File file = new File(filePath);
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType (Uri.fromFile(file), "application/pdf");
            startActivity(Intent.createChooser(intent, "请选择程序打开伤口报告"));
        }

    }

}
