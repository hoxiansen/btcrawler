package com.hxs.bt.task;

import com.hxs.bt.schedule.CreateTableSchedule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author HJF
 * @date 2018/11/30 20:12
 */
@Component
@Slf4j
public class CreateTableTask {
    @Resource
    CreateTableSchedule createTableSchedule;

    public void start() {
        createTableSchedule.createTableToday();
    }
}
