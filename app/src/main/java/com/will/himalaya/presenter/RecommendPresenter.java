package com.will.himalaya.presenter;

import android.support.annotation.Nullable;

import com.will.himalaya.api.XimalayApi;
import com.will.himalaya.interfaces.IRecommendPresenter;
import com.will.himalaya.interfaces.IRecommendViewCallback;
import com.will.himalaya.util.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.List;

/**
 * 推荐粘贴板实现类
 */
public class RecommendPresenter implements IRecommendPresenter{

    private static final String TAG = "RecommendPresenter";
    private List<IRecommendViewCallback> mCallbacks = new ArrayList<>();
    private List<Album> mCurrentRecommend = null;

    private RecommendPresenter(){}

    private static volatile RecommendPresenter sInstance = null;

    private static Byte[] mBytes = new Byte[10];

    public static RecommendPresenter getInstance(){
        if (null == sInstance){
            synchronized (mBytes){
                if (null == sInstance){
                    sInstance = new RecommendPresenter();
                }
            }
        }

        return sInstance;
    }

    /**
     * \获取当前的推荐专辑列表
     * @return  使用前判空
     */
    public List<Album> getCurrentRecommend(){
        return mCurrentRecommend;
    }

    /**
     * 获取推荐内容,其实是猜你喜欢
     */
    @Override
    public void getRecommendList() {
        updateLoading();
        XimalayApi.getXimalayApi().getRecommendList(new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(@Nullable GussLikeAlbumList gussLikeAlbumList) {
                if (gussLikeAlbumList != null){

                    if (gussLikeAlbumList != null) {
                        List<Album> albumList = gussLikeAlbumList.getAlbumList();
                        LogUtil.i(TAG,"size------>" + albumList.size());

                        handerRecommendResult(albumList);
                    }

                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.e(TAG,"errorCode--->"+errorCode + ",errorMsg-->"+ errorMsg);
                handlerError();
            }
        });
    }

    private void handlerError() {
        if (mCallbacks != null){
            for (IRecommendViewCallback callback : mCallbacks){
                callback.onNetworkError();
            }
        }
    }


    /**
     * 通知UI更新
     * @param albumList
     */
    private void handerRecommendResult(List<Album> albumList) {
        if (mCallbacks != null) {

            if (albumList != null) {
                if (albumList.size() == 0) {
                    for (IRecommendViewCallback callback : mCallbacks) {
                        callback.onEmpty();
                    }
                } else {
                    for (IRecommendViewCallback callback : mCallbacks) {
                        callback.onRecommendListLoaded(albumList);
                    }

                    this.mCurrentRecommend = albumList;
                }
            }
        }

    }

    private void updateLoading(){
        if (mCallbacks != null) {
            for (IRecommendViewCallback callback : mCallbacks) {
                callback.onLoading();
            }
        }
    }

    @Override
    public void pullToRefreshMore() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void registerViewCallback(IRecommendViewCallback callback) {
        if (null != mCallbacks && !mCallbacks.contains(callback)){
            mCallbacks.add(callback);
        }
    }

    @Override
    public void unRegisterViewCallback(IRecommendViewCallback callback) {
        if (mCallbacks != null){
            mCallbacks.remove(callback);
        }
    }
}
