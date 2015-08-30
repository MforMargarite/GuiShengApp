package com.muxistudio.guishengapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spanned;
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


public class ListViewAdapter extends BaseAdapter {
    Context context;
    ViewHolder holder;
    Bitmap bitmap;
    ArrayList<HashMap<String, Object>> list;

    ListViewAdapter(Context context, ArrayList<HashMap<String, Object>> list) {
        this.context = context;
        this.list = list;
    }

    class ViewHolder {
        private TextView title;
        private TextView author;
        private TextView timestamp;
        private ImageView image;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_layout, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.author = (TextView) convertView.findViewById(R.id.writer);
            holder.timestamp = (TextView) convertView.findViewById(R.id.time);
            holder.image = (ImageView) convertView.findViewById(R.id.pic);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
            final HashMap<String, Object> map = list.get(position);
            holder.title.setText(map.get(Api.title).toString());
            holder.author.setText((Spanned)map.get(Api.image));
            holder.timestamp.setText(map.get(Api.timestamp).toString());
            if(map.get(Api.image).toString().equals("null"))
                holder.image.setVisibility(View.GONE);
           else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL(Api.api + map.get(Api.image).toString());
                            bitmap = BitmapFactory.decodeStream(url.openStream());
                            holder.image.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        return convertView;
    }
}