package com.dnion.app.android.injuriesapp;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dnion.app.android.injuriesapp.utils.CommonUtil;

import java.util.List;

/**
 * Created by 卫华 on 2017/5/14.
 */

public class TypeArrayAdapter extends ArrayAdapter<Pair> {

    public TypeArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Pair> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);
        } else {
            view = convertView;
        }
        Pair pair = getItem(position);
        TextView text = (TextView) view.findViewById(android.R.id.text1);
        text.setText("" + pair.second);
        text.setTag(pair.first);
        text.setTextSize(CommonUtil.SELECT_TEXT_SIZE);
        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        } else {
            view = convertView;
        }
        Pair pair = getItem(position);
        TextView text = (TextView) view.findViewById(android.R.id.text1);
        text.setText("" + pair.second);
        text.setTag(pair.first);
        text.setTextSize(CommonUtil.SELECT_TEXT_SIZE);
        return view;
    }
}
