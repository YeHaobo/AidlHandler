package com.yhb.aidlmessage;

import android.os.RemoteException;
import com.yhb.aidlhandler.IServiceAidlCallback;
import com.yhb.aidlhandler.service.BaseAidlService;

public class MyService extends BaseAidlService {

    /**
     * 1、不能处理耗时操作
     * 2、使用callback回调必须在当前线程
     */
    @Override
    public void uiPost(String action, String params, IServiceAidlCallback callback) {
        try {
            callback.onResult(200,action + " is success");//使用callback回调必须在当前线程
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 1、异步调用
     * 2、可以处理耗时操作
     */
    @Override
    public void asynPost(String action, String params, IServiceAidlCallback callback) {
        try {
            Thread.sleep(7*1000);//模拟耗时
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            callback.onResult(200,action + " is success");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}