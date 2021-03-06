package com.muxistudio.guishengapp;


import java.security.MessageDigest;

public class Common {
   public static String md5(String sourceStr){
       byte[] source = sourceStr.getBytes();
       String s = null;
       char hexDigits[] = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
       try{
           MessageDigest md = MessageDigest.getInstance("MD5");
           md.update(source);
           byte[] temp = md.digest();
           char[] str = new char[32];
           int k=0;
           for(int i=0;i<16;i++){
               byte byte0 = temp[i];
               str[k++] = hexDigits[byte0>>>4 & 0xf];
               str[k++] = hexDigits[byte0 & 0xf];
           }
           s = new String(str);
       }catch(Exception e){
           e.printStackTrace();
       }
       return s;
   }
}

