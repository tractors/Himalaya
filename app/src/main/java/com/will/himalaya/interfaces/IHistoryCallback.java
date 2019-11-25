package com.will.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

/**
 * 历史模块的回调接口
 */
public interface IHistoryCallback {
    /**
     * 获取历史加载结果的回调
     * @param tracks
     */
    void onHistoriesLoaded(List<Track> tracks);
}
