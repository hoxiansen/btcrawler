package com.hxs.bt.task;

import com.hxs.bt.common.manager.NodeManager;
import com.hxs.bt.config.Config;
import com.hxs.bt.pojo.Node;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * 初始化Node
 *
 * @author HJF
 * @date 2018/11/14 10:44
 */
@Slf4j
@Component
public class InitNodeTask implements PauseOption {
    private final Config config;
    private final NodeManager nodeManager;

    public InitNodeTask(Config config,
                        NodeManager nodeManager) {
        this.config = config;
        this.nodeManager = nodeManager;
    }

    @Override
    public void start() {
        log.info("添加初始Node到NodeQueue");
        for (Node node : config.getBootNodeList()) {
            nodeManager.add(node);
        }
    }
}
