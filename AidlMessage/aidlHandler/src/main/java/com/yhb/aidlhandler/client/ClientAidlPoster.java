package com.yhb.aidlhandler.client;

import android.os.RemoteException;
import android.util.Log;
import com.yhb.aidlhandler.IClientAidlCall;
import com.yhb.aidlhandler.IServiceAidlCall;
import com.yhb.aidlhandler.IServiceAidlCallback;

/**消息发送者*/
public class ClientAidlPoster {
    private static final String TAG = "ClientAidlPoster";

    /**远程访问的对象*/
    private IServiceAidlCall iServiceAidlCall;

    /**构造*/
    public ClientAidlPoster(IServiceAidlCall iServiceAidlCall) {
        this.iServiceAidlCall = iServiceAidlCall;
    }

    /**连接是否正常*/
    public boolean isConnect(){
        if(iServiceAidlCall == null){
            Log.e(TAG,"The binder is null, please connect");
            return false;
        }
        if(!iServiceAidlCall.asBinder().pingBinder()){
            Log.e(TAG,"The binder is dead, Please check the server process");
            return false;
        }
        if(!iServiceAidlCall.asBinder().isBinderAlive()){
            Log.e(TAG,"The server is dead, Please check the server process");
            return false;
        }
        return true;
    }

    /**发送消息*/
    public void onewayPost(String action, String params, IServiceAidlCallback.Stub callback){
        Log.e(TAG,"onewayPost");
        if(isConnect()){
            try{
                iServiceAidlCall.onewayPost(action, params, callback);
            }catch (RemoteException e){
                e.printStackTrace();
                Log.e(TAG,e.toString());
            }
        }
    }

    /**发送消息*/
    public void uiPost(String action, String params, IServiceAidlCallback.Stub callback){
        Log.e(TAG,"uiPost");
        if(isConnect()){
            try{
                iServiceAidlCall.uiPost(action, params, callback);
            }catch (RemoteException e){
                e.printStackTrace();
                Log.e(TAG,e.toString());
            }
        }
    }

    /**发送消息*/
    public void asynPost(String action, String params, IServiceAidlCallback.Stub callback){
        Log.e(TAG,"asynPost");
        if(isConnect()){
            try{
                iServiceAidlCall.asynPost(action, params, callback);
            }catch (RemoteException e){
                e.printStackTrace();
                Log.e(TAG,e.toString());
            }
        }
    }

    /**订阅被观察者*/
    public void register(IClientAidlCall.Stub call){
        Log.e(TAG,"register");
        if(isConnect()){
            try{
                iServiceAidlCall.register(call);
            }catch (RemoteException e){
                e.printStackTrace();
                Log.e(TAG,e.toString());
            }
        }
    }

    /**解除订阅被观察者*/
    public void unregister(IClientAidlCall.Stub call){
        Log.e(TAG,"unregister");
        if(isConnect()){
            try{
                iServiceAidlCall.unregister(call);
            }catch (RemoteException e){
                e.printStackTrace();
                Log.e(TAG,e.toString());
            }
        }
    }

}