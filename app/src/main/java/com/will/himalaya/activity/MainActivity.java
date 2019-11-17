package com.will.himalaya.activity;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.will.himalaya.R;
import com.will.himalaya.adapter.IndicatorAdapter;
import com.will.himalaya.adapter.MainContentAdapter;
import com.will.himalaya.interfaces.IPlayerViewCallback;
import com.will.himalaya.presenter.PlayerPresenter;
import com.will.himalaya.presenter.RecommendPresenter;
import com.will.himalaya.util.IntentActivity;
import com.will.himalaya.wiget.RoundRectImageView;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.List;

public class MainActivity extends FragmentActivity implements IPlayerViewCallback {

    private static final String TAG = "MainActivity";
    private MagicIndicator mMagicIndicator;
    private ViewPager mContentViewPager;
    private IndicatorAdapter mIndicatorAdapter;
    private RoundRectImageView mMainTrackCoverIV;
    private TextView mHeaderTitle;
    private TextView mSubTitle;
    private ImageView mPlayControl;
    private PlayerPresenter mPlayerPresenter;
    private LinearLayout mPlayControlItem;
    private ImageView mSearchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();

        //播放控制的presenter
        initPresenter();
    }

    private void initPresenter() {
        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);
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

        mPlayControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPlayerPresenter != null) {
                    boolean hasPlayList = mPlayerPresenter.hasPlayList();
                    if (!hasPlayList) {
                        //没有设置过播放列表，我们就播放默认的第一个推荐专辑
                        //第一个推荐专辑，每天都会变的
                        playFirstRecommend();
                    } else {
                        if (mPlayerPresenter.isPlaying()) {
                            mPlayerPresenter.pause();
                        } else {
                            mPlayerPresenter.play();
                        }
                    }

                }
            }
        });

        mPlayControlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转播放器界面
                boolean hasPlayList = mPlayerPresenter.hasPlayList();

                if (!hasPlayList) {
                    playFirstRecommend();
                }

                startActivity(new Intent(MainActivity.this,PlayerActivity.class));
            }
        });

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentActivity.startActivity(MainActivity.this,SearchActivity.class);
            }
        });
    }

    /**
     * 播放第一个推荐专辑
     */
    private void playFirstRecommend() {
        List<Album> currentRecommend = RecommendPresenter.getInstance().getCurrentRecommend();
        if (currentRecommend != null) {
            Album album = currentRecommend.get(0);
            long albumId = album.getId();
            mPlayerPresenter.playByAlbumId(albumId);
        }
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

        //播放控制相关的
        mMainTrackCoverIV = findViewById(R.id.main_track_cover);

        mHeaderTitle = this.findViewById(R.id.main_head_title);
        mHeaderTitle.setSelected(true);
        mSubTitle = this.findViewById(R.id.main_sub_title);

        mPlayControl = this.findViewById(R.id.main_play_control);

        mPlayControlItem = this.findViewById(R.id.main_play_control_item);

        mSearchBtn = this.findViewById(R.id.search_btn);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegisterViewCallback(this);
        }
    }

    @Override
    public void onPlayStart() {
        updatePlayControlStatus(true);
    }

    private void updatePlayControlStatus(boolean isPlaying){
        if (mPlayControl != null) {
            mPlayControl.setImageResource(isPlaying ? R.drawable.selector_player_pause : R.drawable.selector_player_play);
        }
    }

    @Override
    public void onPlayPause() {
        updatePlayControlStatus(false);
    }

    @Override
    public void onPlayStop() {

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

    }

    @Override
    public void onPlayModeChange(PlayMode mode) {

    }

    @Override
    public void onProgressChange(int currentProgress, int total) {

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinished() {

    }

    @Override
    public void onTrackUpdate(Track track, int playIndex) {
        if (track != null) {
            String trackTitle = track.getTrackTitle();
            String nickname = track.getAnnouncer().getNickname();
            String coverUrlLarge = track.getCoverUrlMiddle();
            if (mHeaderTitle != null) {
                mHeaderTitle.setText(trackTitle);
            }

            if (mSubTitle != null) {
                mSubTitle.setText(nickname);
            }

            if (mMainTrackCoverIV != null) {
                Glide.with(this).load(coverUrlLarge).into(mMainTrackCoverIV);
            }
        }
    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }
}
