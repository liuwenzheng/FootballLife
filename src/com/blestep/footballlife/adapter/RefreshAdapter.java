package com.blestep.footballlife.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blestep.footballlife.R;
import com.blestep.footballlife.entity.SportItem;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by lwz on 2016/5/15 0015.
 */
public class RefreshAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<SportItem> items;

    public RefreshAdapter(Context context, ArrayList<SportItem> items) {
        this.context = context;
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
        ViewHolder holder = null;
        SportItem item = items.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.sports_params_item, parent, false);
            holder.icon = ButterKnife.findById(convertView, R.id.iv_sport_item_icon);
            holder.name = ButterKnife.findById(convertView, R.id.tv_sport_item_name);
            holder.value = ButterKnife.findById(convertView, R.id.tv_sport_item_value);
            holder.progress = ButterKnife.findById(convertView, R.id.pb_sport_item_progress);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position % 3 == 0) {
            holder.icon.setBackgroundResource(R.drawable.sports_item_bg_yellow);
        } else if (position % 3 == 1) {
            holder.icon.setBackgroundResource(R.drawable.sports_item_bg_red);
        } else {
            holder.icon.setBackgroundResource(R.drawable.sports_item_bg_blue);
        }
        holder.icon.setImageResource(item.iconId);
        holder.name.setText(item.name);
        holder.value.setText(item.showValue);
        holder.progress.setProgress((int) item.value);
        holder.progress.setMax((int) item.maxValue);
        return convertView;
    }

    class ViewHolder {
        public ImageView icon;
        public TextView name;
        public TextView value;
        public ProgressBar progress;
    }
}
