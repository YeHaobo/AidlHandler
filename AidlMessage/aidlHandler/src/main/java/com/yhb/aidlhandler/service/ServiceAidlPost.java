package com.yhb.aidlhandler.service;

import com.yhb.aidlhandler.IClientAidlReceive;
import com.yhb.aidlhandler.IServiceAidlResult;

/**
 * *****************************************服务端接收请求的接口********************************************
 * ********************************注意：接口的实现必须要保证线程安全！！！**********************************
 */
public interface ServiceAidlPost {
    /**同步调用*/
    void syncPost(String action, String params, IServiceAidlResult result);
    /**异步调用*/
    void asyncPost(String action, String params, IServiceAidlResult result);
    /**注册*/
    void registerReceive(IClientAidlReceive receive);
    /**解注册*/
    void unregisterReceive(IClientAidlReceive receive);
}