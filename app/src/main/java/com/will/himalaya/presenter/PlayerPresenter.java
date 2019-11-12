package com.will.himalaya.presenter;

import com.will.himalaya.base.BaseApplication;
import com.will.himalaya.interfaces.IPlayerPresenter;
import com.will.himalaya.interfaces.IPlayerViewCallback;
import com.will.himalaya.util.LogUtil;
import com.will.himalaya.util.SharedPreferenceUtil;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

/**
 * 播放功能的粘贴板
 */
public class PlayerPresenter implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {

    private static final String TAG = "PlayerPresenter";
    private static volatile PlayerPresenter sPlayerPresenter = null;
    private static byte[] bytes = new byte[10];
    private XmPlayerManager mPlayerManager;

    private List<IPlayerViewCallback> mIPlayerViewCallbacks = new ArrayList<>();
    private Track mCurrentTrack;
    private int mCurrentIndex = -1;
    public final SharedPreferenceUtil mPlayModeSp;

    /**
     * PLAY_MODEL_LIST
       PLAY_MODEL_LIST_LOOP
       PLAY_MODEL_RANDOM
       PLAY_MODEL_SINGLE_LOOP
     */
    public static final int PLAY_MODEL_LIST_INT = 0;
    public static final int PLAY_MODEL_LIST_LOOP_INT = 1;
    public static final int PLAY_MODEL_RANDOM_INT = 2;
    public static final int PLAY_MODEL_SINGLE_LOOP_INT = 3;

    /**
     * 定义保存的key
     */

    public static final String  PLAY_MODE_SP_KEY = "currentPlayMode";
    public static final String PLAY_LIST_IS_REVERSE_KEY = "mIsReverse";

    private PlayMode mCurrentPlayMode = PlayMode.PLAY_MODEL_LIST;

    private boolean mIsReverse = false;

    public PlayerPresenter(){
        mPlayerManager = XmPlayerManager.getInstance(BaseApplication.getAppContext());

        //广告物料相关的接口
        mPlayerManager.addAdsStatusListener(this);

        //注册播放器状态相关的回调接口
        mPlayerManager.addPlayerStatusListener(this);

        //需要记录当前的播放模式
        mPlayModeSp = SharedPreferenceUtil.getInstanceForPlayer(BaseApplication.getAppContext());

    }

    public static PlayerPresenter getPlayerPresenter(){
        if (null == sPlayerPresenter) {
            synchronized (bytes){
                if (null == sPlayerPresenter) {
                    sPlayerPresenter = new PlayerPresenter();
                }
            }
        }
        return sPlayerPresenter;
    }

    private boolean isPlayListSet = false;
    public void setPlayList(List<Track> list,int index){
        if (null != list && null != mPlayerManager) {
            if (!mIsReverse) {
                //第一个参数是播放列表集合，第二个参数是播放下标
                //获取反转后的下标
                mCurrentIndex = list.size()-1-mCurrentIndex;

            } else {
                mCurrentTrack = list.get(index);
            }
            mPlayerManager.setPlayList(list,index);
            isPlayListSet = true;

        } else {
            LogUtil.d(TAG,"list or mPlayerManager is null");
        }
    }

    private int getIntByPlayMode(PlayMode mode){
        switch (mode) {
            case PLAY_MODEL_SINGLE_LOOP:
                return PLAY_MODEL_SINGLE_LOOP_INT;
            case PLAY_MODEL_LIST_LOOP:
                return PLAY_MODEL_LIST_LOOP_INT;
            case PLAY_MODEL_RANDOM:
                return PLAY_MODEL_RANDOM_INT;
            case PLAY_MODEL_LIST:
                return PLAY_MODEL_LIST_INT;
        }
        return PLAY_MODEL_LIST_INT;
    }

    private PlayMode getModeByInt(int index){

        switch (index) {
            case PLAY_MODEL_SINGLE_LOOP_INT:
                return PLAY_MODEL_SINGLE_LOOP;
            case PLAY_MODEL_LIST_LOOP_INT:
                return PLAY_MODEL_LIST_LOOP;
            case PLAY_MODEL_RANDOM_INT:
                return PLAY_MODEL_RANDOM;
            case PLAY_MODEL_LIST_INT:
                return PLAY_MODEL_LIST;
        }
        return PLAY_MODEL_LIST;
    }
    @Override
    public void play() {
        if (isPlayListSet) {
            mPlayerManager.play();
        }
    }

    @Override
    public void pause() {
        if (null != mPlayerManager) {
            mPlayerManager.pause();
        }
    }

    @Override
    public void stop() {
        if (null != mPlayerManager) {
            mPlayerManager.stop();
        }
    }

    @Override
    public void playPre() {
        if (null != mPlayerManager) {
            mPlayerManager.playPre();
        }
    }

    @Override
    public void playNext() {
        if (null != mPlayerManager) {
            mPlayerManager.playNext();
        }
    }

    @Override
    public void switchPlayMode(PlayMode mode) {
        if (mPlayerManager != null) {
            mPlayerManager.setPlayMode(mode);

            mCurrentPlayMode = mode;
            //通知UI更新播放模式
            for (IPlayerViewCallback iPlayerViewCallback : mIPlayerViewCallbacks) {
                iPlayerViewCallback.onPlayModeChange(mode);
            }

            mPlayModeSp.saveInt(PLAY_MODE_SP_KEY,getIntByPlayMode(mode));
        }
    }

