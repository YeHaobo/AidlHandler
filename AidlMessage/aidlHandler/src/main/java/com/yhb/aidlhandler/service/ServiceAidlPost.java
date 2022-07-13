package com.yhb.aidlhandler.service;

import com.yhb.aidlhandler.IServiceAidlCallback;

/**
 * *****************************************服务端接收请求的接口*******************************************
 * ********************************注意：接口的实现必须要保证线程安全！！！**********************************
 */
public interface ServiceAidlPost {
    /**
     * 运行在服务端Binder线程池中
     * callback回调在客户端的工作线程
     * 一个一个串行执行，先进先出
     */
    void onewayPost(String action, String params, IServiceAidlCallback callback);
    /**
     * 运行在当前service线程
     * callback需要与客户端远程调用所在的线程同步，所以尽量不要在其他线程中使用callback进行回调
     * 不要在该方法中执行耗时的操作
     */
    void uiPost(String action, String params, IServiceAidlCallback callback);
    /**
     * 运行在内部线程池
     * callback回调在客户端的binder线程池
     * 异步并行执行，并发回调
     */
    void asynPost(String action, String params, IServiceAidlCallback callback);

}