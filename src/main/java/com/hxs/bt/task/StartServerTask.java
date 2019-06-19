package com.hxs.bt.task;

import com.hxs.bt.config.Config;
import com.hxs.bt.socket.DHTServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author HJF
 * @date 2018/11/13 18:13
 */
@Slf4j
@Component
public class StartServerTask {
    private final DHTServer server;
    private final Config config;

    public StartServerTask(DHTServer server,
                           Config config) {
        this.server = server;
        this.config = config;
    }

    /**
     * 开启DHT服务，利用CountDownLatch类使得所有Channel开启前阻塞当前线程
     */
    public void start() {
        new Thread(() -> server.start(config.getPort()), "ServerStart").start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            log.error("服务器启动时异常退出");
        }
    }
}
