package com.dnion.app.android.injuriesapp;

import android.Manifest;
import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dnion.app.android.injuriesapp.dao.ConfigDao;
import com.dnion.app.android.injuriesapp.dao.DeepCameraInfo;
import com.dnion.app.android.injuriesapp.dao.DeepCameraInfoDao;
import com.dnion.app.android.injuriesapp.dao.PatientDao;
import com.dnion.app.android.injuriesapp.dao.PatientInfo;
import com.dnion.app.android.injuriesapp.dao.RecordDao;
import com.dnion.app.android.injuriesapp.dao.RecordImage;
import com.dnion.app.android.injuriesapp.dao.RecordImageDao;
import com.dnion.app.android.injuriesapp.dao.RecordInfo;
import com.dnion.app.android.injuriesapp.utils.CommonUtil;
import com.dnion.app.android.injuriesapp.utils.ImageTools;
import com.dnion.app.android.injuriesapp.utils.SDCardHelper;
import com.dnion.app.android.injuriesapp.utils.SharedPreferenceUtil;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    public static final String TAG = "main_activty";

    private PatientInfo patientInfo = new PatientInfo();

    private RecordInfo recordInfo = new RecordInfo();

    private MainActivity mainActivity;

    private RecordDao recordDao;

    private PatientDao patientDao;

    private ConfigDao configDao;

    private RecordImageDao recordImageDao;

    private DeepCameraInfoDao deepCameraInfoDao;

    private ImageView image_perview;

    private RelativeLayout image_perview_panel;

    private ImageButton btn_close_panel;

    private DeepCameraInfo deepCameraInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;
        recordDao = new RecordDao(this);
        patientDao = new PatientDao(this);
        recordImageDao = new RecordImageDao(this);
        deepCameraInfoDao = new DeepCameraInfoDao();
        configDao = new ConfigDao(this);

        settingDocTitle();
        addTopButtonEvent();
        image_perview = (ImageView) findViewById(R.id.image_perview);
        image_perview_panel = (RelativeLayout) findViewById(R.id.image_perview_panel);
        btn_close_panel = (ImageButton) findViewById(R.id.btn_close_panel);
        btn_close_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image_perview.setImageBitmap(null);
                image_perview_panel.setVisibility(View.GONE);
            }
        });

        /*
        Intent intent = getIntent();
        String selectInpatientNo = intent.getStringExtra("selectInpatientNo");
        String acquisitionTime = intent.getStringExtra("acquisitionTime");
        if (selectInpatientNo != null && selectInpatientNo.length() > 0) {
            patientInfo = patientDao.queryPatientInfo(selectInpatientNo);
            mainActivity.settingPatientTitle(patientInfo.getName(), patientInfo.getInpatientNo());
            if (acquisitionTime != null && acquisitionTime.length() > 0) {
                recordInfo = recordDao.queryRecordInfo(selectInpatientNo, acquisitionTime);
            }
            recordInfo.setInpatientNo(selectInpatientNo);
        }
        */
        // 加载opencv
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }

        TopMenuFragment fragment = TopMenuFragment.createInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, fragment)
                .commit();
    }

    /**
     * 加载opencv
     */
    @Override
    public void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) { //默认加载opencv_java.so库
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
            //加载依赖opencv_java.so的jni库
            System.loadLibrary("opencv_java");
        }
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    // Load native library after(!) OpenCV initialization
                    System.loadLibrary("rooxin_camm");
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };


    public String queryConfig(String key) {
        return configDao.queryValue(key);
    }

    public void saveConfig(String key, String value) {
        configDao.saveValue(key, value);
    }

