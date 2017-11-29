package com.dnion.app.android.injuriesapp.ui;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.dnion.app.android.injuriesapp.MainActivity;
import com.dnion.app.android.injuriesapp.R;
import com.dnion.app.android.injuriesapp.dao.RecordInfo;

import java.io.File;

import fan.soundrecordingdemo.utils.RecordPlayer;

/**
 * Created by 卫华 on 2017/8/8.
 */

public class PlayItem extends FrameLayout {

    private MainActivity mActivity;

    private File path;

    private RecordPlayer recordPlayer;

    private ImageButton btn_delete;

    private String name;

    private Button btn_item;

    public PlayItem(@NonNull Context context) {
        super(context);
        initView(context);
    }
    public PlayItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    public PlayItem(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(@NonNull Context context) {
        mActivity = (MainActivity)context;
        //initStateListDrawable();
        // 加载自定义布局到当前 ViewGroup
        LayoutInflater.from(mActivity).inflate(R.layout.player_item, this);
        btn_delete = (ImageButton)findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(deleteMic);

        btn_item = (Button)findViewById(R.id.btn_item);
        btn_item.setOnClickListener(playMic);
    }

    public void setPath(String path, String name) {
        this.name = name;
        btn_item.setText(name);
        this.path = new File(path, name);
    }

    public String getName() {
        return this.name;
    }

    public void setRecordPlayer(RecordPlayer recordPlayer) {
        this.recordPlayer = recordPlayer;
    }

    private View.OnClickListener deleteMic = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (recordPlayer != null) {
                recordPlayer.releasePalyer();
            }
            if (path != null && path.exists()) {
                path.delete();
            }
            RecordInfo recordInfo = mActivity.getRecordInfo();
            String complains =  recordInfo.getComplains();
            complains = complains.replace(name + ",", "");
            complains = complains.replace(name, "");
            recordInfo.setComplains(complains);
            mActivity.saveRecordInfo();
            removeSelf();
        }
    };

    private void removeSelf() {
        ViewGroup parent = (ViewGroup)this.getParent();
        parent.removeView(this);
    }

    private View.OnClickListener playMic = new OnClickListener() {
        @Override
        public void onClick(View v) {
            playMic();
        }
    };

    private void playMic() {
        if (recordPlayer == null) {
            return;
        }
        recordPlayer.releasePalyer();
        if (path != null && path.exists()) {
            recordPlayer.playRecordFile(path);
        }
    }
}
