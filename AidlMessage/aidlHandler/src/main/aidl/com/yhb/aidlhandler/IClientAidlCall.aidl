// IClientAidlCall.aidl
package com.yhb.aidlhandler;

//**************************该接口用于客户端在服务端上的注册***************************
//***客户端向服务端注册后，服务端可以在客户端解注册之前的任意时间从该接口回调客户端***
interface IClientAidlCall {
    void accept(String action, String params);
}