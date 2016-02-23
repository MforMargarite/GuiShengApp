package com.muxistudio.guishengapp;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener{
    TextView comment_area,cancel,send;
    String result;
    MyDialog dialog;
    GuiShengDao guiShengDao;
    int tab;
    int id;
    public static MainActivity mainActivity;

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
                String comment = comment_area.getText().toString();
                if (Api.log_status != 1) {
                   logDialog();
                }else {
                    if (!comment.equals("")) {
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
                                if (values[0] == -1) {
                                    hint.setText("发送中...");
                                } else if (values[0] == 1) {
                                    hint.setText(CommentActivity.this.getResources().getText(R.string.send_ok));
                                    ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.progress_bar);
                                    progressBar.setVisibility(View.GONE);
                                } else {
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
                                    JSONObject object = new JSONObject();
                                    object.put("body", result);
                                    object.put("author", Api.logged_username);
                                    URL url = new URL(Api.api + tabString + "/" + id + "/" + Api.comments);
                                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                    conn.setDoOutput(true);
                                    conn.setUseCaches(false);
                                    conn.setRequestProperty("Authorization", "Basic " + Base64.encodeToString(Api.user_token.getBytes(), Base64.NO_WRAP));
                                    conn.setRequestMethod("POST");
                                    conn.connect();
                                    DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                                    dos.writeBytes(object.toString());
                                    dos.flush();
                                    dos.close();
                                    conn.disconnect();
                                    isSuccess = 1;
                                } catch (Exception e) {
                                    isSuccess = 0;
                                    e.printStackTrace();
                                } finally {
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
                    } else {
                        Toast.makeText(CommentActivity.this, "发送内容不能为空~", Toast.LENGTH_SHORT).show();
                    }
                }
            case R.id.comment_area:
                comment_area.requestFocus();
                break;
        }
    }

    private void logDialog() {
        dialog = new MyDialog(CommentActivity.this, R.layout.log_dialog_interface, new MyDialog.MyDialogListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.dialog_positive_button:
                        EditText dialog_email_edit = (EditText) dialog.findViewById(R.id.dialog_name_edit);
                        dialog_email_edit.setHorizontallyScrolling(false);
                        final String email = dialog_email_edit.getText().toString();
                        EditText dialog_password_edit = (EditText) dialog.findViewById(R.id.dialog_password_edit);
                        final String password = dialog_password_edit.getText().toString();
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String name_password= email+":"+password;
                                    URL url = new URL(Api.api+Api.token);
                                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                    conn.setRequestMethod("GET");
                                    conn.setDoInput(true);
                                    conn.setUseCaches(false);
                                    conn.setRequestProperty("Authorization", "Basic " + Base64.encodeToString(name_password.getBytes(), Base64.DEFAULT));
                                    conn.connect();
                                    InputStream is = conn.getInputStream();
                                    String whole_data = HttpUtils.readInputStream(is);
                                    JSONObject object = new JSONObject(whole_data);
                                    //登陆成功后 取用户名和用户id信息 存在数据库里头
                                    Api.user_token = object.get("token").toString();
                                    Api.user_id = object.get("id").toString();
                                    is.close();
                                    conn.disconnect();
                                    if(Api.user_id!=null ) {
                                        String user_info = Api.api + Api.users + Integer.parseInt(Api.user_id);
                                        url = new URL(user_info);
                                        conn = (HttpURLConnection) url.openConnection();
                                        conn.setRequestMethod("GET");
                                        conn.setDoInput(true);
                                        conn.setUseCaches(false);
                                        conn.connect();
                                        is = conn.getInputStream();
                                        whole_data = HttpUtils.readInputStream(is);
                                        JSONObject jsonObject = new JSONObject(whole_data);
                                        guiShengDao.insertUserInfo(email, Api.user_id, Api.user_token, jsonObject.getString("username"), "");
                                        is.close();
                                        conn.disconnect();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        Thread thread = new Thread(runnable);
                        thread.start();
                        while (true) {
                            if (!thread.isAlive())
                                break;
                        }
                        if(Api.user_id!=null) {//&& guiShengDao.getUserToken(email)!=null
                            mainActivity.changeToLogged(email);
                            dialog.dismiss();
                        }else{
                            Toast.makeText(CommentActivity.this,"用户名或密码错误!",Toast.LENGTH_SHORT).show();
                            dialog_password_edit.setText("");
                        }
                        break;
                    case R.id.dialog_negative_button:
                        dialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });
        DialogStandardSetting();
    }

    private void DialogStandardSetting() {
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        lp.y = 144;
    }

}
