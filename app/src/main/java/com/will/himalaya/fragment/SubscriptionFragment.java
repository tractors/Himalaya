package com.will.himalaya.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.will.himalaya.R;
import com.will.himalaya.base.BaseFragment;

/**
 * 订阅页面
 */
public class SubscriptionFragment extends BaseFragment{

    @Override
    protected View onSubViewLoaded(LayoutInflater inflater, ViewGroup container) {
        View rootView  = inflater.inflate(R.layout.fragment_subscription,container,false);
        return rootView;
    }
}
