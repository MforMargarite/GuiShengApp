package com.muxistudio.guishengapp;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


public class CommentListView extends ListView{
    Context context;
    View footer,footer_success_load,footer_fail_load,footer_is_newest;
    int footer_tag = 0;
    boolean isPrepared = false;
    float touchDownY,instanceY;
    MyListView.OnRefreshListener onRefreshListener;
    public CommentListViewAdapter adapter;

    public CommentListView(Context context,AttributeSet attributeSet){
        super(context,attributeSet);
        this.context = context;
        footer = LayoutInflater.from(context).inflate(R.layout.footerview_layout,null);
        footer.setPadding(0,-footer.getMeasuredHeight(),0,0);
        footer_success_load = footer.findViewById(R.id.footer_loading);
        footer_is_newest = footer.findViewById(R.id.is_newest);
        footer_fail_load = footer.findViewById(R.id.failed_loading);
        measureView(footer);
        footer.setPadding(0, -footer.getMeasuredHeight(), 0, 0);
        footer.invalidate();
        addFooterView(footer);
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
        child.measure(childWidthSpec, childHeightSpec);
    }

    private boolean prepareFooterView(){
        if(footer_tag == 0) {
            footer.setPadding(0,0,0,0);
            footer_tag = 1;
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                touchDownY = event.getY();
                if(getLastVisiblePosition()==getCount() && footer_tag == 0)
                    isPrepared = prepareFooterView();
                break;
            case MotionEvent.ACTION_MOVE:
                instanceY=event.getY();
                int offset = (int)(touchDownY - instanceY);
                if(offset>160)
                    offset = 160;
                if(isPrepared)
                    footer.setPadding(0,0,0,offset);
                break;
            case MotionEvent.ACTION_UP:
                if(isPrepared) {
                    footer.setPadding(0, 8, 0, 8);
                    if (onRefreshListener != null)
                        onRefreshListener.onHeaderRefresh();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    public void setOnRefreshListener(MyListView.OnRefreshListener onRefreshListener){
        this.onRefreshListener = onRefreshListener;
    }

    public void setFooter_is_newest(){
        footer_success_load.setVisibility(GONE);
        footer_is_newest.setVisibility(VISIBLE);
    }


    public void setFooter_fail_load(){
        footer_success_load.setVisibility(GONE);
        footer_fail_load.setVisibility(VISIBLE);
    }

    public void rollback(){
        footer_tag = 0;
        isPrepared = false;
        adapter.notifyDataSetChanged();
        footer.setPadding(0,-footer.getMeasuredHeight(),0,0);
        footer_fail_load.setVisibility(GONE);
        footer_is_newest.setVisibility(GONE);
        footer_success_load.setVisibility(GONE);
    }


}
