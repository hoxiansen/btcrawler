package com.hxs.bt;

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

    public CrawlerApplication(StartServerTask startServerTask,
                              InitNodeTask initNodeTask,
                              FindNodeTask findNodeTask) {
        this.startServerTask = startServerTask;
        this.initNodeTask = initNodeTask;
        this.findNodeTask = findNodeTask;
    }

    public static void main(String[] args) {
        SpringApplication.run(CrawlerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        startServerTask.start();
        initNodeTask.start();
        findNodeTask.start();
    }
}
