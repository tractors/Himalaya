package com.will.himalaya.presenter;

import android.support.annotation.Nullable;

import com.will.himalaya.api.XimalayApi;
import com.will.himalaya.interfaces.ISearchCallback;
import com.will.himalaya.interfaces.ISearchPresenter;
import com.will.himalaya.util.Constant;
import com.will.himalaya.util.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索presenter
 */
public class SearchPresenter implements ISearchPresenter{

    private List<Album> mSearchResult = new ArrayList<>();
    private static final String TAG = "SearchPresenter";
    private static volatile SearchPresenter sSearchPresenter = null;
    private static byte[] sBytes = new byte[10];
    //当前搜索的关键字
    private String mCurrentKeyword = null;
    //api
    private XimalayApi mXimalayApi;
    //默认页码
    private static final int DEFAULT_PAGE = 1;
    //当前页，默认是1
    private int mCurrentPage = DEFAULT_PAGE;

    private boolean mIsLoadMore = false;

    private List<HotWord> mHotWords = new ArrayList<>();
    private SearchPresenter(){
        mXimalayApi = XimalayApi.getXimalayApi();
    }

    public static SearchPresenter getSearchPresenter(){
        if (sSearchPresenter == null) {
            synchronized (sBytes){
                if (sSearchPresenter == null) {
                    sSearchPresenter = new SearchPresenter();
                }
            }
        }

        return sSearchPresenter;
    }

    List<ISearchCallback> mSearchCallbacks = new ArrayList<>();




    @Override
    public void doSearch(String keyword) {
        mCurrentPage = DEFAULT_PAGE;
        mIsLoadMore = true;
        //用于新搜索
        //当网络不佳时，用户点击重新搜索
        mSearchResult.clear();
        this.mCurrentKeyword = keyword;
        search(keyword);
    }


    /**
     * 搜索
     * @param keyword
     */
    private void search(String keyword) {
        mXimalayApi.searchByKeyword(keyword, mCurrentPage, new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(@Nullable SearchAlbumList searchAlbumList) {
                List<Album> albums = searchAlbumList.getAlbums();
                mSearchResult.addAll(albums);
                if (albums != null) {
                    if (mIsLoadMore) {
                        for (ISearchCallback iSearchCallback : mSearchCallbacks) {
                                iSearchCallback.onLoadMoreResult(mSearchResult,(albums.size() == 0) ? false : true);
                        }
                        mIsLoadMore = false;
                    } else {
                        for (ISearchCallback searchCallback : mSearchCallbacks) {
                            searchCallback.onSearchResultLoaded(albums);
                        }
                    }

                } else {
                    for (ISearchCallback searchCallback : mSearchCallbacks) {

                    }
                }


            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                for (ISearchCallback searchCallback : mSearchCallbacks) {
                    if (mIsLoadMore) {
                        searchCallback.onLoadMoreResult(mSearchResult,false);
                        mIsLoadMore = false;
                        mCurrentPage --;
                    } else {

                        searchCallback.onError(errorCode,errorMsg);
                    }
                }
            }
        });
    }

    @Override
    public void reSearch() {
        search(mCurrentKeyword);
    }

    @Override
    public void loadMore() {
        //判断有没有必要加载更多
        if (Constant.DETAIL_COUNT > mSearchResult.size()) {
            for (ISearchCallback iSearchCallback : mSearchCallbacks) {
                iSearchCallback.onLoadMoreResult(mSearchResult,false);
            }
        } else {
            mIsLoadMore = true;
            mCurrentPage++;
            search(mCurrentKeyword);
        }

    }

    @Override
    public void getHotWord() {
        if (mHotWords == null || 0 == mHotWords.size()) {
            mXimalayApi.getHotWord(new IDataCallBack<HotWordList>() {
                @Override
                public void onSuccess(@Nullable HotWordList hotWordList) {
                    if (hotWordList != null) {
                        List<HotWord> hotWords = hotWordList.getHotWordList();
                        if (hotWords != null && 0 < hotWords.size()) {
                            mHotWords.clear();
                            mHotWords.addAll(hotWords);
                            for (ISearchCallback iSearchCallback: mSearchCallbacks) {
                                iSearchCallback.onHotWordLoaded(mHotWords);
                            }
                        }
                    }
                }

                @Override
                public void onError(int errorCode, String errorMsg) {
                    for (ISearchCallback searchCallback : mSearchCallbacks) {
                        searchCallback.onError(errorCode,errorMsg);
                    }
                }
            });
        } else {
            for (ISearchCallback iSearchCallback: mSearchCallbacks) {
                iSearchCallback.onHotWordLoaded(mHotWords);
            }
        }

    }

    /**
     * 获取联想词
     * @param keyword
     */
    @Override
    public void getRecommendWord(String keyword) {
        mXimalayApi.getSuggestWord(keyword, new IDataCallBack<SuggestWords>() {
            @Override
            public void onSuccess(@Nullable SuggestWords suggestWords) {
                if (suggestWords != null) {
                    List<QueryResult> keyWordList = suggestWords.getKeyWordList();
                    LogUtil.d(TAG,"getRecommendWord keyWordList"+ keyWordList.size());
                    if (keyWordList != null && 0 < keyWordList.size()) {
                        for (ISearchCallback iSearchCallback : mSearchCallbacks) {
                            iSearchCallback.onRecommendWord(keyWordList);
                        }
                    }
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                for (ISearchCallback searchCallback : mSearchCallbacks) {
                    searchCallback.onError(errorCode,errorMsg);
                }
            }
        });
    }

    @Override
    public void registerViewCallback(ISearchCallback iSearchCallback) {
        if (!mSearchCallbacks.contains(iSearchCallback)) {
            mSearchCallbacks.add(iSearchCallback);
        }
    }

    @Override
    public void unRegisterViewCallback(ISearchCallback iSearchCallback) {
        if (mSearchCallbacks != null) {
            mSearchCallbacks.remove(iSearchCallback);
        }
    }
}
