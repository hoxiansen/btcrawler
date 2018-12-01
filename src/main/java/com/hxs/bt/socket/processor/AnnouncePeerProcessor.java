package com.hxs.bt.socket.processor;

import com.hxs.bt.pojo.KrpcMessage;
import com.hxs.bt.handler.InfoHashHandler;
import com.hxs.bt.socket.Sender;
import com.hxs.bt.util.BTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author HJF
 * @date 2018/11/19 17:13
 */
@Slf4j
@Order(40)
@Component
public class AnnouncePeerProcessor extends AbstractProcessor {
    private final Sender sender;
    private final InfoHashHandler infoHashHandler;

    public AnnouncePeerProcessor(Sender sender,
                                 InfoHashHandler infoHashHandler) {
        this.sender = sender;
        this.infoHashHandler = infoHashHandler;
    }

    //{"t":"aa", "y":"q","q":"announce_peer",
    // "a":{"id":"abcdefghij0123456789","info_hash":"mnopqrstuvwxyz123456", "port":6881, "token": "aoeusnth"}}
    @Override
    public boolean useThisProcess(KrpcMessage message) {
        return "q".equals(message.getY())
                && "announce_peer".equals(message.getQ())
                && null != message.getA();
    }

    @Override
    public void process0(KrpcMessage message) {
        if (!BTUtils.getTokenStr(message.getA().getId()).equals(message.getA().getToken())) {
            return;
        }
        log.debug("收到Announce_Peer:{}", message);
        sender.sendAnnouncePeerReply(
                message.getT(),
                message.getA().getId(),
                message.getSender(),
                message.getIndex());
        infoHashHandler.handleInfoHash(message.getA().getInfo_hash());
    }
}
