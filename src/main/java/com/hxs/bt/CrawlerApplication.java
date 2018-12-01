package com.hxs.bt;

import com.hxs.bt.common.GlobalMonitor;
import com.hxs.bt.task.CreateTableTask;
import com.hxs.bt.task.FindNodeTask;
import com.hxs.bt.task.InitNodeTask;
import com.hxs.bt.task.StartServerTask;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CrawlerApplication implements CommandLineRunner {
    private final StartServerTask startServerTask;
    private final InitNodeTask initNodeTask;
    private final FindNodeTask findNodeTask;
    private final CreateTableTask createTableTask;
    private final GlobalMonitor globalMonitor;

    public CrawlerApplication(StartServerTask startServerTask,
                              InitNodeTask initNodeTask,
                              FindNodeTask findNodeTask,
                              CreateTableTask createTableTask,
                              GlobalMonitor globalMonitor) {
        this.startServerTask = startServerTask;
        this.initNodeTask = initNodeTask;
        this.findNodeTask = findNodeTask;
        this.createTableTask = createTableTask;
        this.globalMonitor = globalMonitor;
    }

    public static void main(String[] args) {
        SpringApplication.run(CrawlerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        startServerTask.start();
        //等待所有服务器启动...
        initNodeTask.start();
        createTableTask.start();
        findNodeTask.start();
        globalMonitor.startFindNodeIntervalMonitor();
    }
}
