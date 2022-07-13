package com.yhb.aidlhandler.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.yhb.aidlhandler.IServiceAidlCall;

/**连接者*/
public class ClientAidlConnector {
    private static final String TAG = "ClientAidlConnector";

    /**上下文*/
    private Context context;

    /**包名*/
    private String packageName;

    /**服务名*/
    private String serviceName;

    /**连接回调*/
    private ConnectResult connectResult;

    /**已绑定服务端接口*/
    private IServiceAidlCall iServiceAidlCall;

    /**私有构造*/
    private ClientAidlConnector(Builder builder) {
        this.context = builder.context;
        this.packageName = builder.packageName;
        this.serviceName = builder.serviceName;
        this.connectResult = builder.connectResult;
    }

    /**Builder模式*/
    public static class Builder{
        private Context context;
        private String packageName;
        private String serviceName;
        private ConnectResult connectResult;
        public Builder context(Context context){
            this.context = context;
            return this;
        }
        public Builder packageName(String packageName){
            this.packageName = packageName;
            return this;
        }
        public Builder serviceName(String serviceName){
            this.serviceName = serviceName;
            return this;
        }
        public Builder connectResult(ConnectResult connectResult){
            this.connectResult = connectResult;
            return this;
        }
        public ClientAidlConnector build(){
            return new ClientAidlConnector(this);
        }
    }

    /**开始连接*/
    public void connect(){
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(packageName, serviceName);
        intent.setComponent(componentName);
        context.bindService(intent,connection, Context.BIND_AUTO_CREATE);
    }

    /**断开连接*/
    public void disconnect(){
        context.unbindService(connection);//解除绑定
    }

    /**服务连接*/
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG,"The service status is connected");
            iServiceAidlCall = IServiceAidlCall.Stub.asInterface(service);
            try {
                iServiceAidlCall.asBinder().linkToDeath(deathRecipient,0);//设置死亡代理
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if(connectResult != null){
                connectResult.onResult(new ClientAidlPoster(iServiceAidlCall));
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG,"The service status is disconnected");
        }
    };

    /**连接死亡代理*/
    private IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.e(TAG,"Try to reconnect the service");
            iServiceAidlCall.asBinder().unlinkToDeath(deathRecipient,0);
            iServiceAidlCall = null;
            connect();
        }
    };

}