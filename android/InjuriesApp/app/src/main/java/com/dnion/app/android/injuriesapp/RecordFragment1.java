package com.dnion.app.android.injuriesapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.dnion.app.android.injuriesapp.dao.PatientInfo;
import com.dnion.app.android.injuriesapp.dao.RecordDao;
import com.dnion.app.android.injuriesapp.dao.RecordInfo;
import com.dnion.app.android.injuriesapp.ui.PlayItem;
import com.dnion.app.android.injuriesapp.utils.BaseTextWatcher;
import com.dnion.app.android.injuriesapp.utils.CommonUtil;
import com.dnion.app.android.injuriesapp.utils.DateUtils;
import com.dnion.app.android.injuriesapp.utils.SDCardHelper;
import com.dnion.app.android.injuriesapp.utils.ToastUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fan.soundrecordingdemo.utils.AudioRecoderUtils;
import fan.soundrecordingdemo.utils.PopupWindowFactory;
import fan.soundrecordingdemo.utils.RecordPlayer;
import fan.soundrecordingdemo.utils.TimeUtils;

import static android.util.TypedValue.COMPLEX_UNIT_SP;

/**
 * Created by 卫华 on 2017/4/15.
 */

public class RecordFragment1 extends Fragment {

    public static final String TAG = "record_fragment1";

    private MainActivity mActivity;

    private PopupWindowFactory mPop;

    private ImageView mImageView;

    private TextView mTextView;

    private Button mButton;

    private AudioRecoderUtils mAudioRecoderUtils;

    private RecordPlayer recordPlayer;

    private LinearLayout play_list;

    private AlertDialog dialog;

    private View rootView;
    public static RecordFragment1 createInstance() {
        RecordFragment1 fragment = new RecordFragment1();

        Bundle bundle = new Bundle();
        //bundle.putLong(ARGUMENT_USER_ID, userId);
        //bundle.putString(ARGUMENT_USER_NAME, userName);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity)RecordFragment1.this.getActivity();
        recordPlayer = new RecordPlayer(mActivity);
        //userDao = new UserDao(mActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.record1_fragment, container, false);
        configView(rootView);
        return rootView;
    }

