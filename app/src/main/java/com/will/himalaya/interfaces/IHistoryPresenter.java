package com.will.himalaya.interfaces;

import com.will.himalaya.base.IBasePresenter;
import com.ximalaya.ting.android.opensdk.model.track.Track;

/**
 * 历史模块的presenter层接口
 */
public interface IHistoryPresenter extends IBasePresenter<IHistoryCallback> {
    /**
     * 获取历史数据列表接口
     */
    void listHistories();

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
     * 清除历史接口
     */
    void cleanHistories();
}
