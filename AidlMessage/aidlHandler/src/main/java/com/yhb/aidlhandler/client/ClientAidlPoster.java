package com.yhb.aidlhandler.client;

import android.os.RemoteException;
import android.util.Log;
import com.yhb.aidlhandler.IClientAidlReceive;
import com.yhb.aidlhandler.IServiceAidl;
import com.yhb.aidlhandler.IServiceAidlResult;

/**消息发送者*/
public class ClientAidlPoster {

    /**TAG*/
    private static final String TAG = "ClientAidlPoster";

    /**远程服务*/
    private IServiceAidl service;

    /**构造*/
    public ClientAidlPoster(IServiceAidl service) {
        this.service = service;
    }

    /**发送消息*/
    public void syncPost(String action, String params, IServiceAidlResult.Stub result){
        try{
            service.syncPost(action, params, result);
        }catch (RemoteException e){
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
    }

    /**发送消息*/
    public void asyncPost(String action, String params, IServiceAidlResult.Stub result){
        try{
            service.asyncPost(action, params, result);
        }catch (RemoteException e){
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
    }

    /**订阅被观察者*/
    public void registerReceive(IClientAidlReceive.Stub receive){
        try{
            service.registerReceive(receive);
        }catch (RemoteException e){
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
    }

    /**解除订阅被观察者*/
    public void unregisterReceive(IClientAidlReceive.Stub receive){
        try{
            service.unregisterReceive(receive);
        }catch (RemoteException e){
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
    }

}