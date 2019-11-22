package com.will.himalaya.base;

/**
 * presenter基础回调
 * @param <T>
 */
public interface IBasePresenter<T> {
    /**
     * 注册监听
     * @param callback
     */
    void registerViewCallback(T callback);

    /**
     * 取消监听
     * @param callback
     */
    void unRegisterViewCallback(T callback);
}
