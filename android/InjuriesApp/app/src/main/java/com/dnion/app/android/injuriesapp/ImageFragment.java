package com.dnion.app.android.injuriesapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.dnion.app.android.injuriesapp.dao.UserDao;
import com.dnion.app.android.injuriesapp.utils.CommonUtil;
import com.dnion.app.android.injuriesapp.utils.MD5;
import com.dnion.app.android.injuriesapp.utils.SharedPreferenceUtil;
import com.dnion.app.android.injuriesapp.utils.ToastUtil;

import java.util.Map;

/**
 * A fragment representing a single Item detail screen.
 * on handsets.
 */
public class ImageFragment extends DialogFragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String TAG = "image_fragment";

    private MainActivity mActivity;

    private static final String IMAGE_PATH_NAME = "image_path";

    public static ImageFragment createInstance(String imagePath) {
        ImageFragment fragment = new ImageFragment();

        Bundle bundle = new Bundle();
        //bundle.putLong(ARGUMENT_USER_ID, userId);
        bundle.putString(IMAGE_PATH_NAME, imagePath);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity)ImageFragment.this.getActivity();
        this.setCancelable(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.image_fragment, container, false);
        configView(rootView);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        return rootView;
    }

    private void configView(View rootView) {
        ImageView image_perview = (ImageView)rootView.findViewById(R.id.image_perview1);
        String imagePath = getArguments().getString(IMAGE_PATH_NAME);
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        image_perview.setImageBitmap(bitmap);
    }


}
