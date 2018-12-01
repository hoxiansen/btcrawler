package com.hxs.bt.util;

import org.apache.commons.lang3.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author HJF
 * @date 2018/11/29 12:10
 */
public class DateHelper {
    private static final ThreadLocal<SimpleDateFormat> SF = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMdd"));

    /**
     * 获取当前日期，用户更改当前数据存储的表
     *
     * @return 格式化的时间，比如：20181129
     */
    public static String getTableNameSuffixToday() {
        return SF.get().format(System.currentTimeMillis());
    }

    public static String getTableNameSuffixTomorrow(){
        return SF.get().format(DateUtils.addDays(new Date(System.currentTimeMillis()),1));
    }

    public static void main(String[] args) {
        System.out.println(DateHelper.getTableNameSuffixTomorrow());
    }
}
