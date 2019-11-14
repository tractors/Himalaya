package com.will.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

/**
 * 专辑详情回调
 */
public interface IAlbumDetailViewCallback {
    /**
     * 专辑详情内容回调
     * @param tracks
     */
    void onDetailListLoaded(List<Track> tracks);

    /**
     * 把album传UI使用
     * @param album
     */
    void onAlbumLoaded(Album album);

    /**
     * 网络错误
     */
    void onNetworkError(int errorCode,String errorMsg);

    /**
     * 加载更多的结果
     * @param isOkay true表示加载成功，false表示加载失败
     */
    void onLoaderMoreFinished(boolean isOkay);

    /**
     * 下拉刷新结果
     * @param isOkay true表示加载成功，false表示加载失败
     */
    void onRefreshFinished(boolean isOkay);
}
