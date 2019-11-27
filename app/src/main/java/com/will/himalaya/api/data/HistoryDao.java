package com.will.himalaya.api.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.will.himalaya.base.BaseApplication;
import com.will.himalaya.util.Constant;
import com.will.himalaya.util.LogUtil;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * 历史记录的dao层
 */
public class HistoryDao implements IHistoryDao {

    private static final String TAG = "HistoryDao";
    private final SubscriptionDBHelper mSubscriptionDBHelper;
    private IHistoryDaoCallback mIHistoryDaoCallback = null;
    private static volatile HistoryDao sInstance = null;
    private static byte[] sBytes = new byte[10];
    private Object mLock = new Object();

    private HistoryDao() {
        mSubscriptionDBHelper = new SubscriptionDBHelper(BaseApplication.getAppContext());
    }

    public static HistoryDao getInstance() {
        if (sInstance == null) {
            synchronized (sBytes) {
                if (sInstance == null) {
                    sInstance = new HistoryDao();
                }
            }
        }

        return sInstance;
    }


    @Override
    public void setCallback(IHistoryDaoCallback historyDaoCallback) {
        this.mIHistoryDaoCallback = historyDaoCallback;
    }

    @Override
    public void addHistory(Track track) {
        synchronized (mLock) {

            SQLiteDatabase db = null;
            boolean isSuccess = false;
            try {
                db = mSubscriptionDBHelper.getWritableDatabase();
                //先删除
                int delete = db.delete(Constant.HISTORY_TB_NAME, Constant.HISTORY_TRACK_ID + " = ? ", new String[]{track.getDataId() + ""});

                db.beginTransaction();
                //封装数据
                ContentValues values = new ContentValues();
                values.put(Constant.HISTORY_TRACK_ID, track.getDataId());
                values.put(Constant.HISTORY_TITLE, track.getTrackTitle());
                values.put(Constant.HISTORY_COVER, track.getCoverUrlLarge());
                values.put(Constant.HISTORY_PLAY_COUNT, track.getPlayCount());
                values.put(Constant.HISTORY_DURATION, track.getDuration());
                values.put(Constant.HISTORY_UPDATE_TIME, track.getUpdatedAt());
                values.put(Constant.HISTORY_AUTHOR, track.getAnnouncer().getNickname());
                //插入数据
                db.insert(Constant.HISTORY_TB_NAME, null, values);
                db.setTransactionSuccessful();
                isSuccess = true;
            } catch (Exception e) {
                isSuccess = false;
                e.printStackTrace();
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                if (mIHistoryDaoCallback != null) {
                    mIHistoryDaoCallback.onHistoryAdd(isSuccess);
                }
            }
        }
    }

    @Override
    public void delHistory(Track track) {
        SQLiteDatabase db = null;
        boolean isSuccess = false;
        try {
            db = mSubscriptionDBHelper.getWritableDatabase();
            db.beginTransaction();

            int delete = db.delete(Constant.HISTORY_TB_NAME, Constant.HISTORY_TRACK_ID + " = ? ", new String[]{track.getDataId() + ""});
            LogUtil.d(TAG, "delHistory:delete---->" + delete);
            db.setTransactionSuccessful();
            isSuccess = true;
        } catch (Exception e) {
            isSuccess = false;
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }

            if (mIHistoryDaoCallback != null) {
                mIHistoryDaoCallback.onHistoryDel(isSuccess);
            }
        }
    }

    @Override
    public void clearHistory() {
        SQLiteDatabase db = null;
        boolean isSuccess = false;
        try {
            db = mSubscriptionDBHelper.getWritableDatabase();
            db.beginTransaction();
            //删除数据
            int delete = db.delete(Constant.HISTORY_TB_NAME, null, null);
            LogUtil.d(TAG, "clearHistory:delete----->" + delete);
            db.setTransactionSuccessful();
            isSuccess = true;
        } catch (Exception e) {
            isSuccess = false;
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }

            if (mIHistoryDaoCallback != null) {
                mIHistoryDaoCallback.onHistoriesClean(isSuccess);
            }

        }
    }

    @Override
    public void listHistories() {
        //从数据表中查询历史记录
        synchronized (mLock) {

            SQLiteDatabase db = null;

            List<Track> histories = new ArrayList<>();
            try {
                db = mSubscriptionDBHelper.getReadableDatabase();
                db.beginTransaction();

                Cursor cursor = db.query(Constant.HISTORY_TB_NAME, null, null, null, null, null, "_id desc");

                while (cursor.moveToNext()) {
                    Track track = new Track();
                    int trackId = cursor.getInt(cursor.getColumnIndex(Constant.HISTORY_TRACK_ID));
                    track.setDataId(trackId);
                    String title = cursor.getString(cursor.getColumnIndex(Constant.HISTORY_TITLE));
                    track.setTrackTitle(title);
                    String cover = cursor.getString(cursor.getColumnIndex(Constant.HISTORY_COVER));
                    track.setCoverUrlLarge(cover);
                    track.setCoverUrlMiddle(cover);
                    track.setCoverUrlSmall(cover);
                    int playCount = cursor.getInt(cursor.getColumnIndex(Constant.HISTORY_PLAY_COUNT));
                    track.setPlayCount(playCount);
                    int duration = cursor.getInt(cursor.getColumnIndex(Constant.HISTORY_DURATION));
                    track.setDuration(duration);
                    long updateTime = cursor.getLong(cursor.getColumnIndex(Constant.HISTORY_UPDATE_TIME));
                    track.setUpdatedAt(updateTime);
                    String author = cursor.getString(cursor.getColumnIndex(Constant.HISTORY_AUTHOR));
                    Announcer announcer = new Announcer();
                    announcer.setNickname(author);
                    track.setAnnouncer(announcer);

                    histories.add(track);
                }

                cursor.close();

                db.setTransactionSuccessful();

            } catch (Exception e) {
                histories = null;
                e.printStackTrace();
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }

                if (mIHistoryDaoCallback != null) {
                    mIHistoryDaoCallback.onHistoriesLoaded(histories);
                }
            }

        }
    }
}
