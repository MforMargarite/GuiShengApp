package com.muxistudio.guishengapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


public class NetDataObtain {
    static URL url;
    static HttpURLConnection conn;
    static InputStream is;
    static String jsonString;
    public static int REFRESH_STATE = -1;


    public int DataRequireAppend() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    REFRESH_STATE=-1;//initialize
                    url = new URL(Api.api + Api.news);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.setUseCaches(false);
                    conn.setRequestMethod("GET");
                    conn.connect();
                    is = conn.getInputStream();
                    jsonString = HttpUtils.readInputStream(is);
                    try {
                        JSONObject whole_data = new JSONObject(jsonString);
                        int i=Api.news_last_id-7;
                        if(i<0)
                            i=Api.news_last_id;
                        if(i!=1) {//能一次加载常规加载量
                            JSONArray jsonArray = whole_data.getJSONArray("news");
                            while (i >= 0) {
                                HashMap<String, Object> map = new HashMap<>();
                                JSONObject jsonObject = jsonArray.getJSONObject(--i);
                                URL author_url =new URL(jsonObject.get(Api.author).toString());
                                String author_name = getAuthorNameInfo(author_url);
                                map.put(Api.author, author_name);
                                map.put(Api.body, jsonObject.getString(Api.body));
                                map.put(Api.timestamp, jsonObject.getString(Api.timestamp));
                                map.put(Api.title, jsonObject.getString(Api.title));
                                map.put(Api.comments, jsonObject.get(Api.comments));
                                map.put(Api.url, jsonObject.get(Api.url));
                                Spanned map_source = Html.fromHtml(jsonObject.getString(Api.image));
                                Log.i("why",map_source.toString());
                                map.put(Api.image, map_source);
                                Api.news_list.add(map);
                                Api.news_last_id--;
                            }
                            is.close();
                            conn.disconnect();
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


    public int DataRequireOver() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    REFRESH_STATE=-1;//initialize
                    url = new URL(Api.api + Api.news);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.setUseCaches(false);
                    conn.setRequestMethod("GET");
                    conn.connect();
                    is = conn.getInputStream();
                    jsonString = HttpUtils.readInputStream(is);
                    try {
                        JSONObject whole_data = new JSONObject(jsonString);
                        int last_current_id = Api.news_current_id;
                        Api.news_current_id = whole_data.getInt("count");
                        int i = Api.news_current_id -last_current_id;//if refreshing is neccesary
                        if(i!=0) {
                            JSONArray jsonArray = whole_data.getJSONArray("news");
                            int temp = Api.news_current_id -1;//the newest id
                            int refreshing_number = 7;// the number of adding
                            while (refreshing_number > 0) {
                                HashMap<String, Object> map = new HashMap<>();
                                JSONObject jsonObject = jsonArray.getJSONObject(temp);
                                URL author_url =new URL(jsonObject.getString(Api.author));
                                String author_name = getAuthorNameInfo(author_url);
                                map.put(Api.author, author_name);
                                map.put(Api.body, jsonObject.getString(Api.body));
                                map.put(Api.timestamp, jsonObject.getString(Api.timestamp));
                                map.put(Api.title, jsonObject.getString(Api.title));
                                map.put(Api.comments, jsonObject.get(Api.comments));
                                map.put(Api.url, jsonObject.get(Api.url));
                                Log.i("why", jsonObject.getString(Api.image));
                                Spanned map_source = Html.fromHtml(jsonObject.getString(Api.image));
                                map.put(Api.image, map_source);
                                Api.temp_news_list.add(map);
                                refreshing_number--;
                                temp--;
                            }
                            is.close();
                            conn.disconnect();
                            Api.news_list.clear();
                            Api.news_list.addAll(Api.temp_news_list);
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

}

