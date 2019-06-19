package com.hxs.bt.disruptor.handler;

import com.hxs.bt.disruptor.event.KrpcEvent;
import org.springframework.stereotype.Component;

/**
 * @author HJF
 * @date 2019/1/12 11:58
 */
@Component
public class PingEventHandler extends AbstractEventHandler {

    //{"t":"aa", "y":"q","q":"ping", "a":{"id":"abcdefghij0123456789"}}
    @Override
    protected boolean canHandleEvent(KrpcEvent message) {
        return "q".equals(message.getY())
                && "ping".equals(message.getQ())
                && null != message.getA();
    }

    /*
     *回复其他节点的ping请求
     *{"t":"aa", "y":"r", "r":{"id":"mnopqrstuvwxyz123456"}}
     */
    @Override
    protected void handleEvent(KrpcEvent message) {
        getSender().sendPingReply(
                message.getT(),
                message.getA().getId(),
                message.getSender());
    }
}
