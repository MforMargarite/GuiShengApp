package com.muxistudio.guishengapp;

import com.qiniu.storage.UploadManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UploadClient {
    static String avatar_url ;
    public String uploadImg(final byte[] imgPath,final int id){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String token = new UploadServer().getUploadToken();
                if(token!=null){
                    String key = getPhotoName(id);//生成用于上传头像的文件名
                    UploadManager uploadManager = new UploadManager();
                    try {
                        uploadManager.put(imgPath, key, token);
                        avatar_url = Api.avatar_root+key;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        while(true)
            if(!thread.isAlive())
                break;
        return avatar_url;
    }

    private String getPhotoName(int id){
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + id +".PNG";
    }
}
