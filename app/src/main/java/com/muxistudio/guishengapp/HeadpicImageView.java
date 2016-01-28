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

import java.lang.ref.SoftReference;


public class HeadpicImageView extends ImageView {
    PorterDuffXfermode porterDuffXfermode ;
    Bitmap bitmap;
    Rect src_rect,dst_rect;
    Paint paint;
    Path path;
    PaintFlagsDrawFilter paintFlagsDrawFilter;
    Context context;
    SoftReference head_pic;
    public static int parent_width,parent_height;

    public HeadpicImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.unlog);
        head_pic = new SoftReference<>(bitmap);
        paint = new Paint();
        path = new Path();
        paintFlagsDrawFilter = new PaintFlagsDrawFilter(0,Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
    }

    public HeadpicImageView(Context context) {
        super(context);
        this.context = context;
        porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.unlog);
        head_pic = new SoftReference<>(bitmap);
        paint = new Paint();
        path = new Path();
        paintFlagsDrawFilter = new PaintFlagsDrawFilter(0,Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
    }

    @Override
    public void onDraw(Canvas canvas){
        dst_rect = new Rect(0,0,getWidth(),getHeight());
        path.addCircle(getWidth() / 2, getHeight() / 2, getHeight() / 2, Path.Direction.CW);
        canvas.setDrawFilter(paintFlagsDrawFilter);
        canvas.clipPath(path);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setAntiAlias(true);
        paint.setARGB(255, 153, 153, 153);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getHeight() / 2, paint);
        path.reset();
        path.addCircle(getWidth() / 2, getHeight() / 2, getHeight() / 2 , Path.Direction.CW);
        canvas.clipPath(path);
        if(head_pic.get()!=null)
            canvas.drawBitmap((Bitmap)head_pic.get(), src_rect, dst_rect, null);
        else
            canvas.drawBitmap(bitmap, src_rect, dst_rect, null);
    }

    @Override
    public void setImageBitmap(Bitmap bitmap){
        this.bitmap = bitmap;
        head_pic = new SoftReference<>(bitmap);
        super.setImageBitmap(bitmap);
        src_rect = new Rect(0,0,getWidth(),getHeight());
    }
}