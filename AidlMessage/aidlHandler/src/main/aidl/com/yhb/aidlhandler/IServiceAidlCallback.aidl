// IServiceAidlCallback.aidl
package com.yhb.aidlhandler;

//***********************该接口用于客户端远程调用服务后回调*************************
//**********客户端远程调用服务，在服务端处理完成后通过该接口回调客户端************
interface IServiceAidlCallback {
    void onResult(int code, String params);
}