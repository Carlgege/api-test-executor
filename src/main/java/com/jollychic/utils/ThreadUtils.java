package com.jollychic.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public final class ThreadUtils {

    private ThreadUtils() {

    }

    public static void sleep(long time) {
        try {
            Thread.sleep(time);
            log.debug("wait time:" + time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static boolean sleepTrue(long time) {
        try {
            Thread.sleep(time);
            log.debug("wait time:" + time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static ExecutorService getThreadPool() {
        return ThreadPool.threadPool;
    }

    private static class ThreadPool {
        private static ExecutorService threadPool;
        static {
            ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("task-pool-%d").build();
            threadPool = new ThreadPoolExecutor(5, 20,50000L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
        }

    }
}
