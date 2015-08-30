package com.muxistudio.guishengapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.widget.ImageView;

public class ShadeView extends ImageView {
    public ShadeView(Context context) {
        super(context);
        setWillNotDraw(false);
        setBackgroundColor(Color.BLACK);
        setAlpha(0.5f);
    }
    @Override
    public void onDraw(Canvas canvas){
        Paint paint_for_transparent = new Paint();
        paint_for_transparent.setAlpha(0);
        paint_for_transparent.setStyle(Paint.Style.FILL_AND_STROKE);
        paint_for_transparent.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, 240, paint_for_transparent);
    }
    }

