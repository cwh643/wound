package com.dnion.app.android.injuriesapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.dnion.app.android.injuriesapp.dao.RecordImage;
import com.dnion.app.android.injuriesapp.dao.RecordImageDao;
import com.dnion.app.android.injuriesapp.dao.RecordInfo;
import com.dnion.app.android.injuriesapp.utils.ImageTools;

import java.io.File;

import static android.app.Activity.RESULT_OK;

/**
 * Created by 卫华 on 2017/4/15.
 */

public class RecordFragment7 extends Fragment {

    public static final String TAG = "record_fragment7";

    private RecordAdapter adapter;

    private MainActivity mActivity;

    private RecordInfo patientInfo;

    public static final int SCALE = 2;

    private RecordImageDao recordImageDao;

    public static RecordFragment7 createInstance() {
        RecordFragment7 fragment = new RecordFragment7();

        Bundle bundle = new Bundle();
        //bundle.putLong(ARGUMENT_USER_ID, userId);
        //bundle.putString(ARGUMENT_USER_NAME, userName);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity)RecordFragment7.this.getActivity();
        recordImageDao = new RecordImageDao(mActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.record7_fragment, container, false);
        configView(rootView);
        return rootView;
    }

    private void configView(final View rootView) {
        patientInfo = mActivity.getRecordInfo();
        //btn_dpl_image
        Button btn_mr_image = (Button)rootView.findViewById(R.id.btn_mr_image);
        btn_mr_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = patientInfo.getWoundMr();
                if (path == null || path.length() == 0) {
                    Toast.makeText(mActivity, "请先拍照！", Toast.LENGTH_SHORT).show();
                } else {
                    mActivity.showImage(path);
                }
            }
        });

        //btn_dpl_camera
        Button btn_mr = (Button)rootView.findViewById(R.id.btn_mr);
        btn_mr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String picPath = mActivity.getImagePath();
                Uri uri = Uri.fromFile(new File(picPath));
                //为拍摄的图片指定一个存储的路径
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                // 启动相机
                startActivityForResult(intent, 1002);
            }
        });

        //
        Button btn_petct_image = (Button)rootView.findViewById(R.id.btn_petct_image);
        btn_petct_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = patientInfo.getWoundPetct();
                if (path == null || path.length() == 0) {
                    Toast.makeText(mActivity, "请先拍照！", Toast.LENGTH_SHORT).show();
                } else {
                    mActivity.showImage(path);
                }
            }
        });

        //bt
        Button btn_petct = (Button)rootView.findViewById(R.id.btn_petct);
        btn_petct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String picPath = mActivity.getImagePath();
                Uri uri = Uri.fromFile(new File(picPath));
                //为拍摄的图片指定一个存储的路径
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                // 启动相机
                startActivityForResult(intent, 1003);
            }
        });

        //下一步
        /*Button btn_step_next = (Button)rootView.findViewById(R.id.btn_step_next);
        btn_step_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.saveRecordInfo();
                RecordFragment8 fragment = RecordFragment8.createInstance();
                mActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, fragment)
                        .commit();
            }
        });*/
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            //将保存在本地的图片取出并缩小后显示在界面上
            String picPath = mActivity.getImagePath();
            Bitmap bitmap = BitmapFactory.decodeFile(picPath);
            Bitmap newBitmap = ImageTools.zoomBitmap(bitmap, bitmap.getWidth() / SCALE, bitmap.getHeight() / SCALE);
            //由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
            bitmap.recycle();

            String type = "other";
            //将处理过的图片显示在界面上，并保存到本地
            String path = mActivity.getImagePath(type);
            long time = System.currentTimeMillis();
            if(requestCode==1002){
                mActivity.saveImage(newBitmap, path, "mr_"+time+".jpg");
                String images = mActivity.getImagePaths(patientInfo.getWoundMr(), type + File.separator + "mr_"+time+".jpg");
                patientInfo.setWoundMr(images);
            } else if (requestCode==1003) {
                mActivity.saveImage(newBitmap, path, "petct_"+time+".jpg");
                String images = mActivity.getImagePaths(patientInfo.getWoundPetct(), type + File.separator + "petct_"+time+".jpg");
                patientInfo.setWoundPetct(images);
            }
            newBitmap.recycle();

            mActivity.saveRecordInfo();
        }
    }

    /*
    private void showImage(String picPath) {
        Bitmap bitmap = BitmapFactory.decodeFile(picPath);
        ImageView imageView = mActivity.getImagePreView();
        imageView.setImageBitmap(bitmap);
        imageView.setVisibility(View.VISIBLE);
    }
    */

}
