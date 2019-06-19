package com.hxs.bt.disruptor.handler;

import com.hxs.bt.disruptor.event.KrpcEvent;
import org.springframework.stereotype.Component;

/**
 * @author HJF
 * @date 2019/1/12 13:15
 */
@Component
public class FindNodeQueryEventHandler extends AbstractEventHandler {

    //{"t":"aa", "y":"q","q":"find_node", "a":{"id":"abcdefghij0123456789","target":"mnopqrstuvwxyz123456"}}
    @Override
    protected boolean canHandleEvent(KrpcEvent message) {
        return "q".equals(message.getY())
                && "find_node".equals(message.getQ())
                && null != message.getA();
    }

    @Override
    protected void handleEvent(KrpcEvent message) {
        getSender().sendFindNodeReply(
                message.getT(),
                message.getA().getId(),
                message.getSender());
    }
}
