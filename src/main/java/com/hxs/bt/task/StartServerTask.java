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
public class StartServerTask implements PauseOption {
    private final DHTServer server;
    private final Config config;

    public StartServerTask(DHTServer server, Config config) {
        this.server = server;
        this.config = config;
    }

    /**
     * 开启DHT服务，利用CountDownLatch类使得所有Channel开启前阻塞当前线程
     */
    @Override
    public void start() {
        List<Integer> portList = config.getPortList();
        CountDownLatch countDownLatch = new CountDownLatch(portList.size());
        for (int i = 0, len = portList.size(); i < len; i++) {
            final int index = i;
            new Thread(() -> server.start(portList.get(index), index, countDownLatch),"Server-"+index+"-Start").start();
        }
        try {
            countDownLatch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("服务器启动时异常退出");
        } finally {
            log.info("所有服务器启动完成");
        }
    }
}
