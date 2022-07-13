package com.yhb.aidlmessage;

import android.os.RemoteException;
import android.util.Log;
import com.yhb.aidlhandler.IServiceAidlCallback;
import com.yhb.aidlhandler.service.BaseAidlService;

public class MyService extends BaseAidlService {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("onCreate",Thread.currentThread().getName());
    }

    /**
     * 1、可以处理耗时操作
     * 2、串行执行，先进先出
     */
    @Override
    public void onewayPost(String action, String params, IServiceAidlCallback callback) {
        Log.e("onewayPost",Thread.currentThread().getName());
        try {
            Thread.sleep(7*1000);//模拟耗时
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            callback.onResult(200,"onewayPost-result");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 1、不能处理耗时操作
     * 2、使用callback回调必须在当前线程
     */
    @Override
    public void uiPost(String action, String params, IServiceAidlCallback callback) {
        Log.e("uiPost",Thread.currentThread().getName());
        try {
            callback.onResult(200,"uiPost-result");//使用callback回调必须在当前线程
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 1、可以处理耗时操作
     * 2、并行执行，异步回调
     */
    @Override
    public void asynPost(String action, String params, IServiceAidlCallback callback) {
        Log.e("asynPost",Thread.currentThread().getName());
        try {
            Thread.sleep(7*1000);//模拟耗时
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            callback.onResult(200,"asynPost-result");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}