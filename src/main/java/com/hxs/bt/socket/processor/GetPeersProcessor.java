package com.hxs.bt.socket.processor;

import com.hxs.bt.config.Config;
import com.hxs.bt.pojo.KrpcMessage;
import com.hxs.bt.handler.InfoHashHandler;
import com.hxs.bt.socket.Sender;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author HJF
 * @date 2018/11/19 17:05
 */
@Order(30)
@Component
public class GetPeersProcessor extends AbstractProcessor {
    private final Sender sender;
    private final Config config;
    private final InfoHashHandler infoHashHandler;

    public GetPeersProcessor(Sender sender,
                             Config config,
                             InfoHashHandler infoHashHandler) {
        this.sender = sender;
        this.config = config;
        this.infoHashHandler = infoHashHandler;
    }

    //{"t":"aa", "y":"q","q":"get_peers", "a":{"id":"abcdefghij0123456789","info_hash":"mnopqrstuvwxyz123456"}}
    @Override
    public boolean useThisProcess(KrpcMessage message) {
        return "q".equals(message.getY())
                && "get_peers".equals(message.getQ())
                && null != message.getA();
    }

    @Override
    public void process0(KrpcMessage message) {
        sender.sendGetPeersReply(
                message.getT(),
                message.getA().getId(),
                message.getSender(),
                message.getIndex());
        if (config.getHandleGetPeersInfoHash()) {
            infoHashHandler.handleInfoHash(message.getA().getInfo_hash());
        }
    }
}
