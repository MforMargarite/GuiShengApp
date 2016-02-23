package com.muxistudio.guishengapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Message;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Handler;


public class ListViewAdapter extends BaseAdapter {
    Context context;
    ViewHolder holder;
    ArrayList<HashMap<String, Object>> list;
    ImageLoad imageLoad;

    ListViewAdapter(Context context, ArrayList<HashMap<String, Object>> list) {
        this.context = context;
        this.list = list;
        imageLoad = new ImageLoad();
    }

    class ViewHolder {
        private TextView title;
        private TextView author;
        private TextView timestamp;
        private ImageView image;
    }

    @Override
    public int getCount() {
        return list.size() -1;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_layout, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.author = (TextView) convertView.findViewById(R.id.writer);
            holder.timestamp = (TextView) convertView.findViewById(R.id.time);
            holder.image = (ImageView) convertView.findViewById(R.id.pic_along);
            holder.image.setTag(position+1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position < getCount()) {
            final HashMap<String, Object> map = list.get(position + 1);
            holder.title.setText(map.get(Api.title).toString());
            holder.author.setText(map.get(Api.writer).toString());
            holder.timestamp.setText(map.get(Api.date).toString());
            if (map.get(Api.image).toString().equals("null"))
                holder.image.setVisibility(View.GONE);
            else {
                imageLoad.showImageByThread(holder.image, Api.image_api + map.get("image").toString());
            }
        }
        return convertView;
    }
}