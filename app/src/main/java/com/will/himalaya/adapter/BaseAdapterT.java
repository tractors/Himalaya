package com.will.himalaya.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAdapterT <T,K extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<K>{

    protected Context mContext;
    protected List<T> mList;
    protected LayoutInflater mInflater = null;

    public BaseAdapterT(Context context,List<T> list){
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
        if (null == list || 0 == list.size()){
            this.mList = new ArrayList<>();
        } else {
            this.mList = list;
        }
    }

    public void addList(List<T> list){
        if (null != list && 0 != list.size()){

            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void setData(List<T> list){
        if (null != list && 0 != list.size()){
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return (null == mList || 0 == mList.size()) ? 0 : mList.size();
    }
}
