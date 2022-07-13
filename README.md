# AidlHandler

AidlHandler是基于AIDL的Android多进程通讯解决方案。  
1、支持多客户端并发。  
2、支持客户端远程同步调用、异步调用、队列式调用服务端。  
3、支持服务端主动向客户端发送消息，客户端需要先向服务端注册，服务端主动发起回调客户端所有已注册接口。  
4、在使用当中服务端和客户端需要统一消息动作标识。为了良好的可拓展性，项目未使用固定的序列化实体类传参，所以实体参数需要通过JSON的方式传输和解析。

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
    implementation 'com.android.support:appcompat-v7:28.0.0'//AndroidX项目不用添加support-v7包
    implementation 'com.github.YeHaobo:AidlHandler:1.0'
    ... ...
  }
```

## 服务端使用

### 1、新建Service实现BaseAidlService

注意：继承BaseAidlService的Service中的方法和接口实现需要保证线程安全

```java
public class MyService extends BaseAidlService {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("onCreate",Thread.currentThread().getName());
    }

    /**
     * 1、可以处理耗时操作
     * 2、串行执行，先进先出
     */
    @Override
    public void onewayPost(String action, String params, IServiceAidlCallback callback) {
        Log.e("onewayPost",Thread.currentThread().getName());
        try {
            Thread.sleep(7*1000);//模拟耗时
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            callback.onResult(200,"onewayPost-result");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 1、不能处理耗时操作
     * 2、使用callback回调必须在当前线程
     */
    @Override
    public void uiPost(String action, String params, IServiceAidlCallback callback) {
        Log.e("uiPost",Thread.currentThread().getName());
        try {
            callback.onResult(200,"uiPost-result");//使用callback回调必须在当前线程
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 1、可以处理耗时操作
     * 2、并行执行，异步回调
     */
    @Override
    public void asynPost(String action, String params, IServiceAidlCallback callback) {
        Log.e("asynPost",Thread.currentThread().getName());
        try {
            Thread.sleep(7*1000);//模拟耗时
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            callback.onResult(200,"asynPost-result");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
```
### 2、在AndroidManifest.xml中注册服务
```java
      <service android:name=".MyService" android:enabled="true" android:exported="true">
          <intent-filter>
              <action android:name="com.yhb.aidlmessage.MyService"/>
          </intent-filter>
      </service>
```
### 3、Service中发送广播
```java
      doAccept("action","params");//客户端可以根据action来判断是否需要操作
```
## 客户端使用
### 1、连接服务端
```java
        //创建连接
        ClientAidlConnector clientAidlConnector = new ClientAidlConnector
                .Builder()
                .context(this)
                .packageName("com.yhb.aidlmessage")//连接服务的包名
                .serviceName("com.yhb.aidlmessage.MyService")//服务的name,也就是在AndroidManifest.xml中service标签下的name属性
                .connectResult(new ConnectResult() {//连接回调
                    @Override
                    public void onResult(ClientAidlPoster poster) {
                        //远程调用需要使用该发送者对象
                        clientAidlPoster = poster;
                    }
                })
                .build();

        //连接服务
        clientAidlConnector.connect();
```

### 2、远程调用
```java

      //串行调用
      clientAidlPoster.onewayPost("111", "aaaa", new IServiceAidlCallback.Stub() {
          @Override
          public void onResult(final int code, final String params) throws RemoteException {
              //回调在工作线程
              runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      Toast.makeText(ClientActivity.this,"onewayPost->onResult：{" + code + params + "}",Toast.LENGTH_SHORT).show();
                  }
              });
              Log.e("onewayPost->onResult","ThreadName = "+Thread.currentThread().getName());
          }
      });

      //UI线程同步调用
      clientAidlPoster.uiPost("222", "bbbb", new IServiceAidlCallback.Stub() {
          @Override
          public void onResult(int code, String params) throws RemoteException {
              //回调在当前UI线程
              Toast.makeText(ClientActivity.this,"uiPost->onResult：{" + code + params + "}",Toast.LENGTH_SHORT).show();
              Log.e("uiPost->onResult",Thread.currentThread().getName());
          }
      });

      //异步调用
      clientAidlPoster.asynPost("333", "cccc", new IServiceAidlCallback.Stub() {
          @Override
          public void onResult(final int code, final String params) throws RemoteException {
              //回调在工作线程
              runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      Toast.makeText(ClientActivity.this,"asynPost->onResult：{" + code + params + "}",Toast.LENGTH_SHORT).show();
                  }
              });
              Log.e("asynPost->onResult",Thread.currentThread().getName());
          }
      });
```
### 3、注册/解注册
```java
    //实例回调接口
    IClientAidlCall.Stub clientAidlCall = new IClientAidlCall.Stub() {
        @Override
        public void accept(String action, String params) throws RemoteException {
            //回调在工作线程
            Log.e("accept",Thread.currentThread().getName());
        }
    };
    
    //注册
    clientAidlPoster.register(clientAidlCall);
    
    //解注册
    clientAidlPoster.unregister(clientAidlCall);
    
```

### 4、断开连接
提示：在使用完成后建议断开连接，减少服务端资源占用
```java
    clientAidlConnector.disconnect();
```


## 问题及其他
1、依赖的包经过jitpack编译后可能丢失源码中的注释，若要看注释请打开源码查阅。  
2、若客户端无法连接，请确认服务端进程是否未启动或被杀死。  


