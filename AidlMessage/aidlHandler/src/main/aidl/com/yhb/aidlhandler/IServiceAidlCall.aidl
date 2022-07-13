// IServiceAidlCall.aidl
package com.yhb.aidlhandler;

import com.yhb.aidlhandler.IServiceAidlCallback;
import com.yhb.aidlhandler.IClientAidlCall;

//************************该接口用于客户端远程调用服务端****************************
//***********************服务端的接口实现需要保证线程安全****************************
interface IServiceAidlCall {
    //-----------------------------------onewayPost、uiPost、asynPost-----------------------------------------------------------------------
    //
    //作用：供客户端远程调用的服务端接口
    //
    //参数：
    //action 动作名称：用于鉴别客户端需要执行的操作。
    //params 参数：客户端的传参。为更加灵活，接口未指定序列化的实体类，若需要传输对象可以使用两端统一的JSON进行传输
    //callback 回调：服务端调用完成后，使用该接口回调至客户端
    //--------------------------------------------------------------------------------------------------------

    //注意:
    //onewayPost接口使用 oneway 修饰，oneway 具有异步调用和串行化两种特性。
    //1、onewayPost接口在客户端远程调用后将会运行在服务端的Binder线程池中，服务端运行完成后将会回调至客户端的binder工作线程中。
    //  客户端的callback实现中不可以直接更新UI，需要切换线程。
    //2、onewayPost接口在客户端并发调用服务端时，服务端执行过程为串行，当一个方法在执行时其他线程将会被阻塞，先进先出。
    //  客户端在使用该接口时尽量避免超高的并发,防止占满binder驱动的缓存。

    //使用场景：
    //1、服务端处理的是耗时操作
    //2、服务端需要一个一个的串行处理，不需要并发响应
    //3、客户端不会超高并发的调用此接口
    oneway void onewayPost(String action, String params, IServiceAidlCallback callback);

    //注意:
    //1、uiPost接口在客户端远程调用后将会运行在服务端当前的service线程中，服务端运行完成后将会回调至客户端的调用线程。
    //  即客户端在哪个线程远程调用uiPost，就会在哪个线程回调。

    //使用场景：
    //1、客户端需要同步回调在UI线程
    //2、服务端处理的不是耗时操作
    void uiPost(String action, String params, IServiceAidlCallback callback);

    //注意：
    //asynPost接口在客户端远程调用后服务端将会开启子线程执行，所有线程是并行的，执行完成后将会回调在客户端的binder工作线程。

    //使用场景：
    //1、服务端需要处理的是耗时操作
    //2、需要异步，回调在客户端的工作线程中
    //3、服务端需要并行处理，或客户端需要并发响应
    void asynPost(String action, String params, IServiceAidlCallback callback);


    //-------------------------------------register、unregister---------------------------------------------------------------------
    //作用：客户端注册接口至服务端，服务端在特定时候可以发送广播给所有已注册的接口、类似于观察者模式。
    //参数：call 消息接收，接收服务端下发的广播。
    //注意：call回调在客户端的工作线程
    //--------------------------------------------------------------------------------------------------------

    //注册
    void register(IClientAidlCall call);

    //解除注册
    void unregister(IClientAidlCall call);

}