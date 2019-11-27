package com.will.himalaya.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.will.himalaya.R;
import com.will.himalaya.activity.DetailActivity;
import com.will.himalaya.adapter.BaseAdapterT;
import com.will.himalaya.adapter.RecommendListAdapter;
import com.will.himalaya.base.BaseFragment;
import com.will.himalaya.interfaces.IRecommendViewCallback;
import com.will.himalaya.presenter.AlbumDetailPresenter;
import com.will.himalaya.presenter.RecommendPresenter;
import com.will.himalaya.util.IntentActivity;
import com.will.himalaya.wiget.RItemDecoration;
import com.will.himalaya.wiget.UILoader;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 *推荐界面
 */
public class RecommendFragment extends BaseFragment implements IRecommendViewCallback, UILoader.OnRetryClickListener, BaseAdapterT.OnItemClickListener<Album> {

    private static final String TAG = "RecommendFragment";
    private View mRootView;
    private RecyclerView mRecommendRv;
    private List<Album> mAlbumList;
    private RecommendListAdapter mRecommendListAdapter;
    private RecommendPresenter mRecommendPresenter;
    private UILoader mUiLoader;

    @Override
    protected View onSubViewLoaded(final LayoutInflater inflater, ViewGroup container) {

        mUiLoader = new UILoader(getContext()) {
            @Override
            protected View getSuccessView(ViewGroup container) {
                return createSuccessView(inflater,container);
            }
        };


        mRecommendPresenter = RecommendPresenter.getInstance();

        mRecommendPresenter.registerViewCallback(this);

        mRecommendPresenter.getRecommendList();


        if (mUiLoader.getParent() instanceof ViewGroup) {
            ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
        }

        mUiLoader.setOnRetryClickListener(this);

        return mUiLoader;
    }

    private View createSuccessView(LayoutInflater inflater, ViewGroup container) {
        mRootView = inflater.inflate(R.layout.fragment_recommend,container,false);

        mRecommendRv = mRootView.findViewById(R.id.recommend_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        TwinklingRefreshLayout twinklingRefreshLayout = mRootView.findViewById(R.id.over_scroll_view);
        twinklingRefreshLayout.setPureScrollModeOn();

        mRecommendRv.setLayoutManager(linearLayoutManager);

        mRecommendRv.addItemDecoration(new RItemDecoration());
        RequestManager mRequestManager= Glide.with(this);
        mRecommendListAdapter = new RecommendListAdapter(getContext(),mAlbumList,mRequestManager);

        mRecommendRv.setAdapter(mRecommendListAdapter);

        mRecommendListAdapter.setOnItemClickListener(this);

        return mRootView;
    }


    @Override
    public void onRecommendListLoaded(List<Album> result) {
        //获取数据成功就会被调用加载数据
        mRecommendListAdapter.setData(result);
        mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
    }

    @Override
    public void onNetworkError() {
        mUiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
    }

    @Override
    public void onEmpty() {
        mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
    }

    @Override
    public void onLoading() {
        mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        //取消接口的注册
        if (mRecommendPresenter != null){
            mRecommendPresenter.unRegisterViewCallback(this);
        }

    }

    @Override
    public void onRetryClick() {
        //网络不佳时，用户点击了重试
        //重新获取数据
        if (mRecommendPresenter != null){
            mRecommendPresenter.getRecommendList();
        }
    }


    @Override
    public void onItemClick(Album field, int position) {
        AlbumDetailPresenter.getInstance().setTargetAlbum(field);
        //item被点击
        IntentActivity.startActivity(getContext(),DetailActivity.class);
    }
}
