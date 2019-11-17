package com.will.himalaya.interfaces;

import com.will.himalaya.base.IBasePresenter;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode;

/**
 * 播放界面的接口
 */
public interface IPlayerPresenter extends IBasePresenter<IPlayerViewCallback> {

    /**
     * 播放
     */
    void play();

    /**
     * 暂停播放
     */
    void pause();

    /**
     * 停止播放
     */
    void stop();

    /**
     * 播放上一首
     */
    void playPre();

    /**
     * 播放下一首
     */
    void playNext();

    /**
     * 切换播放模式
     * @param mode
     */
    void switchPlayMode(PlayMode mode);

    /**
     * 获取播放列表
     */
    void getPlayList();

    /**
     * 根据节目的位置进行播放
     * @param index 节目在列表中的位置
     */
    void playByIndex(int index);

    /**
     * 更改播放进度
     * @param progress
     */
    void seekTo(int progress);

    /**
     * 判断播放器是否在播放
     * @return
     */
    boolean isPlaying();

    /**
     * 播放列表内容反转
     */
    void reversePlayList();

    /**
     * 播放器中是否有播放列表接口
     * @return
     */
    boolean hasPlayList();

    /**
     * 通过专辑的ID 播放内容
     * @param id
     */
    void playByAlbumId(long id);

}
