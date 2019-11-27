package com.will.himalaya.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.bezierlayout.BezierLayout;
import com.will.himalaya.R;
import com.will.himalaya.adapter.BaseAdapterT;
import com.will.himalaya.adapter.DetailListAdapter;
import com.will.himalaya.base.BaseActivity;
import com.will.himalaya.interfaces.IAlbumDetailViewCallback;
import com.will.himalaya.interfaces.IPlayerViewCallback;
import com.will.himalaya.interfaces.ISubscriptionCallback;
import com.will.himalaya.nativeclass.NativeHelper;
import com.will.himalaya.presenter.AlbumDetailPresenter;
import com.will.himalaya.presenter.PlayerPresenter;
import com.will.himalaya.presenter.SubscriptionPresenter;
import com.will.himalaya.util.Constant;
import com.will.himalaya.util.IntentActivity;
import com.will.himalaya.util.LogUtil;
import com.will.himalaya.wiget.DetailListItemDecoration;
import com.will.himalaya.wiget.UILoader;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 详情列表模块
 */
public class DetailActivity extends BaseActivity implements IAlbumDetailViewCallback, UILoader.OnRetryClickListener, BaseAdapterT.OnItemClickListenerList<Track>,IPlayerViewCallback, ISubscriptionCallback {

    private static final String TAG = "DetailActivity";
    private ImageView mLargeCover;
    private ImageView mSmallCover;
    private TextView mAlbumTitle;
    private TextView mAlbumAuthor;
    private AlbumDetailPresenter mAlbumDetailPresenter;
    private int mCurrentPage = 1;
    private RecyclerView mDetailList;

    private List<Track> mTrackList = new ArrayList<>();
    private DetailListAdapter mDetailListAdapter;
    private FrameLayout mDetailListContainer;
    private UILoader mUiLoader;
    private long mCurrentId = -1;
    private ImageView mPlayControlBtn;
    private TextView mPlayControlTips;
    private PlayerPresenter mPlayerPresenter;
    private LinearLayout mPlayStatusContainer;
    //初始化当前专辑详情，为播放器没有列表数据做准备
    private List<Track> mCurrentTracks = null;
    //默认播放下标为0
    private static final int DEFAULT_PLAY_INDEX = 0;
    private TwinklingRefreshLayout mDetailRefreshLayout;
    private String mCurrentTrackTitle = null;
    private TextView mSubBtn;
    private SubscriptionPresenter mSubscriptionPresenter;
    private Album mCurrentAlbum = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        initView();
        initPresenter();

