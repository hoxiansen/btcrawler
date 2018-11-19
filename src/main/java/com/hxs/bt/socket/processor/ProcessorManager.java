package com.hxs.bt.socket.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dampcake.bencode.Bencode;
import com.dampcake.bencode.BencodeException;
import com.dampcake.bencode.Type;
import com.hxs.bt.pojo.KrpcMessage;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * @author HJF
 * @date 2018/11/19 16:33
 */
@Slf4j
public class ProcessorManager {
    private AbstractProcessor first;
    private AbstractProcessor last;

    private final Bencode bencode;

    public ProcessorManager(Bencode bencode) {
        this.bencode = bencode;
    }

    public void process(byte[] bytes, InetSocketAddress sender, int index) {
        try {
            Map<String, Object> krpcMap = bencode.decode(bytes, Type.DICTIONARY);
            KrpcMessage message = JSONObject.parseObject(JSON.toJSONString(krpcMap), KrpcMessage.class);
            message.setSender(sender);
            message.setIndex(index);
            beginProcess(message);
        } catch (BencodeException e) {
            // 解析出错了。。。
        }
    }

    public void register(AbstractProcessor processor) {
        log.info("添加Processor:{}",processor.getClass().getName());
        if (null == first) {
            first = last = processor;
            return;
        }
        last.setNext(processor);
        last = processor;
    }

    private void beginProcess(KrpcMessage message) {
        first.process(message);
    }
}
