package com.will.himalaya.util;

/**
 * 公共基类
 */
public class Constant {

    //获取推荐列表的专辑数量
    public static final int RECOMMEND_COUNT = 50;
    //默认专辑详情列表请求数量
    public static final int DETAIL_COUNT = 50;
    //默认热词列表请求数量
    public static final int COUNT_HOT_WORD = 20;
    //数据库名字
    public static final String DB_NAME = "subscription.db";
    //数据库版本
    public static final int DB_VERSION_CODE = 1;

    //数据库字段设定常量
    //订阅的表名
    public static final String SUB_TB_NAME = "tb_subscription";
    public static final String SUB_ID = "_id";
    public static final String SUB_COVER_URL = "cover_url";
    public static final String SUB_TITLE = "title";
    public static final String SUB_DESCRIPTION = "description";
    public static final String SUB_PLAY_COUNT = "play_count";
    public static final String SUB_TRACKS_COUNT = "tracks_count";
    public static final String SUB_AUTHOR_NAME = "author_name";
    public static final String SUB_ALBUM_ID = "album_id";
    //订阅最多个数
    public static final int MAX_SUB_COUNT = 100;
    //历史记录的表名
    public static final String HISTORY_TB_NAME = "tb_history";
    public static final String HISTORY_ID = "_id";
    public static final String HISTORY_TRACK_ID = "history_track_id";
    public static final String HISTORY_TITLE = "history_title";
    public static final String HISTORY_PLAY_COUNT = "history_play_count";
    public static final String HISTORY_DURATION = "history_duration";
    public static final String HISTORY_UPDATE_TIME = "history_update_time";
    public static final String HISTORY_COVER = "history_cover";
    public static final String HISTORY_AUTHOR = "history_author";
    //最大历史记录数
    public static final int MAX_HISTORY_COUNT = 100;
}
