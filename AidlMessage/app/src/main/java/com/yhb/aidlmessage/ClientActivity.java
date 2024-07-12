package com.yhb.aidlmessage;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.yhb.aidlhandler.IClientAidlReceive;
import com.yhb.aidlhandler.IServiceAidlResult;
import com.yhb.aidlhandler.client.ClientAidlConnector;
import com.yhb.aidlhandler.client.ClientAidlPoster;
import com.yhb.aidlhandler.client.ConnectResult;

/**客户端（子进程）*/
public class ClientActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ClientActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
        findViewById(R.id.btn3).setOnClickListener(this);
        findViewById(R.id.btn4).setOnClickListener(this);
        clientAidlConnector.connect();//连接服务
    }

    @Override
    protected void onDestroy() {
        clientAidlConnector.disconnect();//断开连接
        super.onDestroy();
    }

    private ClientAidlPoster clientAidlPoster;

    private ClientAidlConnector clientAidlConnector = new ClientAidlConnector
            .Builder()
            .context(this)
            .packageName("com.yhb.aidlmessage")//连接服务的包名
            .serviceName("com.yhb.aidlmessage.MyService")//服务的name,也就是在AndroidManifest.xml内service中action标签的name属性
            .connectResult(new ConnectResult() {
                @Override
                public void connected(ClientAidlPoster poster) {
                    Log.e(TAG, "connected");
                    clientAidlPoster = poster;//远程调用需要使用该发送者对象
                }
                @Override
                public boolean disconnected() {
                    Log.e(TAG, "disconnected");
                    return true;//true:重连 false:不重连
                }
            }).build();

    //实例回调接口
    IClientAidlReceive.Stub clientReceive = new IClientAidlReceive.Stub() {
        @Override
        public void onReceived(String action, String params) throws RemoteException {
            Log.e("onReceived",action + " " + params);
        }
    };

    @Override
    public void onClick(View v) {
        if(clientAidlPoster == null) return;
        switch (v.getId()){
            case R.id.btn1://注册
                clientAidlPoster.registerReceive(clientReceive);
                break;
            case R.id.btn2://解注册
                clientAidlPoster.unregisterReceive(clientReceive);
                break;
            case R.id.btn3://阻塞调用
                clientAidlPoster.syncPost("111", "111", new IServiceAidlResult.Stub() {
                    @Override
                    public void onResult(int code, String params) throws RemoteException {
                        Log.e("syncPost",code + " " + params);
                    }
                });
                break;
            case R.id.btn4:
                clientAidlPoster.asyncPost("222", "222", new IServiceAidlResult.Stub() {
                    @Override
                    public void onResult(int code, String params) throws RemoteException {
                        Log.e("asyncPost",code + " " + params);
                    }
                });
                break;
            default:
                break;
        }
    }

}