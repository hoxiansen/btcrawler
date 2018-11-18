package com.hxs.bt.socket;

import com.dampcake.bencode.Bencode;
import com.dampcake.bencode.Type;
import com.hxs.bt.common.factory.DHTServerEventLoopFactory;
import com.hxs.bt.common.manager.NodeManager;
import com.hxs.bt.config.Config;
import com.hxs.bt.pojo.Node;
import com.hxs.bt.util.BTUtils;
import com.hxs.bt.util.Utils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author HJF
 * @date 2018/11/13 14:57
 */
@Slf4j
@Component
public class DHTServer {
    private final Config config;
    private final Bencode bencode;
    private final Sender sender;
    private final NodeManager nodeManager;
    private final InfoHashHandler infoHashHandler;

    public DHTServer(Config config,
                     Bencode bencode,
                     Sender sender,
                     NodeManager nodeManager,
                     InfoHashHandler infoHashHandler) {
        this.config = config;
        this.bencode = bencode;
        this.sender = sender;
        this.nodeManager = nodeManager;
        this.infoHashHandler = infoHashHandler;
    }

    public void start(int port, int index, CountDownLatch countDownLatch) {
        final EventLoopGroup eventLoopGroup = new NioEventLoopGroup(
                config.getServerEventLoopThreadNum(),
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
                        ch.pipeline().addLast(new DHTServerHandler(countDownLatch, config, bencode, sender,
                                nodeManager, infoHashHandler, index));
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
        private final Config config;
        private final Bencode bencode;
        private final Sender sender;
        private final NodeManager nodeManager;
        private final InfoHashHandler infoHashHandler;
        private final int index;

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            sender.setChannel(ctx.channel(), index);
            log.info("服务器-" + index + "启动完成");
            countDownLatch.countDown();
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
            byte[] bytes = new byte[msg.content().readableBytes()];
            msg.content().readBytes(bytes);
            InetSocketAddress sender = msg.sender();
            Map<String, Object> map = bencode.decode(bytes, Type.DICTIONARY);
            //如果不是正常的krpc数据包，一律以错误处理
            String type = getObject(map, "y", "e");
            switch (type) {
                //处理错误
                case "e":
                    log.info("error:" + map);
                    break;
                //处理query
                case "q":
                    handleQuery(map, sender);
                    break;
                case "r":
                    handleFindNodeReply(map, sender);
                    break;
                default:
                    log.info("避开了所有的关卡：" + map.toString());
            }
        }

        /*
        处理请求(y=q)
         */
        private void handleQuery(Map<String, Object> krpcMsg, InetSocketAddress address) {
            try {
                String tid = getObject(krpcMsg, "t");
                String q = getObject(krpcMsg, "q");
                Map<String, Object> aMap = getObject(krpcMsg, "a");
                String nid = getObject(aMap, "id");
                String infoHash, token;
                switch (q) {
                    //处理ping
                    case "ping":
                        handlePingQuery(tid, nid, address);
                        break;
                    case "find_node":
                        handleFindNodeQuery(tid, nid, address);
                        break;
                    case "get_peers":
                        infoHash = getObject(aMap, "info_hash");
                        handleGetPeersQuery(tid, nid, infoHash, address);
                        break;
                    case "announce_peer":
                        infoHash = getObject(aMap, "info_hash");
                        token = getObject(aMap, "token");
                        handleAnnouncePeer(tid, nid, address, infoHash, token);
                        break;
                }
            } catch (IllegalArgumentException ignored) {
            }
        }

        /*
        回复其他节点的ping请求
        {"t":"aa", "y":"r", "r":{"id":"mnopqrstuvwxyz123456"}}
         */
        private void handlePingQuery(String tid, String otherNid, InetSocketAddress address) {
            sender.sendPingReply(tid, otherNid, address, index);
        }

        /*
        回复其他节点的find_node请求
        {"t":"aa", "y":"r", "r":{"id":"0123456789abcdefghij", "nodes":"def456..."}}
         */
        private void handleFindNodeQuery(String tid, String otherNid, InetSocketAddress address) {
            sender.sendFindNodeReply(tid, otherNid, address, index);
        }

        /*
        回复其他节点的get_peer请求
        {"t":"aa", "y":"r", "r":{"id":"abcdefghij0123456789", "token":"aoeusnth","nodes": "def456..."}}
         */
        private void handleGetPeersQuery(String tid, String otherNid, String infoHash, InetSocketAddress address) {
            log.debug("收到GetPeers");
            sender.sendGetPeersReply(tid, otherNid, address, index);
            if (config.getHandleGetPeersInfoHash()) {
                infoHashHandler.handleInfoHash(infoHash);
            }
        }

        /*
        处理infoHash
         */
        private void handleAnnouncePeer(String tid, String otherNid, InetSocketAddress address, String infoHash, String token) {
            if (!BTUtils.getTokenStr(otherNid).equals(token)) {
                return;
            }
            sender.sendAnnouncePeerReply(tid, otherNid, address, index);
            infoHashHandler.handleInfoHash(infoHash);
        }

        private void handleFindNodeReply(Map<String, Object> map, InetSocketAddress address) {
            log.debug("收到FindNodeReply");
            try {
                Map<String, Object> rMap = getObject(map, "r");
                String nodes = getObject(rMap, "nodes");
                splitNode(nodes.getBytes(CharsetUtil.ISO_8859_1));
            } catch (IllegalArgumentException ignored) {
            }
        }

        /*
        根据map的key获取值，没有这个key则返回预置值ob
         */
        @SuppressWarnings("unchecked")
        private <T> T getObject(Map<String, Object> map, String key, T ob) {
            Object obj = map.get(key);
            if (null == obj) return ob;
            return (T) obj;
        }

        /*
        根据map的key获取值，没有这个key则抛出异常
         */
        @SuppressWarnings("unchecked")
        private <T> T getObject(Map<String, Object> map, String key) {
            Object obj = map.get(key);
            if (null == obj) throw new IllegalArgumentException("不存在Key:" + key);
            return (T) obj;
        }

        private void splitNode(byte[] nodes) {
            int len = nodes.length;
            if (len % 26 != 0) {
                log.info("noe解析失败：length % 26!=0");
                return;
            }
            int index = 0;
            while (index < len) {
                byte[] nid = ArrayUtils.subarray(nodes, index, index + 20);

                try {
                    InetAddress ip = Inet4Address.getByAddress(ArrayUtils.subarray(nodes, index + 20, index + 24));
                    int port = Utils.bytesToPort(ArrayUtils.subarray(nodes, index + 24, index + 26));
                    nodeManager.add(new Node(new String(nid, CharsetUtil.ISO_8859_1), new InetSocketAddress(ip, port)));
                } catch (UnknownHostException e) {
                    log.info("Node解析出错：" + e.toString());
                } finally {
                    index += 26;
                }
            }
        }
    }

}
