package com.will.himalaya.fragment;

import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.will.himalaya.R;
import com.will.himalaya.adapter.RecommendListAdapter;
import com.will.himalaya.base.BaseFragment;
import com.will.himalaya.interfaces.IRecommendViewCallback;
import com.will.himalaya.presenter.RecommendPresenter;
import com.will.himalaya.util.Constant;
import com.will.himalaya.util.LogUtil;
import com.will.himalaya.wiget.RItemDecoration;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *推荐界面
 */
public class RecommendFragment extends BaseFragment implements IRecommendViewCallback {

    private static final String TAG = "RecommendFragment";
    private View mRootView;
    private RecyclerView mRecommendRv;
    private List<Album> mAlbumList;
    private RecommendListAdapter mRecommendListAdapter;
    private RecommendPresenter mRecommendPresenter;

    @Override
    protected View onSubViewLoaded(LayoutInflater inflater, ViewGroup container) {
        mRootView = inflater.inflate(R.layout.fragment_recommend,container,false);

        mRecommendRv = mRootView.findViewById(R.id.recommend_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecommendRv.setLayoutManager(linearLayoutManager);

        mRecommendRv.addItemDecoration(new RItemDecoration());

        mRecommendListAdapter = new RecommendListAdapter(getContext(),mAlbumList);

        mRecommendRv.setAdapter(mRecommendListAdapter);

        mRecommendPresenter = RecommendPresenter.getInstance();

        mRecommendPresenter.registerViewCallback(this);
        mRecommendPresenter.getRecommendList();
        return mRootView;
    }



    private void upRecommendUI(List<Album> albumList) {
        mRecommendListAdapter.setData(albumList);
    }

    @Override
    public void onRecommendListLoaded(List<Album> result) {
        //获取数据成功就会被调用加载数据
        mRecommendListAdapter.setData(result);
    }

    @Override
    public void onLoadMore(List<Album> result) {

    }

    @Override
    public void onRefreshMore(List<Album> result) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //取消接口的注册
        if (mRecommendPresenter != null){
            mRecommendPresenter.unRegisterViewCallback(this);
        }

    }
}
