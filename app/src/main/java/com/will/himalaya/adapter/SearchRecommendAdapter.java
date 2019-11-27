package com.will.himalaya.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.will.himalaya.R;
import com.will.himalaya.viewholder.SearchRecommendViewHolder;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.List;

/**
 * 联想词的适配器
 */
public class SearchRecommendAdapter extends BaseAdapterT<QueryResult,SearchRecommendViewHolder>{

    private static final String TAG = "SearchRecommendAdapter";

    public SearchRecommendAdapter(Context context, List<QueryResult> list, RequestManager glide) {
        super(context, list, glide);
    }


    @NonNull
    @Override
    public SearchRecommendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_search_recommend,parent,false);
        return new SearchRecommendViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchRecommendViewHolder holder, int position) {
        holder.itemView.setTag(position);

        View itemView = holder.itemView;
        TextView textView = itemView.findViewById(R.id.search_recommend_item);

        QueryResult queryResult = mList.get(position);
        if (queryResult != null) {
            textView.setText(queryResult.getKeyword());
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setClick(view,mList);
            }
        });

    }
}
