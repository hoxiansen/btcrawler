package com.hxs.bt.util;

import cn.hutool.core.date.DateUtil;
import org.apache.commons.lang3.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author HJF
 * @date 2018/11/29 12:10
 */
public class DateHelper {

    /**
     * 获取当前日期，用户更改当前数据存储的表
     *
     * @return 格式化的时间，比如：20181129
     */
    public static String getTableNameSuffixToday() {
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        return sf.format(System.currentTimeMillis());
    }

    public static String getTableNameSuffixTomorrow(){
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        return sf.format(DateUtils.addDays(new Date(System.currentTimeMillis()),1));
    }

    public static void main(String[] args) {
        System.out.println(DateHelper.getTableNameSuffixTomorrow());
        Date date = DateUtil.date(System.currentTimeMillis());
        String today = DateUtil.format(date,"yyyyMMdd");
        System.out.println(today);
        String tomorrow = DateUtil.format(DateUtil.tomorrow(),"yyyyMMdd");
        System.out.println(tomorrow);
    }
}