    private void configView(View rootView) {
        /*
        if (getArguments() == null || getArguments().getString("selectInpatientNo") == null) {
            ToastUtil.showLongToast(mActivity, "先填写患者基本信息");
            return;
        }
        String selectInpatientNo = getArguments().getString("selectInpatientNo");
        */
        final RecordInfo patientInfo = mActivity.getRecordInfo();
        //patientInfo.setInpatientNo(selectInpatientNo);
        //其他伤口类型描述
        final EditText wound_type_desc = (EditText)rootView.findViewById(R.id.wound_type_desc);
        wound_type_desc.setText(patientInfo.getWoundTypeDesc());
        wound_type_desc.addTextChangedListener(new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                patientInfo.setWoundTypeDesc(s.toString());
            }
        });

        //伤口类型
        Spinner wound_type = (Spinner)rootView.findViewById(R.id.wound_type);
        final TypeArrayAdapter typeAdapter = new TypeArrayAdapter(mActivity, android.R.layout.simple_spinner_item, ArchivesData.typeDict);
        wound_type.setAdapter(typeAdapter);
        CommonUtil.setSpinnerItemSelectedByValue(wound_type, patientInfo.getWoundType());
        wound_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view;
                //tv.setTextColor(Color.WHITE);
                tv.setTextSize(CommonUtil.SELECT_TEXT_SIZE);
                Pair pair = (Pair)typeAdapter.getItem(position);
                if (pair != null) {
                    //ToastUtil.showLongToast(mActivity, "" + pair.second);
                    patientInfo.setWoundType((Integer)pair.first);
                    if (0 == Integer.parseInt("" + pair.first)) {
                        wound_type_desc.setVisibility(View.VISIBLE);
                    } else {
                        wound_type_desc.setVisibility(View.INVISIBLE);
                    }
                    return;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        View view =LayoutInflater.from(mActivity).inflate(R.layout.player_dialog, null);
        play_list = (LinearLayout)view.findViewById(R.id.play_list);
        builder.setView(view);
        builder.setTitle("录音列表");
        builder.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();

        //伤口尺寸-长
        EditText wound_width = (EditText)rootView.findViewById(R.id.wound_width);
        if (patientInfo.getWoundWidth() != null) {
            wound_width.setText("" + patientInfo.getWoundWidth());
        }
        wound_width.addTextChangedListener(new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                patientInfo.setWoundWidth(Float.parseFloat(s.toString()));
            }
        });

        //伤口尺寸-宽
        EditText wound_height = (EditText)rootView.findViewById(R.id.wound_height);
        if (patientInfo.getWoundHeight() != null) {
            wound_height.setText("" + patientInfo.getWoundHeight());
        }
        wound_height.addTextChangedListener(new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                patientInfo.setWoundHeight(Float.parseFloat(s.toString()));
            }
        });

        //伤口尺寸-深度
        EditText wound_deep = (EditText)rootView.findViewById(R.id.wound_deep);
        if (patientInfo.getWoundDeep() != null) {
            wound_deep.setText("" + patientInfo.getWoundDeep());
        }
        wound_deep.addTextChangedListener(new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                patientInfo.setWoundDeep(Float.parseFloat(s.toString()));
            }
        });

        //伤口尺寸-面积
        EditText wound_area = (EditText)rootView.findViewById(R.id.wound_area);
        if (patientInfo.getWoundArea() != null) {
            wound_area.setText("" + patientInfo.getWoundArea());
        }
        wound_area.addTextChangedListener(new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                patientInfo.setWoundArea(Float.parseFloat(s.toString()));
            }
        });

        //伤口尺寸-容积
        EditText wound_volume = (EditText)rootView.findViewById(R.id.wound_volume);
        if (patientInfo.getWoundVolume() != null) {
            wound_volume.setText("" + patientInfo.getWoundVolume());
        }
        wound_volume.addTextChangedListener(new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                patientInfo.setWoundVolume(Float.parseFloat(s.toString()));
            }
        });

        //伤口形成时间
        String woundDate = patientInfo.getWoundTime();
        Calendar calendar = Calendar.getInstance();
        if (woundDate != null && woundDate.length() > 0) {
            calendar.setTime(DateUtils.parseDate("yyyy-MM-dd", woundDate));
        }
        final TextView wound_date = (TextView)rootView.findViewById(R.id.wound_date);
        wound_date.setText(DateFormat.format("yyyy-MM-dd", calendar));
        wound_date.setTag(calendar);
        wound_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = (Calendar)view.getTag();
                new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        Calendar calecndar = Calendar.getInstance();
                        calecndar.set(Calendar.YEAR, year);
                        calecndar.set(Calendar.MONTH, month);
                        calecndar.set(Calendar.DAY_OF_MONTH, day);
                        wound_date.setTag(calecndar);
                        wound_date.setText(DateFormat.format("yyyy-MM-dd", calecndar));
                        patientInfo.setWoundTime(wound_date.getText().toString());
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH) ).show();
            }
        });

        //目前采取的措施
        Spinner wound_measures = (Spinner)rootView.findViewById(R.id.wound_measures);
        final TypeArrayAdapter measuresAdapter = new TypeArrayAdapter(mActivity, android.R.layout.simple_spinner_item, ArchivesData.measuresDict);
        wound_measures.setAdapter(measuresAdapter);
        CommonUtil.setSpinnerItemSelectedByValue(wound_measures, patientInfo.getWoundMeasures());
        wound_measures.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view;
                //tv.setTextColor(Color.WHITE);
                tv.setTextSize(CommonUtil.SELECT_TEXT_SIZE);
                Pair pair = (Pair)measuresAdapter.getItem(position);
                if (pair != null) {
                    //ToastUtil.showLongToast(mActivity, "" + pair.second);
                    patientInfo.setWoundMeasures((Integer)pair.first);
                    return;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //下一步
        /*
        Button btn_step_next = (Button)rootView.findViewById(R.id.btn_step_next);
        btn_step_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.saveRecordInfo();

                RecordFragment2 fragment = RecordFragment2.createInstance();
                mActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, fragment)
                        .commit();
            }
        });
        */


        Button btn_player_mic = (Button)rootView.findViewById(R.id.btn_player_mic);
        btn_player_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出播放列表
                play_list.removeAllViews();
                String path = mActivity.getMicPath();
                String complains = patientInfo.getComplains();
                String[] complainsList = complains.split(",");
                for (String name : complainsList) {
                    PlayItem item = new PlayItem(mActivity);
                    item.setPath(path, name);
                    item.setRecordPlayer(recordPlayer);
                    play_list.addView(item);
                }
                dialog.show();

                /*
                String complains = patientInfo.getComplains();
                if (complains == null || complains.length() == 0) {
                    ToastUtil.showLongToast(mActivity, "请先录音");
                    return;
                }
                File f = new File(complains);
                if (f == null || !f.exists()) {
                    ToastUtil.showLongToast(mActivity, "请先录音");
                    return;
                }
                recordPlayer.playRecordFile(f);
                */
            }
        });

        final String micPath = mActivity.getBaseDir();
        mButton = (Button)rootView.findViewById(R.id.btn_record_mic);
        //PopupWindow的布局文件
        view = View.inflate(mActivity, R.layout.layout_microphone, null);
        mPop = new PopupWindowFactory(mActivity,view);
        /*
        mPop.getPopupWindow().setTouchable(true);
        mPop.getPopupWindow().setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Toast.makeText(mActivity, "录音保存在：" + filePath, Toast.LENGTH_SHORT).show();
                mTextView.setText(TimeUtils.long2String(0));
                String complainsPath = micPath + File.separator + "microphone.amr" ;;
                patientInfo.setComplains(complainsPath);
                mActivity.saveRecordInfo();

                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });
        */

        //PopupWindow布局文件里面的控件
        mImageView = (ImageView) view.findViewById(R.id.iv_recording_icon);
        mTextView = (TextView) view.findViewById(R.id.tv_recording_time);

        mAudioRecoderUtils = new AudioRecoderUtils(micPath);

        //录音回调
        mAudioRecoderUtils.setOnAudioStatusUpdateListener(new AudioRecoderUtils.OnAudioStatusUpdateListener() {

            //录音中....db为声音分贝，time为录音时长
            @Override
            public void onUpdate(double db, long time) {
                mImageView.getDrawable().setLevel((int) (3000 + 6000 * db / 100));
                mTextView.setText(TimeUtils.long2String(time));
            }

            //录音结束，filePath为保存路径
            @Override
            public void onStop(String filePath) {
                //Toast.makeText(mActivity, "录音保存在：" + filePath, Toast.LENGTH_SHORT).show();
                File in = new File(micPath + File.separator + "microphone.amr");
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String micFileName = df.format(new Date());
                String fileName = micFileName + ".amr";
                String path = mActivity.getMicPath();
                mActivity.saveMicro(in, path, fileName);
                mTextView.setText(TimeUtils.long2String(0));
                String complains = patientInfo.getComplains();

                if (complains == null) {
                    complains = fileName;
                } else {
                    if (complains.length() > 0) {
                        complains += ",";
                    }
                    complains += fileName;
                }

                patientInfo.setComplains(complains);
                mActivity.saveRecordInfo();
            }
        });

        startListener();

    }

    public void startListener(){
        /*
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = (String)mButton.getTag();
                if ("0".equals(tag)) {
                    mButton.setTag("1");
                    mPop.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                    mButton.setText("停止");
                    mAudioRecoderUtils.startRecord();
                } else {
                    mButton.setTag("0");
                    mAudioRecoderUtils.stopRecord();        //结束录音（保存录音文件）
                    mPop.dismiss();
                    mButton.setText("录音");
                }

            }
        });
        */

        //Button的touch监听
        mButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){

                    case MotionEvent.ACTION_DOWN:

                        mPop.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                        mButton.setText("松开保存");
                        mAudioRecoderUtils.startRecord();
                        break;

                    case MotionEvent.ACTION_UP:

                        mAudioRecoderUtils.stopRecord();        //结束录音（保存录音文件）
//                        mAudioRecoderUtils.cancelRecord();    //取消录音（不保存录音文件）
                        mPop.dismiss();
                        mButton.setText("按住录音");
                        break;
                }
                return true;
            }
        });

    }

}
