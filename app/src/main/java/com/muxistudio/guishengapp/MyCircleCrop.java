package com.muxistudio.guishengapp;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.app.Activity;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import java.io.ByteArrayOutputStream;


public class MyCircleCrop extends Activity implements View.OnClickListener{
    HeadpicView headpicView;
    ShadeView shadeView;
    FrameLayout frameLayout;
    Button headpic_negative_btn;
    Button headpic_positive_btn;
    LinearLayout option;
    Uri uri;
    int parent_width,parent_height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.circle_crop_layout);
        frameLayout = (FrameLayout)findViewById(R.id.frame_layout);
        option = (LinearLayout)findViewById(R.id.options);
        headpic_positive_btn = (Button) findViewById(R.id.positive_headpic);
        headpic_negative_btn = (Button) findViewById(R.id.negative_headpic);
        headpic_positive_btn.setOnClickListener(this);
        headpic_negative_btn.setOnClickListener(this);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        uri = (Uri) bundle.get("data");
        headpicView = new HeadpicView(this,uri);
        shadeView = new ShadeView(this);
        frameLayout.addView(headpicView);
        frameLayout.addView(shadeView);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MyCircleCrop.this, MainActivity.class);
        switch (v.getId()) {
            case R.id.positive_headpic:
                HeadpicView.bitmap.recycle();
                headpicView.setDrawingCacheEnabled(true);
                Bitmap bitmap = headpicView.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 80, baos);
                byte[] pic_byte = baos.toByteArray();
                intent.putExtra("crop_pic", pic_byte);
                HeadpicImageView.parent_height = parent_height;
                HeadpicImageView.parent_width = parent_width;
                headpicView.setDrawingCacheEnabled(false);
                setResult(3, intent);
                bitmap.recycle();
                finish();
                break;
            case R.id.negative_headpic:
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        parent_width = frameLayout.getWidth();
        headpicView.parent_width = parent_width;
        parent_height = frameLayout.getHeight();
        headpicView.parent_height = parent_height;
        if(event.getY()<frameLayout.getHeight())
           return headpicView.onTouchEvent(event);
        else {
            return this.onTouchEvent(event);
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
        int bound = (option.getWidth()-2*headpic_positive_btn.getWidth())+headpic_positive_btn.getWidth();
        if(event.getAction()==MotionEvent.ACTION_DOWN)
            return true;
        if(event.getAction()!=MotionEvent.ACTION_UP) {
            if(event.getX()<=headpic_negative_btn.getWidth())
                headpic_negative_btn.setPressed(true);
            else if(event.getX()>=bound)
                headpic_positive_btn.setPressed(true);
        }else {
            if(event.getX()<=headpic_negative_btn.getWidth()){
                headpic_negative_btn.setPressed(false);
               headpic_negative_btn.performClick();
            }
            else if(event.getX()>=bound) {
                headpic_positive_btn.setPressed(false);
                headpic_positive_btn.performClick();
            }
        }
        return true;
    }
}