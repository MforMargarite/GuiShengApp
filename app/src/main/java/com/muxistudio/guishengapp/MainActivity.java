package com.muxistudio.guishengapp;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener{
    final int PHOTO_REQUEST_SHOOT = 1;
    final int PHOTO_REQUEST_PHOTOGRAPH = 2;
    final int PHOTO_REQUEST_CUT = 3;
    boolean DATA_GET_STATE;
    File tempFile = null;
    File GuiShengApp;
    File GuiShengApp_headpic;
    int isTempFileEmpty = 1;
    GuiShengDao guiShengDao;
    Uri imgUrl = null ;
    Toolbar toolbar;
    SlidingTabLayout mTabLayout;
    ViewPager viewpager;
    LinearLayout unlog_interface;
    FrameLayout toggle_wrapper;
    LinearLayout main_wrapper;
    HeadpicImageView user_headpic;
    Intent show_headpic;
    MyDialog dialog;
    TextView username;
    EditText et_username;
    Message message = new Message();
    AppCompatButton log_now,register;
    DrawerLayout drawerLayout;
    ToggleListView lvMenu;
    boolean okToRegister = true;
    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message){
            switch(message.what){
                case 0://new NetDataObtain().DataRequireOver();
                   DATA_GET_STATE = true;
                   break;
                case 1:Toast.makeText(MainActivity.this,getResources().getText(R.string.err),Toast.LENGTH_SHORT).show();
                    break;
                case 2:
//                    progressDialog.dismiss();
                    username.setText((String) message.obj);
                    Toast.makeText(MainActivity.this, "修改成功!", Toast.LENGTH_SHORT).show();
                    guiShengDao.updateUserName(Api.logged_username, (String) message.obj);
                    break;
                case 3:
  //                  progressDialog.dismiss();
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this,"注册成功!",Toast.LENGTH_SHORT).show();
                    drawerLayout.closeDrawers();
                    break;
                case 4:
                    TextView email_param = (TextView) dialog.findViewById(R.id.email_param);
                    if(!(boolean)message.obj){
                        Toast.makeText(MainActivity.this, "该邮箱已被注册!", Toast.LENGTH_SHORT).show();
                        email_param.setTextColor(getResources().getColor(R.color.guisheng_red));
                        okToRegister = false;
                    }else{
                        email_param.setTextColor(getResources().getColor(R.color.dark_grey));
                        okToRegister = true;
                    }
                break;
                case 5:
                    //progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "修改失败...当前网络较差", Toast.LENGTH_SHORT).show();
                break;
                case 6:
                    user_headpic.setImageBitmap((Bitmap)message.obj);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initStatusBar();
        Intent from_splash = this.getIntent();
        Bundle bundle = from_splash.getExtras();
        DATA_GET_STATE = bundle.getBoolean("DATA_GET_STATE");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectionReceiver, intentFilter);
    }


    private void init() {
        et_username = (EditText) findViewById(R.id.dialog_edit);
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        viewpager.setAdapter(new MyFragmentStatePagerAdapter(this, getSupportFragmentManager()));
        guiShengDao = new GuiShengDao(MainActivity.this);
        mTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tab_layout);
        mTabLayout.setDividerColors(getResources().getColor(R.color.tab_background));
        mTabLayout.setCustomTabView(R.layout.slidingtab_layout, R.id.tab_text);
        mTabLayout.setBackgroundColor(getResources().getColor(R.color.tab_background));
        mTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.guisheng_red));
        mTabLayout.setViewPager(viewpager);

        show_headpic = new Intent(MainActivity.this, Image_Viewer.class);
        GuiShengApp = new File(File.separator + Environment.getExternalStorageDirectory() + File.separator + "GuiShengApp" + File.separator);
        GuiShengApp_headpic = new File(File.separator + Environment.getExternalStorageDirectory() + File.separator + "GuiShengApp" + File.separator + "head_pic" + File.separator);
        CreateFile(GuiShengApp);
        CreateFile(GuiShengApp_headpic);
    }


    private void initStatusBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.guisheng_red);
        }
        if (toolbar != null) {
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
            ImageView side_menu_btn = (ImageView)toolbar.findViewById(R.id.side_menu_btn);
            side_menu_btn.setOnClickListener(this);
            drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
            lvMenu = new ToggleListView(this,Api.screen_width,Api.screen_height);
            unlog_interface =(LinearLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.unlog_interface, null);
            lvMenu.setOnItemClickListener(this);
            toggle_wrapper = (FrameLayout)findViewById(R.id.toggle_wrapper);
            if(Api.log_status == 1) {
                toggle_wrapper.addView(lvMenu);
                user_headpic = (HeadpicImageView) lvMenu.headpicview_wrapper.findViewById(R.id.user_head_pic);
            }else{
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((int)(0.72*Api.screen_width), ViewGroup.LayoutParams.MATCH_PARENT);
                unlog_interface.setLayoutParams(lp);
                toggle_wrapper.addView(unlog_interface);
                log_now = (AppCompatButton)unlog_interface.findViewById(R.id.log_now);
                register = (AppCompatButton)unlog_interface.findViewById(R.id.register);
                log_now.setOnClickListener(this);
                register.setOnClickListener(this);
                user_headpic = (HeadpicImageView) unlog_interface.findViewById(R.id.user_head_pic);
                user_headpic.setOnClickListener(this);
            }
        }
    }
        BroadcastReceiver connectionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!DATA_GET_STATE && NetDataObtain.isNetworkAvailable(context))
                    message.what = 0;
                else if (!NetDataObtain.isNetworkAvailable(context))
                    message.what = 1;
                //handler.sendMessage(message);
            }
        };

        @Override
        protected void onActivityResult ( int requestCode, int resultCode, Intent data){
            switch (requestCode) {
                case PHOTO_REQUEST_SHOOT:
                    if (resultCode == RESULT_OK) {
                        startZoom(Uri.fromFile(tempFile));
                        imgUrl = Uri.fromFile(tempFile);
                        break;
                    }
                    else
                        return;
                case PHOTO_REQUEST_PHOTOGRAPH:
                    if (data != null) {
                        startZoom(data.getData());
                        imgUrl = data.getData();
                        break;
                    }
                    break;
                case PHOTO_REQUEST_CUT:
                    if (data != null) {
                        try {
                            byte[] headpic_byte = data.getByteArrayExtra("crop_pic");
                            show_headpic.putExtra("headpic", imgUrl);
                            final Bitmap bitmap = BitmapFactory.decodeByteArray(headpic_byte, 0, headpic_byte.length);
                            UploadClient uploadClient = new UploadClient();
                            final String avatar_url = uploadClient.uploadImg(headpic_byte,Integer.parseInt(Api.user_id));
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try{
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("avatar_url", avatar_url);
                                        jsonObject.put("username", Api.logged_username);
                                        jsonObject.put("email", Api.logged_user_email);
                                        jsonObject.put("id", Integer.parseInt(Api.user_id));
                                        URL url = new URL(Api.api + Api.users + Api.user_id);
                                        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                                        conn.setRequestMethod("PUT");
                                        conn.setRequestProperty("Content-type", "application/json");
                                        conn.setRequestProperty("Authorization", "Basic " + Base64.encodeToString(Api.user_token.getBytes(), Base64.NO_WRAP));
                                        conn.setDoOutput(true);
                                        conn.setUseCaches(false);
                                        conn.connect();
                                        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                                        dos.writeBytes(jsonObject.toString());
                                        dos.flush();
                                        dos.close();
                                        Log.i("here",conn.getResponseCode()+"");
                                        conn.disconnect();
                                        Message message = new Message();
                                        message.what = 6;
                                        message.obj = bitmap;
                                        handler.sendMessage(message);
                                    }catch(Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
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

    private void uploadUserpicDialog() {
        dialog = new MyDialog(MainActivity.this, R.layout.upload_headpic_dialog_interface, new MyDialog.MyDialogListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.dialog_negative_button:
                        Intent intent_for_shoot = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        tempFile = new File(File.separator + Environment.getExternalStorageDirectory() + File.separator + "GuiShengApp" + File.separator + "head_pic" + File.separator, getPhotoFileName());
                        isTempFileEmpty = 0;
                        intent_for_shoot.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                        dialog.dismiss();
                        startActivityForResult(intent_for_shoot, PHOTO_REQUEST_SHOOT);
                        break;
                    case R.id.dialog_positive_button:
                        Intent intent_for_photograph = new Intent(Intent.ACTION_GET_CONTENT);
                        intent_for_photograph.setType("image/*");
                        dialog.dismiss();
                        startActivityForResult(intent_for_photograph, PHOTO_REQUEST_PHOTOGRAPH);
                        break;
                    default:
                        break;
                }
            }
        });
        DialogStandardSetting();
    }

    private void changeNameDialog() {
        dialog = new MyDialog(MainActivity.this, R.layout.change_name_dialog_interface, new MyDialog.MyDialogListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.dialog_positive_button:
                        et_username = (EditText) dialog.findViewById(R.id.dialog_edit);
                        final String new_name = et_username.getText().toString();
                        username = (TextView) findViewById(R.id.username);
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                Looper.prepare();
                                //progressDialog = new ProgressDialog(MainActivity.this);
                               // progressDialog.show();
                                try{
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("id",Integer.parseInt(Api.user_id));
                                    jsonObject.put("email",Api.logged_user_email);
                                    jsonObject.put("username", new_name);
                                    jsonObject.put("avatar","null");
                                    String update_name = Api.api + Api.users + Integer.parseInt(Api.user_id);
                                    URL url = new URL(update_name);
                                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                    conn.setRequestMethod("PUT");
                                    conn.setDoOutput(true);
                                    conn.setDoInput(false);
                                    conn.setUseCaches(false);
                                    conn.setRequestProperty("Content-type", "application/json");
                                    conn.setRequestProperty("Authorization", "Basic " + Base64.encodeToString(Api.user_token.getBytes(), Base64.NO_WRAP));
                                    conn.connect();
                                    DataOutputStream dos = new DataOutputStream( conn.getOutputStream());
                                    dos.writeBytes(jsonObject.toString());
                                    dos.flush();
                                    dos.close();
                                    Log.i("what", jsonObject.toString()+" ");
                                    try{
                                        Message message = new Message();
                                        message.what = 2;
                                        message.obj = new_name;
                                        handler.sendMessage(message);
                                    }catch (Exception e)
                                    {
                                        Message message = new Message();
                                        message.what = 5;
                                        handler.sendMessage(message);
                                    }
                                    conn.disconnect();
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                            }
                        };
                        Thread thread = new Thread(runnable);
                        thread.start();
                        while(true)
                            if(!thread.isAlive())
                                break;
                        et_username.setText("");
                        dialog.dismiss();
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


    private void startZoom(Uri uri) {
        Intent intent = new Intent(MainActivity.this, MyCircleCrop.class);
        intent.putExtra("data", uri);
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }


    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".PNG";
    }

    private void CreateFile(File filename) {
        try {
            if (!filename.exists())
                filename.mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void chooseUnlogDialog() {
        dialog = new MyDialog(MainActivity.this, R.layout.choose_unlog_dialog_interface, new MyDialog.MyDialogListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.dialog_positive_button:
                        Api.log_status = 0;
                        drawerLayout.closeDrawers();
                        toggle_wrapper.removeAllViews();
                        toggle_wrapper.addView(unlog_interface);
                        dialog.dismiss();
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


    private void logDialog() {
        dialog = new MyDialog(MainActivity.this, R.layout.log_dialog_interface, new MyDialog.MyDialogListener() {
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
                                String token;
                                String userid;
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
                                    token = object.get("token").toString();
                                    userid = object.get("id").toString();
                                    Api.user_token = token;
                                    Api.user_id = userid;
                                    is.close();
                                    conn.disconnect();
                                    if(userid!=null && !userid.equals(guiShengDao.getUserID(email))) {
                                        String user_info = Api.api + Api.users + Integer.parseInt(userid);
                                        url = new URL(user_info);
                                        conn = (HttpURLConnection) url.openConnection();
                                        conn.setRequestMethod("GET");
                                        conn.setDoInput(true);
                                        conn.setUseCaches(false);
                                        conn.connect();
                                        is = conn.getInputStream();
                                        whole_data = HttpUtils.readInputStream(is);
                                        JSONObject jsonObject = new JSONObject(whole_data);
                                        guiShengDao.insertUserInfo(email, userid, token, jsonObject.getString("username"), "");
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
                        if(guiShengDao.getUserToken(email)!=null && Api.user_id!=null) {
                            drawerLayout.closeDrawers();
                            toggle_wrapper.removeAllViews();
                            toggle_wrapper.addView(lvMenu);
                            Api.log_status = 1;
                            user_headpic = (HeadpicImageView)lvMenu.findViewById(R.id.user_head_pic);
                            TextView username = (TextView)lvMenu.findViewById(R.id.username);
                            Api.logged_username = guiShengDao.getUserName(email);
                            Api.logged_user_email = email;
                            username.setText(Api.logged_username);
                            dialog.dismiss();
                        }else{
                            Toast.makeText(MainActivity.this,"用户名或密码错误!",Toast.LENGTH_SHORT).show();
                            dialog_password_edit.setText("");
                        }
                        break;
                    case R.id.dialog_negative_button:
                        drawerLayout.closeDrawers();
                        dialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });
        DialogStandardSetting();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(connectionReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.log_now:
                logDialog();
                break;
            case R.id.register:
                registerUser();
                break;
            case R.id.user_head_pic:
                startActivity(show_headpic);
                break;
            case R.id.side_menu_btn:
                if(!drawerLayout.isDrawerOpen(GravityCompat.START))
                    drawerLayout.openDrawer(GravityCompat.START);
                else
                    drawerLayout.closeDrawers();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch(position){
            case 0:startActivity(show_headpic);
                break;
            case 1:uploadUserpicDialog();
                break;
            case 2:changeNameDialog();
                break;
            case 3:chooseUnlogDialog();
                break;
            default:Intent intent = new Intent(MainActivity.this,About.class);
                startActivity(intent);
        }
    }

    private void registerUser(){
        dialog = new MyDialog(MainActivity.this, R.layout.register_dialog_layout, new MyDialog.MyDialogListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.dialog_positive_button:
                        final EditText input_email = (EditText) dialog.findViewById(R.id.input_email);
                        final EditText input_username = (EditText) dialog.findViewById(R.id.input_username);
                        final EditText input_password = (EditText) dialog.findViewById(R.id.input_password);
                        if (input_email.getText().toString().equals("") || input_username.getText().toString().equals("") || input_password.getText().toString().equals(""))
                            okToRegister = false;
                        else
                            okToRegister = true;
                        if (okToRegister) {
                            dialog.dismiss();
                            //progressDialog = new ProgressDialog(MainActivity.this);
                            //progressDialog.show();
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject new_user = new JSONObject();
                                        new_user.put("email", input_email.getText().toString());
                                        new_user.put("username", input_username.getText().toString());
                                        new_user.put("password", input_password.getText().toString());
                                        URL register_url = new URL(Api.api + Api.users);
                                        HttpURLConnection conn = (HttpURLConnection) register_url.openConnection();
                                        conn.setRequestMethod("POST");
                                        conn.setRequestProperty("Content-type", "application/json");
                                        conn.setDoOutput(true);
                                        conn.setDoInput(false);
                                        conn.setUseCaches(false);
                                        conn.connect();
                                        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                                        dos.writeBytes(new_user.toString());
                                        dos.flush();
                                        dos.close();
                                        Message message = new Message();
                                        message.what = 3;
                                        message.obj = 1;
                                        handler.sendMessage(message);
                                        conn.disconnect();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            Thread thread = new Thread(runnable);
                            thread.start();
                            while (true)
                                if (!thread.isAlive())
                                    break;
                        }else{
                            Toast.makeText(MainActivity.this,"信息不全或邮箱已被注册..",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.dialog_negative_button:dialog.dismiss();
                        break;
                }
            }
        });
        final EditText input_email = (EditText)dialog.findViewById(R.id.input_email);
        input_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                   new Thread(new Runnable() {
                      @Override
                      public void run() {
                          Looper.prepare();
                          try {
                              URL get_user_email = new URL(Api.api + Api.users);
                              HttpURLConnection conn = (HttpURLConnection)get_user_email.openConnection();
                              conn.setRequestMethod("GET");
                              conn.setDoInput(true);
                              conn.setUseCaches(false);
                              conn.connect();
                              InputStream is = conn.getInputStream();
                              String result = HttpUtils.readInputStream(is);
                              JSONObject whole = new JSONObject(result);
                              JSONArray users = whole.getJSONArray("user");
                              int i;
                              for(i = 0;i<users.length();i++){
                                  if(input_email.getText().toString().equals(users.getJSONObject(i).get("email").toString())) {
                                      Message message = new Message();
                                      message.what = 4;
                                      message.obj = false;
                                      handler.sendMessage(message);
                                      break;
                                  }
                              }
                              if(i==users.length()){
                                  Message message = new Message();
                                  message.what = 4;
                                  message.obj =true;
                                  handler.sendMessage(message);
                              }
                              is.close();
                              conn.disconnect();
                          }catch(Exception e){
                              e.printStackTrace();
                          }
                      }
                   }).start();//新开线程检查邮箱是否重复
                }
            }
        });
        DialogStandardSetting();
        dialog.show();
    }
}






