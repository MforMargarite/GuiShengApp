package com.muxistudio.guishengapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.InputStream;

public class Headpic_Viewer extends Activity {
    Bitmap bitmap ;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_headpic_view);
        RelativeLayout headpic_layout = (RelativeLayout)findViewById(R.id.headpic_viewer_layout);
        headpic_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Headpic_Viewer.this.finish();
            }
        });
        ImageView headpic_viewer = (ImageView) findViewById(R.id.headpic_viewer);
        Intent intent = getIntent();
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.unlog);
        try {
            byte[] bytes = intent.getByteArrayExtra("headpic");
            bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        }catch(Exception e){
            e.printStackTrace();
        }
        finally{
            headpic_viewer.setScaleType(ImageView.ScaleType.FIT_CENTER);
            headpic_viewer.setImageBitmap(bitmap);
        }
    }

}
