package com.muxistudio.guishengapp;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.Toast;


public class MyViewPager extends ViewPager{
    Context context;

    public MyViewPager(Context context,AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if ( MainActivity.status == 1)
                    return true;
        }
        return super.onInterceptTouchEvent(event);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
       switch (event.getAction()) {
           case MotionEvent.ACTION_DOWN:
               if(MainActivity.status == 1)
                  return false;
               else
                   return true;
       }
        return super.onTouchEvent(event);
    }
}
