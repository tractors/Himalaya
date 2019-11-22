package com.will.himalaya.util;

import android.content.Context;

/**
 * sharedPreference工具
 */
public class SharedPreferenceUtil extends BaseSharedPreferencesUtil{

    private static SharedPreferenceUtil instance;

    public SharedPreferenceUtil(Context context, String name) {
        super(context, name);
    }

    public SharedPreferenceUtil(Context context, String name, int mode) {
        super(context, name, mode);

    }

    public static SharedPreferenceUtil getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferenceUtil(context, "play_data");
        }

        return instance;
    }

    public static SharedPreferenceUtil getInstanceForPlayer(Context context) {
        instance = new SharedPreferenceUtil(context, "play_mode_data");
        return instance;
    }
}
