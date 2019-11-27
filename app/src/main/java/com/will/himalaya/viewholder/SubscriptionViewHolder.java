package com.will.himalaya.viewholder;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.will.himalaya.R;
import com.ximalaya.ting.android.opensdk.model.album.Album;

/**
 * 订阅的ViewHolder
 */
public class SubscriptionViewHolder extends RecyclerView.ViewHolder{

    private RequestManager mGlide = null;
    public SubscriptionViewHolder(View itemView, RequestManager glide) {
        super(itemView);
        this.mGlide = glide;
    }

    public void setData(Album album) {
        TextView descriptionTv = itemView.findViewById(R.id.album_description_tv);
        ImageView albumCoverIv = itemView.findViewById(R.id.album_cover);
        TextView albumTitleTv = itemView.findViewById(R.id.album_title_tv);
        TextView albumPlayCountTv = itemView.findViewById(R.id.album_play_count);
        TextView albumContentCountTv = itemView.findViewById(R.id.album_content_size);

        albumTitleTv.setText(album.getAlbumTitle());
        descriptionTv.setText(album.getAlbumIntro());
        albumPlayCountTv.setText(album.getPlayCount()+"");
        albumContentCountTv.setText(album.getIncludeTrackCount()+"");
        String coverUrlLarge = album.getCoverUrlLarge();
        if (!TextUtils.isEmpty(coverUrlLarge)) {
                mGlide.load(album.getCoverUrlLarge()).into(albumCoverIv);
        } else {
            albumCoverIv.setImageResource(R.mipmap.logo);
        }
    }
}
