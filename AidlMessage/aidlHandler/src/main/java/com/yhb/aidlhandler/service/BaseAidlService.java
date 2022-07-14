package com.yhb.aidlhandler.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * 继承该Base类实现你服务端的Service
 */
public abstract class BaseAidlService extends Service implements ServiceAidlPost {
    private static final String TAG = "BaseAidlService";

    /**服务端Aidl接口实现（IBinder）*/
    private ServiceAidlCall serviceAidlCall = new ServiceAidlCall(this);

    /**服务创建*/
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG,"onCreate()");
    }

    /**服务销毁*/
    @Override
    public void onDestroy() {
        Log.e(TAG,"onDestroy()");
        if(serviceAidlCall != null){
            serviceAidlCall.clearRegister();//清空注册消息
            serviceAidlCall = null;
        }
        super.onDestroy();
    }

    /**服务绑定*/
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG,"onBind()");
        return serviceAidlCall;
    }

    /**下发广播*/
    public void doAccept(String action, String params){
        try {
            if(serviceAidlCall != null){
                serviceAidlCall.accept(action,params);
            }else{
                Log.e(TAG,"The server bind is null");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.e(TAG,e.toString());
        }
    }

}