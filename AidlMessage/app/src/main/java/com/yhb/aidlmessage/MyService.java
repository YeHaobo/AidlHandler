package com.yhb.aidlmessage;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.RemoteException;
import android.util.Log;
import com.yhb.aidlhandler.IClientAidlReceive;
import com.yhb.aidlhandler.IServiceAidlResult;
import com.yhb.aidlhandler.service.BaseAidlService;

public class MyService extends BaseAidlService {

    private static final String TAG = "MyService";
    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread handlerThread = new HandlerThread("yhb");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        clientReceive();
    }

    /**每5秒给客户端已注册接口发送消息*/
    private void clientReceive(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                clientReceive("clientReceive", "params");
                Log.e(TAG, "clientReceive params");
                clientReceive();
            }
        }, 5*1000);
    }

    /**同步调用*/
    @Override
    public void syncPost(String action, String params, IServiceAidlResult result) {
        Log.e(TAG, "syncPost " + action + " " + params);
        try {
            result.onResult(200, action);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**异步调用*/
    @Override
    public void asyncPost(String action, String params, IServiceAidlResult result) {
        Log.e(TAG, "asyncPost " + action + " " + params);
        try {
            result.onResult(200, action);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**客户端注册*/
    @Override
    public void registerReceive(IClientAidlReceive receive) {
        try {
            Log.e(TAG, "register " + receive.asBinder().getInterfaceDescriptor());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**客户端解注册*/
    @Override
    public void unregisterReceive(IClientAidlReceive receive) {
        try {
            Log.e(TAG, "unregister " + receive.asBinder().getInterfaceDescriptor());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}