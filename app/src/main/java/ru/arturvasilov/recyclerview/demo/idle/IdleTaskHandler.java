package ru.arturvasilov.recyclerview.demo.idle;

import android.os.Looper;
import android.os.MessageQueue;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;

class IdleTaskHandler {

    @NonNull
    private final MessageQueue mMessageQueue = Looper.myQueue();

    @NonNull
    private final Queue<Runnable> mTaskQueue = new ArrayDeque<>();

    @NonNull
    private final NextIdleHandler mHandler = new NextIdleHandler();

    @MainThread
    IdleTaskHandler() {
    }

    @MainThread
    public void register(@NonNull Runnable task) {
        if (mTaskQueue.size() == 0) {
            mMessageQueue.addIdleHandler(mHandler);
        }

        mTaskQueue.add(task);
    }

    @MainThread
    public void unregister(@NonNull Runnable task) {
        mTaskQueue.remove(task);

        if (mTaskQueue.size() == 0) {
            mMessageQueue.removeIdleHandler(mHandler);
        }
    }

    @MainThread
    protected void removeExecutedTask(@NonNull Runnable task) {
        mTaskQueue.remove(task);
    }

    public class NextIdleHandler implements MessageQueue.IdleHandler {

        @MainThread
        @Override
        public boolean queueIdle() {
            pollAndExecuteIdleTask();
            return !mTaskQueue.isEmpty();
        }

        private void pollAndExecuteIdleTask() {
            Runnable task = mTaskQueue.peek();
            if (task == null) {
                return;
            }
            try {
                task.run();
            } finally {
                removeExecutedTask(task);
            }
        }
    }

}
