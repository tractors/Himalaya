package com.will.himalaya.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.will.himalaya.R;
import com.will.himalaya.viewholder.RecommendViewHolder;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 * 搜索结果适配器
 */
public class SearchResultListAdapter extends BaseAdapterT<Album,RecommendViewHolder>{
    public SearchResultListAdapter(Context context, List<Album> list) {
        super(context, list);
    }

    @NonNull
    @Override
    public RecommendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_recommend,parent,false);
        return new RecommendViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendViewHolder holder, int position) {
        holder.itemView.setTag(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setClick(view,mList);
            }
        });

        holder.setData(mList.get(position));
    }
}
