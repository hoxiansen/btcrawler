package com.hxs.bt.schedule;

import com.hxs.bt.util.DateHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

/**
 * @author HJF
 * @date 2018/11/29 15:36
 */
@Slf4j
@Component
public class CreateTableSchedule {
    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_FORMATTER = "CREATE TABLE IF NOT EXISTS  %s (" +
            "id int(10) NOT NULL AUTO_INCREMENT," +
            "info_hash char(40) ," +
            "PRIMARY KEY (id)" +
            ") ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci;";

    @Autowired
    public CreateTableSchedule(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 每天23:00:00创建第二天的数据表
     */
    @Scheduled(cron = "0 0 23 * * ?")
    public void start() {
        createTableTomorrow();
    }

    private void createTableTomorrow() {
        String tableName = "btih" + DateHelper.getTableNameSuffixTomorrow();
        log.info("创建数据库表：{}", tableName);
        String sql = String.format(SQL_FORMATTER, tableName);
        log.debug(sql);
        jdbcTemplate.execute(sql);
    }

    public void createTableToday() {
        String tableName = "btih" + DateHelper.getTableNameSuffixToday();
        log.info("创建数据库表：{}", tableName);
        String sql = String.format(SQL_FORMATTER, tableName);
        jdbcTemplate.execute(sql);
    }
}
