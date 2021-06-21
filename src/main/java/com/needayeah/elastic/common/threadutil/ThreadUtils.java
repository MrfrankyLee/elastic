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
    /**
     * 1.你说你看过源码，那你肯定知道线程池里的 ctl 是干嘛的咯？
     * ctl 是一个涵盖了两个概念的原子整数类,它将工作线程数和线程池状态结合在一起维护，
     * 低 29 位存放 workerCount，高 3 位存放 runState。
     *
     * 2.你知道线程池有几种状态吗？
     * RUNNING：能接受新任务，并处理阻塞队列中的任务
     * SHUTDOWN：不接受新任务，但是可以处理阻塞队列中的任务
     * STOP：不接受新任务，并且不处理阻塞队列中的任务，并且还打断正在运行任务的线程，就是直接撂担子不干了！
     * TIDYING：所有任务都终止，并且工作线程也为0，处于关闭之前的状态
     * TERMINATED：已关闭。
     *
     */
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
