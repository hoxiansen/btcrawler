package com.hxs.bt.common.factory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author HJF
 * @date 2019/1/12 18:45
 */
public class DisruptorThreadFactory implements ThreadFactory {
    private static final String NAME_PREFIX = "EventHandler-";
    private static final AtomicInteger INDEX = new AtomicInteger(0);

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, NAME_PREFIX + INDEX.getAndIncrement());
        t.setDaemon(true);
        return t;
    }
}
