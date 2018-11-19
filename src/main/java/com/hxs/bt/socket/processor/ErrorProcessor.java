package com.hxs.bt.socket.processor;

import com.hxs.bt.pojo.KrpcMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author HJF
 * @date 2018/11/19 18:10
 */
@Order(50)
@Slf4j
@Component
public class ErrorProcessor extends AbstractProcessor {
    @Override
    public boolean useThisProcess(KrpcMessage message) {
        return "e".equals(message.getY());
    }

    @Override
    public void process0(KrpcMessage message) {
        log.info("errorKrpc:{}",message.getE());
    }
}
