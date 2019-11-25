package com.will.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 * 订阅回调接口
 */
public interface ISubscriptionCallback {
    /**
     * 添加结果回调  通知UI更新结果
     * @param isSuccess
     */
    void onAddResult(boolean isSuccess);

    /**
     * 删除结果回调 通知UI更新结果
     * @param isSuccess
     */
    void onDelResult(boolean isSuccess);

    /**
     * 获取专辑订阅结果回调
     * @param result
     */
    void onSubscriptionsLoaded(List<Album> result);

    /**
     * 订阅数量超出限制
     */
    void onSubFull();
}
