package com.yah.manager.teachingmanage.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CommentAdapter extends BaseAdapter {

    public CommentAdapter(Context context,) {
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
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
