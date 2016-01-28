package com.muxistudio.guishengapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.MotionEvent;
import android.view.View;

import org.xml.sax.XMLReader;

import java.net.URL;


public class MTagHandler implements Html.TagHandler {
    private Context context;
    MTagHandler(Context context){
        this.context = context;
    }
    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        if(tag.toLowerCase().equals("img")){
            int len = output.length();
            ImageSpan[] imgSpan = output.getSpans(len - 1, len, ImageSpan.class);
            String imgUrl = imgSpan[0].getSource();
            output.setSpan(new ImageClick(context,imgUrl),len-1,len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
}

class ImageClick extends ClickableSpan implements View.OnClickListener{
    protected Context context;
    private String imgUrl;
    public ImageClick(Context context,String imgUrl){
        this.context = context;
        this.imgUrl = imgUrl;
    }

    @Override
    public void onClick(View widget) {
        Image_Viewer image_viewer = new Image_Viewer();
        try {
            image_viewer.bitmap = BitmapFactory.decodeStream(new URL(imgUrl).openStream());
            Intent intent = new Intent(context,Image_Viewer.class);
            context.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}