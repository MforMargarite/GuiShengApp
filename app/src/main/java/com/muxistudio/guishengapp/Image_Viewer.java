package com.muxistudio.guishengapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.InputStream;

public class Image_Viewer extends Activity {
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
                Image_Viewer.this.finish();
            }
        });
        ImageView headpic_viewer = (ImageView) findViewById(R.id.headpic_viewer);
        Intent intent = getIntent();
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.unlog);
        try {
            Bundle bundle = intent.getExtras();
            Uri imgUri = (Uri) bundle.get("headpic");
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imgUri);
        }catch(Exception e){
            e.printStackTrace();
        }
        finally{
            headpic_viewer.setScaleType(ImageView.ScaleType.FIT_CENTER);
            headpic_viewer.setImageBitmap(bitmap);
        }
    }

}
