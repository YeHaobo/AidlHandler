# AidlHandler

AidlHandler是基于AIDL的Android多进程通讯解决方案。  
<1> 支持多客户端连接和远程调用。  
<2> 支持服务端主动向客户端下发消息。   

***

## 依赖

### 1、在Project的build.gradle文件中添加
```java
  allprojects {
    repositories {
      ... ...
      maven { url "https://jitpack.io" }
      ... ...
    }
  }
```

### 2、在app的build.gradle文件中添加
```java
  dependencies {
    ... ...
    implementation 'com.github.YeHaobo:AidlHandler:1.2'
    ... ...
  }
```

## 服务端

### 1、新建Service实现BaseAidlService
注意：继承BaseAidlService的Service中的方法和接口实现需要保证线程安全
```java
public class MyService extends BaseAidlService {

    /**同步调用*/
    @Override
    public void syncPost(String action, String params, IServiceAidlResult result) {
        Log.e(TAG, "syncPost " + action + " " + params);
        try {
            result.onResult(200, action);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**异步调用*/
    @Override
    public void asyncPost(String action, String params, IServiceAidlResult result) {
        Log.e(TAG, "asyncPost " + action + " " + params);
        try {
            result.onResult(200, action);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**客户端注册*/
    @Override
    public void registerReceive(IClientAidlReceive receive) {
        try {
            Log.e(TAG, "register " + receive.asBinder().getInterfaceDescriptor());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**客户端解注册*/
    @Override
    public void unregisterReceive(IClientAidlReceive receive) {
        try {
            Log.e(TAG, "unregister " + receive.asBinder().getInterfaceDescriptor());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
```

### 2、在AndroidManifest.xml中注册服务
```java
      <service android:name=".MyService" android:enabled="true" android:exported="true"/>
```

### 3、向客户端发送消息  
```java
public class MyService extends BaseAidlService {
    ... ...
    private void sendMsg(){
      ... ...
      clientReceive("action", "params");//发送消息，客户端根据action来判断是否需要操作
      ... ...
    }
    ... ...
}
```

## 客户端使用

### 1、连接服务端
```java
    //客户端发送者
    private ClientAidlPoster aidlPoster;
    //aidl连接器
    private ClientAidlConnector aidlConnector = new ClientAidlConnector
            .Builder()
            .context(this)
            .packageName("com.yhb.aidlmessage")//包名
            .serviceName("com.yhb.aidlmessage.MyService")//服务名
            .connectResult(new ConnectResult() {
                @Override
                public void connected(ClientAidlPoster poster) {//已连接回调
                    Log.e(TAG, "connected");
                    aidlPoster = poster;//远程调用需要使用该发送者对象
                }
                @Override
                public boolean disconnected() {//连接断开回调
                    Log.e(TAG, "disconnected");
                    return true;//true:重连 false:不重连
                }
            }).build();

    //连接服务
    aidlConnector.connect();
```

### 2、远程调用
```java
      //同步调用（阻塞）
      aidlPoster.syncPost("111", "111", new IServiceAidlResult.Stub() {
          @Override
          public void onResult(int code, String params) throws RemoteException {
              Log.e("syncPost",code + " " + params);
          }
      });

      //异步调用（非阻塞）
      aidlPoster.asyncPost("222", "222", new IServiceAidlResult.Stub() {
          @Override
          public void onResult(int code, String params) throws RemoteException {
              Log.e("asyncPost",code + " " + params);
          }
      });
```

### 3、注册/解注册
```java
  //实例回调接口
  private IClientAidlReceive.Stub clientReceive = new IClientAidlReceive.Stub() {
      @Override
      public void onReceived(String action, String params) throws RemoteException {
          Log.e("onReceived",action + " " + params);
      }
  };

  //注册
  aidlPoster.registerReceive(clientReceive);
  
  //解注册
  aidlPoster.unregisterReceive(clientReceive);
    
```

### 4、断开连接
提示：在使用完成后建议调用断开连接，减少服务端资源占用
```java
    @Override
    protected void onDestroy() {
        aidlConnector.disconnect();//断开连接
        super.onDestroy();
    }
```

## 问题及其他
1、在使用过程中两端需要处理好线程同步，服务端需要处理好线程安全。  
2、若客户端无法连接，请确认服务端进程是否未启动或被杀死。   
3、项目经过编译后可能丢失注释，详细注释请看源码。    


