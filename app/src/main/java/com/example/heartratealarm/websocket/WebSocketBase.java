package com.example.heartratealarm.websocket;

import android.util.Log;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketBase extends WebSocketListener {
    private static final String TAG = "WebSocketBase2";
    //private static final String ENDPOINT = "ws://192.168.1.77:3683/android";
    private static final String ENDPOINT = "ws://192.168.50.251:3683/android";
    private final Object messageLock;

    private String message;
    private final Object stopLock;
    private boolean stop;

    private final WebSocket webSocket;

    // An interval observable that should get disposed on a websocket error.
    private Disposable pingDisposable;

    public WebSocketBase() {
        Request request = new Request.Builder().url(ENDPOINT)
                .build();
        OkHttpClient client = new OkHttpClient();
        webSocket = client.newWebSocket(request, this);
        messageLock = new Object();
        stopLock = new Object();
        stop = false;
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

    public void cancel(){
        webSocket.cancel();
    }

    private Optional<String> getMessage() {
        synchronized (messageLock) {
            try {
                messageLock.wait();
            } catch (InterruptedException e) {
                Log.d(TAG, "Got a Thread interupt exception. Nothing to see here", e);
                return Optional.empty();
            }
            synchronized (stopLock) {
                if (stop) {
                    return Optional.empty();
                }
            }
            return Optional.of(message);
        }
    }

    public Observable<Optional<String>> getMessageObservable() {
        return Observable.fromCallable(this::getMessage).repeatUntil(() -> {
            synchronized (stopLock) {
                return stop;
            }
        }).subscribeOn(Schedulers.newThread());
    }

    public void interval(Consumer<Long> f) {
        pingDisposable = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(f,
                        e -> {
                            Log.e(TAG, "Issue with interval", e);
                        });
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
        Log.i(TAG, "closing websocket");
        synchronized (stopLock) {
            stop = true;
        }
        synchronized (messageLock) {
            messageLock.notify();
        }
        if (pingDisposable != null && !pingDisposable.isDisposed()) {
            pingDisposable.dispose();
        }
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        Log.i(TAG, "websocket is now closed");
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        Log.d(TAG, "had a websocket oopsie", t);
        synchronized (stopLock) {
            stop = true;
        }
        synchronized (messageLock) {
            messageLock.notify();
        }
        if (pingDisposable != null && !pingDisposable.isDisposed()) {
            pingDisposable.dispose();
        }
    }
}
