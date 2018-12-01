package com.hxs.bt.common;

import com.hxs.bt.common.manager.NodeManager;
import com.hxs.bt.config.Config;
import com.hxs.bt.pojo.Node;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author HJF
 * @date 2018/11/27 15:15
 */
@Slf4j
@Component
public class GlobalMonitor implements DisposableBean {
    private static final int CORE_SIZE = 5;
    private static final int KEEP_ALIVE = 0;
    private static final int NODE_QUEUE_MONITOR_INTERVAL = 10 * 1000;
    private static final int FIND_NODE_MONITOR_INTERVAL = 10 * 60 * 1000;
    private final Config config;
    private final NodeManager nodeManager;
    private final ThreadPoolExecutor executor;
    /**
     * 发送FindNode请求的间隔时间，初始时间为0ms，随着NodeQueue变满，这个值会增加。
     */
    private final AtomicInteger findNodeIntervalMS = new AtomicInteger(0);


    public GlobalMonitor(Config config,
                         NodeManager nodeManager) {
        this.config = config;
        this.nodeManager = nodeManager;
        executor = new ThreadPoolExecutor(CORE_SIZE, CORE_SIZE, KEEP_ALIVE, TimeUnit.NANOSECONDS,
                new SynchronousQueue<>(),
                new BasicThreadFactory.Builder()
                        .namingPattern("monitor-%d")
                        .daemon(true)
                        .priority(Thread.NORM_PRIORITY).build(),
                (r, executor) -> {
                    log.error("全局监测线程池溢出！", executor.getTaskCount());
                    r.run();
                });
    }

    public void addFindNodeInterval() {
        findNodeIntervalMS.addAndGet(1);
    }

    public int getFindNodeInterval() {
        return findNodeIntervalMS.get();
    }

    /**
     * 开启NodeQueue的监测线程，当检测到NodeQueue为空时，重新添加启动Tracker。
     */
    public void startNodeQueueMonitor() {
        executor.submit(() -> {
            while (true) {
                if (nodeManager.getSize() == 0) {
                    log.info("重新添加初始Node到NodeQueue");
                    for (Node node : config.getBootNodeList()) {
                        nodeManager.add(node);
                    }
                }
                try {
                    Thread.sleep(NODE_QUEUE_MONITOR_INTERVAL);
                } catch (InterruptedException e) {
                    //...
                }
            }
        });
    }

    public void startFindNodeIntervalMonitor() {
        executor.submit(() -> {
            while (true) {
                Thread.sleep(FIND_NODE_MONITOR_INTERVAL);
                log.info("FindNodeInterval:{}", getFindNodeInterval());
            }
        });
    }

    @Override
    public void destroy() {
        executor.shutdown();
    }
}
