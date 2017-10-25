package ru.arturvasilov.recyclerview.demo.idle;

import android.os.Looper;
import android.os.MessageQueue;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.LinkedList;

class IdleTaskHandler {

    @NonNull
    private final MessageQueue mMessageQueue = Looper.myQueue();

    @NonNull
    private final LinkedList<IdleTask> mTaskQueue = new LinkedList<>();

    @NonNull
    private final NextIdleHandler mHandler = new NextIdleHandler();

    @MainThread
    IdleTaskHandler() {
    }

    @MainThread
    public void register(@NonNull IdleTask task) {
        if (mTaskQueue.size() == 0) {
            mMessageQueue.addIdleHandler(mHandler);
        }

        mTaskQueue.offer(task);
    }

    @MainThread
    public void unregister(@NonNull IdleTask task) {
        mTaskQueue.remove(task);

        if (mTaskQueue.size() == 0) {
            mMessageQueue.removeIdleHandler(mHandler);
        }
    }

    @MainThread
    protected void removeExecutedTask(@NonNull IdleTask idleTask) {
        mTaskQueue.remove(idleTask);
    }

    public class NextIdleHandler implements MessageQueue.IdleHandler {

        @MainThread
        @Override
        public boolean queueIdle() {
            pollAndExecuteIdleTask();
            return !mTaskQueue.isEmpty();
        }

        private void pollAndExecuteIdleTask() {
            IdleTask task = mTaskQueue.peek();
            if (task == null) {
                return;
            }
            try {
                task.runOnce();
            } finally {
                removeExecutedTask(task);
            }
        }
    }

}
