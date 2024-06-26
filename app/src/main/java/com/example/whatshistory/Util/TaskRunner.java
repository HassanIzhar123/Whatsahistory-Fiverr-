package com.example.whatshistory.Util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TaskRunner {
    private final Executor executor = Executors.newSingleThreadExecutor(); // change according to your requirements
    //    private final Executor executor = Executors.newFixedThreadPool(5);
    private final Handler handler = new Handler(Looper.getMainLooper());

    public interface Callback<R> {
        void onStart();

        void onComplete(R result);

        void onError(Exception e);
    }

    public <R> void executeAsync(Callable<R> callable, Callback<R> callback) {
        callback.onStart();
        executor.execute(() -> {
            handler.post(() -> {
                try {
                    R result = callable.call();
                    callback.onComplete(result);
                } catch (Exception e) {
                    callback.onError(e);
                }
            });
        });
    }
}