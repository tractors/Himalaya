package com.will.himalaya.interfaces;

import com.will.himalaya.base.IBasePresenter;
import com.ximalaya.ting.android.opensdk.model.album.Album;

/**
 * 订阅的presenter的接口 订阅有上限，一般不超过100个
 */
public interface ISubscriptionPresenter extends IBasePresenter<ISubscriptionCallback>{
    /**
     * 添加订阅
     * @param album
     */
    void addSubscription(Album album);

    /**
     * 删除订阅
     * @param album
     */
    void deleteSubscription(Album album);

    /**
     * 获取订阅
     */
    void getSubscriptionList();

    /**
     * 加载失败
     * @param errorCode
     * @param errorMsg
     */
    void onError(int errorCode,int errorMsg);


    /**
     * 判断是否已经订阅
     * @param album
     */
    boolean isSub(Album album);
}
