package com.needayeah.elastic.common.threadutil;

import com.needayeah.elastic.config.queue.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author lixiaole
 * @date 2021/5/31
 */
@Slf4j
public class ThreadUtils {
    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(8,
            Runtime.getRuntime().availableProcessors() * 2,
            60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000),
            new NamedThreadFactory("ThreadUtils-"), new ThreadPoolExecutor.CallerRunsPolicy());

    public static void execute(Runnable command) {
        log.info("thread pool ActiveCount:{}, QueueSize:{}, CompletedTaskCount:{}",
                executor.getActiveCount(), executor.getQueue().size(),
                executor.getCompletedTaskCount());
        executor.execute(command);
    }

    public static <T> Future<T> submit(Callable task) {
        log.info("thread pool ActiveCount:{}, QueueSize:{}, CompletedTaskCount:{}",
                executor.getActiveCount(), executor.getQueue().size(),
                executor.getCompletedTaskCount());
        return executor.submit(task);
    }
}
