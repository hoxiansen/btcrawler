package com.hxs.bt.common.manager;

import com.hxs.bt.config.Config;
import com.hxs.bt.entity.Node;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 管理全局的Node，所有的服务器共用一个Node容器
 *
 * @author HJF
 * @date 2018/11/16 16:36
 */
@Getter
@Component
public class NodeManager {
    private final BlockingQueue<Node> NODE_QUEUE;

    public NodeManager(Config config) {
        NODE_QUEUE = new LinkedBlockingQueue<>(config.getNodeQueueMaxLength());
    }

    public boolean add(Node node) {
        return NODE_QUEUE.offer(node);
    }

    public Node get() throws InterruptedException {
        return NODE_QUEUE.take();
    }

    public boolean isEmpty() {
        return NODE_QUEUE.isEmpty();
    }
}
