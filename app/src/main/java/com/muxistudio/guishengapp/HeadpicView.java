package com.muxistudio.guishengapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.net.Uri;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;
import java.io.InputStream;



public class HeadpicView extends ImageView{
    public Matrix matrix,current_matrix;
    ScaleGestureDetector multiplyTouchesListener;
    GestureDetector singleTouchListener;
    public int pic_width,pic_height;
    public int parent_width,parent_height;
    int count,status;
    float values[],points[];
    RectF srcRect,dstRect;
    public RectF circleRect;
    public static Bitmap bitmap;


    public HeadpicView(Context context){
        super(context);
    }

    public HeadpicView(Context context,Uri uri) {
        super(context);
        count = 0;
        status = 1;
        values = new float[9];
        points = new float[4];
        float scale=1;
        setLongClickable(true);
        multiplyTouchesListener = new ScaleGestureDetector(context, new MultiplyTouchesListener());
        singleTouchListener = new GestureDetector(context, new SingleTouchListener());
        try{
            InputStream is = context.getContentResolver().openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, options);
            is.close();
            pic_width = options.outWidth;
            pic_height = options.outHeight;
            float size = (float)pic_width/800;
            if(size<1f)
                size = 1;
            else
                size = 2;
            options.inSampleSize = (int)size;
            options.inJustDecodeBounds = false;
            options.inMutable = true;
            is = context.getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(is, null, options);
            pic_width = bitmap.getWidth();
            pic_height = bitmap.getHeight();
            is.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        matrix = new Matrix();
        current_matrix = null;
        srcRect = new RectF(0,0,bitmap.getWidth(),bitmap.getHeight());
        matrix.postTranslate((Api.screen_width-pic_width)/2,(Api.screen_height-pic_height)/2);
        dstRect = new RectF();
    }

    @Override
    public void onDraw(Canvas canvas) {
            canvas.save();
            canvas.drawBitmap(bitmap, matrix, null);
            canvas.restore();
            }


    @Override
    public boolean onTouchEvent(MotionEvent event){
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
                Log.i("what","上下"+parent_height+" "+Api.scale+(parent_height-480*Api.scale)/ 2.0+" "+dstRect.top+" "+offsetY+" "+dstRect.bottom);
                Log.i("what","左右"+(Api.screen_width-480*Api.scale)/ 2.0+" "+dstRect.left+" "+offsetX+" "+dstRect.right);
                if(dstRect.top+offsetY>=(parent_height-480*Api.scale)/ 2.0)//上边线出界
                    offsetY = (float)((parent_height-480*Api.scale)/ 2.0 -dstRect.top);
                else if(dstRect.bottom+offsetY<=(parent_height+480*Api.scale)/ 2.0)
                    offsetY = (float)((parent_height+480*Api.scale)/ 2.0 - dstRect.bottom);
                matrix.set(current_matrix);
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
                double_tap_count = 1;
            } else {
                matrix.postScale(0.5f, 0.5f, e.getX(), e.getY());
                current_matrix = matrix;
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
            current_matrix = matrix;
            return true;
        }
    }

}

