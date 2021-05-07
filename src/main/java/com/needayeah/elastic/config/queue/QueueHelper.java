package com.needayeah.elastic.config.queue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * 异步队列处理
 *
 * @author lixiaole
 */
@Component
@ConditionalOnClass(value = AsyncQueue.class)
@Lazy
public class QueueHelper {

    @Value("${async.queue.threadNum:0}")
    private int threadNum;

    @Value("${async.queue.size:0}")
    private int queueSize;

    @Value("${async.queue.retryNum:3}")
    private int retryNum;

    private AsyncQueue<Runnable> asyncQueue;

    @PostConstruct
    public void init() {
        asyncQueue = new AsyncQueue<>(retryNum, threadNum <= 0 ? AsyncQueue.DEFAULT_THREAD : threadNum, queueSize <= 0 ? AsyncQueue.DEFAULT_SIZE : queueSize, new DefaultQueueConsumer());
    }

    public class DefaultQueueConsumer implements QueueConsumer<Runnable> {
        @Override
        public void doConsume(Runnable runnable) {
            runnable.run();
        }
    }

    /**
     * 异步执行任务，没有返回结果
     *
     * @param runnable 任务
     */
    public void asyncExecute(Runnable runnable) {
        asyncQueue.put(runnable);
    }

    /**
     * 异步执行任务，有返回结果
     *
     * @param callable callable
     * @param <T>      return type
     * @return T 's Object
     */
    public <T> Future<T> asyncExecute(Callable<T> callable) {
        FutureTask<T> futureTask = new FutureTask<T>(callable);
        asyncExecute(futureTask);
        return futureTask;
    }

    @PreDestroy
    public void close() {
        if (asyncQueue != null) {
            asyncQueue.close();
        }
    }
}
