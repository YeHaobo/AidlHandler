// IServiceAidl.aidl
package com.yhb.aidlhandler;

import com.yhb.aidlhandler.IClientAidlReceive;
import com.yhb.aidlhandler.IServiceAidlResult;

//******************************************该接口由远程服务实现，供客户端远程调用****************************************************
//***********************由于客户端的远程调用是可以从不同进程和不同线程发起的，所以服务端的接口实现需要保证线程安全****************************
interface IServiceAidl {

    //-------------------------------------------oneway关键字修饰------------------------------------------------
    //无oneway：
    //客户端远程调用没有oneway关键字修饰的方法后，该方法会在服务端的Binder线程池中执行，同时客户端的调用线程也会被挂起。只有当服务端执行完毕后，客户端被挂起的线程才会继续运行。
    //所以如果服务端执行耗时的操作，客户端不可以在UI线程调用，否则可能引发ANR。
    //换句话说，没有oneway关键字修饰的接口的调用是同步的，所以客户端不能在UI线程里远程调用服务耗时的接口。
    //有oneway：
    //oneway修饰的接口不能有返回值，也不能带out或inout的参数，参数只能为默认的in类型。
    //客户端远程调用有oneway关键字修饰的方法后，调用线程不会被挂起，所以服务端中即使做了耗时任务，也不影响客户端的调用线程。
    //同一个binder服务进入方法不会同时执行，binder驱动会将它们串行化后排队依次调用，先进先出。所以远程服务实现该接口时尽量避免超高并发，防止占满binder驱动的缓存。


    //-------------------------------------------syncPost、asyncPost------------------------------------------------
    //action 动作名称：用于鉴别客户端需要执行的操作。
    //params 参数：客户端的传参。为更加灵活，接口未指定序列化的实体类作为参数，若需要传输对象可以使用两端统一的JSON进行传输
    //result 回调：服务端执行完成后，使用该接口回调至客户端

    //同步调用（阻塞）
    //1、客户端的调用与回调在相同线程（服务端需要直接在syncPost的当前线程使用result回调，若在其他线程回调则客户端会回调在binder线程）
    //2、同步阻塞式的执行与回调
    void syncPost(String action, String params, IServiceAidlResult result);

    //异步调用（非阻塞）
    //1、客户端调用后回调在Binder线程中
    //2、异步非阻塞式的执行与回调
    oneway void asyncPost(String action, String params, IServiceAidlResult result);


    //-----------------------------------------------registerReceive、unregisterReceive----------------------------------------------
    //receive 接口：向服务端注册一个接口，服务端可以通过该接口发送信息至客户端。类似于观察者模式。

    //注册
    void registerReceive(IClientAidlReceive receive);

    //解注册
    void unregisterReceive(IClientAidlReceive receive);

}