package com.will.himalaya.activity;

import android.annotation.SuppressLint;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.will.himalaya.R;
import com.will.himalaya.adapter.PlayerTrackPagerAdapter;
import com.will.himalaya.interfaces.IPlayerViewCallback;
import com.will.himalaya.presenter.PlayerPresenter;
import com.will.himalaya.util.LogUtil;
import com.will.himalaya.util.TimeUtil;
import com.will.himalaya.wiget.SobPopwindow;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.will.himalaya.presenter.PlayerPresenter.PLAY_LIST_IS_REVERSE_KEY;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

public class PlayerActivity extends AppCompatActivity implements IPlayerViewCallback {

    private static final String TAG = "PlayerActivity";
    private ImageView mControlBtn;
    private PlayerPresenter mPlayerPresenter;
    private TextView mTotalDuration;
    private TextView mCurrentPosition;
    private SeekBar mDurationBar;
    private int mCurrentProgress = 0;
    private boolean mIsUserTouchProgressBar = false;
    private ImageView mPlayNextBtn;
    private ImageView mPlayPreviousBtn;
    private TextView mTrackTitleTv;
    private String mTrackTitleText;
    private ViewPager mTrackPageView;
    private PlayerTrackPagerAdapter mTrackPagerAdapter;

    //标记是否是用户滑动，默认是false
    private boolean mIsUserSlidePager =false;
    private ImageView mPlayModeSwitchBtn;

    //默认当前播放模式
    private PlayMode mCurrentMode = PlayMode.PLAY_MODEL_LIST;
    //播放模式集合
    private static Map<PlayMode,PlayMode> sPlayModeRule = new HashMap<>();

    //处理播放模式的切换
    //1、默认的是：PLAY_MODEL_LIST
    //2、列表循环：PLAY_MODEL_LIST_LOOP
    //2、随机循环：PLAY_MODEL_RANDOM
    //4、单曲循环：PLAY_MODEL_SINGLE_LOOP
    static {
        sPlayModeRule.put(PLAY_MODEL_LIST, PLAY_MODEL_LIST_LOOP);
        sPlayModeRule.put(PLAY_MODEL_LIST_LOOP, PLAY_MODEL_RANDOM);
        sPlayModeRule.put(PLAY_MODEL_RANDOM, PLAY_MODEL_SINGLE_LOOP);
        sPlayModeRule.put(PLAY_MODEL_SINGLE_LOOP, PLAY_MODEL_LIST);
    }

