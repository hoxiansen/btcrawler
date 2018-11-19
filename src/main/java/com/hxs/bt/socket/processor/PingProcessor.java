package com.hxs.bt.socket.processor;

import com.hxs.bt.pojo.KrpcMessage;
import com.hxs.bt.socket.Sender;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author HJF
 * @date 2018/11/19 17:10
 */
@Order(20)
@Component
public class PingProcessor extends AbstractProcessor {
    private final Sender sender;

    public PingProcessor(Sender sender) {
        this.sender = sender;
    }

    //{"t":"aa", "y":"q","q":"ping", "a":{"id":"abcdefghij0123456789"}}
    @Override
    public boolean useThisProcess(KrpcMessage message) {
        return "q".equals(message.getY())
                && "ping".equals(message.getQ())
                && null != message.getA();
    }

    /*
     *回复其他节点的ping请求
     *{"t":"aa", "y":"r", "r":{"id":"mnopqrstuvwxyz123456"}}
     */
    @Override
    public void process0(KrpcMessage message) {
        sender.sendPingReply(
                message.getT(),
                message.getA().getId(),
                message.getSender(),
                message.getIndex());
    }
}
