package com.will.himalaya.api;

import com.will.himalaya.util.Constant;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.HashMap;
import java.util.Map;

public class XimalayApi {

    private static XimalayApi sXimalayApi = null;

    private static byte[] sBytes = new byte[10];


    private XimalayApi(){}

    public static XimalayApi getXimalayApi(){
        if (sXimalayApi == null) {
            synchronized (sBytes){
                if (sXimalayApi == null) {
                    sXimalayApi = new XimalayApi();
                }
            }
        }

        return sXimalayApi;
    }

    /**
     * 获取推荐内容
     * @param callBack
     */
    public void getRecommendList(IDataCallBack<GussLikeAlbumList> callBack){
        Map<String,String> map = new HashMap<>();
        //这个参数表示一页数据返回多条
        map.put(DTransferConstants.LIKE_COUNT, String.valueOf(Constant.RECOMMEND_COUNT));
        CommonRequest.getGuessLikeAlbum(map,callBack);
    }


    /**
     * 根据id获取专辑内容列表
     * @param callBack 获取专辑详情的回调
     * @param currentAlbumId  当前专辑的id
     * @param currentPageIndex 当前页的页码指针
     */
    public void getAlbumDetail(long currentAlbumId,int currentPageIndex,IDataCallBack<TrackList> callBack){
        //根据页码和专辑id获取列表
        Map<String,String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID,String.valueOf(currentAlbumId));
        map.put(DTransferConstants.SORT,"asc");
        map.put(DTransferConstants.PAGE,String.valueOf(currentPageIndex));
        map.put(DTransferConstants.PAGE_SIZE,String.valueOf(Constant.DETAIL_COUNT));
        CommonRequest.getTracks(map,callBack);
    }


    public void searchByKeyword(String keyword, int page, IDataCallBack<SearchAlbumList> callBack) {
        Map<String,String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY,keyword);
        map.put(DTransferConstants.PAGE,page+"");
        map.put(DTransferConstants.PAGE_SIZE,String.valueOf(Constant.DETAIL_COUNT));
        CommonRequest.getSearchedAlbums(map,callBack);
    }
}
