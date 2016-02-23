package com.muxistudio.guishengapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;



public class ImageLoad {
    private ImageView imageView;
    private String url;
    private LruCache<String,Bitmap> cache;
    final Handler handler = new Handler(){
        public void handleMessage(Message msg){
            Bitmap bitmap;
            Matrix matrix;
            switch (msg.what) {
                case 0:
                    bitmap = (Bitmap) msg.obj;
                    imageView.setImageBitmap((Bitmap) msg.obj);
                    imageView.setMinimumWidth((int)(0.29*(float)Api.screen_width/bitmap.getWidth()*bitmap.getHeight()));
                    matrix = new Matrix();
                    matrix.setScale((float) 0.29*Api.screen_width / bitmap.getWidth(), (float) 0.29*Api.screen_width / bitmap.getWidth());
                    imageView.setScaleType(ImageView.ScaleType.MATRIX);
                    imageView.setImageMatrix(matrix);
                    break;
                default:
                    bitmap = (Bitmap) msg.obj;
                    matrix = new Matrix();
                    matrix.setScale((float) Api.screen_width / bitmap.getWidth(),(float) Api.screen_width / bitmap.getWidth() );
                    bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
                    imageView.setImageBitmap(bitmap);
                    break;
            }
        }
    };
    Message message = new Message();

    public ImageLoad(){
        int maxMemory = (int)Runtime.getRuntime().maxMemory();
        int Memorysize = maxMemory/4;
        cache = new LruCache<String,Bitmap>(Memorysize){
            @Override
            protected int sizeOf(String key,Bitmap value){
                return value.getByteCount();
            }
        };
    }

    public void addBitmapToCache(String url,Bitmap bitmap){
        if(getBitmapFromCache(url)==null)
            cache.put(url,bitmap);
    }

    public Bitmap getBitmapFromCache(String url){
        Bitmap bmp ;
        bmp = cache.get(url);
        return bmp;
    }

    public Bitmap getBitmap(String url){
        Bitmap bitmap = null;
        InputStream is;
        if(url!=null) {
            try {
                URL myurl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.connect();
                is = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(is);
                is.close();
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public void showImageByThread(ImageView imageView,final String url){
        this.imageView = imageView;
        this.url = url;
        new Thread(){
            @Override
            public void run(){
                super.run();
                Bitmap bmp ;
                bmp = getBitmapFromCache(url);
                if(bmp==null) {
                    bmp = getBitmap(url);
                    addBitmapToCache(url,bmp);
                }
                Message msg = new Message();
                msg.obj = bmp;
                msg.what = 0;
                handler.sendMessage(msg);
            }
        }.start();
    }

    public void showHeaderImageByThread(ImageView imageView,final String url){
        this.imageView = imageView;
        this.url = url;
        new Thread(){
            @Override
            public void run(){
                super.run();
                Bitmap bmp ;
                bmp = getBitmapFromCache(url);
                if(bmp==null) {
                    bmp = getBitmap(url);
                    addBitmapToCache(url,bmp);
                }
                message.obj = bmp;
                message.what = 1;
                handler.sendMessage(message);

            }
        }.start();
    }

}
