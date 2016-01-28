package com.muxistudio.guishengapp;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener{
    TextView comment_area,cancel,send;
    String result;
    int tab;
    int id;

    @Override
    public void onCreate(Bundle savedInstanceState){
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_comment_layout);
        Intent intent = getIntent();
        tab = intent.getIntExtra("tab",-1);
        id = intent.getIntExtra("id",-1);

        comment_area = (TextView)findViewById(R.id.comment_area);
        cancel = (TextView)findViewById(R.id.cancel);
        send = (TextView)findViewById(R.id.send_area);
        send.setOnClickListener(this);
        cancel.setOnClickListener(this);
        if(!comment_area.isFocused())
            comment_area.requestFocus();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.cancel:
                CommentActivity.this.finish();
                break;
            case R.id.send_area:
                Log.i("what","sending~");
                String comment = comment_area.getText().toString();
                if(!comment.equals("")) {
                    new AsyncTask<String, Integer, Void>() {
                        AlertDialog dialog;
                        @Override
                        protected void onProgressUpdate(Integer... values) {
                            super.onProgressUpdate(values);
                                AlertDialog.Builder builder = new AlertDialog.Builder(CommentActivity.this);
                                builder.setView(LayoutInflater.from(CommentActivity.this).inflate(R.layout.sending_dialog_interface, null));
                                dialog = builder.create();
                                dialog.show();
                                Window dialogWindow = dialog.getWindow();
                                dialogWindow.setGravity(Gravity.CENTER);
                                TextView hint = (TextView) dialog.findViewById(R.id.dialog_title);
                            if(values[0]==-1) {
                                hint.setText("发送中...");
                            }else if (values[0]==1) {
                                hint.setText(CommentActivity.this.getResources().getText(R.string.send_ok));
                                ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.progress_bar);
                                progressBar.setVisibility(View.GONE);
                            }else {
                                hint.setText(CommentActivity.this.getResources().getText(R.string.send_fail));
                                ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.progress_bar);
                                progressBar.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public Void doInBackground(String... params) {
                            result = params[0];
                            int isSuccess = -1;//还没开始传输数据
                            publishProgress(isSuccess);
                            try {
                                String tabString;
                                 switch (tab) {
                                    default:
                                        tabString = Api.news;
                                        break;
                                    case 1:
                                        tabString = Api.origins;
                                        break;
                                    case 2:
                                        tabString = Api.inter;
                                        break;
                                }
                                if (Api.log_status == 1) {
                                    JSONObject object = new JSONObject();
                                    object.put("body", result);
                                    URL url = new URL(Api.api + tabString + "/" + id + "/" + Api.comments);
                                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                    conn.setDoOutput(true);
                                    conn.setUseCaches(false);
                                    conn.setRequestMethod("POST");
                                    conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
                                    conn.setRequestProperty("Authorization", "Basic " + Base64.encodeToString(Api.user_token.getBytes(), Base64.DEFAULT));
                                    conn.connect();
                                    OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                                    osw.write(object.toString());
                                    osw.flush();
                                    osw.close();
                                    conn.disconnect();
                                    isSuccess = 1;
                                }
                            } catch (Exception e) {
                                isSuccess = 0;
                                e.printStackTrace();
                            }finally {
                                publishProgress(isSuccess);
                            }
                            return null;
                        }

                        @Override
                        public void onPostExecute(Void result) {
                            dialog.dismiss();
                            CommentActivity.this.finish();
                        }
                    }.execute(comment, null, null);
                    break;
                }else{
                    Toast.makeText(CommentActivity.this,"发送内容不能为空~",Toast.LENGTH_SHORT).show();
                }
            case R.id.comment_area:
                comment_area.requestFocus();
                break;
        }
    }
}
