package com.hxs.bt.socket;

import com.dampcake.bencode.Bencode;
import com.hxs.bt.config.Config;
import com.hxs.bt.entity.Node;
import com.hxs.bt.util.BTUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Optional;

/**
 * @author HJF
 * @date 2018/11/13 16:13
 */
@Slf4j
@Component
public class Sender {

    private Channel channel;
    @Resource
    private Bencode bencode;

    public void sendFindNode(Node node) {
        byte[] bytes = bencode.encode(new HashMap<String, Object>() {{
            put("t", BTUtils.randTidStr());
            put("y", "q");
            put("q", "find_node");
            put("a", new HashMap<String, String>() {{
                put("id", BTUtils.fakeNidStr(Config.SELF_NID, node.getNid()));
                put("target", BTUtils.randNidStr());
            }});
        }});
        send(bytes, node.getAddress());
    }

    public void sendFindNodeReply(String tid, String otherNid, InetSocketAddress address) {
        byte[] bytes = bencode.encode(new HashMap<String, Object>() {{
            put("t", Optional.ofNullable(tid).orElse(BTUtils.randTidStr()));
            put("y", "r");
            put("r", new HashMap<String, Object>() {{
                put("id", BTUtils.fakeNidStr(
                        Config.SELF_NID,
                        Optional.ofNullable(otherNid).orElse(BTUtils.randNidStr())));
                put("nodes", "");
            }});
        }});
        send(bytes, address);
    }

    public void sendGetPeersReply(String tid, String otherNid, InetSocketAddress address) {
        byte[] bytes = bencode.encode(new HashMap<String, Object>() {{
            put("t", Optional.ofNullable(tid).orElse(BTUtils.randTidStr()));
            put("y", "r");
            put("r", new HashMap<String, Object>() {{
                put("id", BTUtils.fakeNidStr(
                        Config.SELF_NID,
                        Optional.ofNullable(otherNid).orElse(BTUtils.randNidStr())));
                put("token", BTUtils.getTokenStr(Optional.ofNullable(otherNid).orElse(BTUtils.randNidStr())));
                put("nodes", "");
            }});
        }});
        send(bytes, address);
    }

    public void sendPingReply(String tid, String otherNid, InetSocketAddress address) {
        byte[] bytes = bencode.encode(new HashMap<String, Object>() {{
            put("t", Optional.ofNullable(tid).orElse(BTUtils.randTidStr()));
            put("y", "r");
            put("r", new HashMap<String, Object>() {{
                put("id", BTUtils.fakeNidStr(Config.SELF_NID,
                        Optional.ofNullable(otherNid).orElse(BTUtils.randNidStr())));
            }});
        }});
        send(bytes, address);
    }

    public void sendAnnouncePeerReply(String tid, String otherNid, InetSocketAddress address) {
        sendPingReply(tid, otherNid, address);
    }

    private void send(byte[] bytes, InetSocketAddress address) {
        channel.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(bytes), address));
    }

    void setChannel(Channel channel) {
        this.channel = channel;
    }
}
