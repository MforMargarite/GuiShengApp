package com.muxistudio.guishengapp;


import java.util.ArrayList;
import java.util.HashMap;


public class Api {
    public static final String api = "http://121.43.230.104:5000/api/v1.0/";
    public static float scale;
    public static final String image_api="http://121.43.230.104:5000";
    public static final String token = "token";
    public static final String news = "news?page=";
    public static final String origins = "origins?page=";
    public static final String inter = "inters?page=";
    public static final String users= "users/";
    public static String user_token;
    public static String user_id;
    public static int screen_width;
    public static int screen_height;
    public static int log_status = 0;
    public static String logged_username;
    public static String logged_user_email;
    public static final String avatar_root = "7xq66c.com1.z0.glb.clouddn.com/";
    public static String user_jsonString;
    public static final String author="author";
    public static final String body = "body";
    public static final String comments = "comments";
    public static final String timestamp = "timestamp";
    public static final String title = "title";
    public static final String image = "image";
    public static int news_current_id = 0;
    public static int original_current_id = 0;
    public static int interact_current_id = 0;
    public static int news_page = 1;
    public static int original_page = 1;
    public static int interact_page = 1;
    public static String newscomment = "newscomments/";
    public static String originscomment = "originscomments/";
    public static String intersscomment = "intersscomments/";
    public static ArrayList<HashMap<String,Object>> news_list = new ArrayList<>();
    public static ArrayList<HashMap<String,Object>> temp_news_list = new ArrayList<>();
    public static ArrayList<HashMap<String,Object>> original_list = new ArrayList<>();
    public static ArrayList<HashMap<String,Object>> temp_original_list = new ArrayList<>();
    public static ArrayList<HashMap<String,Object>> interact_list = new ArrayList<>();
    public static ArrayList<HashMap<String,Object>> temp_interact_list = new ArrayList<>();
    public static String ACCESSKEY = "Rp5cnFUf9q6YaiFiYoWXX9DJZ64S_KUfcF5wTqUR";
    public static String SECRETKEY = "HNla55oWKVfsXP9z5RL1b-JIhelJZAaro6Z9pWE-";
}
