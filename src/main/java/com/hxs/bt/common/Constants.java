package com.hxs.bt.common;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 全局常量
 */
public interface Constants {
    Charset CHARSET = StandardCharsets.ISO_8859_1;

    String SQL_FORMATTER = "CREATE TABLE IF NOT EXISTS  %s (" +
            "id int(10) NOT NULL AUTO_INCREMENT," +
            "info_hash char(40) ," +
            "PRIMARY KEY (id)" +
            ") ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci;";
}
