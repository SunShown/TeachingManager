package com.yah.manager.teachingmanage.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yah.manager.teachingmanage.Bean.Comment;
import com.yah.manager.teachingmanage.R;

import java.util.ArrayList;

public class CommentAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Comment> comments;
    private LayoutInflater inflater;
    public CommentAdapter(Context context, ArrayList<Comment> comments) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.comments = comments;
    }

    @Override
    public int getCount() {
        return comments.size();
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_comment, null);
            holder = new ViewHolder();
            holder.username = convertView.findViewById(R.id.tv_user_name);
            holder.content = convertView.findViewById(R.id.tv_content);
            holder.time = convertView.findViewById(R.id.tv_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (comments!= null){
            holder.username.setText(comments.get(position).getUserName());
            holder.content.setText(comments.get(position).getContent());
            holder.time.setText(comments.get(position).getTime());
        }
        return convertView;
    }


    private class  ViewHolder {
        public TextView username;
        public TextView content;
        public TextView time;
    }
}
