package com.muxistudio.guishengapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class MyListView extends ListView implements AdapterView.OnItemClickListener{
    Context context;
    public ListViewAdapter adapter;
    View footer_view,header_loading_view,header_view,footer_loading,header_loading;
    View begin_refresh,ready_to_refresh,refreshing,footer_success_load,footer_fail_load,footer_is_newest;
    int footer_tag = 0;// is footer tag loaded
    float touchDownY,instanceY;
    int header_state =-2;
    int one_refresh_a_time = 0;
    private static final int BEGIN_A_REFRESH=0;//after that no refresh gesture will be recognized until its finishing
    private static final int READY_TO_REFRESH=1;
    private static final int REFRESHING=2;
    private static final int ROLL_BACK=3;
    private static final int DONE=-1;
    boolean isPrepared = false;
    NetDataObtain netDataObtain;
    OnRefreshListener onRefreshListener;
    ImageView bitmap;
    public MyListView(Context context,AttributeSet attributeSet) {
        super(context, attributeSet);
        header_loading_view = LayoutInflater.from(getContext()).inflate(R.layout.header_loading_view, null,false);
        header_view = LayoutInflater.from(getContext()).inflate(R.layout.headerview_layout, null,false);
        footer_view = LayoutInflater.from(getContext()).inflate(R.layout.footerview_layout, null,false);
        footer_loading = footer_view.findViewById(R.id.footer_loading);
        header_loading = header_loading_view.findViewById(R.id.header_loading);
        measureView(header_loading_view);
        header_loading_view.setPadding(0, -header_loading_view.getMeasuredHeight(), 0, 0);
        header_loading_view.invalidate();
        measureView(footer_view);
        footer_view.setPadding(0, -footer_view.getMeasuredHeight(), 0, 0);
        footer_view.invalidate();
        begin_refresh = header_loading_view.findViewById(R.id.begin_refresh);
        ready_to_refresh = header_loading_view.findViewById(R.id.ready_to_refresh);
        refreshing = header_loading_view.findViewById(R.id.refreshing);
        footer_success_load = footer_view.findViewById(R.id.footer_view);
        footer_fail_load = footer_view.findViewById(R.id.failed_loading);
        footer_is_newest = footer_view.findViewById(R.id.is_newest);

        setHeaderDividersEnabled(false);
        addHeaderView(header_loading_view);
        initHeaderView(Integer.parseInt(getTag().toString()));
        addHeaderView(header_view);
        addFooterView(footer_view);
        setFooterDividersEnabled(false);
        setOnItemClickListener(MyListView.this);
        setOverScrollMode(OVER_SCROLL_NEVER);
        netDataObtain = new NetDataObtain(context);
    }


    private void initHeaderView(int tag) {
          HashMap<String, Object> map;
         switch (tag) {
             case 1:
                 if (Api.original_current_id > 0)
                     map = Api.original_list.get(0);
                 else
                     map = null;
                 break;
             case 2:
                 if (Api.interact_current_id > 0)
                     map = Api.interact_list.get(0);
                 else
                     map = null;
                 break;
             default:
                 if (Api.news_current_id > 0) {
                     map = Api.news_list.get(0);
                 }else
                     map = null;
                 break;
         }
         if (map != null) {
             TextView every_title = (TextView) header_view.findViewById(R.id.every_title);
             every_title.setText(map.get("title").toString());
             ImageView every_pic = (ImageView)header_view.findViewById(R.id.every_pic);
             new ImageLoad().showHeaderImageByThread(every_pic, Api.image_api + map.get("image").toString());
         } else {
             TextView underline = (TextView) header_view.findViewById(R.id.underline);
             underline.setBackgroundColor(getResources().getColor(R.color.white));
             ImageView imageView = (ImageView) header_view.findViewById(R.id.title_icon);
             imageView.setVisibility(GONE);
         }
     }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getContext(),subActivity.class);
        intent.putExtra("id", position);
        intent.putExtra("tab", Integer.parseInt(getTag().toString()));
        getContext().startActivity(intent);
    }

    private boolean prepareFooterView(){
        if(footer_tag == 0) {
            footer_success_load.setVisibility(View.VISIBLE);
            footer_tag = 1;
            return true;
        }
        return false;
    }

    public void setFooter_is_newest(){
        footer_success_load.setVisibility(GONE);
        footer_is_newest.setVisibility(VISIBLE);
    }


    public void setFooter_fail_load(){
        footer_success_load.setVisibility(GONE);
        footer_fail_load.setVisibility(VISIBLE);
    }


    public void rollback(int tag){
        if(tag==0){
            header_loading_view.setPadding(0,-header_loading_view.getMeasuredHeight(),0,0);
            refreshing.setVisibility(GONE);
            begin_refresh.setVisibility(GONE);
        }
        else{
            footer_tag = 0;
            isPrepared = false;
            adapter.notifyDataSetChanged();
            footer_view.setPadding(0,-footer_view.getMeasuredHeight(),0,0);
            footer_fail_load.setVisibility(GONE);
            footer_is_newest.setVisibility(GONE);
            footer_success_load.setVisibility(GONE);
        }
    }

    public void alreadyRefreshed(){
        Toast.makeText(getContext(),getResources().getText(R.string.is_newest),Toast.LENGTH_SHORT).show();
        header_loading_view.setPadding(0, -header_loading_view.getMeasuredHeight(), 0, 0);
        refreshing.setVisibility(GONE);
        begin_refresh.setVisibility(GONE);
        one_refresh_a_time=0;
    }

    public void refresh(){
        adapter.notifyDataSetChanged();
        header_loading_view.setPadding(0, -header_loading_view.getMeasuredHeight(), 0, 0);
        refreshing.setVisibility(GONE);
        begin_refresh.setVisibility(GONE);
        one_refresh_a_time=0;

    }

    public void refreshFail(){
        Toast.makeText(getContext(),getResources().getText(R.string.refresh_fail),Toast.LENGTH_SHORT).show();
        header_loading_view.setPadding(0, -header_loading_view.getMeasuredHeight(), 0, 0);
        refreshing.setVisibility(GONE);
        begin_refresh.setVisibility(GONE);
        one_refresh_a_time=0;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (one_refresh_a_time == 0 && getFirstVisiblePosition() == 0 ) {
                    touchDownY = event.getY();
                    header_state = BEGIN_A_REFRESH;
                } else
                    header_state = DONE;
                actAccordingState();
                if(header_state!=DONE || footer_tag!=0)
                    return true;
                if (getLastVisiblePosition() == getCount()-1  &&  instanceY<touchDownY && footer_tag==0 )
                    isPrepared = prepareFooterView();
                break;
            case MotionEvent.ACTION_MOVE:
                instanceY = event.getY();
                if (getFirstVisiblePosition() == 0 && one_refresh_a_time == 0 && header_state==BEGIN_A_REFRESH && instanceY-touchDownY>24) {
                    begin_refresh.setVisibility(View.VISIBLE);
                    one_refresh_a_time = 1;
                    header_loading_view.setPadding(0, 0, 0, 0);
                }else if (instanceY - touchDownY < 176 && getFirstVisiblePosition() == 0 && one_refresh_a_time == 1)
                    header_loading_view.setPadding(0, (int) (instanceY - touchDownY), 0, 0);
                else if ((instanceY - touchDownY) > 176 && getFirstVisiblePosition() == 0 && one_refresh_a_time == 1) {
                    header_state = READY_TO_REFRESH;
                    actAccordingState();
                }
                if(isPrepared){
                int distance = (int) (touchDownY - instanceY);
                    if (distance > 160)
                        distance = 160;
                    footer_view.setPadding(0, 0, 0, distance);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (one_refresh_a_time == 1) {
                    if (header_state == READY_TO_REFRESH) {
                        header_loading_view.setPadding(0, 0, 0, 0);
                        ready_to_refresh.setVisibility(GONE);
                        refreshing.setVisibility(VISIBLE);
                        header_state = REFRESHING;
                    } else
                        header_state = ROLL_BACK;
                    actAccordingState();
                }
                if(isPrepared) {
                    footer_view.setPadding(0, 8, 0, 8);
                    if (onRefreshListener != null )
                        onRefreshListener.onFooterRefresh();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void actAccordingState(){
        switch(header_state){
            case BEGIN_A_REFRESH:
                break;
            case READY_TO_REFRESH:
                int pull_down = (int)(instanceY-touchDownY);
                begin_refresh.setVisibility(View.GONE);
                ready_to_refresh.setVisibility(View.VISIBLE);
                if(pull_down>240)
                    pull_down = 240;
                header_loading_view.setPadding(0, pull_down, 0, 0);
                break;
            case REFRESHING:
                if(onRefreshListener!=null)
                   onRefreshListener.onHeaderRefresh();
                break;
            case ROLL_BACK:
                header_loading_view.setPadding(0,-header_loading_view.getMeasuredHeight(),0,0);
                one_refresh_a_time = 0;
                begin_refresh.setVisibility(View.GONE);
                header_state = DONE;
                break;
        case DONE:break;
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
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,MeasureSpec.EXACTLY);
        else
            childHeightSpec = MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED);
        child.measure(childWidthSpec,childHeightSpec);
    }


    public interface OnRefreshListener{
         void onHeaderRefresh();
         void onFooterRefresh();
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener){
        this.onRefreshListener = onRefreshListener;
    }
}