    @Override
    public void getPlayList() {
        if (mPlayerManager != null) {
            List<Track> playList = mPlayerManager.getPlayList();
            if (!mIsReverse) {
                Collections.reverse(playList);
                //第一个参数是播放列表集合，第二个参数是播放下标
                //获取反转后的下标
                mCurrentIndex = playList.size()-1-mCurrentIndex;

            }

            mPlayerManager.setPlayList(playList,mCurrentIndex);

            for (IPlayerViewCallback iPlayerViewCallback : mIPlayerViewCallbacks) {
                iPlayerViewCallback.onListLoaded(playList);
                iPlayerViewCallback.onTrackUpdate(mCurrentTrack,mCurrentIndex);
                iPlayerViewCallback.updateListOrder(mIsReverse);
            }
        }
    }

    @Override
    public void playByIndex(int index) {
        // 切换播放器到index播放
        mPlayerManager.play(index);
    }

    @Override
    public void seekTo(int progress) {
        mPlayerManager.seekTo(progress);
    }

    @Override
    public boolean isPlay() {
        //返回当前是否正在播放
        return mPlayerManager.isPlaying();
    }

    @Override
    public void reversePlayList() {
        //播放内容列表反转
        List<Track> playList = mPlayerManager.getPlayList();
        Collections.reverse(playList);
        //第一个参数是播放列表集合，第二个参数是播放下标
        //获取反转后的下标
        mCurrentIndex = playList.size()-1-mCurrentIndex;
        mPlayerManager.setPlayList(playList,mCurrentIndex);

        mCurrentTrack = (Track) mPlayerManager.getCurrSound();

        mIsReverse = !mIsReverse;

        for (IPlayerViewCallback iPlayerViewCallback : mIPlayerViewCallbacks) {
            iPlayerViewCallback.onListLoaded(playList);
            iPlayerViewCallback.onTrackUpdate(mCurrentTrack,mCurrentIndex);
            iPlayerViewCallback.updateListOrder(mIsReverse);
        }


        mPlayModeSp.saveBoolean(PLAY_LIST_IS_REVERSE_KEY,mIsReverse);
    }

    @Override
    public void registerViewCallback(IPlayerViewCallback callback) {
        callback.onTrackUpdate(mCurrentTrack,mCurrentIndex);
        //从sp中取出 播放模式
        int modeIndex = mPlayModeSp.getInt(PLAY_MODE_SP_KEY,PLAY_MODEL_LIST_INT);

        //从sp中取出 排序

        mIsReverse = mPlayModeSp.getBoolean(PLAY_LIST_IS_REVERSE_KEY,false);
        //callback.updateListOrderFrist(mIsReverse);

        mCurrentPlayMode = getModeByInt(modeIndex);
        callback.onPlayModeChange(mCurrentPlayMode);
        if (!mIPlayerViewCallbacks.contains(callback)) {
            mIPlayerViewCallbacks.add(callback);
        }
    }

    @Override
    public void unRegisterViewCallback(IPlayerViewCallback callback) {
        if (null != mIPlayerViewCallbacks) {
            mIPlayerViewCallbacks.remove(callback);
        }
    }

    //==============广告相关的回调 start==========================
    @Override
    public void onStartGetAdsInfo() {

    }

    @Override
    public void onGetAdsInfo(AdvertisList advertisList) {

    }

    @Override
    public void onAdsStartBuffering() {

    }

    @Override
    public void onAdsStopBuffering() {

    }

    @Override
    public void onStartPlayAds(Advertis advertis, int i) {

    }

    @Override
    public void onCompletePlayAds() {

    }

    @Override
    public void onError(int what, int extra) {

    }

    //==============广告相关的回调 end==========================

    //=======播放器相关的回调方法 start=============
    @Override
    public void onPlayStart() {
        for (IPlayerViewCallback iPlayerViewCallback : mIPlayerViewCallbacks) {
            iPlayerViewCallback.onPlayStart();
        }
    }

    @Override
    public void onPlayPause() {
        for (IPlayerViewCallback iPlayerViewCallback : mIPlayerViewCallbacks) {
            iPlayerViewCallback.onPlayPause();
        }
    }

    @Override
    public void onPlayStop() {
        for (IPlayerViewCallback iPlayerViewCallback : mIPlayerViewCallbacks) {
            iPlayerViewCallback.onPlayStop();
        }
    }

    @Override
    public void onSoundPlayComplete() {

    }

    @Override
    public void onSoundPrepared() {
        if (mPlayerManager != null) {
            mPlayerManager.setPlayMode(mCurrentPlayMode);
            mPlayerManager.play();
        }
    }

    @Override
    public void onSoundSwitch(PlayableModel lastModel, PlayableModel curModel) {
        if (curModel instanceof Track) {
            Track currentTrack = (Track) curModel;

            mCurrentIndex = mPlayerManager.getCurrentIndex();

            if (currentTrack != null) {
                mCurrentTrack = currentTrack;
                //更新UI
                for (IPlayerViewCallback iPlayerViewCallback : mIPlayerViewCallbacks) {

                    iPlayerViewCallback.onTrackUpdate(mCurrentTrack,mCurrentIndex);

                }
            }

        }
    }

    @Override
    public void onBufferingStart() {

    }

    @Override
    public void onBufferingStop() {

    }

    @Override
    public void onBufferProgress(int progress) {

    }

    @Override
    public void onPlayProgress(int currPos, int duration) {
        //单位毫秒
        for (IPlayerViewCallback iPlayerViewCallback : mIPlayerViewCallbacks) {
            iPlayerViewCallback.onProgressChange(currPos,duration);
        }
    }

    @Override
    public boolean onError(XmPlayerException e) {
        return false;
    }

    //=======播放器相关的回调方法  end=============

}
