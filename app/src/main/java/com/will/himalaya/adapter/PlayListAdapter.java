package com.will.himalaya.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.will.himalaya.R;
import com.will.himalaya.base.BaseApplication;
import com.will.himalaya.viewholder.PopWindowPlayListViewHolder;
import com.will.himalaya.wiget.SobPopwindow;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

/**
 * 播放器列表的适配器
 */
public class PlayListAdapter extends BaseAdapterT<Track,PopWindowPlayListViewHolder>{

    private int playingIndex = -1;
    private SobPopwindow.PlayListItemClickListener mListener = null;
    public PlayListAdapter(Context context, List<Track> list) {
        super(context, list);
    }

    @NonNull
    @Override
    public PopWindowPlayListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_play_list,parent,false);
        return new PopWindowPlayListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PopWindowPlayListViewHolder holder, final int position) {
        holder.itemView.setTag(position);
        //设置数据
        Track track = mList.get(position);
        TextView trackTitleTv = holder.itemView.findViewById(R.id.track_title_tv);
        trackTitleTv.setTextColor(
                BaseApplication.getAppContext().getResources().getColor(
                        playingIndex == position ? R.color.main_color : R.color.play_list_text_color));
        trackTitleTv.setText(track.getTrackTitle());
        ImageView playingIconView = holder.itemView.findViewById(R.id.play_icon_iv);
        playingIconView.setVisibility(playingIndex == position ? View.VISIBLE : View.INVISIBLE);


        /**
         * 接口传递事件
         */

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(position);
                }
            }
        });
    }

    /**
     * 设置当前播放位置
     * @param position
     */
    public void setCurrentPlayPosition(int position) {
        this.playingIndex = position;
        notifyDataSetChanged();
    }


    public void setOnItemClickListener(SobPopwindow.PlayListItemClickListener listener){
        this.mListener = listener;
    }
}
