package com.will.himalaya.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class BaseSharedPreferencesUtil {

    private SharedPreferences settings;
    public static final int SHARE_MODEL;

    static {
        SHARE_MODEL = Build.VERSION.SDK_INT >= 24 ? 4 : 7;
    }


    @SuppressLint("WrongConstant")
    public BaseSharedPreferencesUtil(Context context, String name) {
        this.settings = context.getSharedPreferences(name, SHARE_MODEL);
    }

    public BaseSharedPreferencesUtil(Context context, String name, int mode) {
        this.settings = context.getSharedPreferences(name, mode);
    }

    public void saveLong(String key, long value) {
        this.apply(this.settings.edit().putLong(key, value));
    }

    public void saveFloat(String key, float value) {
        this.apply(this.settings.edit().putFloat(key, value));
    }

    public float getFloat(String key) {
        return this.settings.getFloat(key, -1.0F);
    }

    @SuppressLint({"NewApi"})
    public void apply(SharedPreferences.Editor editor) {
        if (Build.VERSION.SDK_INT >= 9) {
            editor.apply();
        } else {
            editor.commit();
        }

    }

    /**
     * 保存一个数据
     * @param key 键
     * @param value 值
     */
    <T> void put(String key, T value){
        this.apply(this.settings.edit());
    }

    public SharedPreferences getSettings() {
        return this.settings;
    }

    public long getLong(String key) {
        return this.settings.getLong(key, -1L);
    }

    public long getLong(String key, long defult) {
        return this.settings.getLong(key, defult);
    }

    public void saveString(String key, String value) {
        this.apply(this.settings.edit().putString(key, value));
    }

    public String getString(String key) {
        return this.settings.getString(key, "");
    }

    public void saveInt(String key, int value) {
        this.apply(this.settings.edit().putInt(key, value));
    }

    public int getInt(String key, int defaultValue) {
        return this.settings.getInt(key, defaultValue);
    }

    public Double getOptDouble(String key) {
        String retStr = this.settings.getString(key, (String)null);
        Double ret = null;

        try {
            ret = Double.parseDouble(retStr);
        } catch (Exception var5) {
            ;
        }

        return ret;
    }

    public Boolean getOptBoolean(String key) {
        String retStr = this.settings.getString(key, (String)null);
        Boolean ret = null;

        try {
            ret = Boolean.parseBoolean(retStr);
        } catch (Exception var5) {
            ;
        }

        return ret;
    }

    public Double getDouble(String key) {
        String retStr = this.settings.getString(key, (String)null);
        Double ret = null;

        try {
            if (retStr != null) {
                ret = Double.parseDouble(retStr);
                return ret;
            } else {
                return null;
            }
        } catch (Exception var5) {
            return null;
        }
    }

    public void saveHashMap(String key, Map<String, String> map) {
        JSONObject ret = new JSONObject(map);
        this.apply(this.settings.edit().putString(key, ret.toString()));
    }

    public void saveConcurrentHashMap(String key, ConcurrentHashMap<String, Object> map) {
        JSONObject ret = new JSONObject(map);
        this.apply(this.settings.edit().putString(key, ret.toString()));
    }

    public ConcurrentHashMap<String, String> getConcurrentHashMapByKey(String key) {
        ConcurrentHashMap<String, String> ret = new ConcurrentHashMap();
        String mapStr = this.settings.getString(key, "{}");
        JSONObject mapJson = null;

        try {
            mapJson = new JSONObject(mapStr);
        } catch (Exception var8) {
            return ret;
        }

        if (mapJson != null) {
            Iterator it = mapJson.keys();

            while(it.hasNext()) {
                String theKey = (String)it.next();
                String theValue = mapJson.optString(theKey);
                ret.put(theKey, theValue);
            }
        }

        return ret;
    }

    public HashMap<String, String> getHashMapByKey(String key) {
        HashMap<String, String> ret = new HashMap();
        String mapStr = this.settings.getString(key, "{}");
        JSONObject mapJson = null;

        try {
            mapJson = new JSONObject(mapStr);
        } catch (Exception var8) {
            return ret;
        }

        if (mapJson != null) {
            Iterator it = mapJson.keys();

            while(it.hasNext()) {
                String theKey = (String)it.next();
                String theValue = mapJson.optString(theKey);
                ret.put(theKey, theValue);
            }
        }

        return ret;
    }

    public void saveBoolean(String key, boolean bool) {
        this.apply(this.settings.edit().putBoolean(key, bool));
    }

    public boolean getBoolean(String key) {
        return this.settings.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean is) {
        return this.settings.getBoolean(key, is);
    }

    public void saveCopyOnWriteList(String key, CopyOnWriteArrayList<String> copyOnWriteArrayList) {
        this.apply(this.settings.edit().putString(key, (new Gson()).toJson(copyOnWriteArrayList)));
    }

    public CopyOnWriteArrayList<String> getCopyOnWriteList(String key) {
        CopyOnWriteArrayList<String> ret = new CopyOnWriteArrayList();
        String listStr = this.settings.getString(key, "{}");
        JSONArray listJson = null;

        try {
            listJson = new JSONArray(listStr);
        } catch (Exception var7) {
            return ret;
        }

        if (listJson != null) {
            for(int i = 0; i < listJson.length(); ++i) {
                String temp = listJson.optString(i);
                ret.add(temp);
            }
        }

        return ret;
    }

    public void saveArrayList(String key, ArrayList<String> list) {
        this.apply(this.settings.edit().putString(key, (new Gson()).toJson(list)));
    }

    public ArrayList<String> getArrayList(String key) {
        ArrayList<String> ret = new ArrayList();
        String listStr = this.settings.getString(key, "{}");
        JSONArray listJson = null;

        try {
            listJson = new JSONArray(listStr);
        } catch (Exception var7) {
            return ret;
        }

        if (listJson != null) {
            for(int i = 0; i < listJson.length(); ++i) {
                String temp = listJson.optString(i);
                ret.add(temp);
            }
        }

        return ret;
    }

    public void appendStringToList(String key, String content) {
        ArrayList<String> arrayList = this.getArrayList(key);
        if (arrayList != null && !arrayList.contains(content)) {
            arrayList.add(content);
        }

        this.saveArrayList(key, arrayList);
    }

    public void removeByKey(String key) {
        this.apply(this.settings.edit().remove(key));
    }

    public boolean contains(String key) {
        return this.settings.contains(key);
    }

    public void clear() {
        this.apply(this.settings.edit().clear());
    }


}