        //获取订阅按钮的状态并更新UI
        updateSubState();
        //获取播放状态并更新UI
        updatePlayStatus(mPlayerPresenter.isPlaying());
        initListener();

    }

    /**
     * 获取订阅按钮的状态并更新UI
     */
    private void updateSubState() {
        if (mSubscriptionPresenter != null) {
            boolean isSub = mSubscriptionPresenter.isSub(mCurrentAlbum);
            mSubBtn.setText(isSub ? R.string.cancel_sub_tips_text : R.string.sub_tips_text);
        }
    }

    /**
     * 初始化Presenter
     */
    private void initPresenter() {
        //专辑详情的presenter
        mAlbumDetailPresenter = AlbumDetailPresenter.getInstance();

        mAlbumDetailPresenter.registerViewCallback(this);
        //播放器的presenter
        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);
        //订阅相关的presenter
        mSubscriptionPresenter = SubscriptionPresenter.getInstance();
        //每次进入时都去获取订阅列表，这样在第一次时，数据才会准确
        mSubscriptionPresenter.getSubscriptionList();

        mSubscriptionPresenter.registerViewCallback(this);

    }

    private void initView() {

        mDetailListContainer = this.findViewById(R.id.detail_list_container);

        if (null == mUiLoader) {

            mUiLoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }
            };

            mDetailListContainer.removeAllViews();
            mDetailListContainer.addView(mUiLoader);

            mUiLoader.setOnRetryClickListener(this);
        }
        mLargeCover = this.findViewById(R.id.iv_large_cover);
        mSmallCover = this.findViewById(R.id.iv_small_cover);
        mAlbumTitle = this.findViewById(R.id.tv_album_title);
        mAlbumTitle.setSelected(true);
        mAlbumAuthor = this.findViewById(R.id.tv_album_author);

        //播放控制的图标
        mPlayControlBtn = this.findViewById(R.id.detail_play_control);
        mPlayControlTips = this.findViewById(R.id.play_control_tv);
        mPlayControlTips.setSelected(true);

        mPlayStatusContainer = this.findViewById(R.id.detail_play_status_container);
        //订阅相关的
        mSubBtn = this.findViewById(R.id.detail_sub_btn);

    }

    private void initListener() {

            mPlayStatusContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //判断播放器是否有播放列表
                    if (mPlayerPresenter != null) {
                        boolean has = mPlayerPresenter.hasPlayList();
                        if (has) {
                            handlePlayControl();
                        } else {
                            handleNoPlayList();
                        }
                    }

                }
            });

            mSubBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mSubscriptionPresenter != null) {
                        boolean isSub = mSubscriptionPresenter.isSub(mCurrentAlbum);
                        //如果已经订阅，就取消订阅，反之，就添加订阅
                        if (isSub) {
                            mSubscriptionPresenter.deleteSubscription(mCurrentAlbum);
                        } else {
                            mSubscriptionPresenter.addSubscription(mCurrentAlbum);
                        }
                    }
                }
            });

    }

    /**
     * 播放器中没有播放列表,就要进行处理
     */
    private void handleNoPlayList() {
        mPlayerPresenter.setPlayList(mCurrentTracks,DEFAULT_PLAY_INDEX);
    }

    /**
     * 设置播放控制
     */
    private void handlePlayControl() {
        if (mPlayerPresenter.isPlaying()) {
            //正在播放，那么就暂停
            mPlayerPresenter.pause();
        } else {
            //暂停播放，就播放
            mPlayerPresenter.play();
        }
    }

    //是否是加载更多，标记，默认是false
    private boolean mIsLoadedMore = false;

    private View createSuccessView(ViewGroup container) {
        View detailListView = LayoutInflater.from(this).inflate(R.layout.item_detail_list,container,false);

        mDetailList = detailListView.findViewById(R.id.album_detail_list);

        mDetailRefreshLayout = detailListView.findViewById(R.id.detail_refresh_layout);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);

        mDetailList.setLayoutManager(linearLayoutManager);


        int left = UIUtil.dip2px(this,0);
        int right = UIUtil.dip2px(this,0);
        int topBottom = UIUtil.dip2px(this,2);


        mDetailList.addItemDecoration(new DetailListItemDecoration(left,right,topBottom,getResources().getColor(R.color.recycler_back_ground_color)));
        mDetailListAdapter = new DetailListAdapter(this,mTrackList,null);
        mDetailList.setAdapter(mDetailListAdapter);

        mDetailListAdapter.setOnItemClickListenerList(this);
        //刷新空间监听
        BezierLayout bezierLayout = new BezierLayout(this);
        mDetailRefreshLayout.setHeaderView(bezierLayout);
        mDetailRefreshLayout.setMaxHeadHeight(140);
        mDetailRefreshLayout.setOverScrollBottomShow(false);
        mDetailRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                if (mAlbumDetailPresenter != null) {
                    mAlbumDetailPresenter.loadMore();
                    mIsLoadedMore = true;
                }
            }
        });
        return detailListView;
    }

    @Override
    public void onDetailListLoaded(List<Track> tracks) {
        if (mIsLoadedMore && mDetailRefreshLayout != null) {
            mIsLoadedMore = false;
            mDetailRefreshLayout.finishLoadmore();
        }
        this.mCurrentTracks = tracks;
        //判断数据结果，根据数据结果更新设置UI
        if (null == tracks || 0 == tracks.size()) {
            if (null != mUiLoader) {
                mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
            }
        }

        if (null != tracks) {
            if (null != mUiLoader) {
                mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
            }

            mDetailListAdapter.setData(tracks);
        }


    }

    @Override
    public void onAlbumLoaded(Album album) {
        this.mCurrentAlbum = album;
        long id = album.getId();
        mCurrentId = id;
        //获取专辑的详情数据
        if (null != mAlbumDetailPresenter) {

            mAlbumDetailPresenter.getAlbumDetail(id,mCurrentPage);
        }

        if (null != mUiLoader) {
            mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        }
        if (album != null) {
            mAlbumTitle.setText(album.getAlbumTitle());
        }

        if (null != mAlbumAuthor) {
            mAlbumAuthor.setText(album.getAnnouncer().getNickname());
        }

        //做毛玻璃效果
        if (mLargeCover != null) {

            Glide.with(this).asBitmap().load(album.getCoverUrlLarge()).into(new SimpleTarget<Bitmap>(){
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    Bitmap bm = NativeHelper.blurNatively(resource,5,true);
                    if (null != bm){
                        mLargeCover.setImageBitmap(bm);
                    } else {
                        mLargeCover.setImageBitmap(resource);
                    }
                }
            });
        }

        if (null != mSmallCover) {
            Glide.with(this).load(album.getCoverUrlSmall()).into(mSmallCover);
        }


    }

    @Override
    public void onNetworkError(int errorCode, String errorMsg) {
        //请求发生错误,网络错误
        if (null != mUiLoader) {
            mUiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
        }
    }

    @Override
    public void onLoaderMoreFinished(boolean isOkay) {

            Toast.makeText(this,isOkay ? "加载成功" : "没有更多的数据",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRefreshFinished(boolean isOkay) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAlbumDetailPresenter != null) {
            mAlbumDetailPresenter.unRegisterViewCallback(this);
        }
        mAlbumDetailPresenter = null;

        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegisterViewCallback(this);
        }
        mPlayerPresenter = null;

        if (mSubscriptionPresenter != null) {
            mSubscriptionPresenter.unRegisterViewCallback(this);
        }

        mSubscriptionPresenter = null;
    }

    @Override
    public void onRetryClick() {
        //用户网络不佳时，点击重新加载
        if (null != mAlbumDetailPresenter) {

            mAlbumDetailPresenter.getAlbumDetail(mCurrentId,mCurrentPage);
            if (null != mUiLoader) {
                mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
            }
        }


    }


    @Override
    public void onItemClickList(List<Track> field, int position) {

        PlayerPresenter playerPresenter = PlayerPresenter.getPlayerPresenter();

        playerPresenter.setPlayList(field,position);

        IntentActivity.startActivity(this,PlayerActivity.class);
    }

    @Override
    public void onPlayStart() {
        //修改为暂停的状态，文字修改正在播放
        updatePlayStatus(true);
    }

    @Override
    public void onPlayPause() {
        //修改成播放图标，文字修改成暂停播放
        updatePlayStatus(false);
    }

    @Override
    public void onPlayStop() {
       updatePlayStatus(false);
    }

    /**
     * 根据播放状态修改播放图标和文字
     * @param isPlaying
     */
   private void updatePlayStatus(boolean isPlaying){
           //修改成播放图标，文字修改成暂停播放
           if (mPlayControlBtn != null && mPlayControlTips != null) {
               mPlayControlBtn.setImageResource(isPlaying? R.drawable.selector_play_control_pause : R.drawable.selector_play_control_play);
               if (!isPlaying) {
                   mPlayControlTips.setText(R.string.click_play_tips_text);
               } else {
                   if (!TextUtils.isEmpty(mCurrentTrackTitle)) {
                       mPlayControlTips.setText(mCurrentTrackTitle);
                   }
               }
           }
    }

    @Override
    public void onPlayError() {

    }

    @Override
    public void onNextPlay(Track track) {

    }

    @Override
    public void onPrePlay(Track track) {

    }

    @Override
    public void onListLoaded(List<Track> list) {

    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode mode) {

    }

    @Override
    public void onProgressChange(int currentProgress, int total) {

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinished() {

    }

    @Override
    public void onTrackUpdate(Track track, int playIndex) {
       //更新歌曲标题
        if (track != null) {
            mCurrentTrackTitle = track.getTrackTitle();
            if (!TextUtils.isEmpty(mCurrentTrackTitle) && mPlayControlTips != null) {
                mPlayControlTips.setText(mCurrentTrackTitle);
            }
        }
    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }


    @Override
    public void onAddResult(boolean isSuccess) {
       if (isSuccess){
           //如果添加成功，UI就修改成取消订阅
           mSubBtn.setText(R.string.cancel_sub_tips_text);
       }

       //是否订阅成功
        String tipsText = isSuccess ? "订阅成功" : "订阅失败";
       Toast.makeText(this,tipsText,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDelResult(boolean isSuccess) {
        if (isSuccess) {
            //如果删除成功,UI修改成订阅
            mSubBtn.setText(R.string.sub_tips_text);
        }

        //是否删除成功
        String tipsText = isSuccess ? "删除成功" : "删除失败";
        Toast.makeText(this,tipsText,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSubscriptionsLoaded(List<Album> result) {
        for (Album album : result) {
            LogUtil.d(TAG,"album : ----->" + album.getAlbumTitle());
        }
    }

    @Override
    public void onSubFull() {
        Toast.makeText(this,"订阅数量不能超过"+ Constant.MAX_SUB_COUNT,Toast.LENGTH_SHORT).show();
    }
}
