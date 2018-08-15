package com.ezparking.com.blelearn;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by zyh
 */

public class Logutils {
    public static void e(Context context,String info){
        e(context,null,info);
    }
    public static void e(Context context,String tag,String info){
         String tag_;
        if(TextUtils.isEmpty(tag)){
             tag_ = "zyh";
         }else {
            tag_ = "zyh "+context.getPackageName();
        };
        Log.e(tag_,info);
    }
}
