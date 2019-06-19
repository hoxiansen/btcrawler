package com.hxs.bt.disruptor.handler;

import com.hxs.bt.disruptor.event.KrpcEvent;
import com.hxs.bt.socket.Sender;
import com.lmax.disruptor.EventHandler;
import lombok.Getter;

import javax.annotation.Resource;

/**
 * @author HJF
 * @date 2019/1/12 12:59
 */
@Getter
public abstract class AbstractEventHandler implements EventHandler<KrpcEvent> {
    @Resource
    private Sender sender;

    @Override
    public void onEvent(KrpcEvent event, long sequence, boolean endOfBatch) throws Exception {
        handleThisEvent(event);
    }

    /**
     * @param message {@link KrpcEvent}
     * @return 是否能够处理这个message
     */
    protected abstract boolean canHandleEvent(KrpcEvent message);

    protected abstract void handleEvent(KrpcEvent message);

    private void handleThisEvent(KrpcEvent message) {
        if (canHandleEvent(message)) {
            handleEvent(message);
        }
    }
}
