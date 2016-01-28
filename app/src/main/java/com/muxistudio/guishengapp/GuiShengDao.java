package com.muxistudio.guishengapp;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiShengDao {
    private SQLiteDatabase db;

    public GuiShengDao(Context context){
        db = NetOffDatabaseHelper.getInstance(context);
    }

    public void insertComment(String title, String content, String date){
        String insertDiarySQL = "INSERT INTO " + NetOffDatabaseHelper.TABLE_DATA
                + " VALUES (NULL, ?, ?, ?)";
        db.execSQL(insertDiarySQL, new String[]{title, content, date});
    }


    public void insertUserInfo(String logname, String userid,String token,String username,String avatar){
        Cursor cursor = db.rawQuery("SELECT * FROM " + NetOffDatabaseHelper.TABLE_USER + " WHERE log_name = ? ", new String[]{logname});
        if(cursor.getCount()==0) {
            String insertSQL = "INSERT INTO " + NetOffDatabaseHelper.TABLE_USER
                    + " VALUES (NULL, ?, ?, ?, ?, ?)";
            db.execSQL(insertSQL, new String[]{logname, userid, token, username, avatar});
        }
        cursor.close();
    }

    public List<Map<String, String>> loadData(){
        String querySQL = "SELECT * from " + NetOffDatabaseHelper.TABLE_DATA;
        Cursor cursor = db.rawQuery(querySQL, null);
        List<Map<String, String>> list = new ArrayList<>();
        if (cursor.getCount() >= 1){
            while (cursor.moveToNext()){
                Map<String, String> map = new HashMap<>();
                map.put(NetOffDatabaseHelper.KEY_TITLE,
                        cursor.getString(cursor.getColumnIndex(NetOffDatabaseHelper.KEY_TITLE)));
                map.put(NetOffDatabaseHelper.KEY_BITMAP,
                        cursor.getString(cursor.getColumnIndex(NetOffDatabaseHelper.KEY_BITMAP)));
                map.put(NetOffDatabaseHelper.KEY_AUTHOR,
                        cursor.getString(cursor.getColumnIndex(NetOffDatabaseHelper.KEY_AUTHOR)));
                map.put(NetOffDatabaseHelper.KEY_DATE,
                        cursor.getString(cursor.getColumnIndex(NetOffDatabaseHelper.KEY_DATE)));
                map.put(NetOffDatabaseHelper.KEY_DATA_ID,
                        cursor.getString(cursor.getColumnIndex(NetOffDatabaseHelper.KEY_DATA_ID)));
                list.add(map);
            }
        }
        cursor.close();
        return list;
    }

    public String getUserToken(String username){
        Cursor cursor;
        cursor = db.rawQuery("SELECT * FROM " + NetOffDatabaseHelper.TABLE_USER + " WHERE log_name = ? ", new String[]{username});
        if(cursor.getCount() == 1) {
            cursor.moveToFirst();
            String token = cursor.getString(cursor.getColumnIndex("token"));
            cursor.close();
            return token;
        }else {
            cursor.close();
            return null;
        }
    }

    public String getUserName(String username){
        Cursor cursor;
        cursor = db.rawQuery("SELECT * FROM " + NetOffDatabaseHelper.TABLE_USER + " WHERE log_name = ? ", new String[]{username});
        if(cursor.getCount() == 1) {
            cursor.moveToFirst();
            String name = cursor.getString(cursor.getColumnIndex("user_name"));
            cursor.close();
            return name;
        }else {
            cursor.close();
            return null;
        }
    }


    public String getUserID(String username){
        Cursor cursor;
        cursor = db.rawQuery("SELECT * FROM " + NetOffDatabaseHelper.TABLE_USER + " WHERE log_name = ? ", new String[]{username});
        if(cursor.getCount() == 1) {
            cursor.moveToFirst();
            String id = cursor.getString(cursor.getColumnIndex("id"));
            cursor.close();
            return id;
        }else {
            cursor.close();
            return null;
        }
    }

    public void updateUserName(String username,String new_name){
        String updateUserName = "UPDATE "+ NetOffDatabaseHelper.TABLE_USER +" SET user_name = ? WHERE user_name = ? ";
        db.execSQL(updateUserName,new String[]{new_name,username});
    }
}
