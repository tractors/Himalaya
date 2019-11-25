package com.will.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;

/**
 * 历史模块数据处理dao层接口
 */
public interface IHistoryDao {
    /**
     * 设置回调接口
     * @param historyDaoCallback
     */
    void setCallback(IHistoryDaoCallback historyDaoCallback);

    /**
     * 添加历史接口
     * @param track
     */
    void addHistory(Track track);

    /**
     * 删除历史接口
     * @param track
     */
    void delHistory(Track track);

    /**
     * 清除历史内容
     */
    void clearHistory();

    /**
     * 获取历史内容
     */
    void listHistories();
}
