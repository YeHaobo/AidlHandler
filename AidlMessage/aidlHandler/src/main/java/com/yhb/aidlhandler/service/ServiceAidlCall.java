package com.yhb.aidlhandler.service;

import android.os.RemoteCallbackList;
import android.util.Log;
import com.yhb.aidlhandler.IClientAidlReceive;
import com.yhb.aidlhandler.IServiceAidl;
import com.yhb.aidlhandler.IServiceAidlResult;

/**服务端Aidl接口实现*/
public class ServiceAidlCall extends IServiceAidl.Stub {

    /**TAG*/
    private static final String TAG = "ServiceAidlCall";

    /**用于存储客户端注册*/
    private final RemoteCallbackList<IClientAidlReceive> clients;
    /**处理客户端消息的实现*/
    private ServiceAidlPost post;

    /**构造*/
    public ServiceAidlCall(ServiceAidlPost post) {
        this.clients = new RemoteCallbackList<>();
        this.post = post;
    }

    /**syncPost调用*/
    @Override
    public void syncPost(String action, String params, IServiceAidlResult result) {
        post.syncPost(action, params, result);
    }

    /**asyncPost调用*/
    @Override
    public void asyncPost(String action, String params, IServiceAidlResult result) {
        post.asyncPost(action, params, result);
    }

    /**注册*/
    @Override
    public void registerReceive(IClientAidlReceive receive) {
        clients.register(receive);
        post.registerReceive(receive);
    }

    /**解注册*/
    @Override
    public void unregisterReceive(IClientAidlReceive receive) {
        post.unregisterReceive(receive);
        clients.unregister(receive);
    }

    /**清空注册*/
    public int clearRegister() {
        synchronized (clients){//对象锁
            int count = clients.beginBroadcast();//准备使用已注册接口
            clients.finishBroadcast();//使用完成后必须调用finishBroadcast()
            clients.kill();//清除
            return count;
        }
    }

    /**下发消息至客户端*/
    public void clientReceive(String action, String params) {
        synchronized (clients){//对象锁
            int count = clients.beginBroadcast();//准备使用已注册接口
            for (int i = 0; i < count; i ++) {
                try{
                    clients.getBroadcastItem(i).onReceived(action, params);//RemoteException NullPointerException
                } catch (Exception e){
                    e.printStackTrace();
                    Log.e(TAG, "onReceived client error: " + e.getMessage());
                }
            }
            clients.finishBroadcast();//使用完成后必须调用finishBroadcast()
        }
    }

}