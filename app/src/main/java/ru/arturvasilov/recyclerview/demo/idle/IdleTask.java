package ru.arturvasilov.recyclerview.demo.idle;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

public class IdleTask {

    private boolean mIsFinished;

    @NonNull
    private final Runnable mRunnable;

    public IdleTask(@NonNull Runnable runnable) {
        mIsFinished = false;
        mRunnable = runnable;
    }

    @MainThread
    public void runOnce() {
        if (!mIsFinished) {
            try {
                mRunnable.run();
            } catch (Throwable ignored) {
            }
            mIsFinished = true;
        }
    }
}
