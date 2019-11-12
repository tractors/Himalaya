package com.will.himalaya.interfaces;

import com.will.himalaya.base.IBasePresenter;

/**
 * 推荐粘贴板
 */
public interface IRecommendPresenter extends IBasePresenter<IRecommendViewCallback>{

    /**
     * 获取推荐内容接口
     */
    void getRecommendList();

    /**
     * 下拉刷新
     */
    void pullToRefreshMore();

    /**
     * 上了加载更多
     */
    void loadMore();

}
