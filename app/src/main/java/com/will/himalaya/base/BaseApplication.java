package com.will.himalaya.base;

import android.app.Application;
import android.content.Context;

import com.will.himalaya.util.LogUtil;
import com.will.himalaya.util.SharedPreferenceUtil;
import com.will.himalaya.wiget.WeakHandler;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

public class BaseApplication extends Application {

    private static WeakHandler sHandler = null;

    public static Context mContext = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        CommonRequest mXimalaya = CommonRequest.getInstanse();
        if (DTransferConstants.isRelease) {
            String mAppSecret = "8646d66d6abe2efd14f2891f9fd1c8af";
            mXimalaya.setAppkey("9f9ef8f10bebeaa83e71e62f935bede8");
            mXimalaya.setPackid("com.app.test.android");
            mXimalaya.init(this, mAppSecret);
        } else {
            String mAppSecret = "0a09d7093bff3d4947a5c4da0125972e";
            mXimalaya.setAppkey("f4d8f65918d9878e1702d49a8cdf0183");
            mXimalaya.setPackid("com.ximalaya.qunfeng");
            mXimalaya.init(this, mAppSecret);
        }

        //初始化播放器
        XmPlayerManager.getInstance(this).init();

        LogUtil.init(this.getPackageName(),false);

        sHandler = new WeakHandler();
    }


    public static WeakHandler getHandler(){
        return sHandler;
    }

    public static Context getAppContext(){
        return mContext;
    }

}
