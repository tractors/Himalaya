package com.will.himalaya.adapter;

import android.content.Context;
import android.graphics.Color;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.will.himalaya.R;
import com.will.himalaya.util.LogUtil;

import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;


public class IndicatorAdapter extends CommonNavigatorAdapter {

    private static final String TAG = "IndicatorAdapter";
    private String[] mTitles = null;
    private Context mContext;
    private OnIndicatorTabClickListener mOnTabClickListener;

    public IndicatorAdapter(@NonNull String[] titles, Context context) {
        if (null != titles && 0 !=titles.length){

            mTitles = new String[titles.length];
            mTitles = titles.clone();
        }

        this.mContext = context;
    }

    @Override
    public int getCount() {
        return (null == mTitles || 0 == mTitles.length) ? 0 : mTitles.length;
    }

    @Override
    public IPagerTitleView getTitleView(Context context, final int index) {

        //创建view
        ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);
        //设置一般情况下的颜色是灰色
        colorTransitionPagerTitleView.setNormalColor(Color.parseColor("#aaffffff"));
        //设置选中情况下的颜色为黑色
        colorTransitionPagerTitleView.setSelectedColor(Color.parseColor("#ffffff"));
        //单位sp
        colorTransitionPagerTitleView.setTextSize(18);
        //设置要显示的内容
        colorTransitionPagerTitleView.setText(mTitles[index]);
        //设置title的点击事件，这里的话，如果点击了text,那么选中下面的viewPage到对应的index里面去
        //也就是说，当我们点击了title的时候，下面的viewPager会对应着index进行切换内容.
        colorTransitionPagerTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //切换viewPager的内容，如果index不一样的话，
                if (mOnTabClickListener != null){
                    mOnTabClickListener.onTabClick(index);
                }
            }
        });
        //把这个创建好的view返回去
        return colorTransitionPagerTitleView;

//        CommonPagerTitleView commonPagerTitleView = new CommonPagerTitleView(mContext);
//        commonPagerTitleView.setContentView(R.layout.simple_pager_title_layout);
//
//
//        final TextView titleText = (TextView) commonPagerTitleView.findViewById(R.id.tv_title);
//        titleText.setText(mTitles[index]);
//        titleText.setTextSize(18);
//
//        commonPagerTitleView.setOnPagerTitleChangeListener(new CommonPagerTitleView.OnPagerTitleChangeListener() {
//            @Override
//            public void onSelected(int index, int totalCount) {
//                titleText.setTextColor(Color.parseColor("#ffffff"));
//            }
//
//            @Override
//            public void onDeselected(int index, int totalCount) {
//                titleText.setTextColor(Color.parseColor("#aaffffff"));
//            }
//
//            @Override
//            public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
//                titleText.setScaleX(1.3f + (0.8f - 1.3f) * leavePercent);
//                titleText.setScaleY(1.3f + (0.8f - 1.3f) * leavePercent);
//            }
//
//            @Override
//            public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
//                titleText.setScaleX(0.8f + (1.3f - 0.8f) * enterPercent);
//                titleText.setScaleY(0.8f + (1.3f - 0.8f) * enterPercent);
//            }
//        });
//
//        commonPagerTitleView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mOnTabClickListener != null){
//                    mOnTabClickListener.onTabClick(index);
//                }
//            }
//        });
//
//        //把这个创建好的view返回去
//        return commonPagerTitleView;

//        SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(mContext);
//        simplePagerTitleView.setText(mTitles[index]);
//        simplePagerTitleView.setTextSize(18);
//        simplePagerTitleView.setNormalColor(Color.parseColor("#aaffffff"));
//        simplePagerTitleView.setSelectedColor(Color.parseColor("#ffffff"));
//        simplePagerTitleView.setSelectedColor(Color.WHITE);
//        simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //切换viewPager的内容，如果index不一样的话，
//            }
//        });


        //把这个创建好的view返回去
//        return simplePagerTitleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        LinePagerIndicator indicator = new LinePagerIndicator(context);
        indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
        indicator.setColors(Color.parseColor("#ffffff"));
        return indicator;
    }

    public void setOnIndicatorTabClickListener(OnIndicatorTabClickListener listener){
        this.mOnTabClickListener = listener;
    }

    public interface OnIndicatorTabClickListener{
        void onTabClick(int index);
    }
}
