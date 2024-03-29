package com.will.himalaya.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 适配器基类
 * @param <T> 数据类型
 * @param <K> viewHolder类型
 */
public abstract class BaseAdapterT <T,K extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<K>{

    protected Context mContext;
    protected List<T> mList;
    protected RequestManager mGlide = null;
    protected LayoutInflater mInflater = null;
    protected OnItemClickListener mOnItemClickListener = null;
    private OnItemClickListenerList mOnItemClickListenerList = null;
    private OnItemLongClickListener mOnItemLongClickListener = null;

    public BaseAdapterT(Context context,List<T> list,RequestManager glide){
        this.mContext = context;
        this.mGlide = glide;
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

    public int getDataSize(){
        return (null == mList || 0 == mList.size()) ? 0 : mList.size();
    }

    @Override
    public int getItemCount() {
        return (null == mList || 0 == mList.size()) ? 0 : mList.size();
    }


    /**
     * 设置回调结果有实例的监听事件
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener){
        this.mOnItemClickListener = listener;
    }

    /**
     * 设置回调结果有实例的监听事件
     * @param <T>
     */
    public interface  OnItemClickListener<T>{
        void onItemClick(T field, int position);
    }

    /**
     * 设置回调结果有实例的监听事件
     * @param view
     * @param data
     */
    protected void setClick(View view,List<T> data){
        int clickPosition = (int) view.getTag();
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick((T)(data.get(clickPosition)),clickPosition);
        }
    }

    /**
     * 设置回调结果有集合实例的监听事件
     * @param listener
     */
    public void setOnItemClickListenerList(OnItemClickListenerList listener){
        this.mOnItemClickListenerList = listener;
    }

    /**
     * 设置回调结果有集合实例的监听事件
     * @param <T>
     */
    public interface OnItemClickListenerList<T>{
        void onItemClickList(List<T> field,int position);
    }


    /**
     * 设置回调结果有集合实例的监听事件
     * @param view
     * @param data
     */
    protected void setClickList(View view,List<T> data){
        int clickPosition = (int) view.getTag();
        if (null != mOnItemClickListenerList) {
            mOnItemClickListenerList.onItemClickList(data,clickPosition);
        }
    }


    /**
     * 设置长按回调结果有实例的监听事件
     * @param longClickListener
     */
    public void setOnItemLongClickListener(OnItemLongClickListener longClickListener){
        this.mOnItemLongClickListener = longClickListener;
    }

    /**
     * 设置长按回调结果有实例的监听事件
     * @param <T>
     */
    public interface OnItemLongClickListener<T>{
        void onItemLongClick(T field, int position);
    }

    /**
     * 设置长按回调结果有实例的监听事件
     * @param view
     * @param data
     */
    protected  void setLongClick(View view,List<T> data){
        int clickPosition = (int) view.getTag();

        if (null != mOnItemLongClickListener) {
            mOnItemLongClickListener.onItemLongClick((T)(data.get(clickPosition)),clickPosition);
        }
    }

}
