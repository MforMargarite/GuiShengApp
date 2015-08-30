package com.muxistudio.guishengapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
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
    float _width,_height;
    float values[];
    public static Bitmap bitmap;

    public HeadpicView(Context context){
        super(context);
    }

    public HeadpicView(Context context,Uri uri) {
        super(context);
        count = 0;
        status = 1;
        values = new float[9];
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
            parent_width = context.getResources().getDisplayMetrics().widthPixels;
            parent_height = 10*context.getResources().getDisplayMetrics().heightPixels/11;
            float scale = (float)pic_width/800;
            scale = (float)pic_height/800>scale?(float)pic_height/parent_height:scale;
            if(scale<1f)
                scale = 1f;
            else if(scale<3f)
                scale = 2f;
            else
                scale = 4f;
            options.inSampleSize = (int)scale;
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
        matrix.postScale((float) parent_width / pic_width, (float) parent_width / pic_width);
        if(parent_height>pic_height)
           matrix.postTranslate(0, 0.5f * (parent_height - pic_height*(float)parent_width / pic_width));
        current_matrix = null;
    }

    @Override
    public void onDraw(Canvas canvas) {
            canvas.save();
            canvas.drawBitmap(bitmap, matrix, null);
            canvas.restore();
    }

    private void initWidthAndHeight() {
            _width = parent_width;
            _height = (float) parent_width * pic_height / pic_width + 0.5f * (parent_height - (float)pic_height*parent_width / pic_width);
            if( _height < parent_height)
                _height = parent_height;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(count == 0) {
            initWidthAndHeight();
            count++;
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
            if(values[Matrix.MSCALE_Y]>=(float)parent_height/pic_height){
                if (offsetY + values[Matrix.MTRANS_Y] > 0)
                    offsetY = -values[Matrix.MTRANS_Y];
                else if (offsetY + values[Matrix.MTRANS_Y] < -(pic_height * values[Matrix.MSCALE_Y] - _height))
                    offsetY = -(pic_height * values[Matrix.MSCALE_Y] - _height) - values[Matrix.MTRANS_Y];
            if(values[Matrix.MSCALE_X]>=(float)parent_width/pic_width) {
                if (offsetX + values[Matrix.MTRANS_X] > 0)
                    offsetX = -values[Matrix.MTRANS_X];
                else if (offsetX + values[Matrix.MTRANS_X] < -(pic_width * values[Matrix.MSCALE_X] - _width))
                    offsetX = -(pic_width * values[Matrix.MSCALE_X] - _width)  - values[Matrix.MTRANS_X];
                }else{
                if (offsetX + values[Matrix.MTRANS_X] < 0)
                    offsetX = -values[Matrix.MTRANS_X];
                else if (offsetX + values[Matrix.MTRANS_X] > ( _width - pic_width * values[Matrix.MSCALE_X]))
                    offsetX = ( _width - pic_width * values[Matrix.MSCALE_X]) - values[Matrix.MTRANS_X];
            }
            }else{
                if (offsetY + values[Matrix.MTRANS_Y] < 0)
                    offsetY = -values[Matrix.MTRANS_Y];
                else if (offsetY + values[Matrix.MTRANS_Y] > ( _height - pic_height * values[Matrix.MSCALE_Y]))
                    offsetY = (_height - pic_height * values[Matrix.MSCALE_Y] ) - values[Matrix.MTRANS_Y];
                if(values[Matrix.MSCALE_X]>=(float)parent_width/pic_width) {
                    if (offsetX + values[Matrix.MTRANS_X] > 0)
                        offsetX = -values[Matrix.MTRANS_X];
                    else if (offsetX + values[Matrix.MTRANS_X] < -(pic_width * values[Matrix.MSCALE_X] - _width))
                        offsetX = -(pic_width * values[Matrix.MSCALE_X] - _width) - values[Matrix.MTRANS_X];
                }else{
                    if (offsetX + values[Matrix.MTRANS_X] < 0)
                        offsetX = -values[Matrix.MTRANS_X];
                    else if (offsetX + values[Matrix.MTRANS_X] > ( _width - pic_width * values[Matrix.MSCALE_X]))
                        offsetX = ( _width - pic_width * values[Matrix.MSCALE_X]) - values[Matrix.MTRANS_X];
                }
            }
            matrix.set(current_matrix);
            matrix.postTranslate(offsetX, offsetY);
            invalidate();
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
            float min_scale = 2*parent_width/(pic_width+480);
            float scale_factor = detector.getScaleFactor();
            matrix.getValues(values);
            if(scale_factor*values[Matrix.MSCALE_X]>max_scale )
                scale_factor = max_scale/values[Matrix.MSCALE_X];
            if(scale_factor*values[Matrix.MSCALE_X]<min_scale)
                scale_factor = min_scale/values[Matrix.MSCALE_X];
            matrix.postScale(scale_factor, scale_factor, detector.getFocusX(), detector.getFocusY());
            current_matrix = matrix;
            invalidate();
            return true;
        }
    }

}

