package com.yhb.aidlmessage;

import android.os.RemoteException;
import android.util.Log;
import com.yhb.aidlhandler.IServiceAidlCallback;
import com.yhb.aidlhandler.service.BaseAidlService;

public class MyService extends BaseAidlService {

    /**
     * 注意：该实现类需要保证线程安全
     */

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("onCreate",Thread.currentThread().getName());
    }

    @Override
    public void onewayPost(String action, String params, IServiceAidlCallback callback) {
        /**
         * 1、可以处理耗时操作
         * 2、串行执行，先进先出
         */
        Log.e("onewayPost",Thread.currentThread().getName());
        try {
            callback.onResult(999,"action");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void uiPost(String action, String params, IServiceAidlCallback callback) {
        /**
         * 1、不能处理耗时操作
         * 2、只能在当前线程使用callback回调
         */
        Log.e("uiPost",Thread.currentThread().getName());
        try {
            callback.onResult(999,"action");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void asynPost(String action, String params, IServiceAidlCallback callback) {
        /**
         * 1、可以处理耗时操作
         * 2、并行执行，异步回调
         */
        Log.e("asynPost",Thread.currentThread().getName());
        try {
            callback.onResult(999,"action");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}