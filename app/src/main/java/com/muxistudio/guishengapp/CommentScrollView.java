package com.muxistudio.guishengapp;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class CommentScrollView extends ScrollView {//记录当前加载到的评论id RequireAppend时可以继续加载 全局变量
    Context context;
    public View footer, footer_success_load, footer_fail_load, footer_is_newest;
    int footer_tag = 0;//加载标志
    public int tab;//类型
    public int id;//新闻/原创/灌水列表id
    int current_num = 0;
    int state = -1;
    public Handler handler;
    public ArrayList<HashMap<String,Object>> comment_list = new ArrayList<>();//当前所有加载评论
    boolean isPrepared = false;
    float touchDownY, instanceY;
    MyListView.OnRefreshListener onRefreshListener;

    public CommentScrollView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
    }


    private boolean prepareFooterView() {
        if (footer_tag == 0) {
            footer.setPadding(0, 0, 0, 0);
            footer_tag = 1;
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        View view = getChildAt(getChildCount() - 1);
        int d = view.getBottom();
        d -= (getHeight() + getScrollY());
        if (d == 0) {
            new Thread(new Runnable() {
                URL url;
                @Override
                public void run() {
                    try{
                        switch (tab){
                            case 0:url = new URL(Api.api+"news/"+id+"/"+Api.comments);
                                break;
                            case 1:url = new URL(Api.api+"origins/"+id+"/"+Api.comments);
                                break;
                            default:url = new URL(Api.api+"inter/"+id+"/"+Api.comments);
                                break;
                        }
                        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setDoInput(true);
                        conn.setUseCaches(false);
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        if(is!=null)
                            comment_list.clear();
                        String jsonString = HttpUtils.readInputStream(is);
                        JSONObject whole_data = new JSONObject(jsonString);
                        JSONArray comment = whole_data.getJSONArray("posts");
                        int request_length=10;//一次加载条目数
                        request_length = request_length>comment.length()?comment.length():request_length;
                        for(int i=0;i<request_length;i++) {
                            HashMap<String, Object> map = new HashMap<>();
                            JSONObject item = comment.getJSONObject(i);
                            String stringDate = item.getString(Api.date);
                            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
                            try {
                                Date date = sdf.parse(stringDate);
                                sdf = new SimpleDateFormat("MM-dd", Locale.US);
                                map.put(Api.date, sdf.format(date));
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                map.put("author", item.get("author").toString());
                                map.put("body", item.get("body").toString());
                                comment_list.add(map);
                            }
                        }
                        current_num = comment_list.size();
                        is.close();
                        conn.disconnect();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    Message msg = new Message();
                    msg.what = 3;
                    handler.sendMessage(msg);
                }
            }).start();
        }
        else
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDownY = event.getY();
                if (getScrollY() + getHeight() >= computeVerticalScrollRange() && footer_tag == 0) {
                    isPrepared = prepareFooterView();
                    return true;
                } else
                    break;
            case MotionEvent.ACTION_MOVE:
                instanceY = event.getY();
                int offset = (int) (touchDownY - instanceY);
                if (offset > 160)
                    offset = 160;
                if (isPrepared)
                    footer.setPadding(0, 0, 0, offset);
                break;
            case MotionEvent.ACTION_UP:
                if (isPrepared) {
                    footer.setPadding(0, 8, 0, 8);
                    if (onRefreshListener != null)
                        onRefreshListener.onFooterRefresh();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    public void setOnRefreshListener(MyListView.OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    public void setFooter_is_newest() {
        footer_success_load.setVisibility(GONE);
        footer_is_newest.setVisibility(VISIBLE);

    }

    public int appendComment(){
        Runnable runnable = new Runnable() {
        @Override
        public void run() {
            URL url;
            try{
                switch (tab){
                    case 0:url = new URL(Api.api+"news/"+id+"/"+Api.comments);
                        break;
                    case 1:url = new URL(Api.api+"origins/"+id+"/"+Api.comments);
                        break;
                    default:url = new URL(Api.api+"inter/"+id+"/"+Api.comments);
                        break;
                }
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.connect();
                InputStream is = conn.getInputStream();
                String jsonString = HttpUtils.readInputStream(is);
                JSONObject whole_data = new JSONObject(jsonString);
                JSONArray comment = whole_data.getJSONArray("posts");
                if(current_num<comment.length()) {
                    int request_length=10;//一次加载条目数
                    request_length = request_length>comment.length()-current_num?comment.length()-current_num:request_length;
                    for (int i = 0; i < request_length; i++) {
                        HashMap<String, Object> map = new HashMap<>();
                        JSONObject item = comment.getJSONObject(current_num + i);
                        String stringDate = item.getString(Api.date);
                        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
                        try {
                            Date date = sdf.parse(stringDate);
                            sdf = new SimpleDateFormat("MM-dd", Locale.US);
                            map.put(Api.date, sdf.format(date));
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            map.put("author", item.get("author").toString());
                            map.put("body", item.get("body").toString());
                            comment_list.add(map);
                        }
                    }
                    current_num = comment_list.size();
                    state = 0;
                }
                else
                    state = 1;
            }catch(Exception e){
                e.printStackTrace();
            }
            Message msg = new Message();
            msg.what = 3;
            handler.sendMessage(msg);
        }
    };
       Thread thread = new Thread(runnable);
        thread.start();
        while(true)
            if(!thread.isAlive())
                break;
        return state;
    }

    public void setFooter_fail_load() {
        footer_success_load.setVisibility(GONE);
        footer_fail_load.setVisibility(VISIBLE);
    }

    public void rollback() {
        footer_tag = 0;
        isPrepared = false;
        footer.setPadding(0, -footer.getMeasuredHeight(), 0, 0);
        footer_fail_load.setVisibility(GONE);
        footer_is_newest.setVisibility(GONE);
        footer_success_load.setVisibility(VISIBLE);
    }
}