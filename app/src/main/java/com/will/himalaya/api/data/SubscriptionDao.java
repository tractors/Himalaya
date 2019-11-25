package com.will.himalaya.api.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.will.himalaya.base.BaseApplication;
import com.will.himalaya.util.Constant;
import com.will.himalaya.util.LogUtil;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;

import java.util.ArrayList;
import java.util.List;

/**
 * 订阅dao层数据处理，增删查
 */
public class SubscriptionDao implements ISubDao {

    private static final String TAG = "SubscriptionDao";
    private final SubscriptionDBHelper mSubscriptionDBHelper;
    private ISubDaoCallback mISubDaoCallback = null;

    private static class Holder{
        private static final SubscriptionDao ourInstance = new SubscriptionDao();
    }
    public static SubscriptionDao getInstance() {
        return Holder.ourInstance;
    }

    private SubscriptionDao() {
        mSubscriptionDBHelper = new SubscriptionDBHelper(BaseApplication.getAppContext());
    }

    @Override
    public void setSubCallback(ISubDaoCallback iSubDaoCallback) {
        this.mISubDaoCallback = iSubDaoCallback;

    }

    @Override
    public void addAlbum(Album album) {
        SQLiteDatabase db = null;
        boolean isAddSuccess = false;
        try {
            db = mSubscriptionDBHelper.getWritableDatabase();
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();

            contentValues.put(Constant.SUB_COVER_URL,album.getCoverUrlLarge());
            contentValues.put(Constant.SUB_TITLE,album.getAlbumTitle());
            contentValues.put(Constant.SUB_DESCRIPTION,album.getAlbumIntro());
            contentValues.put(Constant.SUB_TRACKS_COUNT,album.getIncludeTrackCount());
            contentValues.put(Constant.SUB_PLAY_COUNT,album.getPlayCount());
            contentValues.put(Constant.SUB_AUTHOR_NAME,album.getAnnouncer().getNickname());
            contentValues.put(Constant.SUB_ALBUM_ID,album.getId());

            db.insert(Constant.SUB_TB_NAME,null,contentValues);
            db.setTransactionSuccessful();

            isAddSuccess = true;

        } catch (Exception e) {
            e.printStackTrace();
            isAddSuccess = false;
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }

            if (mISubDaoCallback != null) {
                mISubDaoCallback.onAddResult(isAddSuccess);
            }
        }
    }

    @Override
    public void delAlbum(Album album) {
        SQLiteDatabase db = null;
        boolean isDelSuccess = false;
        try {
            db = mSubscriptionDBHelper.getWritableDatabase();
            db.beginTransaction();
            db.delete(Constant.SUB_TB_NAME, Constant.SUB_ALBUM_ID + " = ? ", new String[]{album.getId() + ""});

            db.setTransactionSuccessful();
            isDelSuccess = true;

        } catch (Exception e) {
            e.printStackTrace();
            isDelSuccess = false;
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }

            if (mISubDaoCallback != null) {
                mISubDaoCallback.onDelResult(isDelSuccess);
            }
        }
    }

    @Override
    public void listAlbums() {
        SQLiteDatabase db = null;
        List<Album> result = new ArrayList<>();
        try {
            db = mSubscriptionDBHelper.getReadableDatabase();
            //开启事务
            db.beginTransaction();
            Cursor query = db.query(Constant.SUB_TB_NAME, null, null, null, null, null, "_id desc");

            //封装数据
            while (query.moveToNext()) {
                Album album = new Album();
                //专辑图片
                String coverUrl = query.getString(query.getColumnIndex(Constant.SUB_COVER_URL));
                album.setCoverUrlLarge(coverUrl);

                //标题
                String title = query.getString(query.getColumnIndex(Constant.SUB_TITLE));
                album.setAlbumTitle(title);

                //详情描述
                String description = query.getString(query.getColumnIndex(Constant.SUB_DESCRIPTION));
                album.setAlbumIntro(description);

                //点击量
                int tracksCount = query.getInt(query.getColumnIndex(Constant.SUB_TRACKS_COUNT));
                album.setIncludeTrackCount(tracksCount);

                //播放量
                int playCount = query.getInt(query.getColumnIndex(Constant.SUB_PLAY_COUNT));
                album.setPlayCount(playCount);

                //专辑作者
                String authorName = query.getString(query.getColumnIndex(Constant.SUB_AUTHOR_NAME));

                Announcer announcer = new Announcer();
                announcer.setNickname(authorName);
                album.setAnnouncer(announcer);

                //专辑id
                int albumId = query.getInt(query.getColumnIndex(Constant.SUB_ALBUM_ID));
                album.setId(albumId);

                result.add(album);
            }

            query.close();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            result = null;
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }

            LogUtil.d(TAG,"result:--------->"+(result == null || 0 ==result.size() ? 0 : result.size()));
            if (mISubDaoCallback != null) {
                mISubDaoCallback.onSubListLoaded(result);
            }
        }
    }
}
