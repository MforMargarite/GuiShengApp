package com.muxistudio.guishengapp;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ToggleListView extends ListView {
    public View headpicview_wrapper;
    public View about_wrapper;

    public ToggleListView(Context context,int width,int height){
        super(context);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int)(0.722*width),height);
        setLayoutParams(layoutParams);
        int imageID[] = new int[]{R.drawable.upload_headpic,R.drawable.change_name,R.drawable.choose_unlog};
        String nameID[] = new String[]{context.getResources().getString(R.string.upload_headpic),context.getResources().getString(R.string.change_name),context.getResources().getString(R.string.choose_unlog)};
        ArrayList<HashMap<String,Object>> arrayList = new ArrayList<>();
        for(int i=0;i<imageID.length;i++){
            HashMap<String,Object> map = new HashMap<>();
            map.put("image",imageID[i]);
            map.put("name",nameID[i]);
            arrayList.add(map);
        }

        String[] from = new String[]{"image","name"};
        int[] to = new int[]{R.id.sliding_tab_img,R.id.sliding_tab_text};
        SimpleAdapter simple_adapter = new SimpleAdapter(context,arrayList,R.layout.simple_adapter_layout,from,to);
        //动态添加HeadpicImageView
        headpicview_wrapper = LayoutInflater.from(context).inflate(R.layout.toggle_header_layout,null,false);
        RelativeLayout list_menu_wrapper = (RelativeLayout)headpicview_wrapper.findViewById(R.id.list_menu_wrapper);
        list_menu_wrapper.setPadding(0,0,0,(int)(80*Api.scale));
        RelativeLayout.LayoutParams headpic_lp = new RelativeLayout.LayoutParams((int)(260*Api.scale),(int)(260*Api.scale));
        HeadpicImageView user_headpic_view = new HeadpicImageView(context);
        user_headpic_view.setId(R.id.user_head_pic);
        user_headpic_view.setLayoutParams(headpic_lp);
        list_menu_wrapper.addView(user_headpic_view);
        TextView textView = new TextView(context);
        RelativeLayout.LayoutParams textView_lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView_lp.addRule(RelativeLayout.BELOW,user_headpic_view.getId());
        textView_lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        textView_lp.setMargins(0, (int) (Api.scale * 64), 0, 0);
        textView.setLayoutParams(textView_lp);
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setId(R.id.username);
        list_menu_wrapper.addView(textView);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)(0.4*height));
        headpicview_wrapper.setLayoutParams(lp);
        about_wrapper = LayoutInflater.from(context).inflate(R.layout.toggle_bottom_layout,null,false);
        addHeaderView(headpicview_wrapper);
        addFooterView(about_wrapper);
        setHeaderDividersEnabled(false);
        setAdapter(simple_adapter);
    }
}
