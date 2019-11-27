package com.will.himalaya.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.will.himalaya.R;
import com.will.himalaya.viewholder.SubscriptionViewHolder;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 * 订阅列表模块的adapter
 */
public class SubscriptionListAdapter extends BaseAdapterT<Album,SubscriptionViewHolder>{


    public SubscriptionListAdapter(Context context, List<Album> list, RequestManager glide) {
        super(context, list, glide);
    }

    @NonNull
    @Override
    public SubscriptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend,parent,false);
        return new SubscriptionViewHolder(itemView,mGlide);
    }

    @Override
    public void onBindViewHolder(@NonNull SubscriptionViewHolder holder, int position) {
        holder.itemView.setTag(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setClick(view,mList);
            }
        });

        holder.setData(mList.get(position));

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                setLongClick(view,mList);
                //消费该事件
                return true;
            }
        });
    }
}
