package com.hxs.bt.socket.processor;

import com.hxs.bt.common.manager.NodeManager;
import com.hxs.bt.pojo.KrpcMessage;
import com.hxs.bt.pojo.Node;
import com.hxs.bt.socket.Sender;
import com.hxs.bt.util.Utils;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * @author HJF
 * @date 2018/11/19 16:53
 */
@Order(0)
@Slf4j
@Component
public class FindNodeReplyProcessor extends AbstractProcessor {
    private final NodeManager nodeManager;

    public FindNodeReplyProcessor(NodeManager nodeManager) {
        this.nodeManager = nodeManager;
    }

    //{"t":"aa", "y":"r", "r":{"id":"0123456789abcdefghij", "nodes":"def456..."}}
    @Override
    public boolean useThisProcess(KrpcMessage message) {
        return "r".equals(message.getY())
                && null != message.getR()
                && null != message.getR().getNodes();
    }

    @Override
    public void process0(KrpcMessage message) {
        log.debug("收到FindNodeReply");
        splitNode(message.getR().getNodes().getBytes(CharsetUtil.ISO_8859_1));

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
                boolean addSuccess = nodeManager.add(new Node(new String(nid, CharsetUtil.ISO_8859_1), new InetSocketAddress(ip, port)));
                // 如果添加node不成功，说明node队列已满，直接退出不再继续添加。
                if (!addSuccess) return;
            } catch (UnknownHostException e) {
                log.info("Node解析出错：" + e.toString());
            } finally {
                index += 26;
            }
        }
    }
}
