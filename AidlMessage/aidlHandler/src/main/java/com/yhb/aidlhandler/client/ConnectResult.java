package com.yhb.aidlhandler.client;

/**服务连接回调接口*/
public interface ConnectResult {
    /**连接完成回调*/
    void connected(ClientAidlPoster poster);
    /**异常断开回调，返回是否需要重连*/
    boolean disconnected();
}