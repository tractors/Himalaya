package com.will.himalaya.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.will.himalaya.R;
import com.will.himalaya.adapter.BaseAdapterT;
import com.will.himalaya.adapter.DetailListAdapter;
import com.will.himalaya.base.BaseActivity;
import com.will.himalaya.interfaces.IAlbumDetailViewCallback;
import com.will.himalaya.nativeclass.NativeHelper;
import com.will.himalaya.presenter.AlbumDetailPresenter;
import com.will.himalaya.presenter.PlayerPresenter;
import com.will.himalaya.util.IntentActivity;
import com.will.himalaya.wiget.DetailListItemDecoration;
import com.will.himalaya.wiget.UILoader;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends BaseActivity implements IAlbumDetailViewCallback, UILoader.OnRetryClickListener, BaseAdapterT.OnItemClickListenerList<Track> {

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
        mAlbumDetailPresenter = AlbumDetailPresenter.getInstance();

        mAlbumDetailPresenter.registerViewCallback(this);


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
        mAlbumAuthor = this.findViewById(R.id.tv_album_author);

    }

    private View createSuccessView(ViewGroup container) {
        View detailList = LayoutInflater.from(this).inflate(R.layout.item_detail_list,container,false);

        mDetailList = detailList.findViewById(R.id.album_detail_list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);

        mDetailList.setLayoutManager(linearLayoutManager);


        int left = UIUtil.dip2px(this,0);
        int right = UIUtil.dip2px(this,0);
        int topBottom = UIUtil.dip2px(this,2);


        mDetailList.addItemDecoration(new DetailListItemDecoration(left,right,topBottom,getResources().getColor(R.color.recyclerview_ground)));
        mDetailListAdapter = new DetailListAdapter(this,mTrackList);
        mDetailList.setAdapter(mDetailListAdapter);

        mDetailListAdapter.setOnItemClickListenerList(this);
        return detailList;
    }

    @Override
    public void onDetailListLoaded(List<Track> tracks) {
        //更新设置UI
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
    protected void onDestroy() {
        super.onDestroy();
        if (mAlbumDetailPresenter != null) {
            mAlbumDetailPresenter.unRegisterViewCallback(this);
        }


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
}
