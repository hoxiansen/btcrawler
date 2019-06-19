package com.hxs.bt.disruptor.handler;

import cn.hutool.core.util.HexUtil;
import com.hxs.bt.common.manager.NodeManager;
import com.hxs.bt.config.Config;
import com.hxs.bt.disruptor.event.KrpcEvent;
import com.hxs.bt.entity.Node;
import com.hxs.bt.util.Utils;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * @author HJF
 * @date 2019/1/12 13:18
 */
@Slf4j
@Component
public class FindNodeReplyEventHandler extends AbstractEventHandler {
    @Resource
    private NodeManager nodeManager;
    @Resource
    private Config config;

    //{"t":"aa", "y":"r", "r":{"id":"0123456789abcdefghij", "nodes":"def456..."}}
    @Override
    protected boolean canHandleEvent(KrpcEvent message) {
        return "r".equals(message.getY())
                && null != message.getR()
                && null != message.getR().getNodes();
    }

    @Override
    protected void handleEvent(KrpcEvent message) {
        splitNode(message.getR().getNodes().getBytes(CharsetUtil.ISO_8859_1));
    }

    private void splitNode(byte[] nodes) {
        int len = nodes.length;
        if (len % 26 != 0) {
            return;
        }
        int index = 0;
        while (index < len) {
            try {
                InetAddress ip = Inet4Address.getByAddress(ArrayUtils.subarray(nodes, index + 20, index + 24));
                byte[] nid = ArrayUtils.subarray(nodes, index, index + 20);
                int port = Utils.bytesToPort(ArrayUtils.subarray(nodes, index + 24, index + 26));
                boolean addSuccess = nodeManager.add(new Node(new String(nid, CharsetUtil.ISO_8859_1), new InetSocketAddress(ip, port)));
                // 如果添加node不成功，说明node队列已满，直接退出不再继续添加并将FindNode线程的暂停时间+1ms。
                if (!addSuccess) {
                    log.warn("Node队列已满");
                    return;
                }
            } catch (UnknownHostException e) {
                log.info("Node解析出错：" + e.toString());
            } finally {
                index += 26;
            }
        }
    }
}
