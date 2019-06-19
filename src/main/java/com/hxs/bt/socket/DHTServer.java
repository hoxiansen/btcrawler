package com.hxs.bt.socket;

import com.hxs.bt.disruptor.KrpcEventTranslator;
import com.hxs.bt.disruptor.event.KrpcEvent;
import com.lmax.disruptor.dsl.Disruptor;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetSocketAddress;

/**
 * @author HJF
 * @date 2018/11/13 14:57
 */
@Slf4j
@Component
public class DHTServer {
    @Resource
    private Sender sender;
    @Resource
    private Disruptor<KrpcEvent> disruptor;
    @Resource
    private KrpcEventTranslator eventTranslator;

    public void start(int port) {
        final EventLoopGroup eventLoopGroup = new NioEventLoopGroup(
                0,
                new DefaultThreadFactory("EVENT_LOOP"));
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .option(ChannelOption.SO_SNDBUF, 1 << 12)
                .option(ChannelOption.SO_RCVBUF, 1 << 12)
                .handler(new DHTServerHandler(this.sender, this.disruptor, eventTranslator));
        try {
            log.info("启动DHT服务器,PORT: {}", port);
            bootstrap.bind(port).sync().channel().closeFuture().await();
        } catch (InterruptedException e) {
            log.info("关闭DHT服务器");
            eventLoopGroup.shutdownGracefully();
        }
    }

    @AllArgsConstructor
    @Slf4j
    public static class DHTServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {
        private final Sender sender;
        private final Disruptor<KrpcEvent> disruptor;
        private final KrpcEventTranslator eventTranslator;

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            sender.setChannel(ctx.channel());
            disruptor.start();
            log.info("服务器启动完成");
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            disruptor.shutdown();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            log.info("Exception:{}", cause.getMessage());
            cause.printStackTrace();
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) {
            ByteBuf content = msg.content();
            InetSocketAddress sender = msg.sender();
            disruptor.publishEvent(eventTranslator, content, sender);
        }
    }
}

