package com.muxistudio.guishengapp;


import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;



public class URLDrawable extends BitmapDrawable {
    private Drawable drawable;
    private int width;
    public URLDrawable(Drawable defaultDrawable,int width){
        this.width = width;
        setDrawable(defaultDrawable);
    }
    public void setDrawable(Drawable drawable){
        this.drawable = drawable;
        drawable.setBounds(0,0,width,9* width / 16);
        setBounds(0,0,width,9*width/16);
    }

    @Override
    public void draw(Canvas canvas){
        if(drawable!=null)
            drawable.draw(canvas);
    }
}
