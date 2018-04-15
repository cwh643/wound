package com.dnion.app.android.injuriesapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.dnion.app.android.injuriesapp.dao.RecordImage;
import com.dnion.app.android.injuriesapp.dao.RecordInfo;
import com.dnion.app.android.injuriesapp.ui.ClickView;
import com.dnion.app.android.injuriesapp.utils.ImageTools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by 卫华 on 2017/4/15.
 */

public class RecordFragment9 extends Fragment {

    public static final String TAG = "record_fragment6";

    public static final int SCALE = 2;

    private MainActivity mActivity;

    private RecordInfo patientInfo;

    private List<RecordImage> imageList;

    private PhotoListAdapter adapter;

    private TextView txtPositionDesc;

    public static RecordFragment9 createInstance() {
        RecordFragment9 fragment = new RecordFragment9();

        Bundle bundle = new Bundle();
        //bundle.putLong(ARGUMENT_USER_ID, userId);
        //bundle.putString(ARGUMENT_USER_NAME, userName);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity)RecordFragment9.this.getActivity();
        //userDao = new UserDao(mActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.record9_fragment, container, false);
        configView(rootView);
        return rootView;
    }

    private void configView(final View rootView) {
        patientInfo = mActivity.getRecordInfo();
        txtPositionDesc = (TextView) rootView.findViewById(R.id.txt_position_desc);
        GridView wound_container = (GridView) rootView.findViewById(R.id.wound_container);
        imageList = new ArrayList<RecordImage>();
        imageList.add(new RecordImage(R.mipmap.position_1, R.mipmap.position_1s, 1, "头部"));
        imageList.add(new RecordImage(R.mipmap.position_2, R.mipmap.position_2s, 2, "肩部"));
        imageList.add(new RecordImage(R.mipmap.position_3, R.mipmap.position_3s, 3, "手部"));
        imageList.add(new RecordImage(R.mipmap.position_4, R.mipmap.position_4s, 4, "脚部"));
        imageList.add(new RecordImage(R.mipmap.position_5, R.mipmap.position_5s, 5, "腰部"));
        imageList.add(new RecordImage(R.mipmap.position_6, R.mipmap.position_6s, 6, "腿部"));
        imageList.add(new RecordImage(R.mipmap.position_7, R.mipmap.position_7s, 7, "背部"));

        txtPositionDesc.setText("");
        Integer position = patientInfo.getWoundPosition();
        if (position != null) {
            for (RecordImage image : imageList) {
                if (position.equals(image.getPositionId())) {
                    image.setSelected(true);
                    txtPositionDesc.setText(image.getDescribe());
                }
            }
        }

        adapter = new PhotoListAdapter(imageList);
        wound_container.setAdapter(adapter);

        wound_container.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (RecordImage image : imageList) {
                    image.setSelected(false);
                }
                RecordImage recordImage = (RecordImage)adapter.getItem(position);
                recordImage.setSelected(true);
                //显示文字
                txtPositionDesc.setText(recordImage.getDescribe());
                adapter.notifyDataSetChanged();
                patientInfo.setWoundPosition(recordImage.getPositionId());
                patientInfo.setWoundPositionDesc(recordImage.getDescribe());
            }
        });

        ClickView clickView = (ClickView) rootView.findViewById(R.id.click_view);
        clickView.setRecordInfo(patientInfo);
        Integer x = patientInfo.getWoundPositionx();
        Integer y = patientInfo.getWoundPositiony();
        if (x != null && y != null) {
            clickView.setPosition(x, y);
        }


        //btn_dpl_image
        /*
        Button btn_dpl_image = (Button)rootView.findViewById(R.id.btn_dpl_image);
        btn_dpl_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = patientInfo.getWoundDoppler();
                if (path == null || path.length() == 0) {
                    Toast.makeText(mActivity, "请先拍照！", Toast.LENGTH_SHORT).show();
                } else {
                    showImage(path);
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
        */
    }

    /*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(requestCode==1001){
                //Bundle bundle = data.getExtras();
                //Bitmap bitmap = (Bitmap) bundle.get("data");
                //将保存在本地的图片取出并缩小后显示在界面上
                String picPath = mActivity.getImagePath();
                Bitmap bitmap = BitmapFactory.decodeFile(picPath);
                Bitmap newBitmap = ImageTools.zoomBitmap(bitmap, bitmap.getWidth() / SCALE, bitmap.getHeight() / SCALE);
                //由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
                bitmap.recycle();

                //将处理过的图片显示在界面上，并保存到本地
                String path = mActivity.getImagePath(mActivity.getRecordId() + File.separator + "other");
                mActivity.saveImage(newBitmap, path, "dpl.jpg");
                newBitmap.recycle();
                //Log.e("chenwh", file.getPath());
                patientInfo.setWoundDoppler(path + File.separator + "dpl.jpg");
            }
        }
    }
    */

    /*
    private void showImage(String picPath) {
        Bitmap bitmap = BitmapFactory.decodeFile(picPath);
        ImageView imageView = mActivity.getImagePreView();
        imageView.setImageBitmap(bitmap);
        imageView.setVisibility(View.VISIBLE);
    }
    */

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
            if (image.isSelected()) {
                tag.image_item.setImageResource(image.getSelectId());
            } else {
                tag.image_item.setImageResource(image.getId());
            }

            //tag.image_item.setImageResource(R.mipmap.ic_launcher);
            return convertView;
        }
    }

    private class TagObj {
        ImageView image_item;
    }

}
