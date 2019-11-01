package com.will.himalaya.presenter;

import android.support.annotation.Nullable;

import com.will.himalaya.interfaces.IRecommendPresenter;
import com.will.himalaya.interfaces.IRecommendViewCallback;
import com.will.himalaya.util.Constant;
import com.will.himalaya.util.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendPresenter implements IRecommendPresenter{

    private static final String TAG = "RecommendPresenter";
    private List<IRecommendViewCallback> mCallbacks = new ArrayList<>();
    private RecommendPresenter(){}

    private static volatile  RecommendPresenter sInstance = null;

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
     * 获取推荐内容,其实是猜你喜欢
     */
    @Override
    public void getRecommendList() {
        Map<String,String> map = new HashMap<>();

        map.put(DTransferConstants.LIKE_COUNT,String.valueOf(Constant.RECOMMEND_COUNT));
        CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(@Nullable GussLikeAlbumList gussLikeAlbumList) {
                if (gussLikeAlbumList != null){
                    List<Album> albumList = gussLikeAlbumList.getAlbumList();
                    if (albumList != null){
                        LogUtil.i(TAG,"size------>" + albumList.size());
                        //upRecommendUI(albumList);
                        handerRecommendResult(albumList);
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.e(TAG,"errorCode--->"+i + ",errorMsg-->"+ s);
            }
        });
    }


    /**
     * 通知UI更新
     * @param albumList
     */
    private void handerRecommendResult(List<Album> albumList) {
        if (mCallbacks != null){
            for (IRecommendViewCallback callback : mCallbacks) {
                callback.onRecommendListLoaded(albumList);
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
