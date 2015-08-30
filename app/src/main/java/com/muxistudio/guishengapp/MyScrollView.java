package com.muxistudio.guishengapp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;


public class MyScrollView extends ScrollView {
   public MyScrollView(Context context,AttributeSet attributeSet){
       super(context,attributeSet);
   }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(MainActivity.status == 0)
                    return false;
                else
                    return true;
        }
        return super.onTouchEvent(event);
    }

}
