package com.will.himalaya.presenter;

import android.support.annotation.Nullable;

import com.will.himalaya.api.XimalayApi;
import com.will.himalaya.interfaces.ISearchCallback;
import com.will.himalaya.interfaces.ISearchPresenter;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索presenter
 */
public class SearchPresenter implements ISearchPresenter{

    private static final String TAG = "SearchPresenter";
    private static SearchPresenter sSearchPresenter = null;
    private static byte[] sBytes = new byte[10];
    //当前搜索的关键字
    private String mCurrentKeyword = null;
    private XimalayApi mXimalayApi;
    private static final int DEFAULT_PAGE = 1;
    private int mCurrentPage = DEFAULT_PAGE;

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
        this.mCurrentKeyword = keyword;
        mXimalayApi.searchByKeyword(keyword, mCurrentPage, new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(@Nullable SearchAlbumList searchAlbumList) {
                List<Album> albums = searchAlbumList.getAlbums();
                if (albums != null) {

                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {

            }
        });
    }

    @Override
    public void reSearch() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void getHotWord() {

    }

    @Override
    public void getRecommendWord(String keyword) {

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