/*
    private boolean saveRecordInfo(RecordInfo recordInfo) {
        String inpatientNo = recordInfo.getInpatientNo();
        String acquisitionTime = recordInfo.getAcquisitionTime();
        RecordInfo dbRecordInfo = recordDao.queryRecordInfo(inpatientNo, acquisitionTime);
        if (dbRecordInfo != null) {
            recordInfo.setId(dbRecordInfo.getId());
            String login_user = SharedPreferenceUtil.getSharedPreferenceValue(mainActivity, CommonUtil.LOGIN_USER);
            boolean success = recordDao.updateRecordInfo(recordInfo, login_user);
            if (success) {
                ToastUtil.showLongToast(mainActivity, "采集信息更新成功");
            } else {
                ToastUtil.showLongToast(mainActivity, "采集信息更新失败");
            }
            return success;
        } else {
            String login_user = SharedPreferenceUtil.getSharedPreferenceValue(mainActivity, CommonUtil.LOGIN_USER);
            boolean success = recordDao.insertRecordInfo(recordInfo, login_user);
            if (success) {
                ToastUtil.showLongToast(mainActivity, "采集信息保存成功");
            } else {
                ToastUtil.showLongToast(mainActivity, "采集信息保存失败");
            }
            return success;
        }
    }
*/

    public PatientInfo getPatientInfo() {
        return patientInfo;
    }

    public RecordInfo getRecordInfo() {
        return recordInfo;
    }

    public RecordDao getRecordDao() {
        return recordDao;
    }

    public PatientDao getPatientDao() {
        return patientDao;
    }

    public String getRecordUuid() {
        if (recordInfo == null) {
            return null;
        }
        RecordInfo db = recordDao.queryRecordInfo(recordInfo.getInpatientNo(), recordInfo.getRecordTime());
        if (db == null) {
            return null;
        }
        return db.getUuid();
    }

    public Integer getRecordId() {
        if (recordInfo == null) {
            return null;
        }
        RecordInfo db = recordDao.queryRecordInfo(recordInfo.getInpatientNo(), recordInfo.getRecordTime());
        if (db == null) {
            return null;
        }
        return db.getId();
    }

    public ImageView getImagePreView() {
        return image_perview;
    }

    public String getImagePath() {
        //return SDCardHelper.getSDCardBaseDir()+"/image.jpg";
        return getBaseDir() + File.separator + File.separator + "image.jpg";
    }

    public String getImagePath(String type) {
        String path = getRecordPath(getRecordUuid()) + File.separator + type;
        return path;
    }

    public String getImagePathBase() {
        String path = getRecordPath(getRecordUuid());
        return path;
    }

    public String getRecordPath(String uuid) {
        String path = getBaseDir() + File.separator + uuid;
        return path;
    }

    public void showImage(String name) {
        //String picPath = getImagePath(type) + File.separator + name;
        String picPath = getImagePathBase() + File.separator + name;
        Bitmap bitmap = BitmapFactory.decodeFile(picPath);
        ImageView imageView = getImagePreView();
        imageView.setImageBitmap(bitmap);
        image_perview_panel.setVisibility(View.VISIBLE);
    }

    public void updateSyncFlag(int recordId) {
        recordDao.updateSyncFlag(recordId);
    }

    public void saveRecordInfo() {
        String docId = SharedPreferenceUtil.getSharedPreferenceValue(mainActivity, CommonUtil.LOGIN_ID);
        recordInfo.setDoctorId(Integer.parseInt(docId));
        Integer id = recordInfo.getId();
        if (id != null && id > 0) {
            recordDao.updateRecordInfo(recordInfo);
        } else {
            recordDao.insertRecordInfo(recordInfo);
            recordInfo.setId(getRecordId());
        }
    }

    public void saveDeepCameraInfo() {
        String path = deepCameraInfo.getFilepath();
        deepCameraInfoDao.save(deepCameraInfo);
        if (deepCameraInfo.isNew()) {
            RecordImage ri = new RecordImage();
            ri.setRecordId(getRecordId());
            ri.setImageType("deep");
            ri.setImagePath(path);
            recordImageDao.insertRecordImage(ri);
            deepCameraInfo.setNew(false);
        }
    }

    public void saveIRCameraInfo() {
        String path = deepCameraInfo.getFilepath();
        deepCameraInfoDao.save(deepCameraInfo);
        if (deepCameraInfo.isNew()) {
            RecordImage ri = new RecordImage();
            ri.setRecordId(getRecordId());
            ri.setImageType("ir");
            ri.setImagePath(path);
            recordImageDao.insertRecordImage(ri);
            deepCameraInfo.setNew(false);
        }
    }

    public void saveHotCameraInfo(String path) {
        if (path == null) {
            return;
        }
        RecordImage ri = new RecordImage();
        ri.setRecordId(getRecordId());
        ri.setImageType("hot");
        ri.setImagePath(path);
        recordImageDao.insertRecordImage(ri);
    }

    public DeepCameraInfo queryDeepCameraInfo(String path) {
        return deepCameraInfoDao.getDeepCameraInfo(path);
    }

    public void saveImage(Bitmap bitMap, String path, String photoName) {
        ImageTools.savePhotoToSDCard(bitMap, path, photoName);
    }

    public void saveMicro(File f, String path, String fileName) {
        ImageTools.saveMicroToSDCard(f, path, fileName);
    }

    public void saveRecordImage(RecordImage ri) {
        recordImageDao.insertRecordImage(ri);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            quit();
        }
        return false;
    }

    public void setPatientInfo(PatientInfo patientInfo) {
        this.patientInfo = patientInfo;
        if (recordInfo != null) {
            recordInfo.setInpatientNo(patientInfo.getInpatientNo());
        }
        settingPatientTitle(patientInfo.getName(), patientInfo.getInpatientNo());
    }

    public void setRecordInfo(RecordInfo recordInfo) {
        this.recordInfo = recordInfo;
    }

    public void clearInfo() {
        this.patientInfo = new PatientInfo();
        this.recordInfo = null;
        settingPatientTitle("", "");
    }

    public String getBaseDir() {
        String path = SDCardHelper.getSDCardBaseDir() + File.separator + "wound";
        //String path = SDCardHelper.getSDCardPrivateFilesDir(mainActivity, "wound");
        return path;
    }


    public String getMicPath() {
        String path = getImagePath("mic");
        return path;
    }

    public DeepCameraInfo getDeepCameraInfo() {
        if (deepCameraInfo == null) {
            deepCameraInfo = new DeepCameraInfo();
        }
        return deepCameraInfo;
    }

    public void setDeepCameraInfo(DeepCameraInfo deepCameraInfo) {
        this.deepCameraInfo = deepCameraInfo;
    }

    public Bitmap getPdfRgbImage(String recordId) {
        List<RecordImage> imageList = recordImageDao.queryRecordImage(recordId, "'deep'");
        if (imageList.size() <= 0) {
            return null;
        }
        RecordImage image = imageList.get(0);
        DeepCameraInfo dci = queryDeepCameraInfo(image.getImagePath());
        return dci.getRgbBitmap();
    }

    public Bitmap getPdfDeepImage(String recordId) {
        List<RecordImage> imageList = recordImageDao.queryRecordImage(recordId, "'deep'");
        if (imageList.size() <= 0) {
            return null;
        }
        RecordImage image = imageList.get(0);
        DeepCameraInfo dci = queryDeepCameraInfo(image.getImagePath());
        return dci.getPdfBitmap();
    }

    public Bitmap getPdfIrImage(String recordId) {
        List<RecordImage> imageList = recordImageDao.queryRecordImage(recordId, "'ir'");
        if (imageList.size() <= 0) {
            return null;
        }
        RecordImage image = imageList.get(0);
        DeepCameraInfo dci = queryDeepCameraInfo(image.getImagePath());
        return dci.getPdfBitmap();
    }

    public String getPdfRgbImagePath(String recordId) {
        //return getBaseDir() +  "/61d88446b2104bb39eb7fab498017a7e/deep/19700101002717/"+ DeepCameraInfoDao.RGB_FILE_NAME;
        List<RecordImage> imageList = recordImageDao.queryRecordImage(recordId, "'deep'");
        if (imageList.size() <= 0) {
            return null;
        }
        RecordImage image = imageList.get(0);
        return image.getImagePath()+ File.separator + DeepCameraInfoDao.RGB_FILE_NAME;
    }

    public String getPdfDeepImagePath(String recordId) {
        //return getBaseDir() +  "/61d88446b2104bb39eb7fab498017a7e/deep/19700101002717/"+ DeepCameraInfoDao.RGB_FILE_NAME;
        List<RecordImage> imageList = recordImageDao.queryRecordImage(recordId, "'deep'");
        if (imageList.size() <= 0) {
            return null;
        }
        RecordImage image = imageList.get(0);
        return image.getImagePath()+ File.separator + DeepCameraInfoDao.PDF_IMAGE_FILE_NAME;
    }

    public String getPdfIrImagePath(String recordId) {
        //return getBaseDir() +  "/61d88446b2104bb39eb7fab498017a7e/deep/19700101002717/"+ DeepCameraInfoDao.RGB_FILE_NAME;
        List<RecordImage> imageList = recordImageDao.queryRecordImage(recordId, "'ir'");
        if (imageList.size() <= 0) {
            return null;
        }
        RecordImage image = imageList.get(0);
        return image.getImagePath()+ File.separator + DeepCameraInfoDao.PDF_IMAGE_FILE_NAME;
    }

    // 保存MyTouchListener接口的列表
    private ArrayList<MyTouchListener> myTouchListeners = new ArrayList<MainActivity.MyTouchListener>();

    /**
     * 提供给Fragment通过getActivity()方法来注册自己的触摸事件的方法
     *
     * @param listener
     */
    public void registerMyTouchListener(MyTouchListener listener) {
        myTouchListeners.add(listener);
    }

    /**
     * 提供给Fragment通过getActivity()方法来取消注册自己的触摸事件的方法
     *
     * @param listener
     */
    public void unRegisterMyTouchListener(MyTouchListener listener) {
        myTouchListeners.remove(listener);
    }

    /**
     * 分发触摸事件给所有注册了MyTouchListener的接口
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (MyTouchListener listener : myTouchListeners) {
            listener.onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    public interface MyTouchListener {
        public void onTouchEvent(MotionEvent event);
    }
}
