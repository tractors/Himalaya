package com.will.himalaya;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.will.himalaya.adapter.IndicatorAdapter;
import com.will.himalaya.adapter.MainContentAdapter;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

public class MainActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";
    private MagicIndicator mMagicIndicator;
    private ViewPager mContentViewPager;
    private IndicatorAdapter mIndicatorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
    }

    private void initEvent() {
        mIndicatorAdapter.setOnIndicatorTabClickListener(new IndicatorAdapter.OnIndicatorTabClickListener() {
            @Override
            public void onTabClick(int index) {
                if (mContentViewPager != null) {
                    mContentViewPager.setCurrentItem(index);
                }
            }
        });
    }

    private void initView() {
        mMagicIndicator = findViewById(R.id.magic_indicator);
        mMagicIndicator.setBackgroundColor(this.getResources().getColor(R.color.main_color));

        String[] mTitles = this.getResources().getStringArray(R.array.indicator_title);

        mIndicatorAdapter = new IndicatorAdapter(mTitles,this);

        CommonNavigator commonNavigator = new CommonNavigator(this);

        commonNavigator.setAdjustMode(true);

        commonNavigator.setAdapter(mIndicatorAdapter);

        mContentViewPager = findViewById(R.id.content_viewpager);

        mMagicIndicator.setNavigator(commonNavigator);

        //设置viewpager的adapter
        FragmentManager fragmentManager = getSupportFragmentManager();

        MainContentAdapter contentAdapter = new MainContentAdapter(fragmentManager);

        mContentViewPager.setAdapter(contentAdapter);
        //绑定indicator和viewPager
        ViewPagerHelper.bind(mMagicIndicator,mContentViewPager);

    }
}
