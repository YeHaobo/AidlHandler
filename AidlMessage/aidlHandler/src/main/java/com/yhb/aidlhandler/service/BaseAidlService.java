package com.yhb.aidlhandler.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**继承该Base类实现你服务端的Service*/
public abstract class BaseAidlService extends Service implements ServiceAidlPost {

    /**TAG*/
    private static final String TAG = "BaseAidlService";

    /**服务端Aidl接口实现（IBinder）*/
    private ServiceAidlCall serverCall = new ServiceAidlCall(this);

    /**服务绑定*/
    @Override
    public IBinder onBind(Intent intent) {
        return serverCall;
    }

    /**下发信息*/
    public void clientReceive(String action, String params) {
        serverCall.clientReceive(action, params);
    }

    /**服务销毁*/
    @Override
    public void onDestroy() {
        int count = serverCall.clearRegister();//清空注册消息
        Log.e(TAG, "onDestroy clearRegister count is " + count);
        super.onDestroy();
    }

}