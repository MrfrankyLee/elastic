package com.needayeah.elastic.demo;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.IntStream;

/**
 * @author lixiaole
 * @date 2021/6/10
 */
@Slf4j
public class YesThreadPool {

    /**
     * 存放任务的阻塞队列
     */
    BlockingQueue<Runnable> taskQueue;

    /**
     * 线程列表
     */
    List<YesThread> threads;

    public YesThreadPool(BlockingQueue<Runnable> taskQueue, int threadSize) {
        this.taskQueue = taskQueue;
        // 初始化线程，并定义名称
        threads = new ArrayList<>(threadSize);
        IntStream.rangeClosed(1, threadSize).forEach((i) -> {
            YesThread thread = new YesThread("thread--" + i);
            thread.start();
            threads.add(thread);
        });
    }

    public void execute(Runnable task) throws InterruptedException {
        taskQueue.put(task);
    }

    class YesThread extends Thread {

        public YesThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            while (true) {
                Runnable task = null;
                try {
                    //不断从任务队列获取任务
                    task = taskQueue.take();
                } catch (InterruptedException e) {
                    log.error("记录点东西.......", e);
                }
                task.run(); //执行
            }
        }
    }


    public static void main(String[] args) {
        YesThreadPool pool = new YesThreadPool(new LinkedBlockingQueue<>(10), 3);
        IntStream.rangeClosed(1, 5).forEach((i) -> {
            try {
                pool.execute(() -> {
                    System.out.println(Thread.currentThread().getName() + " 公众号：yes的练级攻略");
                });
            } catch (InterruptedException e) {
                log.error("记录点东西.....", e);
            }
        });
    }
}
