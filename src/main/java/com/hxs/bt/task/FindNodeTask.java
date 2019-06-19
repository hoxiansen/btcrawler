package com.hxs.bt.task;

import com.hxs.bt.common.manager.NodeManager;
import com.hxs.bt.config.Config;
import com.hxs.bt.entity.Node;
import com.hxs.bt.socket.Sender;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.*;

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

    public FindNodeTask(Config config,
                        Sender sender,
                        NodeManager nodeManager) {
        this.config = config;
        this.sender = sender;
        this.nodeManager = nodeManager;
        this.executor = new ThreadPoolExecutor(
                config.getFindNodeTaskThreadNum(),
                config.getFindNodeTaskThreadNum(),
                0L,
                TimeUnit.MILLISECONDS,
                new SynchronousQueue<>(),
                new BasicThreadFactory.Builder()
                        .daemon(true)
                        .namingPattern("FindNode-%d")
                        .priority(Thread.NORM_PRIORITY).build(),
                (r, executor) -> log.error("FindNode线程池溢出"));
    }

    private Node getNode() throws InterruptedException {
        return nodeManager.get();
    }

    public void start() {
        if (config.getDebug()) {
            log.info("调试模式下不发送FindNode请求");
            return;
        }
        int threadNum = config.getFindNodeTaskThreadNum();
        log.info("开始发送FindNode请求，线程数：{}", threadNum);
        for (int i = 0; i < threadNum; i++) {
            final int index = i;
            executor.submit(() -> {
                while (!executor.isTerminated()) {
                    try {
                        sender.sendFindNode(getNode());
                    } catch (InterruptedException e) {
                        log.info("FindNode-" + index + "线程退出");
                    }
                }
            });
        }
    }

    @Override
    public void destroy() {
        executor.shutdown();
    }
}
