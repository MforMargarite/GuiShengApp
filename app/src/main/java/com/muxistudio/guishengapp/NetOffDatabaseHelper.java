package com.muxistudio.guishengapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class NetOffDatabaseHelper extends SQLiteOpenHelper {
    private static NetOffDatabaseHelper instance = null;

    private final static String DATABASE_NAME = "when_net_off_db";
    private final static int DATABASE_VERSION = 1;

    public final static String TABLE_DATA = "data";
    public final static String KEY_DATA_ID = "_id";
    public final static String KEY_TITLE = "title";
    public final static String KEY_DATE = "date";
    public final static String KEY_AUTHOR = "author";
    public final static String KEY_BITMAP = "bitmap";// save URL.toString()

    public final static String TABLE_USER = "user";
    public final static String KEY_TABLE_ID = "_id";
    public final static String KEY_LOG_NAME = "log_name";
    public final static String KEY_USER_ID = "user_id";
    public final static String KEY_USER_NAME = "user_name";
    public final static String KEY_TOKEN = "token";
    public final static String KEY_AVATAR = "avatar";


    public final static String TYPE_TEXT = " TEXT, ";

    public NetOffDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static SQLiteDatabase getInstance(Context context){
        if (instance == null){
            instance = new NetOffDatabaseHelper(context);
        }
        return instance.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createGuiShengUserSQL = "CREATE TABLE IF NOT EXISTS " + TABLE_USER
                + "(" + KEY_TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_LOG_NAME + TYPE_TEXT
                + KEY_USER_ID + TYPE_TEXT
                + KEY_TOKEN + TYPE_TEXT
                + KEY_USER_NAME + TYPE_TEXT
                + KEY_AVATAR + " TEXT);";

        String createGuiShengSQL = "CREATE TABLE IF NOT EXISTS " + TABLE_DATA
                + "(" + KEY_DATA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_AUTHOR + TYPE_TEXT
                + KEY_BITMAP + TYPE_TEXT
                + KEY_DATE + TYPE_TEXT
                + KEY_TITLE  + " TEXT);";
        db.execSQL(createGuiShengSQL);
        db.execSQL(createGuiShengUserSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
