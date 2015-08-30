package com.muxistudio.guishengapp;

import android.text.Spanned;

import java.util.ArrayList;
import java.util.HashMap;


public class Api {
    public static final String api = "http://121.43.230.104:5000/api/v1.0/";
    public static final String news = "news/?id=";
    public static final String origins = "origins/";
    public static final String inter = "inter/";
    public static final String author="author";
    public static final String body = "body";
    public static final String comments = "comments";
    public static final String timestamp = "timestamp";
    public static final String title = "title";
    public static final String url = "url";
    public static final String image = "image";
    public static int news_current_id = 1;
    public static int original_current_id = 1;
    public static int interact_current_id = 1;
    public static int news_last_id=1;
    public static int original_last_id=1;
    public static int interact_last_id=1;
    public static ArrayList<HashMap<String,Object>> news_list = new ArrayList<>();
    public static ArrayList<HashMap<String,Object>> temp_news_list = new ArrayList<>();
    public static ArrayList<HashMap<String,Object>> original_list = new ArrayList<>();
    public static ArrayList<HashMap<String,Object>> temp_original_list = new ArrayList<>();
    public static ArrayList<HashMap<String,Object>> interact_list = new ArrayList<>();
    public static ArrayList<HashMap<String,Object>> temp_interact_list = new ArrayList<>();
}
