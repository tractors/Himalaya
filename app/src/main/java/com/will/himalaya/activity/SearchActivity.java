package com.will.himalaya.activity;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.will.himalaya.R;
import com.will.himalaya.adapter.BaseAdapterT;
import com.will.himalaya.adapter.SearchRecommendAdapter;
import com.will.himalaya.adapter.SearchResultListAdapter;
import com.will.himalaya.base.BaseActivity;
import com.will.himalaya.interfaces.ISearchCallback;
import com.will.himalaya.presenter.AlbumDetailPresenter;
import com.will.himalaya.presenter.SearchPresenter;
import com.will.himalaya.util.Constant;
import com.will.himalaya.util.IntentActivity;
import com.will.himalaya.util.LogUtil;
import com.will.himalaya.wiget.FlowTextLayout;
import com.will.himalaya.wiget.RItemDecoration;
import com.will.himalaya.wiget.UILoader;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索模块
 */
public class SearchActivity extends BaseActivity implements ISearchCallback, BaseAdapterT.OnItemClickListener<Album> {

    private static final String TAG = "SearchActivity";
    private ImageView mSearchBack;
    private EditText mInputBox;
    private TextView mSearchBtn;
    private FrameLayout mResultContainer;
    private SearchPresenter mSearchPresenter;
    private FlowTextLayout mFlowTextLayout;
    private UILoader mUILoader = null;
    private RecyclerView mResultListView;
    private List<Album> mSearchResultList = new ArrayList<>();
    private SearchResultListAdapter mSearchResultListAdapter;
    private InputMethodManager mImm;
    private ImageView mDelBtn;
    public static final int TIME_SHOW_IMM = 500;
    private RecyclerView mSearchRecommendListView;

    private List<QueryResult> mSearchRecommendList = new ArrayList<>();
    private SearchRecommendAdapter mSearchRecommendAdapter;

