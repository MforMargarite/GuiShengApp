package com.muxistudio.guishengapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

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

public class NetDataObtain {
    static URL url;
    static HttpURLConnection conn;
    static InputStream is;
    static String jsonString;
    public static int REFRESH_STATE = -1;
    Context context;


    public NetDataObtain(Context context){
        this.context = context;
    }

    public int DataRequireAppend(final int tag) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    REFRESH_STATE=-1;//initialize
                    if(tag == 2) {
                        Api.interact_page++;
                        url = new URL(Api.api + Api.inter + Api.interact_page);
                    } else if(tag == 1){
                        Api.original_page++;
                        url = new URL(Api.api + Api.origins + Api.original_page);
                    }
                    else{
                        Api.news_page++;
                        url = new URL(Api.api + Api.news + Api.news_page);
                    }
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.setUseCaches(false);
                    conn.setRequestMethod("GET");
                    conn.connect();
                    is = conn.getInputStream();
                    jsonString = HttpUtils.readInputStream(is);
                    try {
                        JSONObject whole_data = new JSONObject(jsonString);
                        int i;
                        JSONArray jsonArray;
                        if(tag==0)
                            i=(Api.news_page-1)*10+1;
                        else if(tag==1)
                            i=(Api.original_page-1)*10+1;
                        else
                            i=(Api.interact_page-1)*10+1;
                        if(i<whole_data.getInt("count")) {
                            if(tag==0)
                                jsonArray = whole_data.getJSONArray("news");
                            else if(tag==1)
                                jsonArray = whole_data.getJSONArray("original");
                            else
                                jsonArray = whole_data.getJSONArray("interact");
                            int request_num = 0;
                            while (i<=whole_data.getInt("count") && request_num<10) {
                                HashMap<String, Object> map = new HashMap<>();
                                JSONObject jsonObject = jsonArray.getJSONObject(request_num++);
                                i++;
                                URL author_url = new URL(jsonObject.get(Api.author).toString());
                                String author_name = getAuthorNameInfo(author_url);
                                map.put(Api.author, author_name);
                                map.put(Api.body, jsonObject.getString(Api.body));
                                String stringDate = jsonObject.getString(Api.timestamp);
                                SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
                                try {
                                    Date date = sdf.parse(stringDate);
                                    sdf = new SimpleDateFormat("MM-dd", Locale.US);
                                    map.put(Api.timestamp, sdf.format(date));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    map.put(Api.title, jsonObject.getString(Api.title));
                                    map.put(Api.comments, jsonObject.get(Api.comments));
                                    map.put(Api.image, getFirstURL(jsonObject.getString(Api.body)));
                                    if (tag == 2)
                                        Api.interact_list.add(map);
                                    else if (tag == 1)
                                        Api.original_list.add(map);
                                    else
                                        Api.news_list.add(map);
                                }
                            }
                            is.close();
                            conn.disconnect();
                            if(tag == 2)
                                Api.interact_page++;
                            else if(tag == 1)
                                Api.original_page++;
                            else
                                Api.news_page++;
                            REFRESH_STATE = 0;//加载成功
                        }else
                            REFRESH_STATE=1;//已是最新数据
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        while(true){
            if(!thread.isAlive())
               return REFRESH_STATE;
        }
}


