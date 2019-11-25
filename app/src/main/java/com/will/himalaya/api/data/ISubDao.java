package com.will.himalaya.api.data;

import com.ximalaya.ting.android.opensdk.model.album.Album;

/**
 * 订阅dao层接口
 */
public interface ISubDao {

    /**
     * 设置订阅dao层数据处理回调
     * @param iSubDaoCallback
     */
    void setSubCallback(ISubDaoCallback iSubDaoCallback);

    /**
     * 添加专辑订阅
     * @param album
     */
    void addAlbum(Album album);

    /**
     * 删除订阅内容
     * @param album
     */
    void delAlbum(Album album);

    /**
     * 获取订阅内容
     */
    void listAlbums();
}
