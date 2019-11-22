package com.will.himalaya.interfaces;


import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode;

import java.util.List;

/**
 * 播放回调
 */
public interface IPlayerViewCallback {

    /**
     * 开始播放
     */
    void onPlayStart();

    /**
     * 暂停播放
     */
    void onPlayPause();

    /**
     * 停止播放
     */
    void onPlayStop();

    /**
     * 播放出错
     */
    void onPlayError();

    /**
     * 播放下一首
     */
    void onNextPlay(Track track);

    /**
     * 播放上一首
     */
    void onPrePlay(Track track);

    /**
     * 播放列表数据加载完成
     * @param list
     */
    void onListLoaded(List<Track> list);

    /**
     * 播放模式发生改变
     * @param mode
     */
    void onPlayModeChange(PlayMode mode);

    /**
     * 播放进度改变
     * @param currentProgress
     * @param total
     */
    void onProgressChange(int currentProgress,int total);

    /**
     * 广告正在加载
     */
    void onAdLoading();

    /**
     * 广告结束
     */
    void onAdFinished();

    /**
     * 更新当前节目
     * @param track
     */
    void onTrackUpdate(Track track,int playIndex);

    /**
     * 通知Ui播放列表排序更新
     * @param isReverse
     */
    void updateListOrder(boolean isReverse);
}
