package com.will.himalaya.util;

import java.text.SimpleDateFormat;

public class TimeUtil {


    private static SimpleDateFormat mHourFormat = new SimpleDateFormat("hh:mm:ss");
    private static SimpleDateFormat mMinFormat = new SimpleDateFormat("mm:ss");

    //获取时长超过一小时的
    public static String getHourFormat(Object obj){
        String format = mHourFormat.format(obj);
        return format;
    }

    //获取时长不超过一小时的
    public static String getMinFormat(Object obj){
        String format = mMinFormat.format(obj);
        return format;
    }


}
