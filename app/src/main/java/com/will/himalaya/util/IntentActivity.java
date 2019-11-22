package com.will.himalaya.util;

import android.content.Context;
import android.content.Intent;

/**
 * 跳转类
 */
public class IntentActivity {
    public static void startActivity(Context context, Class clazz){
        Intent intent = new Intent(context,clazz);
        context.startActivity(intent);
    }
}
