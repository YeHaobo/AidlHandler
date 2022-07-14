package com.yhb.aidlhandler.service;

import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import com.yhb.aidlhandler.IClientAidlCall;
import com.yhb.aidlhandler.IServiceAidlCall;
import com.yhb.aidlhandler.IServiceAidlCallback;

/**服务端Aidl接口实现*/
public class ServiceAidlCall extends IServiceAidlCall.Stub {
    private static final String TAG = "ServiceAidlCall";

    /**用于存储客户端注册*/
    private final RemoteCallbackList<IClientAidlCall> remoteCallbackList;

    /**处理客户端消息的实现*/
    private ServiceAidlPost serviceAidlPost;

    /**构造*/
    public ServiceAidlCall(ServiceAidlPost serviceAidlPost) {
        this.remoteCallbackList = new RemoteCallbackList<>();
        this.serviceAidlPost = serviceAidlPost;
    }

    /**uiPost调用*/
    @Override
    public void uiPost(String action, String params, IServiceAidlCallback callback) throws RemoteException {
        Log.e(TAG,"uiPost： " + action + "   " + params);
        if(serviceAidlPost != null){
            serviceAidlPost.uiPost(action, params, callback);
        }
    }

    /**asynPost调用*/
    @Override
    public void asynPost(String action, String params, IServiceAidlCallback callback) throws RemoteException {
        Log.e(TAG,"asynPost： " + action + "   " + params);
        if(serviceAidlPost != null){
            serviceAidlPost.asynPost(action, params, callback);
        }
    }

    /**注册*/
    @Override
    public void register(IClientAidlCall call) throws RemoteException {
        Log.e(TAG,"register");
        if(call != null){
            remoteCallbackList.register(call);
        }
    }

    /**解注册*/
    @Override
    public void unregister(IClientAidlCall call) throws RemoteException {
        Log.e(TAG,"unregister");
        if(call != null){
            remoteCallbackList.unregister(call);
        }
    }

    /**清空注册*/
    public void clearRegister() {
        Log.e(TAG,"clearRegister");
        remoteCallbackList.kill();
    }

    /**下发消息至客户端*/
    public void accept(String action, String params) throws RemoteException {
        Log.e(TAG,"accept： " + action + "   " + params);
        synchronized (remoteCallbackList){//线程不安全，需要加对象锁
            int count = remoteCallbackList.beginBroadcast();
            for (int i = 0; i < count; i ++) {
                IClientAidlCall call = remoteCallbackList.getBroadcastItem(i);
                if(call != null){
                    call.accept(action,params);
                }
            }
            remoteCallbackList.finishBroadcast();
        }
    }

}