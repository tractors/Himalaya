package com.will.himalaya.util;

import android.support.v4.app.Fragment;

import com.will.himalaya.base.BaseFragment;
import com.will.himalaya.fragment.HistoryFragment;
import com.will.himalaya.fragment.RecommendFragment;
import com.will.himalaya.fragment.SubscriptionFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 创建fragment缓冲形式
 */
public class FragmentCreator {

    public final static int INDEX_RECOMMEND = 0;
    public final static int INDEX_SUBSCRIPTION = 1;
    public final static int INDEX_HISTORY = 2;
    public static final int PAGE_COUNT = 3;
    private static Map<Integer,BaseFragment> sCache = new HashMap<>();

    public static BaseFragment getFragment(int index){
        BaseFragment baseFragment = sCache.get(index);
        if (baseFragment != null){
            return baseFragment;
        }

        switch (index) {
            case INDEX_RECOMMEND:
                baseFragment = new RecommendFragment();
                break;
            case INDEX_SUBSCRIPTION:
                baseFragment = new SubscriptionFragment();
                break;
            case INDEX_HISTORY:
                baseFragment = new HistoryFragment();
                break;
            default:
                baseFragment = new RecommendFragment();
                break;

        }
        sCache.put(index,baseFragment);
        return baseFragment;
    }
}
