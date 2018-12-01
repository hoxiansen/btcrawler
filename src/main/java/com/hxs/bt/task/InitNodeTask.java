package com.hxs.bt.task;

import com.hxs.bt.common.GlobalMonitor;
import com.hxs.bt.common.manager.NodeManager;
import com.hxs.bt.config.Config;
import com.hxs.bt.pojo.Node;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 初始化Node
 *
 * @author HJF
 * @date 2018/11/14 10:44
 */
@Slf4j
@Component
public class InitNodeTask {
    private final Config config;
    private final NodeManager nodeManager;
    private final GlobalMonitor globalMonitor;

    public InitNodeTask(Config config,
                        NodeManager nodeManager,
                        GlobalMonitor globalMonitor) {
        this.config = config;
        this.nodeManager = nodeManager;
        this.globalMonitor = globalMonitor;
    }

    private void addNode() {
        log.info("添加初始Node到NodeQueue");
        for (Node node : config.getBootNodeList()) {
            log.trace("AddNode:{}",node.getAddress());
            nodeManager.add(node);
        }
    }

    public void start() {
        addNode();
        // 开启startNodeQueue监测
        globalMonitor.startNodeQueueMonitor();
    }
}
