package com.hxs.bt.socket.processor;

import com.hxs.bt.pojo.KrpcMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author HJF
 * @date 2018/11/19 22:01
 */
@Order(100)
@Slf4j
@Component
public class DefaultProcessor extends AbstractProcessor {
    @Override
    public boolean useThisProcess(KrpcMessage message) {
        return true;
    }

    @Override
    protected void process0(KrpcMessage message) {
//        log.info("未识别的Krpc包:{}", message.toString());
    }
}