    public int DataRequireOver(final int tag) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    Api.temp_news_list.clear();
                    Api.temp_interact_list.clear();
                    Api.temp_original_list.clear();
                    REFRESH_STATE=-1;//initialize
                    if(tag == 2) {
                        Api.interact_page = 1;
                        url = new URL(Api.api + Api.inter +Api.interact_page);
                    }else if(tag == 1) {
                        Api.original_page = 1;
                        url = new URL(Api.api + Api.origins + Api.original_page );
                    }else {
                        Api.news_page=1;
                        url = new URL(Api.api + Api.news+Api.news_page );
                    }
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.setUseCaches(false);
                    conn.setRequestMethod("GET");
                    conn.connect();
                    is = conn.getInputStream();
                    jsonString = HttpUtils.readInputStream(is);
                    try {
                        int i;
                        JSONObject whole_data = new JSONObject(jsonString);
                        if(tag==0) {
                            int last_current_id = Api.news_current_id;
                            Api.news_current_id = whole_data.getInt("count");
                            i = Api.news_current_id - last_current_id;
                        }else if(tag==1) {
                            int last_current_id = Api.original_current_id;
                            Api.original_current_id = whole_data.getInt("count");
                            i = Api.original_current_id - last_current_id;
                        }else {
                            int last_current_id = Api.interact_current_id;
                            Api.interact_current_id = whole_data.getInt("count");
                            i = Api.interact_current_id - last_current_id;
                        }//if refreshing is neccesary
                        if(i!=0) {
                            JSONArray jsonArray = whole_data.getJSONArray("news");
                            for (int index = 0; index < 10; index++) {
                                if (index >= whole_data.getInt("count"))
                                    break;
                                else {
                                    HashMap<String, Object> map = new HashMap<>();
                                    JSONObject jsonObject = jsonArray.getJSONObject(index);
                                    URL author_url = new URL(jsonObject.getString(Api.author));
                                    String author_name = getAuthorNameInfo(author_url);
                                    map.put(Api.author, author_name);
                                    map.put(Api.body, jsonObject.getString(Api.body));
                                    String stringDate = jsonObject.getString(Api.timestamp);
                                    SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
                                    try {
                                        Date date = sdf.parse(stringDate);
                                        sdf = new SimpleDateFormat("MM-dd", Locale.US);
                                        map.put(Api.timestamp, sdf.format(date));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    } finally {
                                        map.put(Api.title, jsonObject.getString(Api.title));
                                        map.put(Api.comments, jsonObject.get(Api.comments));
                                        map.put(Api.image, getFirstURL(jsonObject.getString(Api.body)));
                                        if (tag == 2)
                                            Api.temp_interact_list.add(map);
                                        else if (tag == 1)
                                            Api.temp_original_list.add(map);
                                        else
                                            Api.temp_news_list.add(map);
                                    }
                                }
                                is.close();
                                conn.disconnect();
                                if (tag == 2) {
                                    Api.interact_list.clear();
                                    Api.interact_list.addAll(Api.temp_interact_list);
                                } else if (tag == 1) {
                                    Api.original_list.clear();
                                    Api.original_list.addAll(Api.temp_original_list);
                                } else {
                                    Api.news_list.clear();
                                    Api.news_list.addAll(Api.temp_news_list);
                                }
                                REFRESH_STATE = 0;//加载成功
                            }
                        }else
                            REFRESH_STATE=1;//已是最新数据
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        while(true)
            if(!thread.isAlive())
                return REFRESH_STATE;
    }

    public static boolean isNetworkAvailable(Context context){
        boolean netStatus = false;
        try{
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if(networkInfo!=null)
                if(networkInfo.isAvailable() && networkInfo.isConnected())
                    netStatus = true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return netStatus;
    }

    private String getAuthorNameInfo(URL url){
        String name = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("GET");
            connection.connect();
            InputStream is = connection.getInputStream();
            String jsonString = HttpUtils.readInputStream(is);
            try{
                JSONObject data = new JSONObject(jsonString);
                name = data.getString("username");
                is.close();
                connection.disconnect();
            }catch(Exception e){
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return name;

    }

    private String getFirstURL(String body){
        int ending_tag;
        if(body.length()<14)
            return "null";
        else {
            int img_first_index = body.indexOf("/static/upload");
            if (img_first_index != -1) {
                int img_last_index = body.indexOf(".jpg", img_first_index + 1);
                ending_tag = body.indexOf("/>",img_first_index+1);
                if (img_last_index == -1 || img_last_index>ending_tag)
                    img_last_index = body.indexOf(".png", img_first_index + 1);
                char[] text = new char[img_last_index - img_first_index + 4];
                int k = 0;
                for (int j = img_first_index; j < img_last_index + 4; j++)
                    text[k++] = body.charAt(j);
                return new String(text);
            } else
                return "null";
        }
    }


}

