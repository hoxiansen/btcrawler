package com.hxs.bt.common.factory;

import io.netty.util.concurrent.FastThreadLocalThread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author HJF
 * @date 2018/11/19 23:34
 */
public class ProcessorThreadFactory implements ThreadFactory {
    private final ThreadGroup group;
    private final AtomicInteger threadNum = new AtomicInteger(1);

    public ProcessorThreadFactory() {
        this.group = System.getSecurityManager() == null
                ? Thread.currentThread().getThreadGroup()
                : System.getSecurityManager().getThreadGroup();
    }

    @Override
    public Thread newThread(Runnable r) {
        String prefix = "Processor-";
        Thread t = new FastThreadLocalThread(group, r, prefix + threadNum.getAndIncrement());
        // 将所有的处理器线程设置为守护线程。
        if (t.isDaemon()) t.setDaemon(true);
        if (t.getPriority() != Thread.NORM_PRIORITY) t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }
}
