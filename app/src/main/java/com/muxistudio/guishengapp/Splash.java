package com.muxistudio.guishengapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;


public class Splash extends Activity implements View.OnTouchListener {
    private final long SPLASH_DELAY_LENGTH=3000;
    Intent mainIntent ;
    long endTime,beginTime;
    Handler handler = new Handler();
    int isDataObtainSuccess;
    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash_view);
        mainIntent = new Intent(Splash.this, MainActivity.class);
        if (NetDataObtain.isNetworkAvailable(this)) {
            beginTime = System.currentTimeMillis();
       //     isDataObtainSuccess = new NetDataObtain().DataRequireOver();
            endTime = System.currentTimeMillis();
            if (isDataObtainSuccess!=-1)
                mainIntent.putExtra("DATA_GET_STATE",true);
            else
                mainIntent.putExtra("DATA_GET_STATE", false);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Splash.this.startActivity(mainIntent);
                    Splash.this.finish();
                }
            }, SPLASH_DELAY_LENGTH-(endTime-beginTime));
        }
        else {
            mainIntent.putExtra("DATA_GET_STATE",false);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Splash.this.startActivity(mainIntent);
                    Splash.this.finish();
                }
            }, SPLASH_DELAY_LENGTH);
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }
}
