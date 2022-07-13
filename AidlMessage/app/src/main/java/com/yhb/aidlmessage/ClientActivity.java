package com.yhb.aidlmessage;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.yhb.aidlhandler.IClientAidlCall;
import com.yhb.aidlhandler.IServiceAidlCallback;
import com.yhb.aidlhandler.client.ClientAidlConnector;
import com.yhb.aidlhandler.client.ClientAidlPoster;
import com.yhb.aidlhandler.client.ConnectResult;

/**客户端（子进程）*/
public class ClientActivity extends AppCompatActivity implements View.OnClickListener {

    private ClientAidlPoster clientAidlPoster;
    private ClientAidlConnector clientAidlConnector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
        findViewById(R.id.btn3).setOnClickListener(this);
        findViewById(R.id.btn4).setOnClickListener(this);
        findViewById(R.id.btn5).setOnClickListener(this);

        //创建连接
        clientAidlConnector = new ClientAidlConnector
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
    }

    //实例回调接口
    IClientAidlCall.Stub clientAidlCall = new IClientAidlCall.Stub() {
        @Override
        public void accept(String action, String params) throws RemoteException {
            //回调在工作线程
            Log.e("accept",Thread.currentThread().getName());
        }
    };

    @Override
    public void onClick(View v) {
        if(clientAidlPoster == null) return;
        switch (v.getId()){
            case R.id.btn1:
                //注册
                clientAidlPoster.register(clientAidlCall);
                break;
            case R.id.btn2:
                //解注册
                clientAidlPoster.unregister(clientAidlCall);
                break;
            case R.id.btn3:
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
                break;
            case R.id.btn4:
                //UI线程同步调用
                clientAidlPoster.uiPost("222", "bbbb", new IServiceAidlCallback.Stub() {
                    @Override
                    public void onResult(int code, String params) throws RemoteException {
                        //回调在当前UI线程
                        Toast.makeText(ClientActivity.this,"uiPost->onResult：{" + code + params + "}",Toast.LENGTH_SHORT).show();
                        Log.e("uiPost->onResult",Thread.currentThread().getName());
                    }
                });
                break;
            case R.id.btn5:
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
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clientAidlConnector.disconnect();
    }

}