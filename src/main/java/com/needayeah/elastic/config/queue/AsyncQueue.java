package com.needayeah.elastic.config.queue;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import lombok.extern.slf4j.Slf4j;

/**
 * 简单异步队列
 *
 * @author lixiaole
 */
@Slf4j
public class AsyncQueue<T> {

    private int retryNum;

    public static final int DEFAULT_SIZE = 2048 * 2048;

    public static final int DEFAULT_THREAD = Runtime.getRuntime().availableProcessors();

    private int threadNum;

    private int queueSize;

    private RingBuffer<QueueEvent<T>> queue;

    private Disruptor<QueueEvent<T>> disruptor;

    private QueueConsumer<T> eventHandle;

    public AsyncQueue(int retryNum, int threadNum, int queueSize, QueueConsumer<T> eventHandle) {
        this.threadNum = threadNum;
        this.queueSize = queueSize;
        this.eventHandle = eventHandle;
        this.retryNum = retryNum;
        init();
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    private void init() {
        disruptor = new Disruptor<>(EVENT_FACTORY, this.queueSize, new NamedThreadFactory("AsyncQueue"));
        disruptor.setDefaultExceptionHandler(new DefaultExceptionHandler());
        WorkHandler[] workHandlers = new WorkHandler[this.threadNum];
        for (int i = 0; i < this.threadNum; i++) {
            workHandlers[i] = (WorkHandler<QueueEvent<T>>) event -> {
                for (int i1 = 0; i1 < retryNum; i1++) {
                    try {
                        eventHandle.doConsume(event.value);
                    } catch (Exception e) {
                        continue;
                    }
                    break;
                }
            };
        }
        disruptor.handleEventsWithWorkerPool(workHandlers);
        queue = disruptor.start();
    }

    public final EventFactory<QueueEvent<T>> EVENT_FACTORY = () -> new QueueEvent<T>();


    private class QueueEvent<T> {
        T value;
    }

    private class DefaultExceptionHandler implements ExceptionHandler<QueueEvent<T>> {

        @Override
        public void handleEventException(Throwable ex, long sequence, QueueEvent<T> event) {
            log.error("AsyncQueue handle error, QueueEvent=", event.value, ex);
        }

        @Override
        public void handleOnStartException(Throwable ex) {

        }

        @Override
        public void handleOnShutdownException(Throwable ex) {

        }
    }

    /**
     * 入队列
     *
     * @param t T
     */
    public void put(T t) {
        long sequence = queue.next();
        try {
            QueueEvent<T> event = queue.get(sequence);
            event.value = t;
        } finally {
            queue.publish(sequence);
        }
    }

    public void close() {
        if (disruptor != null) {
            disruptor.shutdown();
        }
    }
}
