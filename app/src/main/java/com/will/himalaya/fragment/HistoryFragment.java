package com.will.himalaya.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.will.himalaya.R;
import com.will.himalaya.base.BaseFragment;

/**
 * 历史页面
 */
public class HistoryFragment extends BaseFragment{

    @Override
    protected View onSubViewLoaded(LayoutInflater inflater, ViewGroup container) {
        View rootView  = inflater.inflate(R.layout.fragment_history,container,false);
        return rootView;
    }
}
