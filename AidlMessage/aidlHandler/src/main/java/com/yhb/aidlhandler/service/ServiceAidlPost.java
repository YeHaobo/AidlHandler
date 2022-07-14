package com.yhb.aidlhandler.service;

import com.yhb.aidlhandler.IServiceAidlCallback;

/**
 * *****************************************服务端接收请求的接口*******************************************
 * ********************************注意：接口的实现必须要保证线程安全！！！**********************************
 */
public interface ServiceAidlPost {
    /**
     * 同步调用，不要在该方法中执行耗时的操作
     * callback需要与客户端远程调用所在的线程同步，不要在其他线程中使用callback进行回调
     */
    void uiPost(String action, String params, IServiceAidlCallback callback);
    /**
     * 异步调用，可执行耗时操作
     */
    void asynPost(String action, String params, IServiceAidlCallback callback);
}