package com.hxs.bt.config;

import com.dampcake.bencode.Bencode;
import com.hxs.bt.common.factory.DisruptorThreadFactory;
import com.hxs.bt.disruptor.event.KrpcEvent;
import com.hxs.bt.disruptor.handler.AbstractEventHandler;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import io.netty.util.CharsetUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author HJF
 * @date 2018/11/13 15:48
 */
@Configuration
public class Beans {
    @Bean
    public Bencode bencode() {
        return new Bencode(CharsetUtil.ISO_8859_1);
    }

    @Bean
    public Disruptor<KrpcEvent> disruptor(List<AbstractEventHandler> eventHandlers) {
        Disruptor<KrpcEvent> disruptor = new Disruptor<>(
                KrpcEvent::new,
                1 << 11,
                new DisruptorThreadFactory(),
                ProducerType.SINGLE,
                new YieldingWaitStrategy());
        eventHandlers.forEach(disruptor::handleEventsWith);
        return disruptor;
    }
}
