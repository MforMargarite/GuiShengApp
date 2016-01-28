package com.muxistudio.guishengapp;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;



public class MyDialog extends Dialog implements View.OnClickListener{
    private Button positive_button,negative_button;
    private MyDialogListener listener;
    @Override
    public void onClick(View v) {
       listener.onClick(v);
    }

    public interface MyDialogListener {
         void onClick(View v);
    }

    MyDialog(Context context, int n, MyDialogListener listener)
    {
        super(context);
        this.listener=listener;
        setCancelable(false);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(n);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        init();
    }
    private void init(){
        positive_button=(Button)findViewById(R.id.dialog_positive_button);
        negative_button=(Button)findViewById(R.id.dialog_negative_button);
        positive_button.setOnClickListener(this);
        negative_button.setOnClickListener(this);
    }

}
