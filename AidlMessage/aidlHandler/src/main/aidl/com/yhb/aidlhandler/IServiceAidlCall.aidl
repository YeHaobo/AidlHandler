// IServiceAidlCall.aidl
package com.yhb.aidlhandler;

import com.yhb.aidlhandler.IServiceAidlCallback;
import com.yhb.aidlhandler.IClientAidlCall;

//*********************************该接口用于客户端远程调用服务****************************
//***********************AIDL是支持并发的，所以服务端的接口实现需要保证线程安全****************************
interface IServiceAidlCall {
    //-----------------------------------asynPost、uiPost-----------------------------------------------------------------------
    //action 动作名称：用于鉴别客户端需要执行的操作。
    //params 参数：客户端的传参。为更加灵活，接口未指定序列化的实体类作为参数，若需要传输对象可以使用两端统一的JSON进行传输
    //callback 回调：服务端执行完成后，使用该接口回调至客户端
    //--------------------------------------------------------------------------------------------------------

    //注意:
    //客户端调用远程服务的方法后，被调用的方法在服务端的Binder线程池中，同时客户端线程也会被挂起。
    //这个时候如果服务端方法执行比较耗时的操作，就会导致客户端线程长时间阻塞在这里，而如果这个客户端线程是UI线程的话，就会导致客户端ANR。
    //换句话说，这些接口的调用和回调是同步的，所以客户端不能在UI线程里远程调用服务耗时的接口。
    //使用场景：
    //1、客户端需要同步回调在UI线程
    //2、服务端处理的不是耗时操作
    void uiPost(String action, String params, IServiceAidlCallback callback);

    //注意:
    //asynPost接口使用oneway修饰，oneway修饰的接口不能有返回值，也不能带out或inout的参数，参数只能为默认的in类型。
    //oneway 具有异步调用和串行化两种特性。
    //客户端调用远程服务的方法后，不需要挂起线程等待binder驱动回复，而是直接结束。这样就算服务端中做了耗时任务，也不会阻塞客户端的运行。
    //服务端所有进入asynPost的方法不会同时执行，binder 驱动会将他们串行化处理，排队一个一个调用，先进先出。
    //客户端的callback实现中不可以直接更新UI，需要切换线程。
    //客户端在使用该接口时尽量避免超高的并发,防止占满binder驱动的缓存。
    //使用场景：
    //1、客户端需要异步
    //2、服务端处理的是耗时操作
    oneway void asynPost(String action, String params, IServiceAidlCallback callback);


    //-------------------------------------register、unregister---------------------------------------------------------------------
    //作用：客户端注册接口至服务端，服务端在特定时候可以发送广播给所有已注册的接口、类似于观察者模式。
    //参数：call 消息接收，接收服务端下发的广播。
    //--------------------------------------------------------------------------------------------------------

    //注意：
    //在服务端使用回调发送广播时，客户端的回调（call）的实现所在的线程是由服务端调用时所在线程决定的。
    //比如：客户端在UI线程同时远程调用uiPost和asynPost，服务端在uiPost内部使用doAccept发送会回调至客户端UI线程，而在asynPost内部使用则回调至客户端binder工作线程。

    //注册
    void register(IClientAidlCall call);

    //解除注册
    void unregister(IClientAidlCall call);

}