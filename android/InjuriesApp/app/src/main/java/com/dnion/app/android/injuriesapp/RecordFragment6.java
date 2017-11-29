package com.dnion.app.android.injuriesapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.dnion.app.android.injuriesapp.dao.DeepCameraInfoDao;
import com.dnion.app.android.injuriesapp.dao.RecordImage;
import com.dnion.app.android.injuriesapp.dao.RecordInfo;
import com.dnion.app.android.injuriesapp.utils.BaseTextWatcher;
import com.dnion.app.android.injuriesapp.utils.CommonUtil;
import com.dnion.app.android.injuriesapp.utils.ImageTools;
import com.dnion.app.android.injuriesapp.utils.SDCardHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by 卫华 on 2017/4/15.
 */

public class RecordFragment6 extends Fragment {

    public static final String TAG = "record_fragment6";

    public static final int SCALE = 2;

    private RecordAdapter adapter;

    private MainActivity mActivity;

    private RecordInfo patientInfo;

    public static RecordFragment6 createInstance() {
        RecordFragment6 fragment = new RecordFragment6();

        Bundle bundle = new Bundle();
        //bundle.putLong(ARGUMENT_USER_ID, userId);
        //bundle.putString(ARGUMENT_USER_NAME, userName);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity)RecordFragment6.this.getActivity();
        //userDao = new UserDao(mActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.record6_fragment, container, false);
        configView(rootView);
        return rootView;
    }

    private void configView(final View rootView) {
        patientInfo = mActivity.getRecordInfo();
        Button btn_dpl_image = (Button)rootView.findViewById(R.id.btn_dpl_image);
        btn_dpl_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = patientInfo.getWoundDoppler();
                if (path == null || path.length() == 0) {
                    Toast.makeText(mActivity, "请先拍照！", Toast.LENGTH_SHORT).show();
                } else {
                    mActivity.showImage(path);
                }
            }
        });

        //btn_dpl_camera
        Button btn_dpl_camera = (Button)rootView.findViewById(R.id.btn_dpl_camera);
        btn_dpl_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String picPath = mActivity.getImagePath();
                Uri uri = Uri.fromFile(new File(picPath));
                //为拍摄的图片指定一个存储的路径
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                // 启动相机
                startActivityForResult(intent, 1001);
            }
        });

        //CTA
        Button btn_cta_image = (Button)rootView.findViewById(R.id.btn_cta_image);
        btn_cta_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = patientInfo.getWoundCta();
                if (path == null || path.length() == 0) {
                    Toast.makeText(mActivity, "请先拍照！", Toast.LENGTH_SHORT).show();
                } else {
                    mActivity.showImage(path);
                }
            }
        });
        Button btn_cta_camera = (Button)rootView.findViewById(R.id.btn_cta_camera);
        btn_cta_camera.setOnClickListener(new View.OnClickListener() {
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

        //exam
        Button btn_exam_image = (Button)rootView.findViewById(R.id.btn_exam_image);
        btn_cta_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = patientInfo.getWoundExam();
                if (path == null || path.length() == 0) {
                    Toast.makeText(mActivity, "请先拍照！", Toast.LENGTH_SHORT).show();
                } else {
                    mActivity.showImage(path);
                }
            }
        });
        Button btn_exam_camera = (Button)rootView.findViewById(R.id.btn_exam_camera);
        btn_exam_camera.setOnClickListener(new View.OnClickListener() {
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

        /*
        EditText wound_cta = (EditText)rootView.findViewById(R.id.wound_cta);
        if (patientInfo.getWoundCta() != null) {
            wound_cta.setText("" + patientInfo.getWoundCta());
        }
        wound_cta.addTextChangedListener(new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                patientInfo.setWoundCta(s.toString());
            }
        });
        */

        //下一步
        Button btn_step_next = (Button)rootView.findViewById(R.id.btn_step_next);
        btn_step_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.saveRecordInfo();
                RecordFragment7 fragment = RecordFragment7.createInstance();
                mActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, fragment)
                        .commit();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //Bundle bundle = data.getExtras();
            //Bitmap bitmap = (Bitmap) bundle.get("data");
            //将保存在本地的图片取出并缩小后显示在界面上
            String picPath = mActivity.getImagePath();
            Bitmap bitmap = BitmapFactory.decodeFile(picPath);
            Bitmap newBitmap = ImageTools.zoomBitmap(bitmap, bitmap.getWidth() / SCALE, bitmap.getHeight() / SCALE);
            //由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
            bitmap.recycle();

            //将处理过的图片显示在界面上，并保存到本地
            String type = "other";
            String path = mActivity.getImagePath(type);
            //Log.e("chenwh", file.getPath());
            if(requestCode==1001){
                mActivity.saveImage(newBitmap, path, "dpl.jpg");
                patientInfo.setWoundDoppler(type + File.separator + "dpl.jpg");
            } else if (requestCode==1002) {
                mActivity.saveImage(newBitmap, path, "cta.jpg");
                patientInfo.setWoundCta(type + File.separator + "cta.jpg");
            } else if (requestCode==1003) {
                mActivity.saveImage(newBitmap, path, "exam.jpg");
                patientInfo.setWoundExam(type + File.separator + "exam.jpg");
            }

            newBitmap.recycle();

        }
    }

    private class PhotoListAdapter extends BaseAdapter {

        private List<RecordImage> items;

        private PhotoListAdapter(List<RecordImage> imageList) {
            this.items = imageList;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.position_item, null);
                TagObj tag = new TagObj();
                convertView.setTag(tag);
                tag.image_item = (ImageView) convertView.findViewById(R.id.image_item);
            }
            RecordImage image = items.get(position);
            TagObj tag = (TagObj) convertView.getTag();
            tag.image_item.setImageResource(image.getId());
            //tag.image_item.setImageResource(R.mipmap.ic_launcher);
            return convertView;
        }
    }

    private class TagObj {
        ImageView image_item;
    }

}
