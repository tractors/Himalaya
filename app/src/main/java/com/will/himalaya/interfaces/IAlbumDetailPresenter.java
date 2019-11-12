package com.will.himalaya.interfaces;

import com.will.himalaya.base.IBasePresenter;

/**
 * 专辑详情粘贴板接口
 */
public interface IAlbumDetailPresenter extends IBasePresenter<IAlbumDetailViewCallback>{

    /**
     * 下拉刷新
     */
    void pullToRefreshMore();

    /**
     * 上了加载更多
     */
    void loadMore();

    /**
     * 获取专辑详情
     * @param albumId
     * @param page
     */
    void getAlbumDetail(long albumId,int page);


}
