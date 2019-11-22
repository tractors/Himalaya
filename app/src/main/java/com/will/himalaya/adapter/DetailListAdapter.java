package com.will.himalaya.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.will.himalaya.R;
import com.will.himalaya.viewholder.DetailListViewHolder;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 详情列表适配器
 */
public class DetailListAdapter extends BaseAdapterT<Track,DetailListViewHolder>{

    public DetailListAdapter(Context context, List<Track> list) {
        super(context, list);
    }

    SimpleDateFormat mUpdateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat mDurationFormat = new SimpleDateFormat("mm:ss");

    @NonNull
    @Override
    public DetailListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_detail,parent,false);
        return new DetailListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailListViewHolder holder, int position) {
        holder.itemView.setTag(position);

        View itemView = holder.itemView;
        TextView orderText = itemView.findViewById(R.id.order_text);
        TextView detailItemTitle = itemView.findViewById(R.id.detail_item_title);
        TextView playCount = itemView.findViewById(R.id.detail_item_play_count);
        TextView playDuration = itemView.findViewById(R.id.detail_item_play_duration);
        TextView updateTime = itemView.findViewById(R.id.detail_item_update_time);

        Track track = mList.get(position);
        if (null != track) {
            orderText.setText((position+1)+"");
            detailItemTitle.setText(track.getTrackTitle());
            playCount.setText(track.getPlayCount()+"");
            String durationFormat = mDurationFormat.format(track.getDuration() * 1000);
            playDuration.setText(durationFormat);
            String updateTimeText = mUpdateFormat.format(track.getUpdatedAt());
            updateTime.setText(updateTimeText);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setClickList(view,mList);
            }
        });
    }
}
