package com.dnion.app.android.injuriesapp;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 卫华 on 2017/4/23.
 */

public class SpinnerAdapter extends BaseAdapter {

    List<Pair> dataList = new ArrayList<Pair>();

    public SpinnerAdapter(List<Pair> list) {
        dataList = list;
    }


    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_item, null);
        }
        TextView text = (TextView)convertView.findViewById(R.id.text1);
        Pair pair = dataList.get(position);
        text.setText(pair.second.toString());
        return convertView;
    }
}
