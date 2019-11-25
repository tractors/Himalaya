package com.will.himalaya.api.data;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

/**
 * 历史回调接口
 */
public interface IHistoryDaoCallback {

    /**
     * 添加历史结果接口
     * @param isSuccess
     */
    void onHistoryAdd(boolean isSuccess);

    /**
     * 删除历史结果接口
     * @param isSuccess
     */
    void onHistoryDel(boolean isSuccess);

    /**
     * 历史数据加载结果接口
     * @param tracks
     */
    void onHistoriesLoaded(List<Track> tracks);

    /**
     * 历史内容清除结果接口
     * @param isSuccess
     */
    void onHistoriesClean(boolean isSuccess);
}
