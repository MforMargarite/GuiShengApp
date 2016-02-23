package com.muxistudio.guishengapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;
import java.io.InputStream;



public class HeadpicView extends ImageView{
    public Matrix matrix;
    ScaleGestureDetector multiplyTouchesListener;
    GestureDetector singleTouchListener;
    public int pic_width,pic_height;
    public int parent_width,parent_height;
    float initTransX, initTransY,initScaleX,initScaleY;//初始化时保证图片包含圆形裁剪区
    float values[];
    RectF srcRect,dstRect;
    public RectF circleRect;
    public static Bitmap bitmap;
    int initTime = 0;

    public HeadpicView(Context context){
        super(context);
    }

    public HeadpicView(Context context,Uri uri) {
        super(context);
        values = new float[9];
        setLongClickable(true);
        multiplyTouchesListener = new ScaleGestureDetector(context, new MultiplyTouchesListener());
        singleTouchListener = new GestureDetector(context, new SingleTouchListener());
        try{
            InputStream is = context.getContentResolver().openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inMutable = true;
            bitmap = BitmapFactory.decodeStream(is, null,options);
            pic_width = bitmap.getWidth();
            pic_height = bitmap.getHeight();
            is.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        matrix = new Matrix();
        srcRect = new RectF(0,0,bitmap.getWidth(),bitmap.getHeight());
        dstRect = new RectF();
        setScaleType(ScaleType.CENTER_INSIDE);
        setImageBitmap(bitmap);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (initTime != 0) {
            canvas.save();
            canvas.drawBitmap(bitmap, matrix, null);
            canvas.restore();
        }else
            super.onDraw(canvas);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(initTime == 0){
            initScaleX = pic_width>=480*Api.scale?1.0f:(float)480.0/pic_width;
            initScaleY = pic_height>=480*Api.scale?1.0f:(float)480.0/pic_height;
            matrix.setScale(initScaleX, initScaleY);
            initTransX = (parent_width-pic_width)/2>(parent_width-480*Api.scale)/2?(parent_width-480*Api.scale)/2:(parent_width-pic_width)/2;
            initTransY = (parent_height-pic_height)/2>(parent_height-480*Api.scale)/2?(parent_height-480*Api.scale)/2:(parent_height-pic_height)/2;
            matrix.postTranslate(initTransX, initTransY);
            initTime++;
            invalidate();
        }
        if(event.getPointerCount()>1)
            multiplyTouchesListener.onTouchEvent(event);
        else
            singleTouchListener.onTouchEvent(event);
        return true;
    }

    class SingleTouchListener extends GestureDetector.SimpleOnGestureListener implements GestureDetector.OnDoubleTapListener {
        int double_tap_count = 0;
        @Override
        public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY) {
            float offsetX = -distanceX;
            float offsetY = -distanceY;
            matrix.getValues(values);
            matrix.mapRect(dstRect, srcRect);
            if (dstRect.contains(circleRect)) {
                if(dstRect.left+offsetX>=(Api.screen_width-480*Api.scale)/ 2.0)//左边线出界
                    offsetX = (float)((Api.screen_width-480*Api.scale)/ 2.0-dstRect.left);
                else if(dstRect.right+offsetX<=(Api.screen_width+480*Api.scale)/ 2.0)
                    offsetX = (float)((Api.screen_width+480*Api.scale)/ 2.0 - dstRect.right);
                if(dstRect.top+offsetY>=(parent_height-480*Api.scale)/ 2.0)//上边线出界
                    offsetY = (float)((parent_height-480*Api.scale)/ 2.0 -dstRect.top)-1;
                else if(dstRect.bottom+offsetY<=(parent_height+480*Api.scale)/ 2.0)
                    offsetY = (float)((parent_height+480*Api.scale)/ 2.0 - dstRect.bottom);
                matrix.postTranslate(offsetX, offsetY);
                invalidate();
            }
            return true;
            }


        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
             return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (double_tap_count == 0) {
                matrix.postScale(2f, 2f, e.getX(), e.getY());
                matrix.mapRect(dstRect,srcRect);
                if(!dstRect.contains(circleRect)) {
                   //设为初始状态
                    matrix.reset();
                    matrix.setScale(initScaleX,initScaleY);
                    matrix.postTranslate(initTransX,initScaleY);
                }
                invalidate();
                double_tap_count = 1;
            } else {
                matrix.postScale(0.5f, 0.5f, e.getX(), e.getY());
                matrix.mapRect(dstRect, srcRect);
                if(!dstRect.contains(circleRect)) {
                    //设为初始状态
                    matrix.reset();
                    matrix.setScale(initScaleX,initScaleY);
                    matrix.postTranslate(initTransX,initTransY);
                }
                double_tap_count = 0;
            }
            invalidate();
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }
    }

    class MultiplyTouchesListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float max_scale = 4f;
            float min_scale = (480*Api.scale)/pic_width>(480*Api.scale)/pic_height?(480*Api.scale)/(float)pic_width:(480*Api.scale)/(float)pic_height;
            float scale_factor = detector.getScaleFactor();
            matrix.getValues(values);
            if(scale_factor*values[Matrix.MSCALE_X]>max_scale )
                scale_factor = max_scale/values[Matrix.MSCALE_X];
            if(scale_factor*values[Matrix.MSCALE_X]<min_scale)
                scale_factor = min_scale/values[Matrix.MSCALE_X];
            matrix.postScale(scale_factor, scale_factor, detector.getFocusX(), detector.getFocusY());
            matrix.mapRect(dstRect,srcRect);
            if(!dstRect.contains(circleRect))
               matrix.postScale(1/scale_factor, 1/scale_factor, detector.getFocusX(), detector.getFocusY());
            invalidate();
            return true;
        }
    }

}

