package com.will.himalaya.api.data;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 * 订阅dao层数据处理回调
 */
public interface ISubDaoCallback {

    /**
     * 添加结果回调
     * @param isSuccess
     */
    void onAddResult(boolean isSuccess);

    /**
     * 删除结果回调
     * @param isSuccess
     */
    void onDelResult(boolean isSuccess);

    /**
     * 获取专辑订阅结果回调
     * @param list
     */
    void onSubListLoaded(List<Album> list);
}
