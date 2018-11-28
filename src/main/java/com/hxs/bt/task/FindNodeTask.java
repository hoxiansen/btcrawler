package com.hxs.bt.task;

import com.hxs.bt.common.GlobalMonitor;
import com.hxs.bt.common.manager.NodeManager;
import com.hxs.bt.config.Config;
import com.hxs.bt.pojo.Node;
import com.hxs.bt.socket.Sender;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author HJF
 * @date 2018/11/13 17:49
 */
@Slf4j
@Component
public class FindNodeTask implements DisposableBean {
    private final Config config;
    private final Sender sender;
    private final NodeManager nodeManager;
    private final ThreadPoolExecutor executor;
    private final GlobalMonitor globalMonitor;

    public FindNodeTask(Config config,
                        Sender sender,
                        NodeManager nodeManager,
                        GlobalMonitor globalMonitor) {
        this.config = config;
        this.sender = sender;
        this.nodeManager = nodeManager;
        this.globalMonitor = globalMonitor;
        executor = new ThreadPoolExecutor(config.getFindNodeTaskThreadNum(),
                config.getFindNodeTaskThreadNum(),
                0,
                TimeUnit.NANOSECONDS,
                new SynchronousQueue<>(),
                new BasicThreadFactory.Builder()
                        .daemon(true)
                        .namingPattern("FindNode-%d")
                        .priority(Thread.NORM_PRIORITY).build(),
                (r, executor) -> log.error("FindNode线程池溢出！"));
    }

    private Node getNode() throws InterruptedException {
        return nodeManager.get();
    }

    public void start() {
        int portNum = config.getPortList().size();
        int threadNum = config.getFindNodeTaskThreadNum();
        log.info("开始发送FindNode请求，线程数：{}", threadNum);
        for (int i = 0; i < threadNum; i++) {
            executor.submit(() -> {
                for (int index = 0; index < portNum; index++) {
                    try {
                        sender.sendFindNode(getNode(), index);
                        Thread.sleep(globalMonitor.getFindNodeInterval());
                    } catch (InterruptedException e) {
                        //...
                    }
                }
            });
        }
    }

    @Override
    public void destroy() throws Exception {
        executor.shutdown();
    }
}
