package com.muxistudio.guishengapp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class subActivity extends Activity implements View.OnClickListener{
    HashMap<String,Object> map;
    ImageButton comment,like;
    TextView comment_body;
    private final int GET_COMMENT=0;
    int like_status=0;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.sub_layout);
        ViewGroup myActionBarLayout = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.mainactivity_actionbar_layout, null);
        ActionBar actionBar = getActionBar();
        actionBar.setCustomView(myActionBarLayout);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        int id = intent.getIntExtra("id", -1);
        int tab = intent.getIntExtra("tab",-1);
        comment = (ImageButton)findViewById(R.id.comment);
        like = (ImageButton)findViewById(R.id.like);
        comment_body = (TextView)findViewById(R.id.body);

        if(tab==0)
            map = Api.news_list.get(id);
        else if(tab==1)
            map = Api.original_list.get(id);
        else
            map = Api.interact_list.get(id);

        final CommentListView commentListView = (CommentListView)findViewById(R.id.comment_listview);
        commentListView.setOnRefreshListener(new MyListView.OnRefreshListener() {
            @Override
            public void onHeaderRefresh() {
            }

            @Override
            public void onFooterRefresh() {
                new AsyncTask<Void, Void, Void>() {
                    int footer_refresh_state;
                    protected Void doInBackground(Void... params) {
                        long beginTime = System.currentTimeMillis();
                        if (NetDataObtain.isNetworkAvailable(subActivity.this))
                            footer_refresh_state = new NetDataObtain().DataRequireAppend();
                        long endTime = System.currentTimeMillis();
                        if (endTime - beginTime < 2000)
                            try {
                                Thread.sleep(2000 - (endTime - beginTime));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        if (footer_refresh_state == 1)
                            commentListView.setFooter_is_newest();
                        else if (footer_refresh_state == -1)
                            commentListView.setFooter_fail_load();
                        new AsyncTask<Void, Void, Void>() {
                            protected Void doInBackground(Void... params) {
                                try {
                                    Thread.sleep(1100);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void result) {
                                commentListView.rollback();
                            }
                        }.execute(null, null, null);
                    }
                }.execute(null, null, null);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.comment:Intent comment_intent = new Intent(subActivity.this,CommentActivity.class);
                startActivity(comment_intent);
                break;
            case R.id.like:
                if(like_status==0) {
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.like);
                    like.setImageBitmap(bitmap);
                    like_status=1;
                }else{
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.unlike);
                    like.setImageBitmap(bitmap);
                    like_status=0;
                }
        }
    }
}
