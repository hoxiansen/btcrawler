package com.hxs.bt.task;

import com.hxs.bt.common.manager.NodeManager;
import com.hxs.bt.config.Config;
import com.hxs.bt.pojo.Node;
import com.hxs.bt.socket.Sender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author HJF
 * @date 2018/11/13 17:49
 */
@Slf4j
@Component
public class FindNodeTask implements PauseOption {
    private final Config config;
    private final Sender sender;
    private final NodeManager nodeManager;
    private final InitNodeTask initNodeTask;

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    public FindNodeTask(Config config,
                        Sender sender,
                        NodeManager nodeManager,
                        InitNodeTask initNodeTask) {
        this.config = config;
        this.sender = sender;
        this.nodeManager = nodeManager;
        this.initNodeTask = initNodeTask;
    }

    private Node getNode() throws InterruptedException {
        Node node;
        // 保证只有一个线程检测到node队列为空，使得initNodeTask只执行一次
        try {
            lock.lockInterruptibly();
            while ((node = nodeManager.get(5, TimeUnit.SECONDS)) == null) {
                log.info("获取Node失败，重新添加初始化Node");
                initNodeTask.start();
            }
        } finally {
            lock.unlock();
        }
        return node;
    }

    @Override
    public void start() {
        log.info("开始发送FindNode请求");
        int pauseTime = config.getFindNodeTaskIntervalMS();
        int portNum = config.getPortList().size();
        int threadNum = config.getFindNodeTaskThreadNum();
        for (int i = 0; i < threadNum; i++) {
            new Thread(() -> {
                for (int index = 0; index < portNum; index++) {
                    try {
                        sender.sendFindNode(getNode(), index);
                        pause(lock, condition, pauseTime, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        //...
                    }
                }
            }, "FindNodeThread-" + i).start();
        }
    }
}
