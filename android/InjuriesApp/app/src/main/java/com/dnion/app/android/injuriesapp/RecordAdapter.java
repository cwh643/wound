package com.dnion.app.android.injuriesapp;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by 卫华 on 2017/4/18.
 */

public class RecordAdapter extends BaseAdapter {
    private LayoutInflater inflater;

    List<ItemObj> dataList = new ArrayList<ItemObj>();

    private Context mContext;

    private Map<String, Integer> alphaIndexer;

    private final int VIEW_TYPE = 1;

    public RecordAdapter(Context context, List<ItemObj> items) {
        mContext = context;
        this.inflater = LayoutInflater.from(context);
        dataList = items;
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
    public int getItemViewType(int position) {
        int type = 0;
        return type;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        int viewType = getItemViewType(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.record_list_item, null);
            holder = new ViewHolder();
            holder.item_type = (TextView) convertView.findViewById(R.id.item_type);
            holder.item_title = (TextView) convertView.findViewById(R.id.item_title);
            holder.item_spinner = (Spinner) convertView.findViewById(R.id.item_spinner);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.item_spinner.setVisibility(View.GONE);
        ItemObj item = dataList.get(position);
        if (item.getType() == ItemType.SELECT) {
            holder.item_title.setText(item.getLabel());
            holder.item_spinner.setVisibility(View.VISIBLE);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, item.getDict());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.item_spinner.setAdapter(adapter);
        }
        return convertView;
    }

    private class SpinnerAdapter extends BaseAdapter {

        List<Pair> items = new ArrayList<>();

        public SpinnerAdapter(List<Pair> items) {
            this.items = items;
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
            SpinnerViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.spinner_item, null);
                holder = new SpinnerViewHolder();
                holder.text1 = (TextView) convertView.findViewById(R.id.text1);
                convertView.setTag(holder);
            } else {
                holder = (SpinnerViewHolder) convertView.getTag();
            }
            Pair item = items.get(position);
            holder.text1.setText("" + item.second);
            return convertView;
        }
    }

    private class ViewHolder {
        TextView item_type;
        TextView item_title;
        Spinner item_spinner;
    }

    private class SpinnerViewHolder {
        TextView text1;
    }
}
