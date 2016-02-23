package com.muxistudio.guishengapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;
import java.io.InputStream;
import java.net.URL;


public class MImageGetter implements Html.ImageGetter {
    private Context context;
    private TextView textView;
    private int width;
    public MImageGetter(Context context,TextView textView,int width){
        this.context = context;
        this.textView = textView;
        this.width = width;
    }

    @Override
    public URLDrawable getDrawable(String source) {
        URLDrawable mDrawable = new URLDrawable(context.getResources().getDrawable(R.drawable.unlog),width);
        new ImageAsyncTask(mDrawable).execute(source);
        return mDrawable;
    }

    class ImageAsyncTask extends AsyncTask<String,Integer, android.graphics.drawable.Drawable>{
        private URLDrawable drawable = null;
        public ImageAsyncTask(URLDrawable drawable){
            this.drawable = drawable;
        }
        @Override
        protected Drawable doInBackground(String... params) {
            Drawable load_drawable = null;
//          String url =Api.image_api+params[0];
            String url = params[0];
            InputStream is;
            try{
                is = new URL(url).openStream();
                load_drawable = Drawable.createFromStream(is, null);
                is.close();
                }catch (Exception e){
                e.printStackTrace();
            }
            return load_drawable;
        }

        @Override
        protected void onPostExecute(Drawable result) {
            super.onPostExecute(result);
            if(result!=null){
                drawable.setDrawable(result);
                textView.invalidate();
            }
        }
    }
}
