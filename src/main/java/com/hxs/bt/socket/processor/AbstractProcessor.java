package com.hxs.bt.socket.processor;

import com.hxs.bt.pojo.KrpcMessage;

/**
 * @author HJF
 * @date 2018/11/19 16:34
 */
public abstract class AbstractProcessor {
    private AbstractProcessor next;

    public void setNext(AbstractProcessor processor) {
        this.next = processor;
    }

    final void process(KrpcMessage message) throws InterruptedException {
        if (useThisProcess(message)) {
            process0(message);
            return;
        }
        if (null != next) {
            next.process(message);
        }
    }

    /**
     * @param message {@link KrpcMessage}
     * @return 是否能够处理这个message，不能则跳过该Processor
     */
    public abstract boolean useThisProcess(KrpcMessage message);

    protected abstract void process0(KrpcMessage message) throws InterruptedException;
}
