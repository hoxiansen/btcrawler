package com.hxs.bt.common.manager;

import com.hxs.bt.config.Config;
import com.hxs.bt.pojo.Node;
import com.hxs.bt.task.InitNodeTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 管理全局的Node，所有的服务器共用一个Node容器
 *
 * @author HJF
 * @date 2018/11/16 16:36
 */
@Slf4j
@Component
public class NodeManager {
    private final int GET_NODE_WAIT_TIME_S = 5;
    private final Lock lock = new ReentrantLock();
    private final BlockingQueue<Node> NODE_QUEUE;
    private final InitNodeTask initNodeTask;

    public NodeManager(Config config,
                       // 解决循环依赖问题
                       @Lazy InitNodeTask initNodeTask) {
        NODE_QUEUE = new LinkedBlockingQueue<>(config.getNodeQueueMaxLength());
        this.initNodeTask = initNodeTask;
    }

    public boolean add(Node node) {
        return NODE_QUEUE.offer(node);
    }

    public Node get() throws InterruptedException {
        Node node;
        lock.lockInterruptibly();
        try {
            while ((node = NODE_QUEUE.poll(GET_NODE_WAIT_TIME_S, TimeUnit.SECONDS)) == null) {
                log.info("获取Node失败，重新添加初始化Node");
                initNodeTask.start();
            }
        } finally {
            lock.unlock();
        }
        return node;
    }
}
