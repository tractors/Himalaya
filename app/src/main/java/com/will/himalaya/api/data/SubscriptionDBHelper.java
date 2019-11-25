package com.will.himalaya.api.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.will.himalaya.util.Constant;

/**
 * 数据库创建工具类
 */
public class SubscriptionDBHelper extends SQLiteOpenHelper {

    public SubscriptionDBHelper(Context context) {
        super(context, Constant.DB_NAME, null, Constant.DB_VERSION_CODE);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //创建数据表
        //订阅相关的字段
        //图片，title，描述，播放量，节目数量，作者名称（详情节目），专辑id
        String subTbSql = "create table "+Constant.SUB_TB_NAME+"(" +
                Constant.SUB_ID+" integer primary key autoincrement," +
                Constant.SUB_COVER_URL+" varchar," +
                Constant.SUB_TITLE+" varchar," +
                Constant.SUB_DESCRIPTION+" varchar," +
                Constant.SUB_PLAY_COUNT+" integer," +
                Constant.SUB_TRACKS_COUNT+" integer," +
                Constant.SUB_AUTHOR_NAME+" varchar," +
                Constant.SUB_ALBUM_ID+" integer" +
                ");";
        sqLiteDatabase.execSQL(subTbSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
