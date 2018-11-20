package com.hxs.bt.socket.processor;

import com.hxs.bt.pojo.KrpcMessage;
import com.hxs.bt.socket.Sender;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author HJF
 * @date 2018/11/19 17:16
 */
@Order(10)
@Component
public class FindNodeQueryProcessor extends AbstractProcessor {
    private final Sender sender;

    public FindNodeQueryProcessor(Sender sender) {
        this.sender = sender;
    }

    //{"t":"aa", "y":"q","q":"find_node", "a":{"id":"abcdefghij0123456789","target":"mnopqrstuvwxyz123456"}}
    @Override
    public boolean useThisProcess(KrpcMessage message) {
        return "q".equals(message.getY())
                && "find_node".equals(message.getQ())
                && null != message.getA();
    }

    @Override
    public void process0(KrpcMessage message) throws InterruptedException {
        sender.sendFindNodeReply(
                message.getT(),
                message.getA().getId(),
                message.getSender(),
                message.getIndex());
    }
}
