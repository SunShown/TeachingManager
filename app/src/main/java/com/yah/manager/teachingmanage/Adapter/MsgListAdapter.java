package com.yah.manager.teachingmanage.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yah.manager.teachingmanage.Bean.MsgList;
import com.yah.manager.teachingmanage.R;

import java.util.List;

/**
 * Created by djzhao on 17/04/30.
 */

public class MsgListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private List<MsgList> mList;

    public MsgListAdapter(Context mContext, List<MsgList> mList) {
        this.inflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

@Override
public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    if (convertView == null) {
        convertView = inflater.inflate(R.layout.item_msg, null);
        holder = new ViewHolder();
        holder.title = (TextView) convertView.findViewById(R.id.tv_title);
        holder.content = convertView.findViewById(R.id.tv_content);
        holder.time = convertView.findViewById(R.id.tv_time);
        holder.parent = convertView.findViewById(R.id.ll_parent);
        convertView.setTag(holder);
    } else {
        holder = (ViewHolder) convertView.getTag();
    }
    if (mList!= null){
        holder.title.setText(mList.get(position).getTitle());
        holder.content.setText(mList.get(position).getContent());
        holder.time.setText(mList.get(position).getData());

    }

    return convertView;
}

    private class  ViewHolder {
        public ImageView bg;
        public TextView title;
        public TextView username;
        public TextView content;
        public TextView time;
        public LinearLayout parent;
    }
}
