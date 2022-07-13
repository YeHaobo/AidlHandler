package com.yhb.aidlmessage;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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

        clientAidlConnector = new ClientAidlConnector
                .Builder()
                .context(this)
                .packageName("com.yhb.aidlmessage")
                .serviceName("com.yhb.aidlmessage.MyService")
                .connectResult(new ConnectResult() {
                    @Override
                    public void onResult(ClientAidlPoster poster) {
                        clientAidlPoster = poster;
                    }
                })
                .build();

        clientAidlConnector.connect();
    }


    private IClientAidlCall.Stub clientAidlCall = new IClientAidlCall.Stub() {
        @Override
        public void accept(String action, String params) throws RemoteException {
            Log.e("accept",Thread.currentThread().getName());
        }
    };

    @Override
    public void onClick(View v) {
        if(clientAidlPoster == null) return;
        switch (v.getId()){
            case R.id.btn1:
                clientAidlPoster.register(clientAidlCall);
                break;
            case R.id.btn2:
                clientAidlPoster.unregister(clientAidlCall);
                break;
            case R.id.btn3:
                clientAidlPoster.onewayPost("222222", "4444", new IServiceAidlCallback.Stub() {
                    @Override
                    public void onResult(int code, String params) throws RemoteException {
                        Log.e("onewayPost->onResult",Thread.currentThread().getName());
                    }
                });
                break;
            case R.id.btn4:
                clientAidlPoster.uiPost("222222", "4444", new IServiceAidlCallback.Stub() {
                    @Override
                    public void onResult(int code, String params) throws RemoteException {
                        Log.e("uiPost->onResult",Thread.currentThread().getName());
                    }
                });
                break;
            case R.id.btn5:
                clientAidlPoster.asynPost("222222", "4444", new IServiceAidlCallback.Stub() {
                    @Override
                    public void onResult(int code, String params) throws RemoteException {
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