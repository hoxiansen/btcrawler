package com.hxs.bt.controller;

import com.hxs.bt.schedule.TopClearIdleSchedule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/control")
public class ControlController {
//    @Resource
//    TopClearIdleSchedule clearIdle;
//
//    @GetMapping("/clearIdleKey")
//    public void clearIdleKey() {
//        log.info("主动清除不活跃key");
//        clearIdle.start();
//    }
}
