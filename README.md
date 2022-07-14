# AidlHandler

AidlHandler是基于AIDL的Android多进程通讯解决方案。  
支持多客户端连接和并发，客户端可以同步或异步的远程调用服务。支持服务端主动向客户端发送消息，客户端需要先向服务端注册，服务端根据需要主动发起广播，回调至客户端所有已注册接口。在使用过程中，服务端和客户端需要统一消息动作标识。为了良好的可拓展性，项目未使用固定的序列化实体类传参，所以实体参数需要通过JSON的方式传输和解析。 

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
    implementation 'com.github.YeHaobo:AidlHandler:1.1'
    ... ...
  }
```

## 服务端使用

### 1、新建Service实现BaseAidlService

注意：继承BaseAidlService的Service中的方法和接口实现需要保证线程安全

```java
public class MyService extends BaseAidlService {

    /**
     * 1、不能处理耗时操作
     * 2、使用callback回调必须在当前线程
     */
    @Override
    public void uiPost(String action, String params, IServiceAidlCallback callback) {
        try {
            callback.onResult(200,action + " is success");//使用callback回调必须在当前线程
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 1、异步调用
     * 2、可以处理耗时操作
     */
    @Override
    public void asynPost(String action, String params, IServiceAidlCallback callback) {
        try {
            Thread.sleep(7*1000);//模拟耗时
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            callback.onResult(200,action + " is success");
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
注意：在服务端使用回调发送广播时，客户端的call实现所在的线程是由服务端调用时所在线程决定的。   
比如：客户端在UI线程同时远程调用uiPost和asynPost，服务端在uiPost内部发送广播则回调至客户端UI线程，而在asynPost内部发送广播则回调至客户端binder工作线程。   
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
                .serviceName("com.yhb.aidlmessage.MyService")//服务的name,也就是在AndroidManifest.xml内service中action标签的name属性
                .connectResult(new ConnectResult() {
                    @Override
                    public void connected(ClientAidlPoster poster) {
                        //已连接回调
                        clientAidlPoster = poster;//远程调用需要使用该发送者对象
                    }
                    @Override
                    public boolean isReconnect() {
                        //连接异常断开回调
                        return true;//true:重连 false:不重连
                    }
                })
                .build();

        //连接服务
        clientAidlConnector.connect();
```

### 2、远程调用
```java

      //同步调用
      clientAidlPoster.uiPost("uiPost", "{...}", new IServiceAidlCallback.Stub() {
          @Override
          public void onResult(int code, String params) throws RemoteException {
              //回调在当前线程
              Toast.makeText(ClientActivity.this,"code: " + code + "\nparams: " + params,Toast.LENGTH_SHORT).show();
          }
      });

      //异步调用
      clientAidlPoster.asynPost("asynPost", "{...}", new IServiceAidlCallback.Stub() {
          @Override
          public void onResult(final int code, final String params) throws RemoteException {
              //回调在binder工作线程
              runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      Toast.makeText(ClientActivity.this,"code: " + code + "\nparams: " + params,Toast.LENGTH_SHORT).show();
                  }
              });
          }
      });
```
### 3、注册/解注册
```java
    //实例回调接口
    IClientAidlCall.Stub clientAidlCall = new IClientAidlCall.Stub() {
        @Override
        public void accept(String action, String params) throws RemoteException {
            if(Looper.getMainLooper().getThread() == Thread.currentThread()){//判断是否在主线程
                Toast.makeText(ClientActivity.this,"action: " + action + "\nparams: " + params,Toast.LENGTH_SHORT).show();
            }else{
                Log.e(TAG,"accept ThreadName: " + Thread.currentThread().getName());
            }
        }
    };
    
    //注册
    clientAidlPoster.register(clientAidlCall);
    
    //解注册
    clientAidlPoster.unregister(clientAidlCall);
    
```

### 4、断开连接
提示：在使用完成后建议调用断开连接，减少服务端资源占用
```java
    @Override
    protected void onDestroy() {
        clientAidlConnector.disconnect();//断开连接
        super.onDestroy();
    }
```


## 问题及其他
1、依赖的包经过jitpack编译后可能丢失源码中的注释，若查看详细注释请在Git上的源码查阅。    
2、若客户端无法连接，请确认服务端进程是否未启动或被杀死。  


