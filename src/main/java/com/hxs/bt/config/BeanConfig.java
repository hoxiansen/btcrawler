package com.hxs.bt.config;

import com.dampcake.bencode.Bencode;
import com.hxs.bt.common.manager.NodeManager;
import com.hxs.bt.socket.DHTServer;
import com.hxs.bt.socket.Sender;
import com.hxs.bt.task.FindNodeTask;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

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
}
