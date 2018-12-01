package com.hxs.bt;

import com.hxs.bt.schedule.CreateTableSchedule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BtCrawlerApplicationTests {
    @Resource
    CreateTableSchedule createTableSchedule;
    @Test
    public void contextLoads() {
        createTableSchedule.createTableToday();
    }

}
