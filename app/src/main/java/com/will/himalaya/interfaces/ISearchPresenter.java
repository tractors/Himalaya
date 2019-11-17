package com.will.himalaya.interfaces;

import com.will.himalaya.base.IBasePresenter;

/**
 * 搜索presenter接口
 */
public interface ISearchPresenter extends IBasePresenter<ISearchCallback>{

    /**
     * 搜索接口
     * @param keyword
     */
    void doSearch(String keyword);

    /**
     * 重新搜索接口
     */
    void reSearch();

    /**
     * 加载更多接口
     */
    void loadMore();

    /**
     * 获取热词接口
     */
    void getHotWord();

    /**
     * 获取推荐的关键字(相关的关键字)
     * @param keyword
     */
    void getRecommendWord(String keyword);
}
