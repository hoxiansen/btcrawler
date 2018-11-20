package com.hxs.bt.socket;

import com.hxs.bt.common.factory.DHTServerEventLoopFactory;
import com.hxs.bt.config.Config;
import com.hxs.bt.socket.processor.ProcessorManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

/**
 * @author HJF
 * @date 2018/11/13 14:57
 */
@Slf4j
@Component
public class DHTServer {
    private final Config config;
    private final Sender sender;
    private final ProcessorManager processorManager;

    public DHTServer(Config config,
                     Sender sender,
                     ProcessorManager processorManager) {
        this.config = config;
        this.sender = sender;
        this.processorManager = processorManager;
    }

    public void start(int port, int index, CountDownLatch countDownLatch) {
        final EventLoopGroup eventLoopGroup = new NioEventLoopGroup(
                config.getPortList().size(),
                new DHTServerEventLoopFactory(index));
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .option(ChannelOption.SO_SNDBUF, 1 << 20)
                .option(ChannelOption.SO_RCVBUF, 1 << 20)
                .handler(new ChannelInitializer<NioDatagramChannel>() {
                    @Override
                    protected void initChannel(NioDatagramChannel ch) throws Exception {
                        ch.pipeline().addLast(new DHTServerHandler(countDownLatch, sender, processorManager, index));
                    }
                });
        try {
            log.info("开启DHT服务器-{},PORT:{}", index, port);
            bootstrap.bind(port).sync().channel().closeFuture().await();
        } catch (InterruptedException e) {
            log.info("DHT服务器关闭-{}", index);
            eventLoopGroup.shutdownGracefully();
        }
    }

    @AllArgsConstructor
    @Slf4j
    public static class DHTServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {
        //countDownLatch用来等待所有的服务器初始化完毕之后再进行下一步
        private final CountDownLatch countDownLatch;
        private final Sender sender;
        private final ProcessorManager processorManager;
        private final int index;

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            sender.setChannel(ctx.channel(), index);
            log.info("服务器-" + index + "启动完成");
            countDownLatch.countDown();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            log.info("Exception:{}", cause.toString());
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) {
            byte[] bytes = new byte[msg.content().readableBytes()];
            msg.content().readBytes(bytes);
            processorManager.process(bytes, msg.sender(), index);
        }
    }
}

