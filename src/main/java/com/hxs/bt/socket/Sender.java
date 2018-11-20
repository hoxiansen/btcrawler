package com.hxs.bt.socket;

import com.dampcake.bencode.Bencode;
import com.hxs.bt.common.manager.NodeManager;
import com.hxs.bt.config.Config;
import com.hxs.bt.pojo.Node;
import com.hxs.bt.util.BTUtils;
import com.hxs.bt.util.Utils;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * @author HJF
 * @date 2018/11/13 16:13
 */
@Slf4j
@Component
public class Sender {

    private List<Channel> channelList;
    private final Bencode bencode;
    private final Config config;
    private final NodeManager nodeManager;

    public Sender(Bencode bencode, Config config,
                  NodeManager nodeManager) {
        this.bencode = bencode;
        this.config = config;
        this.nodeManager = nodeManager;
    }

    private Node getNode() throws InterruptedException {
        return nodeManager.get();
    }

    public void sendFindNode(Node node, int index) {
        byte[] bytes = bencode.encode(new HashMap<String, Object>() {{
            put("t", BTUtils.randTidStr());
            put("y", "q");
            put("q", "find_node");
            put("a", new HashMap<String, String>() {{
                put("id", BTUtils.fakeNidStr(config.getSelfNidList().get(index), node.getNid()));
                put("target", BTUtils.randNidStr());
            }});
        }});
        send(bytes, node.getAddress(), index);
    }

    public void sendFindNodeReply(String tid, String otherNid, InetSocketAddress address, int index) throws InterruptedException {
        byte[] bytes = bencode.encode(new HashMap<String, Object>() {{
            put("t", Optional.ofNullable(tid).orElse(BTUtils.randTidStr()));
            put("y", "r");
            put("r", new HashMap<String, Object>() {{
                put("id", BTUtils.fakeNidStr(
                        config.getSelfNidList().get(index),
                        Optional.ofNullable(otherNid).orElse(BTUtils.randNidStr())));
                put("nodes", Utils.encodeNode(Sender.this.getNode()));
            }});
        }});
        log.debug("SendFindNodeReply");
        send(bytes, address, index);
    }

    public void sendGetPeersReply(String tid, String otherNid, InetSocketAddress address, int index) throws InterruptedException {
        byte[] bytes = bencode.encode(new HashMap<String, Object>() {{
            put("t", Optional.ofNullable(tid).orElse(BTUtils.randTidStr()));
            put("y", "r");
            put("r", new HashMap<String, Object>() {{
                put("id", BTUtils.fakeNidStr(
                        config.getSelfNidList().get(index),
                        Optional.ofNullable(otherNid).orElse(BTUtils.randNidStr())));
                put("token", BTUtils.getTokenStr(Optional.ofNullable(otherNid).orElse(BTUtils.randNidStr())));
                put("nodes", Utils.encodeNode(Sender.this.getNode()));
            }});
        }});
        log.debug("SendGetPeersReply");
        send(bytes, address, index);
    }

    public void sendPingReply(String tid, String otherNid, InetSocketAddress address, int index) {
        byte[] bytes = bencode.encode(new HashMap<String, Object>() {{
            put("t", Optional.ofNullable(tid).orElse(BTUtils.randTidStr()));
            put("y", "r");
            put("r", new HashMap<String, Object>() {{
                put("id", BTUtils.fakeNidStr(config.getSelfNidList().get(index),
                        Optional.ofNullable(otherNid).orElse(BTUtils.randNidStr())));
            }});
        }});
        log.debug("SendPingReply");
        send(bytes, address, index);
    }

    public void sendAnnouncePeerReply(String tid, String otherNid, InetSocketAddress address, int index) {
        log.debug("SendAnnouncePeerReply");
        sendPingReply(tid, otherNid, address, index);
    }

    private void send(byte[] bytes, InetSocketAddress address, int index) {
        Optional.ofNullable(channelList.get(index))
                .map((channel) -> {
                    channel.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(bytes), address));
                    return channel;
                }).orElseGet(() -> {
            log.error("Channel为空，消息发送失败！");
            return null;
        });
    }

    public void setChannel(Channel channel, int index) {
        this.channelList.set(index, channel);
    }

    @PostConstruct
    private void init() {
        int len = this.config.getPortList().size();
        this.channelList = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            channelList.add(null);
        }
    }
}
