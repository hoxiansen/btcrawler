package com.hxs.bt.disruptor;

import com.hxs.bt.disruptor.event.KrpcEvent;
import com.hxs.bt.util.bencode.Decoder;
import com.lmax.disruptor.EventTranslatorTwoArg;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;

@Slf4j
@Component
public class KrpcEventTranslator implements EventTranslatorTwoArg<KrpcEvent, ByteBuf, InetSocketAddress> {
    @Override
    public void translateTo(KrpcEvent event, long sequence, ByteBuf byteBuf, InetSocketAddress sender) {
        try {
            Decoder.decode(byteBuf, event);
            event.setSender(sender);
        } catch (IOException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            log.error("解码失败:{}", e.getMessage());
        }
    }
}
