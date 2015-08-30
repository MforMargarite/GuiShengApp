package com.muxistudio.guishengapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class CommentListViewAdapter extends BaseAdapter {
    Context context;
    ViewHolder holder;
    Bitmap bitmap;
    ArrayList<HashMap<String, Object>> list;

    CommentListViewAdapter(Context context, ArrayList<HashMap<String, Object>> list) {
        this.context = context;
        this.list = list;
    }

    class ViewHolder {
        private TextView username;
        private TextView comment;
        private TextView time;
        private HeadpicImageView image;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.comment_listview_layout, null);
            holder = new ViewHolder();
            holder.comment = (TextView) convertView.findViewById(R.id.comment);
            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.image = (HeadpicImageView) convertView.findViewById(R.id.user_pic);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        HashMap<String, Object> map = list.get(position);
        holder.username.setText(map.get(Api.author).toString());
        holder.time.setText(map.get(Api.timestamp).toString());
        try {
                URL url = new URL(Api.api+ map.get("image").toString());
                bitmap = BitmapFactory.decodeStream(url.openStream());
                holder.image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        return convertView;
    }
}
