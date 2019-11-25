package com.will.himalaya.presenter;

import com.will.himalaya.api.data.ISubDaoCallback;
import com.will.himalaya.api.data.SubscriptionDao;
import com.will.himalaya.base.BaseApplication;
import com.will.himalaya.interfaces.ISubscriptionCallback;
import com.will.himalaya.interfaces.ISubscriptionPresenter;
import com.will.himalaya.util.Constant;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * 订阅的presenter模块
 */
public class SubscriptionPresenter implements ISubscriptionPresenter, ISubDaoCallback {

    private static final String TAG = "SubscriptionPresenter";
    private final SubscriptionDao mSubscriptionDao;
    private Map<Long,Album> mData = new HashMap<>();
    private List<ISubscriptionCallback> mCallbacks = new ArrayList<>();

    private SubscriptionPresenter(){
        mSubscriptionDao = SubscriptionDao.getInstance();
        mSubscriptionDao.setSubCallback(this);
    }

    private void listSubscriptions(){
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                //只调用，不处理结果
                if (mSubscriptionDao != null) {
                    mSubscriptionDao.listAlbums();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }


    private static class Holder {
        private static final SubscriptionPresenter outInstance = new SubscriptionPresenter();
    }

    public static SubscriptionPresenter getInstance(){
        return Holder.outInstance;
    }

    @Override
    public void addSubscription(final Album album) {
        if (mData != null) {
            if (Constant.MAX_SUB_COUNT <= mData.size()) {
                //给出提示
                for (ISubscriptionCallback iSubscriptionCallback : mCallbacks) {
                    iSubscriptionCallback.onSubFull();
                }
                return;
            }
        }
        //添加订阅
        if (album != null) {
            Observable.create(new ObservableOnSubscribe<Object>() {
                @Override
                public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                    if (mSubscriptionDao != null) {
                        mSubscriptionDao.addAlbum(album);
                    }
                }
            }).subscribeOn(Schedulers.io()).subscribe();
        }

    }

    @Override
    public void deleteSubscription(final Album album) {
        //删除订阅
        if (album != null) {
            Observable.create(new ObservableOnSubscribe<Object>() {
                @Override
                public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                    if (mSubscriptionDao != null) {
                        mSubscriptionDao.delAlbum(album);
                    }
                }
            }).subscribeOn(Schedulers.io()).subscribe();
        }
    }

    @Override
    public void getSubscriptionList() {
        //获取订阅列表
        listSubscriptions();
    }

    @Override
    public void onError(int errorCode, int errorMsg) {

    }

    @Override
    public boolean isSub(Album album) {
        Album result = mData.get(album.getId());

        //不为空时，表示已经订阅
        return null != result;
    }

    @Override
    public void registerViewCallback(ISubscriptionCallback callback) {
        if (!mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    @Override
    public void unRegisterViewCallback(ISubscriptionCallback callback) {
        mCallbacks.remove(callback);
    }


    @Override
    public void onAddResult(final boolean isSuccess) {
        listSubscriptions();
        //添加订阅结果的回调
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback iSubscriptionCallback : mCallbacks) {
                    iSubscriptionCallback.onAddResult(isSuccess);
                }
            }
        });
    }

    @Override
    public void onDelResult(final boolean isSuccess) {
         listSubscriptions();
        //删除订阅的回调
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback iSubscriptionCallback : mCallbacks) {
                    iSubscriptionCallback.onDelResult(isSuccess);
                }
            }
        });
    }

    @Override
    public void onSubListLoaded(final List<Album> result) {
        mData.clear();
        //加载收藏订阅的回调
        if (result != null && 0 < result.size()) {
            for (Album album : result) {
                mData.put(album.getId(),album);
            }
        }

        //通知UI更新

        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback iSubscriptionCallback : mCallbacks) {
                    iSubscriptionCallback.onSubscriptionsLoaded(result);
                }
            }
        });
    }

}
