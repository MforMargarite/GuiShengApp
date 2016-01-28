package com.muxistudio.guishengapp;

import com.qiniu.util.Auth;

public class UploadServer {
    Auth auth = Auth.create(Api.ACCESSKEY,Api.SECRETKEY);

    public String getUploadToken(){
        return auth.uploadToken("muxistudio-guishengapp");
    }

    public String getUploadToken(String key){
        return auth.uploadToken("muxistudio-guishengapp",key);
    }
}
