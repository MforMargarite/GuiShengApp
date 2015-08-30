package com.muxistudio.guishengapp;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


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
    public int which_tab=-1;

    public MyListView(final Context context,AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        header_loading_view = LayoutInflater.from(context).inflate(R.layout.header_loading_view, null);
        header_view = LayoutInflater.from(context).inflate(R.layout.headerview_layout, null);
        footer_view = LayoutInflater.from(context).inflate(R.layout.footerview_layout, null);
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
        addHeaderView(header_view);
        addFooterView(footer_view);
        setFooterDividersEnabled(false);
        setOnItemClickListener(MyListView.this);
        setOverScrollMode(OVER_SCROLL_NEVER);
        netDataObtain = new NetDataObtain();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(context,subActivity.class);
        intent.putExtra("id", (int)id);
        intent.putExtra("tab",which_tab);
        context.startActivity(intent);
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
        Toast.makeText(context,getResources().getText(R.string.is_newest),Toast.LENGTH_SHORT).show();
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
        Toast.makeText(context,getResources().getText(R.string.refresh_fail),Toast.LENGTH_SHORT).show();
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