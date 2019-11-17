package com.will.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.List;

/**
 * 搜索回调接口
 */
public interface ISearchCallback {

    /**
     * 搜索结果回调
     * @param result
     */
    void onSearchResultLoaded(List<Album> result);

    /**
     * 热词查询结果回调
     * @param hotWordLists
     */
    void onHotWordLoaded(List<HotWordList> hotWordLists);

    /**
     * 加载更多结果回调
     * @param result    结果
     * @param isOkay    true表示加载更多成功，false表示没更多
     */
    void onLoadMoreResult(List<Album> result,boolean isOkay);

    /**
     * 联想关键字结果的回调
     * @param keyWordList
     */
    void onRecommendWord(List<QueryResult> keyWordList);
}
