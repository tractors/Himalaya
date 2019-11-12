package com.will.himalaya.wiget;


import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.will.himalaya.R;
import com.will.himalaya.adapter.PlayListAdapter;
import com.will.himalaya.base.BaseApplication;
import com.will.himalaya.util.LogUtil;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.ArrayList;
import java.util.List;

public class SobPopwindow extends PopupWindow{


    private static final String TAG = "SobPopwindow";

    private ValueAnimator mEnterBgAnimator;
    private ValueAnimator mOutBgAnimator;
    //动画过度时间
    public final static long BG_ANIMATION_DURATION = 500;
    private final View mPopView;
    private TextView mCloseBtn;
    private RecyclerView mTrackListRv;

    private List<Track> mTrackList = new ArrayList<>();
    private PlayListAdapter mPlayListAdapter;
    private Context mContext;
    private TextView mPlayModeTv;
    private ImageView mPlayModeIv;
    private LinearLayout mPlayModeContainer;
    private PlayListPlayActionClickListener mPlayListPlayActionClickListener = null;
    private LinearLayout mOrderBtnContainer;
    private ImageView mOrderIcon;
    private TextView mOrderText;

    public SobPopwindow(Context context){

        //设置它的宽高
        super(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        this.mContext = context;
        //这里要注意：设置setOutsideTouchable之前，要先设置setBackgroundDrawable
        //否则点击外部无法生效
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setOutsideTouchable(true);
        //加载view
        mPopView = LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.pop_play_list,null);
        //设置内容
        setContentView(mPopView);

        setAnimationStyle(R.style.pop_animation);
        initView();
        initEvent();
    }

    private void initView() {
        mCloseBtn = mPopView.findViewById(R.id.play_list_close_btn);
        mTrackListRv = mPopView.findViewById(R.id.play_list_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(BaseApplication.getAppContext(),RecyclerView.VERTICAL,false);
        mTrackListRv.setLayoutManager(linearLayoutManager);

        mPlayListAdapter = new PlayListAdapter(BaseApplication.getAppContext(),mTrackList);
        mTrackListRv.setAdapter(mPlayListAdapter);
        //recycleView的分割线
        int topBottom = UIUtil.dip2px(BaseApplication.getAppContext(),1);
        mTrackListRv.addItemDecoration(new SobWinndowItemDecoration(10,10,topBottom,BaseApplication.getAppContext().getResources().getColor(R.color.light_gray)));
        //播放模式
        mPlayModeTv = mPopView.findViewById(R.id.play_list_play_mode_tv);
        mPlayModeIv = mPopView.findViewById(R.id.play_list_play_mode_iv);

        mPlayModeContainer = mPopView.findViewById(R.id.play_list_play_mode_container);

        //播放列表排序
        mOrderBtnContainer = mPopView.findViewById(R.id.play_list_order_container);

        mOrderIcon = mPopView.findViewById(R.id.play_list_order_iv);
        mOrderText = mPopView.findViewById(R.id.play_list_order_tv);
    }

    private void initEvent() {
        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mPlayModeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPlayListPlayActionClickListener != null) {
                    mPlayListPlayActionClickListener.onPlayModeClick();
                }
            }
        });

        mOrderBtnContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //切换播放列表排序
                if (mPlayListPlayActionClickListener != null) {
                    mPlayListPlayActionClickListener.onOrderClick();
                }
            }
        });
    }


    /**
     * 动画
     * @param act
     */
    public void initBgAnimation(final Activity act){
        //进入动画
        mEnterBgAnimator = ValueAnimator.ofFloat(1.0f,0.7f);
        mEnterBgAnimator.setDuration(BG_ANIMATION_DURATION);
        mEnterBgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                updateBgAlpha(act,value);
            }
        });

        //退出动画
        mOutBgAnimator = ValueAnimator.ofFloat(0.7f,1.0f);
        mOutBgAnimator.setDuration(BG_ANIMATION_DURATION);
        mOutBgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                updateBgAlpha(act,value);
            }
        });
    }


    /**
     * 设置透明度
     * @param act
     * @param alpha
     * @return
     */
    public void updateBgAlpha(Activity act, float alpha){
        Window window = act.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.alpha = alpha;
        window.setAttributes(attributes);
    }

    /**
     * 关闭视窗
     */
    public void dissmissClick(){
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                mOutBgAnimator.start();
            }
        });
    }

    /**
     * 打开视窗
     */
    public void openClick(){
        mEnterBgAnimator.start();
    }

    /**
     * 设置数据
     * @param tracks
     */
    public void setData(List<Track> tracks){
        if (mPlayListAdapter != null) {

            mPlayListAdapter.setData(tracks);
        }
    }

    //设置当前位置和显示在列表中当前可显示区域
    public void setCurrentPlayPosition(int position){
        mPlayListAdapter.setCurrentPlayPosition(position);
        mTrackListRv.scrollToPosition(position);
    }

    public void setPlayListItemClickListener(PlayListItemClickListener listener){
        mPlayListAdapter.setOnItemClickListener(listener);
    }

    /**
     * 更新播放模式
     * @param mode
     */
    public void updatePlayMode(PlayMode mode) {
        updatePlayModeBtnImg(mode);
    }

    /**
     * 是否更改排序方式
     * @param isOrder
     */
    public void updateOrderIcon(boolean isOrder){
        mOrderIcon.setImageResource(isOrder ? R.drawable.selector_player_mode_list_order : R.drawable.selector_player_mode_list_revers);
        mOrderText.setText(isOrder ? R.string.play_order_ascending : R.string.play_order_descending);
    }

    /**
     * 点击视窗播放列表的事件接口
     */
    public interface PlayListItemClickListener{
        void onItemClick(int position);
    }


    public void setPlayListPlayActionClickListener(PlayListPlayActionClickListener listener){
        this.mPlayListPlayActionClickListener = listener;
    }

    /**
     * 点击视窗播放模式的接口
     */
    public interface PlayListPlayActionClickListener {
        /**
         * 播放模式接口
         */
        void onPlayModeClick();

        /**
         * 播放列表排序接口
         */
        void onOrderClick();
    }

    /**
     * 根据当前的状态，更新播放模式图标
     * 默认的是：PLAY_MODEL_LIST
     * 列表循环：PLAY_MODEL_LIST_LOOP
     * 随机循环：PLAY_MODEL_RANDOM
     * 单曲循环：PLAY_MODEL_SINGLE_LOOP
     * @param currentMode
     */
    private void updatePlayModeBtnImg(PlayMode currentMode) {
        int resId = R.drawable.selector_player_mode_list_order;

        int textId = R.string.play_mode_order_text;
        LogUtil.d(TAG,"currentMode: "+ currentMode.name());
        switch (currentMode){
            case PLAY_MODEL_LIST:
                resId = R.drawable.selector_player_mode_list_order;
                textId = R.string.play_mode_order_text;
                break;
            case PLAY_MODEL_RANDOM:
                resId = R.drawable.selector_player_mode_random;
                textId = R.string.play_mode_random_text;
                break;
            case PLAY_MODEL_LIST_LOOP:
                resId = R.drawable.selector_player_mode_list_order_loop;
                textId = R.string.play_mode_list_play_text;
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                resId = R.drawable.selector_player_mode_single_loop;
                textId = R.string.play_mode_single_play_text;
                break;
        }

        mPlayModeIv.setImageResource(resId);
        mPlayModeTv.setText(textId);
    }
}
