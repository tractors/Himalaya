package com.will.himalaya.wiget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.will.himalaya.R;
import com.will.himalaya.base.BaseApplication;

public abstract class UILoader extends FrameLayout{

    private View mLoadingView;
    private View mSuccessView;
    private View mNetworkErrorView;
    private View mEmptyView;
    private OnRetryClickListener mOnRetryClickListener = null;
    private TextView mTipsView;

    public enum UIStatus{
        LOADING,SUCCESS,NETWORK_ERROR,EMPTY,NONE
    }

    private UIStatus mCurrentStatus = UIStatus.NONE;

    public UILoader(@NonNull Context context) {
        this(context,null);
    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void updateStatus(UIStatus status){
        mCurrentStatus = status;
        //在主线程更新ui
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                switchUIByCurrentStatus();
            }
        });
    }

    private void init() {
        switchUIByCurrentStatus();
    }

    private void switchUIByCurrentStatus() {
        //加载中
        if (null == mLoadingView) {

            mLoadingView = getLoadingView();
            addView(mLoadingView);
        }

        //根据状态设置是否可见
        mLoadingView.setVisibility(mCurrentStatus == UIStatus.LOADING ? VISIBLE : GONE);

        //成功
        if (mSuccessView == null) {
            mSuccessView = getSuccessView(this);
            addView(mSuccessView);
        }

        mSuccessView.setVisibility(mCurrentStatus == UIStatus.SUCCESS ? VISIBLE : GONE);

        //网络错误页面
        if (mNetworkErrorView == null) {
            mNetworkErrorView = getNetworkErrorView();
            addView(mNetworkErrorView);
        }

        mNetworkErrorView.setVisibility(mCurrentStatus == UIStatus.NETWORK_ERROR ? VISIBLE : GONE);

        //数据为空的页面
        if (mEmptyView == null) {
            mEmptyView = getEmptyView();
            addView(mEmptyView);
        }

        mEmptyView.setVisibility(mCurrentStatus == UIStatus.EMPTY ? VISIBLE : GONE);
    }

    protected View getEmptyView() {
        View emptyView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_empty_view,this,false);
        mTipsView = emptyView.findViewById(R.id.empty_view_tips_tv);
        return emptyView;
    }

    public void setEmptyTips(String tips){
        if (mTipsView != null) {
            mTipsView.setText(tips);
        }
    }

    public void setEmptyTipsResId(@StringRes int resId){
        if (mTipsView != null) {
            mTipsView.setText(resId);
        }
    }

    protected View getNetworkErrorView() {
        View networkErrorView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_newtwork_error_view,this,false);
        networkErrorView.findViewById(R.id.network_error_icon).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //重新获取数据
                if (mOnRetryClickListener != null) {
                    mOnRetryClickListener.onRetryClick();
                }
            }
        });
        return networkErrorView;
    }

    protected abstract View getSuccessView(ViewGroup container);

    protected View getLoadingView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_loading_view,this,false);
    }

    public void setOnRetryClickListener(OnRetryClickListener listener){
        this.mOnRetryClickListener = listener;
    }

    public interface OnRetryClickListener{
        void onRetryClick();
    }
}
