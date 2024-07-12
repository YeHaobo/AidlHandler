// IServiceAidlResult.aidl
package com.yhb.aidlhandler;

//***********************客户端远程调用服务后的回调*************************
interface IServiceAidlResult {
    //code 状态码：用于鉴别服务端是否执行成功。
    //params 参数：服务端返回参数。为更加灵活，接口未指定序列化的实体类作为参数，若需要传输对象可以使用两端统一的JSON进行传输
    void onResult(int code, String params);
}