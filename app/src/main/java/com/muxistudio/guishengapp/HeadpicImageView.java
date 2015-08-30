package com.muxistudio.guishengapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;


public class HeadpicImageView extends ImageView {
    PorterDuffXfermode porterDuffXfermode ;
    Bitmap bitmap;
    Rect src_rect,dst_rect;
    Paint paint;
    Path path;
    PaintFlagsDrawFilter paintFlagsDrawFilter;
    Context context;
    public static int parent_width,parent_height;

    public HeadpicImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.unlog);
        paint = new Paint();
        path = new Path();
        paintFlagsDrawFilter = new PaintFlagsDrawFilter(0,Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
    }

    @Override
    public void onDraw(Canvas canvas){
        dst_rect = new Rect(4,4,getWidth()-4,getHeight()-4);
        path.addCircle(getWidth() / 2, getHeight() / 2, getHeight() / 2, Path.Direction.CW);
        canvas.setDrawFilter(paintFlagsDrawFilter);
        canvas.clipPath(path);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setAntiAlias(true);
        paint.setARGB(255, 153, 153, 153);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getHeight() / 2, paint);
        path.reset();
        path.addCircle(getWidth() / 2, getHeight() / 2, getHeight() / 2 - 7f, Path.Direction.CW);
        canvas.clipPath(path);
        canvas.drawBitmap(bitmap,src_rect,dst_rect, null);
    }

    @Override
    public void setImageBitmap(Bitmap bitmap){
        this.bitmap = bitmap;
        super.setImageBitmap(bitmap);
        src_rect = new Rect(parent_width/2-240,parent_height/2-240,parent_width/2+240,parent_height/2+240);
    }
}