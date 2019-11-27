package com.will.himalaya.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.will.himalaya.R;
import com.will.himalaya.activity.PlayerActivity;
import com.will.himalaya.adapter.BaseAdapterT;
import com.will.himalaya.adapter.DetailListAdapter;
import com.will.himalaya.base.BaseFragment;
import com.will.himalaya.interfaces.IHistoryCallback;
import com.will.himalaya.presenter.HistoryPresenter;
import com.will.himalaya.presenter.PlayerPresenter;
import com.will.himalaya.util.IntentActivity;
import com.will.himalaya.wiget.ConfirmCheckBoxDialog;
import com.will.himalaya.wiget.DetailListItemDecoration;
import com.will.himalaya.wiget.UILoader;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 历史页面
 */
public class HistoryFragment extends BaseFragment implements IHistoryCallback, BaseAdapterT.OnItemClickListenerList<Track>,BaseAdapterT.OnItemLongClickListener<Track>,ConfirmCheckBoxDialog.OnDialogClickListener<Track> {

    private UILoader mUiLoader;
    private List<Track>  mTrackList = new ArrayList<>();
    private DetailListAdapter mDetailListAdapter;
    private HistoryPresenter mHistoryPresenter;

    @Override
    protected View onSubViewLoaded(final LayoutInflater inflater, ViewGroup container) {
        FrameLayout rootView  = (FrameLayout) inflater.inflate(R.layout.fragment_history,container,false);
        if (mUiLoader == null) {

            mUiLoader = new UILoader(getContext()) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(inflater,container);
                }
            };
        } else {
            if (mUiLoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
            }
        }

        mUiLoader.setEmptyTipsResId(R.string.empty_tips_history_text);

        //初始化Presenter
        initPresenter();

        rootView.addView(mUiLoader);
        initEvent();
        return rootView;
    }

    private void initPresenter() {
        //历史记录的presenter
        mHistoryPresenter = HistoryPresenter.getInstance();
        mHistoryPresenter.registerViewCallback(this);
        mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        mHistoryPresenter.listHistories();
    }

    private void initEvent() {
        mDetailListAdapter.setOnItemClickListenerList(this);
        mDetailListAdapter.setOnItemLongClickListener(this);
    }

    private View createSuccessView(LayoutInflater inflater, ViewGroup container) {
        View successView = inflater.inflate(R.layout.item_history, container, false);
        //recyclerView
        RecyclerView historyListView = successView.findViewById(R.id.history_list);
        TwinklingRefreshLayout refreshLayout = successView.findViewById(R.id.over_scroll_view);

        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setEnableRefresh(false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        historyListView.setLayoutManager(linearLayoutManager);

        int left = UIUtil.dip2px(getContext(),0);
        int right = UIUtil.dip2px(getContext(),0);
        int topBottom = UIUtil.dip2px(getContext(),2);


        historyListView.addItemDecoration(new DetailListItemDecoration(left,right,topBottom,getResources().getColor(R.color.recycler_back_ground_color)));
        mDetailListAdapter = new DetailListAdapter(getContext(),mTrackList,null);

        historyListView.setAdapter(mDetailListAdapter);

        return successView;
    }

    @Override
    public void onHistoriesLoaded(List<Track> tracks) {
        if (tracks == null || 0 == tracks.size()) {
            mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
        } else {
            mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
            mDetailListAdapter.setData(tracks);
        }


    }

    @Override
    public void onItemClickList(List<Track> field, int position) {
        PlayerPresenter playerPresenter = PlayerPresenter.getPlayerPresenter();

        playerPresenter.setPlayList(field,position);

        IntentActivity.startActivity(getContext(),PlayerActivity.class);
    }

    @Override
    public void onItemLongClick(Track field, int position) {
        ConfirmCheckBoxDialog dialog = new ConfirmCheckBoxDialog(getContext());
        dialog.setOnDialogClickListener(this,field);
        dialog.show();
    }

    @Override
    public void onCancelSubClick() {

    }

    @Override
    public void onConfirmClick(Track data,boolean isChecked) {
        if (mHistoryPresenter != null && data != null) {
            if (isChecked) {
                mHistoryPresenter.cleanHistories();
            } else {
                mHistoryPresenter.delHistory(data);
            }
        }
    }
}