    private ImageView mPlayListBtn;
    private SobPopwindow mSobPopwindow;
    //播放列表是否反转，默认是false
    public boolean isOrder = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
//        PlayerPresenter playerPresenter = PlayerPresenter.getPlayerPresenter();
//        playerPresenter.play();
        initView();
        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);
        mPlayerPresenter.getPlayList();
        initEvent();

    }


    @SuppressLint("ClickableViewAccessibility")
    private void initEvent() {
        mControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //如果现在的状态是正在播放，那么就暂停
                if (mPlayerPresenter.isPlay()) {
                    mPlayerPresenter.pause();
                } else {
                    //如果现在的状态是非播放状态，那么就播放
                    mPlayerPresenter.play();
                }
            }
        });

        mDurationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean isFromUser) {
                if (isFromUser) {
                    mCurrentProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsUserTouchProgressBar = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mIsUserTouchProgressBar = false;
                if (null != mPlayerPresenter) {
                    mPlayerPresenter.seekTo(mCurrentProgress);
                }
            }
        });

        mPlayNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mPlayerPresenter) {
                    mPlayerPresenter.playNext();
                }
            }
        });

        mPlayPreviousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mPlayerPresenter) {
                    mPlayerPresenter.playPre();
                }
            }
        });

        mTrackPageView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                 //当页面选中时，就去切换播放当前内容

                if (mPlayerPresenter != null && mIsUserSlidePager) {
                    mPlayerPresenter.playByIndex(position);
                }
                mIsUserSlidePager = false;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //viewPager滑动监听
        mTrackPageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        mIsUserSlidePager = true;
                        break;
                }
                return false;
            }
        });
        //处理播放模式
        mPlayModeSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchPlayMode();

            }
        });

        //点击视窗事件
        mPlayListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSobPopwindow.showAtLocation(view, Gravity.BOTTOM,0,0);
                mSobPopwindow.openClick();
            }
        });

        //关闭视窗事件
        mSobPopwindow.dissmissClick();

        mSobPopwindow.setPlayListItemClickListener(new SobPopwindow.PlayListItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playByIndex(position);
                }
            }
        });

        mSobPopwindow.setPlayListPlayActionClickListener(new SobPopwindow.PlayListPlayActionClickListener() {
            @Override
            public void onPlayModeClick() {
                switchPlayMode();
            }

            @Override
            public void onOrderClick() {
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.reversePlayList();
                }
            }
        });
    }

    private void switchPlayMode() {
        PlayMode playMode = sPlayModeRule.get(mCurrentMode);

        if (mPlayerPresenter != null) {
            mPlayerPresenter.switchPlayMode(playMode);

        }
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
        LogUtil.d(TAG,"currentMode: "+ currentMode.name());
        switch (currentMode){
            case PLAY_MODEL_LIST:
                resId = R.drawable.selector_player_mode_list_order;
                break;
            case PLAY_MODEL_RANDOM:
                resId = R.drawable.selector_player_mode_random;
                break;
            case PLAY_MODEL_LIST_LOOP:
                resId = R.drawable.selector_player_mode_list_order_loop;
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                resId = R.drawable.selector_player_mode_single_loop;
                break;
        }

        mPlayModeSwitchBtn.setImageResource(resId);
    }

    private void initView() {
        mControlBtn = this.findViewById(R.id.play_or_pause_btn);
        mTotalDuration = this.findViewById(R.id.track_duration);
        mCurrentPosition = this.findViewById(R.id.current_position);
        mDurationBar = this.findViewById(R.id.track_seek_bar);

        mPlayNextBtn = this.findViewById(R.id.play_next);
        mPlayPreviousBtn = this.findViewById(R.id.play_previous);

        mTrackTitleTv = this.findViewById(R.id.track_title);
        mTrackTitleTv.setSelected(true);

        if (!TextUtils.isEmpty(mTrackTitleText)) {
            mTrackTitleTv.setText(mTrackTitleText);
        }

        mTrackPageView = this.findViewById(R.id.track_pager_view);

        mTrackPagerAdapter = new PlayerTrackPagerAdapter();

        mTrackPageView.setAdapter(mTrackPagerAdapter);

        mPlayModeSwitchBtn = this.findViewById(R.id.player_mode_switch_btn);

        mSobPopwindow = new SobPopwindow(this);
        mSobPopwindow.initBgAnimation(PlayerActivity.this);
        mPlayListBtn = this.findViewById(R.id.player_list);

        //：获取textview的焦点
        //mTrackTitleTv.setFocusable(true);
        //mTrackTitleTv.setFocusableInTouchMode(true);
        //mTrackTitleTv.requestFocus();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mPlayerPresenter) {
            mPlayerPresenter.unRegisterViewCallback(this);
        }

        mPlayerPresenter = null;
    }

    @Override
    public void onPlayStart() {
        //开始播放，修改ui成暂停的按钮
        if (null != mControlBtn) {
            mControlBtn.setImageResource(R.drawable.selector_player_stop);
        }
    }

    @Override
    public void onPlayPause() {
        if (null != mControlBtn) {
            mControlBtn.setImageResource(R.drawable.selector_player_play);
        }
    }

    @Override
    public void onPlayStop() {
        if (null != mControlBtn) {
            mControlBtn.setImageResource(R.drawable.selector_player_play);
        }
    }

    @Override
    public void onPlayError() {

    }

    @Override
    public void onNextPlay(Track track) {

    }

    @Override
    public void onPrePlay(Track track) {

    }

    @Override
    public void onListLoaded(List<Track> list) {
        //把数据设置到适配器中
        if (mTrackPagerAdapter != null) {
            mTrackPagerAdapter.setData(list);
        }
        //也要给播放列表一份数据
        if (mSobPopwindow != null) {
            mSobPopwindow.setData(list);
        }
    }

    @Override
    public void onPlayModeChange(PlayMode mode) {
        this.mCurrentMode = mode;
        //更新播放模式
        updatePlayModeBtnImg(mode);
        //同时更新Popwindow中的播放模式
        mSobPopwindow.updatePlayMode(mCurrentMode);
    }

    @Override
    public void onProgressChange(int currentProgress, int total) {
        mDurationBar.setMax(total);
        String totalDuration = null;
        String currentPosition = null;
        if (total > 1000 * 60 * 60) {
            totalDuration = TimeUtil.getHourFormat(total);
            currentPosition = TimeUtil.getHourFormat(currentProgress);
        } else {
            totalDuration = TimeUtil.getMinFormat(total);
            currentPosition = TimeUtil.getMinFormat(currentProgress);
        }

        if (null != mTotalDuration) {

            mTotalDuration.setText(totalDuration);
        }

        if (null != mCurrentPosition) {
            mCurrentPosition.setText(currentPosition);
        }

        //更新进度
//        int percent = (int) (currentProgress * 1.0f / total * 100);

        if (!mIsUserTouchProgressBar) {
            mDurationBar.setProgress(currentProgress);
        }
    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinished() {

    }

    @Override
    public void onTrackUpdate(Track track,int playIndex) {
        this.mTrackTitleText = track.getTrackTitle();
        if (mTrackTitleTv != null) {
            LogUtil.d(TAG,"title:" + mTrackTitleText);
            //设置当前节目的标题
            mTrackTitleTv.setText(mTrackTitleText);
        }
        //当节目改变的时候，我们就获取到当前播放中播放到位置
        if (mTrackPageView != null) {
            mTrackPageView.setCurrentItem(playIndex,true);
        }

        if (mSobPopwindow != null) {
            mSobPopwindow.setCurrentPlayPosition(playIndex);
        }

    }

    @Override
    public void updateListOrder(boolean isReverse) {
        //更新播放列表排序
        if (mSobPopwindow != null) {
            mSobPopwindow.updateOrderIcon(isReverse);
        }
    }

}
