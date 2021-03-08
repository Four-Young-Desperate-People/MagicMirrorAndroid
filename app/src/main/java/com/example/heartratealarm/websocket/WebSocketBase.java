package com.example.heartratealarm.websocket;

import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketBase extends WebSocketListener {
    private static final String TAG = "WebSocketBase2";
    private static final String ENDPOINT = "ws://192.168.1.92:3683/android";
    private final Object messageLock;
    private String message;

    private final WebSocket webSocket;

    public WebSocketBase() {
        Request request = new Request.Builder().url(ENDPOINT)
                .build();
        OkHttpClient client = new OkHttpClient();
        webSocket = client.newWebSocket(request, this);
        messageLock = new Object();
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        Log.i(TAG, String.format("successfully connected to %s", ENDPOINT));
    }

    public void send(String string) {
        webSocket.send(string);
    }

    public void close() {
        webSocket.close(1000, null);
    }

    public String getMessage() throws InterruptedException {
        synchronized (messageLock) {
            messageLock.wait();
            return message;
        }
    }

    public Observable<String> getMessageObservable() {
        return Observable.fromCallable(this::getMessage).repeat().subscribeOn(Schedulers.newThread());
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        synchronized (messageLock) {
            message = text;
            messageLock.notifyAll();
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        Log.e(TAG, "we should not be receiving binary data...");
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        Log.e(TAG, "we should not be receiving binary data...");
        messageLock.notify();
        super.onClosing(webSocket, code, reason);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        Log.e(TAG, "Received CLOSE from server. This should not happen...");
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        Log.e(TAG, "Received CLOSE from server. This should not happen...");
    }
}
