package com.hxs.bt.config;

import com.dampcake.bencode.Bencode;
import com.hxs.bt.socket.processor.AbstractProcessor;
import com.hxs.bt.socket.processor.ProcessorManager;
import io.netty.util.CharsetUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author HJF
 * @date 2018/11/13 15:48
 */
@Configuration
public class BeanConfig {
    @Bean
    public Bencode bencode() {
        return new Bencode(CharsetUtil.ISO_8859_1);
    }

    /**
     * 注册processor，使用@Order注解控制顺序，从小到大（不是从大到小）。
     * @param processors 会自动探测所有AbstractProcessor的子类形成一个list
     * @param bencode {@link Bencode}Bencode编码解码器
     * @param config {@link Config}全局配置文件
     * @return ProcessorManager组件
     */
    @Bean
    public ProcessorManager processorManager(List<AbstractProcessor> processors,
                                             Bencode bencode,
                                             Config config) {
        ProcessorManager processorManager = new ProcessorManager(bencode, config);
        processors.forEach(processorManager::register);
        return processorManager;
    }
}
