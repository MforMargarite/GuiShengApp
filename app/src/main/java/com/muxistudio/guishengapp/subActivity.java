package com.muxistudio.guishengapp;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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


public class subActivity extends AppCompatActivity implements View.OnTouchListener {
    HashMap<String, Object> map;
    ImageButton comment, like;
    CommentListView commentListView;
    CommentScrollView commentScrollView;
    TextView sub_title,detail_author,detail_timestamp;
    AppCompatTextView textView_body;
    WebView webView_body;
    Toolbar toolbar;
    View footer;
    int like_status = 0;
    int id;
    int tab;
    Message msg = new Message();
    SimpleAdapter adapter;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                case 3:
                    adapter = new SimpleAdapter(subActivity.this,commentScrollView.comment_list,R.layout.comment_listview_layout,new String[]{"author","timestamp","body"},new int[]{R.id.username,R.id.time,R.id.comment});
                    commentListView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.sub_layout);
        initStatusBar();
        id = intent.getIntExtra("id", -1);
        tab = intent.getIntExtra("tab", -1);
        comment = (ImageButton) findViewById(R.id.comment);
      //  getComment(tab, id);
        sub_title = (TextView) findViewById(R.id.sub_title);
        detail_author = (TextView) findViewById(R.id.detail_author);
        detail_timestamp = (TextView) findViewById(R.id.detail_time);
        like = (ImageButton) findViewById(R.id.like);
        comment.setOnTouchListener(this);
        like.setOnTouchListener(this);

        if (id > 0) {
            if (tab == 0)
                map = Api.news_list.get(id - 1);
            else if (tab == 1)
                map = Api.original_list.get(id - 1);
            else
                map = Api.interact_list.get(id - 1);
        }

/*
        webView_body = (WebView)findViewById(R.id.webView_body);
        webView_body.getSettings().setJavaScriptEnabled(true);
        webView_body.loadUrl(map.get("body_html").toString());
        final WebViewClient webViewClient = new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webView_body.loadUrl(url);
                return true;
            }
        };
        webView_body.setWebViewClient(webViewClient);

*/
        String bodyContent = map.get(Api.body).toString();
        String title = map.get(Api.title).toString();
        String author = map.get(Api.author).toString();
        String timestamp = map.get(Api.timestamp).toString();
        sub_title.setText(title);
        detail_author.setText(author);
        detail_timestamp.setText(timestamp);
        textView_body = (AppCompatTextView) findViewById(R.id.textView_body);
        textView_body.setClickable(true);
        textView_body.setMovementMethod(LinkMovementMethod.getInstance());
        textView_body.setText(Html.fromHtml(bodyContent, new MImageGetter(this, textView_body, Api.screen_width - 32), new MTagHandler(this)));
       commentScrollView = (CommentScrollView) findViewById(R.id.comment_scrollview);
        RelativeLayout child = (RelativeLayout) commentScrollView.findViewById(R.id.child);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.BELOW, R.id.comment_listview);
        footer = LayoutInflater.from(this).inflate(R.layout.footerview_layout, null);
        measureView(footer);
        commentScrollView.footer = footer;
        commentScrollView.tab = tab;
        commentScrollView.id = id;
        commentScrollView.handler = handler;
        footer.setPadding(0, -footer.getMeasuredHeight(), 0, 0);
        footer.setLayoutParams(lp);
        child.addView(footer);
        commentScrollView.footer_success_load = footer.findViewById(R.id.footer_view);
        commentScrollView.footer_is_newest = footer.findViewById(R.id.is_newest);
        commentScrollView.footer_fail_load = footer.findViewById(R.id.failed_loading);

        commentListView = (CommentListView) findViewById(R.id.comment_listview);
        commentScrollView.setOnRefreshListener(new MyListView.OnRefreshListener() {
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
                            footer_refresh_state = new NetDataObtain(subActivity.this).DataRequireAppend(tab);
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
                            commentScrollView.setFooter_is_newest();
                        else if (footer_refresh_state == -1)
                            commentScrollView.setFooter_fail_load();
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
                               commentScrollView. rollback();
                            }
                        }.execute(null, null, null);
                    }
                }.execute(null, null, null);
            }
        });
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                switch (v.getId()) {
                    case R.id.comment:
                        comment.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case R.id.like:
                        like.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
                switch (v.getId()) {
                    case R.id.comment:
                        Intent comment_intent = new Intent(subActivity.this, CommentActivity.class);
                        comment_intent.putExtra("tab",tab);
                        comment_intent.putExtra("id",id);
                        startActivity(comment_intent);
                        break;
                    case R.id.like:
                        if (like_status == 0) {
                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.like);
                            like.setImageBitmap(bitmap);
                            like_status = 1;
                        } else {
                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.unlike);
                            like.setImageBitmap(bitmap);
                            like_status = 0;
                        }
                        break;
                }
                break;
        }
        return true;
    }


    private void initStatusBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.guisheng_red);
        }
        if (toolbar != null) {
            toolbar.setTitle(getResources().getString(R.string.guisheng));
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

        }
    }

    private void measureView(View child) {
        ViewGroup.LayoutParams params =child.getLayoutParams();
        if(params==null)
            params=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0,0,params.width);
        int lpHeight = params.height;
        int childHeightSpec ;
        if(lpHeight>0)
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(lpHeight, View.MeasureSpec.EXACTLY);
        else
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        child.measure(childWidthSpec, childHeightSpec);
    }

}