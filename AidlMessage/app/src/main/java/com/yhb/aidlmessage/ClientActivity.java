package com.yhb.aidlmessage;

import android.os.Bundle;
import android.os.Looper;
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

    private static final String TAG = "ClientActivity";
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

        //创建连接
        clientAidlConnector = new ClientAidlConnector
                .Builder()
                .context(this)
                .packageName("com.yhb.aidlmessage")//连接服务的包名
                .serviceName("com.yhb.aidlmessage.MyService")//服务的name,也就是在AndroidManifest.xml内service中action标签的name属性
                .connectResult(new ConnectResult() {//连接回调
                    @Override
                    public void connected(ClientAidlPoster poster) {
                        //远程调用需要使用该发送者对象
                        clientAidlPoster = poster;
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
    }

    //实例回调接口
    IClientAidlCall.Stub clientAidlCall = new IClientAidlCall.Stub() {
        @Override
        public void accept(String action, String params) throws RemoteException {
            Log.e(TAG,"accept ThreadName: " + Thread.currentThread().getName());
            if(Looper.getMainLooper().getThread() == Thread.currentThread()){//判断是否在主线程
                Toast.makeText(ClientActivity.this,"action: " + action + "\nparams: " + params,Toast.LENGTH_SHORT).show();
            }
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
                //UI线程同步调用
                clientAidlPoster.uiPost("uiPost", "{...}", new IServiceAidlCallback.Stub() {
                    @Override
                    public void onResult(int code, String params) throws RemoteException {
                        //回调在当前UI线程
                        Toast.makeText(ClientActivity.this,"code: " + code + "\nparams: " + params,Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.btn4:
                //异步调用
                clientAidlPoster.asynPost("asynPost", "{...}", new IServiceAidlCallback.Stub() {
                    @Override
                    public void onResult(final int code, final String params) throws RemoteException {
                        //回调在工作线程
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ClientActivity.this,"code: " + code + "\nparams: " + params,Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        clientAidlConnector.disconnect();
        super.onDestroy();
    }

}