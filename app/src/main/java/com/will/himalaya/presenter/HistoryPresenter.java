package com.will.himalaya.presenter;

import com.will.himalaya.api.data.HistoryDao;
import com.will.himalaya.api.data.IHistoryDao;
import com.will.himalaya.api.data.IHistoryDaoCallback;
import com.will.himalaya.base.BaseApplication;
import com.will.himalaya.interfaces.IHistoryCallback;
import com.will.himalaya.interfaces.IHistoryPresenter;
import com.will.himalaya.util.Constant;
import com.will.himalaya.util.LogUtil;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * 历史记录模块的Presenter层,历史记录最多是100条,若超过，就删除最早的记录，并把当前的加入
 */
public class HistoryPresenter implements IHistoryPresenter, IHistoryDaoCallback {

    private static final String TAG = "HistoryPresenter";
    private List<IHistoryCallback> mCallbacks = new ArrayList<>();
    private final IHistoryDao mHistoryDao;
    //当前获取的历史记录的数据集合
    private List<Track> mCurrentHistories = null;
    //当前要添加的对象
    private Track mCurrentAddTrack = null;
    //大于100条历史记录操作的标记
    private boolean isDoDelAsOutOfSize = false;

    private HistoryPresenter(){
        mHistoryDao = HistoryDao.getInstance();
        mHistoryDao.setCallback(this);
        listHistories();
    }



    private static class Holder{
        private static final HistoryPresenter outInstance = new HistoryPresenter();
    }

    public static HistoryPresenter getInstance(){
        return Holder.outInstance;
    }

    @Override
    public void listHistories() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if (mHistoryDao != null) {
                    mHistoryDao.listHistories();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }


    @Override
    public void addHistory(final Track track) {
        //判断是否 >=100条
        if (mCurrentHistories != null && Constant.MAX_HISTORY_COUNT <= mCurrentHistories.size()) {
            //删除最早的记录，再添加
            delHistory(mCurrentHistories.get(mCurrentHistories.size()-1));
            isDoDelAsOutOfSize = true;
            this.mCurrentAddTrack = track;
        }
        doAddHistory(track);
    }

    /**
     * 添加数据
     * @param track
     */
    private void doAddHistory(final Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if (mHistoryDao != null) {
                    mHistoryDao.addHistory(track);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void delHistory(final Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if (mHistoryDao != null) {
                    mHistoryDao.delHistory(track);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void cleanHistories() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if (mHistoryDao != null) {
                    mHistoryDao.clearHistory();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void registerViewCallback(IHistoryCallback callback) {
        if (!mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    @Override
    public void unRegisterViewCallback(IHistoryCallback callback) {
        if (mCallbacks != null) {
            mCallbacks.remove(callback);
        }
    }

    //===============historyDao实现接口===================

    @Override
    public void onHistoryAdd(boolean isSuccess) {
        listHistories();
    }

    @Override
    public void onHistoryDel(boolean isSuccess) {
        if (isDoDelAsOutOfSize && mCurrentAddTrack != null) {
            addHistory(mCurrentAddTrack);
            isDoDelAsOutOfSize = false;
        } else {

            listHistories();
        }
    }

    @Override
    public void onHistoriesLoaded(final List<Track> tracks) {

        this.mCurrentHistories = tracks;
        LogUtil.d(TAG,"onHistoriesLoaded: tracks ----->"+tracks.size());

        //通知UI更新数据
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (IHistoryCallback iHistoryCallback : mCallbacks) {
                    iHistoryCallback.onHistoriesLoaded(tracks);
                }
            }
        });
    }

    @Override
    public void onHistoriesClean(boolean isSuccess) {
        listHistories();
    }
}
