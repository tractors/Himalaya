package com.will.himalaya.fragment;

import android.app.Activity;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.will.himalaya.R;
import com.will.himalaya.activity.DetailActivity;
import com.will.himalaya.adapter.BaseAdapterT;
import com.will.himalaya.adapter.SubscriptionListAdapter;
import com.will.himalaya.base.BaseApplication;
import com.will.himalaya.base.BaseFragment;
import com.will.himalaya.interfaces.ISubscriptionCallback;
import com.will.himalaya.presenter.AlbumDetailPresenter;
import com.will.himalaya.presenter.SubscriptionPresenter;
import com.will.himalaya.util.Constant;
import com.will.himalaya.util.IntentActivity;
import com.will.himalaya.util.LogUtil;
import com.will.himalaya.wiget.ConfirmDialog;
import com.will.himalaya.wiget.UILoader;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 订阅列表页面
 */
public class SubscriptionFragment extends BaseFragment implements ISubscriptionCallback, UILoader.OnRetryClickListener, BaseAdapterT.OnItemClickListener<Album>, BaseAdapterT.OnItemLongClickListener<Album>,ConfirmDialog.OnDialogActionClickListener<Album> {

    private static final String TAG = "SubscriptionFragment";
    private UILoader mUiLoader;
    private SubscriptionPresenter mSubscriptionPresenter;
    private View mRootView;
    private RecyclerView mSubListView;
    private List<Album> mAlbumList = new ArrayList<>();
    private SubscriptionListAdapter mSubscriptionListAdapter;
    private TwinklingRefreshLayout mRefreshLayout;
    private Activity mContext;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = activity;
    }

    @Override
    protected View onSubViewLoaded(final LayoutInflater inflater, ViewGroup container) {

        if (mUiLoader == null) {

            mUiLoader = new UILoader(getContext()) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(inflater, container);
                }

            };

            if (mUiLoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
            }
            //设置数据为空的提示语
            mUiLoader.setEmptyTipsResId(R.string.empty_tips_sub);
        }

        initPresenter();
        initEvent();

        return mUiLoader;
    }

    /**
     * 设置点击事件
     */
    private void initEvent() {
        mUiLoader.setOnRetryClickListener(this);
        mSubscriptionListAdapter.setOnItemClickListener(this);
        mSubscriptionListAdapter.setOnItemLongClickListener(this);
    }

    /**
     * 初始化Presenter
     */
    private void initPresenter() {
        mSubscriptionPresenter = SubscriptionPresenter.getInstance();
        mSubscriptionPresenter.registerViewCallback(this);

        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        }

        mSubscriptionPresenter.getSubscriptionList();
    }

    //创建加载成功后都UI
    private View createSuccessView(LayoutInflater inflater, ViewGroup container) {
        mRootView = inflater.inflate(R.layout.fragment_subscription, container, false);

        mRefreshLayout = mRootView.findViewById(R.id.over_scroll_view);
        mRefreshLayout.setEnableRefresh(false);
        mRefreshLayout.setEnableLoadmore(false);
        mSubListView = mRootView.findViewById(R.id.sub_list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(container.getContext(), RecyclerView.VERTICAL, false);
        mSubListView.setLayoutManager(linearLayoutManager);

        RequestManager mRequestManager= Glide.with(this);
        mSubscriptionListAdapter = new SubscriptionListAdapter(mContext, mAlbumList,mRequestManager);
        mSubListView.setAdapter(mSubscriptionListAdapter);
        //设置item的间距
        mSubListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 2);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 2);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });
        return mRootView;
    }

    @Override
    public void onAddResult(boolean isSuccess) {

    }

    @Override
    public void onDelResult(boolean isSuccess) {

    }

    @Override
    public void onSubscriptionsLoaded(List<Album> result) {
        //更新ui
        if (result != null && 0 < result.size()) {
            if (mSubscriptionListAdapter != null) {
                mSubscriptionListAdapter.setData(result);
                if (mUiLoader != null) {
                    mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
                }
            }
        } else {
            if (mUiLoader != null) {

                mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
            }
        }
    }

    @Override
    public void onSubFull() {
        Toast.makeText(getContext(), "订阅数量不能超过" + Constant.MAX_SUB_COUNT, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRetryClick() {

    }


    @Override
    public void onItemClick(Album field, int position) {
        AlbumDetailPresenter.getInstance().setTargetAlbum(field);
        //item被点击
        IntentActivity.startActivity(getContext(), DetailActivity.class);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSubscriptionPresenter != null) {
            mSubscriptionPresenter.unRegisterViewCallback(this);
        }

        mSubscriptionListAdapter.setOnItemClickListener(null);
    }


    @Override
    public void onItemLongClick(Album field, int position) {
        //item长按点击事件
        Toast.makeText(BaseApplication.getAppContext(),""+position+",Album:"+field.getAlbumTitle(),Toast.LENGTH_SHORT).show();
        ConfirmDialog confirmDialog = new ConfirmDialog(getActivity());
        confirmDialog.setOnDialogActionClickListener(this,field);
        confirmDialog.show();
        //要在dialog展示后设置,否则会获取不到控件
        confirmDialog.setTips(getString(R.string.dialog_tips_content));
    }

    @Override
    public void onCancelSubClick(Album data) {
        //取消订阅事件
        if (mSubscriptionPresenter != null) {

            mSubscriptionPresenter.deleteSubscription(data);
        }
    }

    @Override
    public void onGiveUpClick() {
        //放弃操作事件
    }
}
