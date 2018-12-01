package com.hxs.bt.config;

import com.hxs.bt.config.hibernate.HibernateInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author HJF
 * @date 2018/11/29 15:01
 */
@Slf4j
@Configuration
public class JpaConfig implements HibernatePropertiesCustomizer {

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        log.debug("添加自定义hibernate拦截器");
        hibernateProperties.put("hibernate.session_factory.statement_inspector", HibernateInterceptor.class);
    }
}