    //定义显示UI的标记
    private static final int FLOW_TEXT_LAYOUT_SHOW = 1;
    private static final int RESULT_LIST_VIEW_SHOW = 2;
    private static final int SEARCH_RECOMMEND_VIEW_SHOW = 3;
    //刷新控件
    private TwinklingRefreshLayout mRefreshLayout;
    //是否推荐联想词，默认是需要
    private boolean mNeedSuggestWord = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initPresenter();
        initEvent();
    }




    private void initView() {
        mSearchBack = this.findViewById(R.id.search_back);
        mInputBox = this.findViewById(R.id.search_input);
        mSearchBtn = this.findViewById(R.id.search_btn);
        mResultContainer = this.findViewById(R.id.search_container);
        mDelBtn = this.findViewById(R.id.search_input_delete);
        mDelBtn.setVisibility(View.GONE);
        if (mUILoader == null) {
            mUILoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView();
                }
            };
        }

        if (mUILoader.getParent() instanceof ViewGroup) {
            ((ViewGroup) mUILoader.getParent()).removeView(mUILoader);
        }

        mResultContainer.addView(mUILoader);
        //mUILoader.setEmptyTipsResId(R.string.empty_tips_search_text);
    }

    /**
     * 数据返回成功后，创建成功的view
     * @return
     */
    private View createSuccessView() {
        View resultView = LayoutInflater.from(this).inflate(R.layout.search_result_layout, null);

        mRefreshLayout = resultView.findViewById(R.id.search_result_refresh_layout);
        //禁止下拉刷新
        mRefreshLayout.setEnableRefresh(false);
        //显示搜索结果的
        mResultListView = resultView.findViewById(R.id.result_list_view);
        //显示热词的控件
        mFlowTextLayout = resultView.findViewById(R.id.recommend_hot_word_view);
        //显示联想词的
        mSearchRecommendListView = resultView.findViewById(R.id.search_recommend_list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);

        mResultListView.setLayoutManager(linearLayoutManager);

        RequestManager mRequestManager= Glide.with(this);
        mSearchResultListAdapter = new SearchResultListAdapter(this,mSearchResultList,mRequestManager);

        mResultListView.addItemDecoration(new RItemDecoration());
        mResultListView.setAdapter(mSearchResultListAdapter);


        
        //设置布局管理器
        LinearLayoutManager searchRecommendLayoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);

        mSearchRecommendListView.setLayoutManager(searchRecommendLayoutManager);

        mSearchRecommendAdapter = new SearchRecommendAdapter(this,mSearchRecommendList,null);
        mSearchRecommendListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(),2);
                outRect.bottom = UIUtil.dip2px(view.getContext(),2);
                outRect.left = UIUtil.dip2px(view.getContext(),5);
                outRect.right = UIUtil.dip2px(view.getContext(),5);
            }
        });
        mSearchRecommendListView.setAdapter(mSearchRecommendAdapter);
        return resultView;
    }

    private void initEvent() {

        //返回点击事件
        mSearchBack.setOnClickListener(mListener);
        //输入框删除按钮点击事件
        mDelBtn.setOnClickListener(mListener);
        //联想词点击搜索事件
        mSearchRecommendAdapter.setOnItemClickListener(new BaseAdapterT.OnItemClickListener<QueryResult>() {
            @Override
            public void onItemClick(QueryResult field, int position) {
                String keyword = field.getKeyword();
                if (!TextUtils.isEmpty(keyword)) {
                    //不触发联想词
                    mNeedSuggestWord = false;
                    switchToSearch(keyword);
                }
            }
        });
        //输入框输入键盘显示
        mInputBox.postDelayed(new Runnable() {
            @Override
            public void run() {
                mInputBox.requestFocus();
                //显示键盘
                mImm.showSoftInput(mInputBox,InputMethodManager.SHOW_IMPLICIT);
            }
        },TIME_SHOW_IMM);
        //输入框的文本监听事件
        mInputBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (TextUtils.isEmpty(charSequence)) {
                    if (mSearchPresenter != null) {
                        mSearchPresenter.getHotWord();
                        mDelBtn.setVisibility(View.GONE);
                    }
                } else {
                    mDelBtn.setVisibility(View.VISIBLE);
                    //是否触发联想词搜索
                    if (mNeedSuggestWord) {
                        getSuggestWord(charSequence.toString());
                    } else {
                        mNeedSuggestWord = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //点击搜索按钮事件
        mSearchBtn.setOnClickListener(mListener);
        //重新加载点击事件
        mUILoader.setOnRetryClickListener(new UILoader.OnRetryClickListener() {
            @Override
            public void onRetryClick() {
                if (mSearchPresenter != null) {
                    mSearchPresenter.reSearch();
                    mUILoader.updateStatus(UILoader.UIStatus.LOADING);
                }
            }
        });

        //热词点击搜索
        mFlowTextLayout.setClickListener(new FlowTextLayout.ItemClickListener() {
            @Override
            public void onItemClick(String text) {
                //不需要联想词
                mNeedSuggestWord = false;
                switchToSearch(text);

            }
        });

        //刷新监听
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                //加载更多内容

                if (mSearchPresenter != null) {
                    mSearchPresenter.loadMore();
                }
            }
        });
        //搜索结果点击进入详情事件
        mSearchResultListAdapter.setOnItemClickListener(this);
    }

    //搜索关键字
    private void switchToSearch(String text) {
        //把热词放到输入框中
        if (!TextUtils.isEmpty(text)) {
            mInputBox.clearComposingText();
            mInputBox.setText(text);
            mInputBox.setSelection(text.length());
            //发起搜索请求
            if (mSearchPresenter != null) {
                mSearchPresenter.doSearch(text);
            }
            //改变UI状态
            if (mUILoader != null) {
                mUILoader.updateStatus(UILoader.UIStatus.LOADING);
            }
        }
    }

    /**
     * 获取联想词
     * @param keyword
     */
    private void getSuggestWord(String keyword) {
        if (mSearchPresenter != null) {
            mSearchPresenter.getRecommendWord(keyword);
        }
    }

    private void initPresenter() {

        mImm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        mSearchPresenter = SearchPresenter.getSearchPresenter();
        //注册UI更新的回调
        mSearchPresenter.registerViewCallback(this);

        mSearchPresenter.getHotWord();
    }

    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id){
                case R.id.search_back:
                    finish();
                    break;
                case R.id.search_btn:
                    searchEvent();
                    break;
                case R.id.search_input_delete:
                    mInputBox.setText("");
                    break;
            }
        }
    };

    private void searchEvent() {
        //点击搜索调用搜索逻辑
        Editable editable = mInputBox.getText();
        if (editable != null) {
            String keywords = mInputBox.getText().toString().trim();
            if (!TextUtils.isEmpty(keywords)) {
                if (mSearchPresenter != null) {
                    mSearchPresenter.doSearch(keywords);
                    mUILoader.updateStatus(UILoader.UIStatus.LOADING);
                }
            }
        }
    }

    @Override
    public void onSearchResultLoaded(List<Album> result) {

        handleSearchResult(result);
        //隐藏键盘
        mImm.hideSoftInputFromWindow(mInputBox.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 处理返回结果及UI的显示
     * @param result
     */
    private void handleSearchResult(List<Album> result) {
        hideOrShow(RESULT_LIST_VIEW_SHOW);
        if (result != null) {
            if (mUILoader != null) {
                if (0 == result.size()) {
                    //如果数据为空
                    mUILoader.updateStatus(UILoader.UIStatus.EMPTY);
                } else {
                    //如果数据不为空，就设置数据
                    mSearchResultListAdapter.setData(result);
                    mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
                }
            }

        }
    }


    private void hideOrShow(int type){
        if (mSearchRecommendListView != null && mResultListView != null && mFlowTextLayout != null) {
            switch (type){
                case FLOW_TEXT_LAYOUT_SHOW:
                    mSearchRecommendListView.setVisibility(View.GONE);
                    mRefreshLayout.setVisibility(View.GONE);
                    mFlowTextLayout.setVisibility(View.VISIBLE);
                    break;
                case RESULT_LIST_VIEW_SHOW:
                    mSearchRecommendListView.setVisibility(View.GONE);
                    mFlowTextLayout.setVisibility(View.GONE);
                    mRefreshLayout.setVisibility(View.VISIBLE);
                    break;
                case SEARCH_RECOMMEND_VIEW_SHOW:
                    mFlowTextLayout.setVisibility(View.GONE);
                    mRefreshLayout.setVisibility(View.GONE);
                    mSearchRecommendListView.setVisibility(View.VISIBLE);
                    break;
            }
        }

    }

    @Override
    public void onHotWordLoaded(List<HotWord> hotWordList) {
        hideOrShow(FLOW_TEXT_LAYOUT_SHOW);
        List<String> hotWords = new ArrayList<>();
        for (HotWord hotWord : hotWordList) {
            String searchWord = hotWord.getSearchword();
            hotWords.add(searchWord);
        }

        mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
        //更新UI
        mFlowTextLayout.setTextContents(hotWords);
    }

    @Override
    public void onLoadMoreResult(List<Album> result, boolean isOkay) {
        //处理加载更多
        if (mRefreshLayout != null) {
            mRefreshLayout.finishLoadmore();
        }

        if (isOkay){
            handleSearchResult(result);
        } else {
            Toast.makeText(this,"没有更多内容",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRecommendWord(List<QueryResult> keyWordList) {
        if (keyWordList != null && 0 < keyWordList.size()) {
            LogUtil.d(TAG,"onRecommendWord:  --->"+ keyWordList.size());
            hideOrShow(SEARCH_RECOMMEND_VIEW_SHOW);
            if (mSearchRecommendAdapter != null) {
                LogUtil.d(TAG,"onRecommendWord: keywordList --->"+ keyWordList.size());
                mSearchRecommendAdapter.setData(keyWordList);
            }

            //设置UI状态
            if (mUILoader != null) {
                mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
            }
        }

    }

    @Override
    public void onError(int errorCode, String errorMsg) {
        if (mUILoader != null) {
            mUILoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSearchPresenter != null) {
            mSearchPresenter.unRegisterViewCallback(this);
        }

        mSearchPresenter = null;
    }

    @Override
    public void onItemClick(Album field, int position) {
        AlbumDetailPresenter.getInstance().setTargetAlbum(field);
        //item被点击
        IntentActivity.startActivity(SearchActivity.this,DetailActivity.class);
    }
}
