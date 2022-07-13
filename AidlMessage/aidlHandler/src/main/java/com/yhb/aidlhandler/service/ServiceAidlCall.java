package com.yhb.aidlhandler.service;

import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import com.yhb.aidlhandler.IClientAidlCall;
import com.yhb.aidlhandler.IServiceAidlCall;
import com.yhb.aidlhandler.IServiceAidlCallback;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**服务端Aidl接口实现*/
public class ServiceAidlCall extends IServiceAidlCall.Stub {
    private static final String TAG = "ServiceAidlCall";

    /**用于存储客户端注册接口*/
    private final RemoteCallbackList<IClientAidlCall> remoteCallbackList;

    /**接收客户端消息的实现接口*/
    private ServiceAidlPost serviceAidlPost;

    /**可缓存线程池、无空闲线程则新建线程运行，有空闲则使用空闲线程，内部空闲线程60秒后将被回收*/
    private ExecutorService cachedThreadPool;

    /**构造*/
    public ServiceAidlCall(ServiceAidlPost serviceAidlPost) {
        this.remoteCallbackList = new RemoteCallbackList<>();
        this.serviceAidlPost = serviceAidlPost;
        this.cachedThreadPool= Executors.newCachedThreadPool();
    }

    /**oneway类型消息*/
    @Override
    public void onewayPost(String action, String params, IServiceAidlCallback callback) throws RemoteException {
        Log.e(TAG,"onewayPost");
        if(serviceAidlPost != null){
            serviceAidlPost.onewayPost(action, params, callback);
        }
    }

    /**uiPost类型消息*/
    @Override
    public void uiPost(String action, String params, IServiceAidlCallback callback) throws RemoteException {
        Log.e(TAG,"uiPost");
        if(serviceAidlPost != null){
            serviceAidlPost.uiPost(action, params, callback);
        }
    }

    /**asynPost类型消息*/
    @Override
    public void asynPost(final String action, final String params, final IServiceAidlCallback callback) throws RemoteException {
        Log.e(TAG,"asynPost");
        if(serviceAidlPost != null){
            cachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    serviceAidlPost.asynPost(action, params, callback);
                }
            });
        }
    }

    /**注册客户端回调*/
    @Override
    public void register(IClientAidlCall call) throws RemoteException {
        Log.e(TAG,"register");
        if(call != null){
            remoteCallbackList.register(call);
        }
    }

    /**解注册客户端回调*/
    @Override
    public void unregister(IClientAidlCall call) throws RemoteException {
        Log.e(TAG,"unregister");
        if(call != null){
            remoteCallbackList.unregister(call);
        }
    }

    /**清空客户端回调*/
    public void clearRegister() {
        Log.e(TAG,"clearRegister");
        remoteCallbackList.kill();
    }

    /**下发消息至客户端*/
    public void accept(String action, String params) throws RemoteException {
        Log.e(TAG,"accept");
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