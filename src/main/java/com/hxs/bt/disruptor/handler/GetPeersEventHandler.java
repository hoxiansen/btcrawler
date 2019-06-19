package com.hxs.bt.disruptor.handler;

import com.hxs.bt.config.Config;
import com.hxs.bt.disruptor.event.KrpcEvent;
import com.hxs.bt.handler.MagnetHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class GetPeersEventHandler extends AbstractEventHandler {
    @Resource
    private MagnetHandler magnetHandler;
    @Resource
    private Config config;

    //{"t":"aa", "y":"q","q":"get_peers", "a":{"id":"abcdefghij0123456789","info_hash":"mnopqrstuvwxyz123456"}}
    @Override
    protected boolean canHandleEvent(KrpcEvent message) {
        return "q".equals(message.getY())
                && "get_peers".equals(message.getQ())
                && null != message.getA();
    }

    @Override
    protected void handleEvent(KrpcEvent message) {
        getSender().sendGetPeersReply(
                message.getT(),
                message.getA().getId(),
                message.getSender());
        if(config.getHandleGetPeersInfoHash()){
            magnetHandler.handleInfoHash(message.getA().getInfo_hash());
        }
    }
}
