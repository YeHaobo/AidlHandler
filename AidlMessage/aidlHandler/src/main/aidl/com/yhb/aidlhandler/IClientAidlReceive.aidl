// IClientAidlReceive.aidl
package com.yhb.aidlhandler;

//**************************该接口用于客户端在服务端上的注册***************************
interface IClientAidlReceive {
    //action 动作名称：用于鉴别客户端需要执行的操作。
    //params 参数：客户端的传参。为更加灵活，接口未指定序列化的实体类作为参数，若需要传输对象可以使用两端统一的JSON进行传输
    void onReceived(String action, String params);
}