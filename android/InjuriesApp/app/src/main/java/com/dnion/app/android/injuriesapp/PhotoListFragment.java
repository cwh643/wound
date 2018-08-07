package com.dnion.app.android.injuriesapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ThermalExpert.ThermalExpert;
import com.dnion.app.android.injuriesapp.dao.DeepCameraInfo;
import com.dnion.app.android.injuriesapp.dao.DeepCameraInfoDao;
import com.dnion.app.android.injuriesapp.dao.PatientInfo;
import com.dnion.app.android.injuriesapp.dao.RecordImage;
import com.dnion.app.android.injuriesapp.dao.RecordImageDao;
import com.dnion.app.android.injuriesapp.utils.AlertDialogUtil;
import com.dnion.app.android.injuriesapp.utils.CommonUtil;
import com.dnion.app.android.injuriesapp.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a single Item detail screen.
 * on handsets.
 */
public class PhotoListFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String TAG = "photo_list_fragment";

    private MainActivity mActivity;

    private RecordImageDao recordImageDao;

    private PhotoListAdapter adapter;

    public static PhotoListFragment createInstance() {
        PhotoListFragment fragment = new PhotoListFragment();

        Bundle bundle = new Bundle();
        //bundle.putLong(ARGUMENT_USER_ID, userId);
        //bundle.putString(ARGUMENT_USER_NAME, userName);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) PhotoListFragment.this.getActivity();
        recordImageDao = new RecordImageDao(mActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_photo_list, container, false);
        configView(rootView);
        return rootView;
    }

    private void configView(View rootView) {
        GridView photo_container = (GridView) rootView.findViewById(R.id.photo_container);
        List<RecordImage> imageList = recordImageDao.queryRecordImage("" + mActivity.getRecordId(), "'deep','ir'");
        for (RecordImage image : imageList) {
            if (!image.getImagePath().startsWith(mActivity.getBaseDir())) {
                image.setImagePath(mActivity.getBaseDir() + File.separator + image.getImagePath());
            }
        }
        adapter = new PhotoListAdapter(imageList);
        photo_container.setAdapter(adapter);
        photo_container.setOnItemClickListener(mItemClickListener);
        ImageButton btn_camera = (ImageButton) rootView.findViewById(R.id.btn_photo);
        btn_camera.setOnClickListener(mShotLitener);

        ImageButton btn_photo_hot = (ImageButton) rootView.findViewById(R.id.btn_photo_ir);
        btn_photo_hot.setOnClickListener(mHotListener);
    }

    private final View.OnClickListener mHotListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecordFragmentIRCamera fragment = RecordFragmentIRCamera.createInstance();
            mActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, fragment)
                    .commit();
        }
    };

    private final View.OnClickListener mShotLitener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecordFragmentDeepCamera fragment = RecordFragmentDeepCamera.createInstance();
            mActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, fragment)
                    .commit();
        }
    };

    private final AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            new ProcessTask().execute("" + position);
        }
    };

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
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item, null);
                TagObj tag = new TagObj();
                convertView.setTag(tag);
                tag.image_item = (ImageView) convertView.findViewById(R.id.image_item);
                tag.image_time = (TextView) convertView.findViewById(R.id.image_time);
            }
            RecordImage image = items.get(position);
            //String imagePath = image.getImagePath() + File.separator + DeepCameraInfoDao.LIST_IMAGE_FILE_NAME;
            String imagePath = image.getImagePath() + File.separator + DeepCameraInfoDao.RGB_FILE_NAME;
            TagObj tag = (TagObj) convertView.getTag();
            tag.image_item.setImageDrawable(new BitmapDrawable(mActivity.getResources(), CommonUtil.readBitMap(imagePath)));
            //tag.image_item.setImageResource(R.mipmap.ic_launcher);
            tag.image_time.setText(image.getCreateTime());
            return convertView;
        }
    }

    private class TagObj {
        ImageView image_item;
        TextView image_time;
    }

    private class ProcessTask extends AsyncTask<String, Object, Long> {
        private RecordImage image;

        @Override
        protected void onPreExecute() {
            AlertDialogUtil.showAlertDialog(mActivity,
                    mActivity.getString(R.string.message_title_tip),
                    mActivity.getString(R.string.message_wait));
        }

        @Override
        protected Long doInBackground(String... params) {
            int position = Integer.parseInt(params[0]);
            image = (RecordImage) adapter.getItem(position);
            DeepCameraInfo dci = mActivity.queryDeepCameraInfo(image.getImagePath());
            mActivity.setDeepCameraInfo(dci);
            return 0L;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            if (image.getImageType().equals("deep")) {
                RecordFragmentWoundMeasure fragment = RecordFragmentWoundMeasure.createInstance();
                mActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, fragment)
                        .commit();
            } else if (image.getImageType().equals("ir")) {
                RecordFragmentWoundIRMeasure fragment = RecordFragmentWoundIRMeasure.createInstance();
                mActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, fragment)
                        .commit();
            }
            AlertDialogUtil.dismissAlertDialog(mActivity);
        }

    }

}