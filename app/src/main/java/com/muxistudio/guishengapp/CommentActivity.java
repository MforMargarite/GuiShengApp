package com.muxistudio.guishengapp;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class CommentActivity extends Activity implements View.OnClickListener{
    TextView comment_area,cancel,send;
    String result;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_comment_layout);
        comment_area = (TextView)findViewById(R.id.comment_area);
        comment_area.requestFocus();
        cancel = (TextView)findViewById(R.id.cancel);
        send = (TextView)findViewById(R.id.send_area);
        send.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.cancel:
                CommentActivity.this.finish();
                break;
            case R.id.send_area:
                String comment = comment_area.getText().toString();
                new AsyncTask<String,Void,Void>(){
                    @Override
                    public Void doInBackground(String...params){
                        result = params[0];
                        return null;
                    }
                    @Override
                    public void onPostExecute(Void result){

                    }
                }.execute(comment,null,null);
                break;
        }
    }
}
