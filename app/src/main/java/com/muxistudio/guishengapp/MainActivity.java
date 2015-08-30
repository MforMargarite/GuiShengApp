package com.muxistudio.guishengapp;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends FragmentActivity implements View.OnTouchListener {
    ImageButton ToLogging;
    int log_status = 1;
    public static int status = 0;
    public static int current_item = 0;
    MyViewPager viewpager;
    HeadpicImageView user_headpic;
    final int PHOTO_REQUEST_SHOOT = 1;
    final int PHOTO_REQUEST_PHOTOGRAPH = 2;
    final int PHOTO_REQUEST_CUT = 3;
    static int position = 0;
    boolean DATA_GET_STATE;
    File tempFile = null;
    File GuiShengApp;
    File GuiShengApp_headpic;
    int isTempFileEmpty = 1;
    Intent show_headpic;
    TextView newsLine, originalLine, interactLine;
    Button news, original, interact;
    LinearLayout upload_headpic, change_name, choose_unlog;
    View dialog_view;
    MyDialog dialog;
    String new_name;
    EditText et_username;
    FrameLayout Log_interface;
    Animation right_slide_back;
    Animation left_slide_in;
    ViewGroup myActionBarLayout;
    Message message = new Message();
    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message){
            switch(message.what){
               case 0://new NetDataObtain().DataRequireOver();
                   DATA_GET_STATE = true;
                   break;
                case 1:Toast.makeText(MainActivity.this,getResources().getText(R.string.err),Toast.LENGTH_SHORT).show();
                    break;
           }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myActionBarLayout = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.mainactivity_actionbar_layout, null);
        ActionBar actionBar = getActionBar();
        actionBar.setCustomView(myActionBarLayout);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        init();
        Intent from_splash = getIntent();
        Bundle bundle = from_splash.getExtras();
        DATA_GET_STATE = bundle.getBoolean("DATA_GET_STATE");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectionReceiver,intentFilter);
    }



   BroadcastReceiver connectionReceiver = new BroadcastReceiver() {
       @Override
       public void onReceive(Context context, Intent intent) {
           if(!DATA_GET_STATE && NetDataObtain.isNetworkAvailable(context))
                message.what=0;
           else if(!NetDataObtain.isNetworkAvailable(context))
               message.what=1;
           handler.sendMessage(message);
       }
   };



    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent data){
        switch (requestCode) {
            case PHOTO_REQUEST_SHOOT:
                if (resultCode==RESULT_OK)
                    startZoom(Uri.fromFile(tempFile));
                else
                    return;
            case PHOTO_REQUEST_PHOTOGRAPH:
                if (data != null)
                    startZoom(data.getData());
                break;
            case PHOTO_REQUEST_CUT:
                if(data != null) {
                    try {
                        byte[] headpic_byte = data.getByteArrayExtra("crop_pic");
                        show_headpic.putExtra("headpic", headpic_byte);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(headpic_byte, 0, headpic_byte.length);
                        user_headpic.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

@Override
public boolean onTouchEvent(MotionEvent event){
    if(event.getX()>Log_interface.getWidth())
       logRollBack();
    return true;
}

    @Override
    public boolean onTouch (View v,MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(status==0)
                    return false;
                else
                    return true;
            case MotionEvent.ACTION_UP:
                v.setPressed(true);
                switch (v.getId()) {
                    case R.id.upload_headpic:
                        if (Log_interface.getVisibility() == View.VISIBLE && log_status == 1) {
                            uploadUserpicDialog();
                            ToLogging.requestFocusFromTouch();
                        }
                        return true;
                    case R.id.change_name:
                        if (Log_interface.getVisibility() == View.VISIBLE && log_status == 1) {
                            changeNameDialog();
                            ToLogging.requestFocusFromTouch();
                        }
                        return true;
                    case R.id.choose_unlog:
                        if (Log_interface.getVisibility() == View.VISIBLE && log_status == 1) {
                            chooseUnlogDialog();
                            ToLogging.requestFocusFromTouch();
                        }
                        return true;
                    case R.id.user_head_pic:
                        startActivity(show_headpic);
                        return true;
                    case R.id.logging:
                        ToLoggingClickAction();
                        return true;
                    case R.id.tab_interact:
                        interactOnClickAction();
                        return true;
                    case R.id.tab_original:
                        originalOnClickAction();
                        return true;
                    case R.id.tab_news:
                        newsOnClickAction();
                        return true;
                        }
                }
        v.setPressed(false);
        return false;
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

    private  void uploadUserpicDialog() {
        dialog = new MyDialog(MainActivity.this, R.layout.upload_headpic_dialog_interface, new MyDialog.ChangeNameDialogListener() {
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
        dialog = new MyDialog(MainActivity.this, R.layout.change_name_dialog_interface, new MyDialog.ChangeNameDialogListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.dialog_positive_button:
                        et_username = (EditText) dialog.findViewById(R.id.dialog_edit);
                        new_name = et_username.getText().toString();
                        TextView username = (TextView) findViewById(R.id.username);
                        username.setText(new_name);
                        et_username.setText("");
                        Log_interface.setAnimation(right_slide_back);
                        Log_interface.setVisibility(View.GONE);
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
        dialog = new MyDialog(MainActivity.this, R.layout.choose_unlog_dialog_interface, new MyDialog.ChangeNameDialogListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.dialog_positive_button:
                        right_slide_back = AnimationUtils.loadAnimation(MainActivity.this, R.anim.right_slide_back);
                        Log_interface.setAnimation(right_slide_back);
                        Log_interface.setVisibility(View.GONE);
                        status = 0;
                        dialog.dismiss();
                        Log_interface = (FrameLayout) findViewById(R.id.unlog_interface);
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

    private void ToLoggingClickAction() {
        right_slide_back = AnimationUtils.loadAnimation(MainActivity.this, R.anim.right_slide_back);
        left_slide_in = AnimationUtils.loadAnimation(MainActivity.this, R.anim.left_slide_in);
        int tag = Log_interface.getVisibility();
        if (tag == View.GONE) {
            ToLogging.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.self_rotate));
            Log_interface.setAnimation(left_slide_in);
            Log_interface.setVisibility(View.VISIBLE);
            status = 1;
        } else if (tag == View.VISIBLE) {
            ToLogging.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.self_rotate));
            Log_interface.setAnimation(right_slide_back);
            Log_interface.setVisibility(View.GONE);
            status = 0;
        }
    }

    private void interactOnClickAction(){
        switch (viewpager.getCurrentItem()) {
            case 2:
                break;
            default:
                news.setTextColor(getResources().getColor(R.color.dark_grey));
                original.setTextColor(getResources().getColor(R.color.dark_grey));
                newsLine.setVisibility(View.INVISIBLE);
                originalLine.setVisibility(View.INVISIBLE);
                interact.setTextColor(getResources().getColor(R.color.guisheng_red));
                interactLine.setVisibility(View.VISIBLE);
                viewpager.setCurrentItem(2);
                break;
        }
        current_item = 2;
    }

    private void originalOnClickAction() {
        switch (viewpager.getCurrentItem()) {
            case 1:
                break;
            default:
                news.setTextColor(getResources().getColor(R.color.dark_grey));
                interact.setTextColor(getResources().getColor(R.color.dark_grey));
                newsLine.setVisibility(View.INVISIBLE);
                interactLine.setVisibility(View.INVISIBLE);
                original.setTextColor(getResources().getColor(R.color.guisheng_red));
                originalLine.setVisibility(View.VISIBLE);
                viewpager.setCurrentItem(1);
                break;
        }
        current_item = 1;
    }

    private void newsOnClickAction(){
        switch (viewpager.getCurrentItem()) {
            case 0:
                break;
            default:
                original.setTextColor(getResources().getColor(R.color.dark_grey));
                interact.setTextColor(getResources().getColor(R.color.dark_grey));
                originalLine.setVisibility(View.INVISIBLE);
                interactLine.setVisibility(View.INVISIBLE);
                news.setTextColor(getResources().getColor(R.color.guisheng_red));
                newsLine.setVisibility(View.VISIBLE);
                viewpager.setCurrentItem(0);
                break;
        }
        current_item = 0;
    }

    private void init() {
        news = (Button) findViewById(R.id.tab_news);
        original = (Button) findViewById(R.id.tab_original);
        interact = (Button) findViewById(R.id.tab_interact);
        newsLine = (TextView) findViewById(R.id.news_line);
        originalLine = (TextView) findViewById(R.id.original_line);
        interactLine = (TextView) findViewById(R.id.interact_line);
        upload_headpic = (LinearLayout) findViewById(R.id.upload_headpic);
        change_name = (LinearLayout) findViewById(R.id.change_name);
        choose_unlog = (LinearLayout) findViewById(R.id.choose_unlog);
        dialog_view = findViewById(R.id.dialog);
        et_username = (EditText) findViewById(R.id.dialog_edit);
        user_headpic = (HeadpicImageView) findViewById(R.id.user_head_pic);
        ToLogging = (ImageButton)myActionBarLayout.findViewById(R.id.logging);
        if (log_status == 1) {
            Log_interface = (FrameLayout) findViewById(R.id.logged_interface);
            dialog_view = getLayoutInflater().inflate(R.layout.change_name_dialog_interface, null);
        } else {
            Log_interface = (FrameLayout) findViewById(R.id.unlog_interface);
        }

        viewpager = (MyViewPager) findViewById(R.id.viewpager);
        viewpager.setAdapter(new MyFragmentStatePagerAdapter(getSupportFragmentManager()));
        show_headpic = new Intent(MainActivity.this, Headpic_Viewer.class);
        GuiShengApp = new File(File.separator + Environment.getExternalStorageDirectory() + File.separator + "GuiShengApp" + File.separator);
        GuiShengApp_headpic = new File(File.separator + Environment.getExternalStorageDirectory() + File.separator + "GuiShengApp" + File.separator + "head_pic" + File.separator);
        CreateFile(GuiShengApp);
        CreateFile(GuiShengApp_headpic);
        news.setOnTouchListener(this);
        original.setOnTouchListener(this);
        interact.setOnTouchListener(this);
        upload_headpic.setOnTouchListener(this);
        change_name.setOnTouchListener(this);
        choose_unlog.setOnTouchListener(this);
        ToLogging.setOnTouchListener(this);
        user_headpic.setOnTouchListener(this);
        right_slide_back = AnimationUtils.loadAnimation(MainActivity.this, R.anim.right_slide_back);
        left_slide_in = AnimationUtils.loadAnimation(MainActivity.this, R.anim.left_slide_in);
        news.setTextColor(getResources().getColor(R.color.guisheng_red));
        newsLine.setVisibility(View.VISIBLE);
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                MainActivity.position = position;
                switch (position){
                    case 0:NewsActionBarSelected();
                        break;
                    case 1:OriginalActionBarSelected();
                        break;
                    case 2:InteractActionBarSelected();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void NewsActionBarSelected() {
        original.setTextColor(getResources().getColor(R.color.dark_grey));
        interact.setTextColor(getResources().getColor(R.color.dark_grey));
        originalLine.setVisibility(View.INVISIBLE);
        interactLine.setVisibility(View.INVISIBLE);
        news.setTextColor(getResources().getColor(R.color.guisheng_red));
        newsLine.setVisibility(View.VISIBLE);
        viewpager.setCurrentItem(0);
        current_item = 0;
    }

    private void OriginalActionBarSelected(){
        news.setTextColor(getResources().getColor(R.color.dark_grey));
        interact.setTextColor(getResources().getColor(R.color.dark_grey));
        newsLine.setVisibility(View.INVISIBLE);
        interactLine.setVisibility(View.INVISIBLE);
        original.setTextColor(getResources().getColor(R.color.guisheng_red));
        originalLine.setVisibility(View.VISIBLE);
        viewpager.setCurrentItem(1);
        current_item = 1;
    }

    private void InteractActionBarSelected(){
        news.setTextColor(getResources().getColor(R.color.dark_grey));
        original.setTextColor(getResources().getColor(R.color.dark_grey));
        newsLine.setVisibility(View.INVISIBLE);
        originalLine.setVisibility(View.INVISIBLE);
        interact.setTextColor(getResources().getColor(R.color.guisheng_red));
        interactLine.setVisibility(View.VISIBLE);
        viewpager.setCurrentItem(2);
        current_item = 2;
    }

    private void logRollBack(){
        if(MainActivity.status == 1) {
            ToLogging.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.self_rotate));
            Animation right_slide_back = AnimationUtils.loadAnimation(MainActivity.this, R.anim.right_slide_back);
            Log_interface.setAnimation(right_slide_back);
            Log_interface.setVisibility(View.GONE);
            status = 0;
        }
    }
@Override
    public void onDestroy(){
      super.onDestroy();
      unregisterReceiver(connectionReceiver);
    }
}




