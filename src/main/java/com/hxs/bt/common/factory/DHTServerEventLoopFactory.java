package com.hxs.bt.common.factory;

import io.netty.util.concurrent.FastThreadLocalThread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用于DHT服务器的NioEventLoopGroup，主要作用是添加线程名字方便调试
 * 多次验证得出每个服务器只有一个线程在运行，所以不需要AtomicInteger来计数，加入了ProcessorThreadFactory.
 *
 * @author HJF
 * @date 2018/11/16 15:41
 */
public class DHTServerEventLoopFactory implements ThreadFactory {
    private final ThreadGroup group;
    private final String namePrefix;

    public DHTServerEventLoopFactory(int index) {
        group = System.getSecurityManager() == null
                ? Thread.currentThread().getThreadGroup()
                : System.getSecurityManager().getThreadGroup();
        namePrefix = "Server-" + index;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = newThread(r, namePrefix);
        if (t.isDaemon())
            t.setDaemon(false);
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }

    private Thread newThread(Runnable r, String name) {
        return new FastThreadLocalThread(group, r, name);
    }
}
