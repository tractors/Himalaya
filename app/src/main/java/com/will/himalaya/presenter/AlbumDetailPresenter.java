package com.will.himalaya.presenter;

import android.support.annotation.Nullable;

import com.will.himalaya.api.XimalayApi;
import com.will.himalaya.interfaces.IAlbumDetailPresenter;
import com.will.himalaya.interfaces.IAlbumDetailViewCallback;
import com.will.himalaya.util.Constant;
import com.will.himalaya.util.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 专辑粘贴板的实现类
 */
public class AlbumDetailPresenter implements IAlbumDetailPresenter {

    private static final String TAG = "AlbumDetailPresenter";
    private static volatile AlbumDetailPresenter sInstance = null;

    private List<Track> mTracks = new ArrayList<>();
    private List<IAlbumDetailViewCallback> mCallbacks = new ArrayList<>();

    private static Byte[] bytes = new Byte[10];
    private Album mTargetAlbum = null;
    //当前专辑的id
    private long mCurrentAlbumId = -1;
    //当前页
    private int mCurrentPageIndex = 0;

    private AlbumDetailPresenter(){}

    public static AlbumDetailPresenter getInstance(){
        if (null == sInstance) {
            synchronized (bytes){
                if (null == sInstance) {
                    sInstance = new AlbumDetailPresenter();
                }
            }
        }

        return sInstance;
    }

    @Override
    public void pullToRefreshMore() {

    }

    @Override
    public void loadMore() {
        //加载更多内容
        mCurrentPageIndex ++;
        doLoaded(true);
    }

    private void doLoaded(final boolean isLoaderMore){
        //根据页码和专辑id获取列表
        XimalayApi.getXimalayApi().getAlbumDetail(mCurrentAlbumId, mCurrentPageIndex, new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(@Nullable TrackList trackList) {
                if (trackList != null) {
                    LogUtil.d(TAG,"trackList size :"+trackList.getTracks().size());
                    List<Track> tracks = trackList.getTracks();
                    if (isLoaderMore) {
                        //这个是上拉加载 结果放在后面
                        mTracks.addAll(tracks);
                        handlerLoaderMoreResult(tracks);
                    } else {
                        //这个是下拉加载 结果放在前面
                        mTracks.addAll(0,tracks);
                    }

                    handlerAlbumDetailResult(mTracks);
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                if (isLoaderMore) {
                    mCurrentPageIndex --;
                }
                LogUtil.d(TAG,"errorCode :"+errorCode+",errorMsg:"+errorMsg);

                handlerAlbumDetailError(errorCode,errorMsg);
            }
        });
    }

    /**
     * 判断加载更多是否成功
     * @param tracks
     */
    private void handlerLoaderMoreResult(List<Track> tracks) {

        boolean result = (tracks != null && 0 < tracks.size())? true : false;

            for (IAlbumDetailViewCallback callback : mCallbacks) {
                callback.onLoaderMoreFinished(result);
            }
    }


    @Override
    public void getAlbumDetail(long albumId, int page) {
        mTracks.clear();
        this.mCurrentAlbumId = albumId;
        this.mCurrentPageIndex = page;
        //根据页码和专辑id获取列表
        doLoaded(false);
//        Map<String,String> map = new HashMap<>();
//        map.put(DTransferConstants.ALBUM_ID,String.valueOf(albumId));
//        map.put(DTransferConstants.SORT,"asc");
//        map.put(DTransferConstants.PAGE,String.valueOf(page));
//        map.put(DTransferConstants.PAGE_SIZE,String.valueOf(Constant.DETAIL_COUNT));
//        CommonRequest.getTracks(map, new IDataCallBack<TrackList>() {
//            @Override
//            public void onSuccess(@Nullable TrackList trackList) {
//                if (trackList != null) {
//                    LogUtil.d(TAG,"trackList size :"+trackList.getTracks().size());
//                    List<Track> tracks = trackList.getTracks();
//                    mTracks.addAll(tracks);
//                    handlerAlbumDetailResult(mTracks);
//                }
//            }
//
//            @Override
//            public void onError(int i, String s) {
//                LogUtil.d(TAG,"errorCode :"+i+",errorMsg:"+s);
//
//                handlerAlbumDetailError(i,s);
//            }
//        });

    }

    private void handlerAlbumDetailError(int errorCode, String errorMsg) {
        for (IAlbumDetailViewCallback callback : mCallbacks) {
            callback.onNetworkError(errorCode,errorMsg);
        }
    }

    private void handlerAlbumDetailResult(List<Track> tracks) {
        for (IAlbumDetailViewCallback callback : mCallbacks) {
            callback.onDetailListLoaded(tracks);
        }
    }

    @Override
    public void registerViewCallback(IAlbumDetailViewCallback detailViewCallback) {
        if ( null != mCallbacks && !mCallbacks.contains(detailViewCallback)) {
            mCallbacks.add(detailViewCallback);
            //更新UI
            detailViewCallback.onAlbumLoaded(mTargetAlbum);
        }
    }

    @Override
    public void unRegisterViewCallback(IAlbumDetailViewCallback detailViewCallback) {
        if (null != mCallbacks) {
            mCallbacks.remove(detailViewCallback);
        }
    }


    public void setTargetAlbum(Album album){
        this.mTargetAlbum = album;
    }
}
