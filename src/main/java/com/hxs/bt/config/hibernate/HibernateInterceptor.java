package com.hxs.bt.config.hibernate;

import com.hxs.bt.util.DateHelper;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.resource.jdbc.spi.StatementInspector;

/**
 * 根据每天的日期动态修改表名实现每天一个表
 * @author HJF
 * @date 2018/11/29 12:07
 */
@Slf4j
public class HibernateInterceptor implements StatementInspector {

    @Override
    public String inspect(String sql) {
        log.debug("sql拦截：{}",sql);
        return sql.replace("btih", "btih" + DateHelper.getTableNameSuffixToday());
    }
}
