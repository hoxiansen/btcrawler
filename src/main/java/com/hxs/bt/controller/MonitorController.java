package com.hxs.bt.controller;

import com.hxs.bt.common.Monitor;
import com.hxs.bt.common.manager.NodeManager;
import com.hxs.bt.config.Config;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/monitor")
public class MonitorController {
    @Resource
    private Config config;

    @Resource
    private NodeManager nodeManager;

    @GetMapping("/nQueue/maxSize")
    public int getNodeQueueMaxSize() {
        return config.getNodeQueueMaxLength();
    }

    @GetMapping("/nQueue/nowSize")
    public int getNodeQueueNowSize() {
        return nodeManager.getNODE_QUEUE().size();
    }

    @GetMapping("/magnetNum")
    public long getMagnetNum() {
        return Monitor.getMagnetNum();
    }
}
