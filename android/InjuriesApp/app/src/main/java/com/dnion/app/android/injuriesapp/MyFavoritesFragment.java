package com.dnion.app.android.injuriesapp;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dnion.app.android.injuriesapp.dao.DeepCameraInfo;
import com.dnion.app.android.injuriesapp.dao.DeepCameraInfoDao;
import com.dnion.app.android.injuriesapp.dao.PatientInfo;
import com.dnion.app.android.injuriesapp.dao.RecordDao;
import com.dnion.app.android.injuriesapp.dao.RecordImage;
import com.dnion.app.android.injuriesapp.dao.RecordImageDao;
import com.dnion.app.android.injuriesapp.dao.RecordInfo;
import com.dnion.app.android.injuriesapp.utils.CommonUtil;

import java.io.File;
import java.util.List;

/**
 * A fragment representing a single Item detail screen.
 * on handsets.
 */
public class MyFavoritesFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String TAG = "my_favorites_fragment";

    private MainActivity mActivity;

    private RecordImageDao recordImageDao;

    private RecordDao recordDao;

    private PhotoListAdapter adapter;

    public static MyFavoritesFragment createInstance() {
        MyFavoritesFragment fragment = new MyFavoritesFragment();

        Bundle bundle = new Bundle();
        //bundle.putLong(ARGUMENT_USER_ID, userId);
        //bundle.putString(ARGUMENT_USER_NAME, userName);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) MyFavoritesFragment.this.getActivity();
        recordImageDao = new RecordImageDao(mActivity);
        recordDao = new RecordDao(mActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.my_favorites_fragment, container, false);
        configView(rootView);
        return rootView;
    }

    private void configView(View rootView) {
        GridView favorite_container = (GridView) rootView.findViewById(R.id.favorties_container);
        List<RecordInfo> favortiesList = recordDao.queryFavortiesRecordList();
        //List<RecordInfo> favortiesList = recordImageDao.queryRecordImage("" + mActivity.getRecordId(), "deep");
        adapter = new PhotoListAdapter(favortiesList);
        favorite_container.setAdapter(adapter);
        favorite_container.setOnItemClickListener(mItemClickListener);
    }

    private final AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            RecordInfo record = (RecordInfo) adapter.getItem(position);
            String recordTime = record.getRecordTime();
            String selectInpatientNo = record.getInpatientNo();
            HomeFragment fragment = HomeFragment.createInstance(selectInpatientNo, recordTime);
            mActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_container, fragment)
                    .commit();
        }
    };

    private class PhotoListAdapter extends BaseAdapter {

        private List<RecordInfo> items;

        private PhotoListAdapter(List<RecordInfo> imageList) {
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
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_item, null);
                TagObj tag = new TagObj();
                convertView.setTag(tag);
                tag.image_item = (ImageView) convertView.findViewById(R.id.image_item);
                tag.user_info = (TextView) convertView.findViewById(R.id.user_info);
            }

            TagObj tag = (TagObj) convertView.getTag();
            RecordInfo recordInfo = items.get(position);
            List<RecordImage> imageList = recordInfo.getDeepList();
            if (imageList.size() > 0) {
                RecordImage image = imageList.get(0);
                String imagePath = image.getImagePath() + File.separator + DeepCameraInfoDao.LIST_IMAGE_FILE_NAME;
                tag.image_item.setImageDrawable(new BitmapDrawable(mActivity.getResources(), CommonUtil.readBitMap(imagePath)));
            }

            String diagnosis = "";
            PatientInfo patientInfo = recordInfo.getPatientInfo();
            if (patientInfo != null) {
                if (patientInfo.getDiagnosis() != null) {
                    diagnosis = patientInfo.getDiagnosis();
                    if (diagnosis.length() > 3) {
                        diagnosis = diagnosis.substring(0,3) + "...";
                    }
                }
                if (diagnosis.length() > 0) {
                    tag.user_info.setText(patientInfo.getName() + "("+diagnosis+")");
                } else {
                    tag.user_info.setText(patientInfo.getName());
                }

            }

            return convertView;
        }
    }

    private class TagObj {
        ImageView image_item;
        TextView user_info;
    }

}