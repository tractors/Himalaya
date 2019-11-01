package com.will.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface IRecommendViewCallback {
    /**
     * 获取推荐内容的结果
     * @param result
     */
    void onRecommendListLoaded(List<Album> result);

    /**
     * 上拉加载更多
     * @param result
     */
    void onLoadMore(List<Album> result);

    /**
     * 下拉刷新
     * @param result
     */
    void onRefreshMore(List<Album> result);
}
