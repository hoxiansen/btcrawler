package com.hxs.bt.disruptor.handler;

import com.hxs.bt.disruptor.event.KrpcEvent;
import com.hxs.bt.handler.MagnetHandler;
import com.hxs.bt.util.BTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author HJF
 * @date 2019/1/12 13:48
 */
@Slf4j
@Component
public class AnnouncePeerEventHandler extends AbstractEventHandler {
    @Resource
    private MagnetHandler magnetHandler;

    //{"t":"aa", "y":"q","q":"announce_peer",
    // "a":{"id":"abcdefghij0123456789","info_hash":"mnopqrstuvwxyz123456", "port":6881, "token": "aoeusnth"}}
    @Override
    protected boolean canHandleEvent(KrpcEvent message) {
        return "q".equals(message.getY())
                && "announce_peer".equals(message.getQ())
                && null != message.getA();
    }

    @Override
    protected void handleEvent(KrpcEvent message) {
        if (!BTUtils.getTokenStr(message.getA().getId()).equals(message.getA().getToken())) {
            log.info("Token验证失败");
            return;
        }
        log.debug("收到Announce_Peer:{}", message);
        getSender().sendAnnouncePeerReply(
                message.getT(),
                message.getA().getId(),
                message.getSender());
        magnetHandler.handleInfoHash(message.getA().getInfo_hash());
    }
}
