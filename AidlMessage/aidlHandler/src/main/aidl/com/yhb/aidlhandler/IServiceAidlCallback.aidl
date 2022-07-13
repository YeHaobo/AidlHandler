// IServiceAidlCallback.aidl
package com.yhb.aidlhandler;

//***********************该接口用于客户端远程调用服务端后的回调*************************
//**********客户端远程调用服务端，在服务端处理完成后通过该接口回调给客户端************
interface IServiceAidlCallback {
    void onResult(int code, String params);
}