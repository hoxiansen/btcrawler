package com.hxs.bt.socket.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dampcake.bencode.Bencode;
import com.dampcake.bencode.BencodeException;
import com.dampcake.bencode.Type;
import com.hxs.bt.common.factory.ProcessorThreadFactory;
import com.hxs.bt.config.Config;
import com.hxs.bt.pojo.KrpcMessage;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author HJF
 * @date 2018/11/19 16:33
 */
@Slf4j
public class ProcessorManager {
    private AbstractProcessor first;
    private AbstractProcessor last;

    private final Bencode bencode;

    private final ThreadPoolExecutor executor;

    public ProcessorManager(Bencode bencode, Config config) {
        this.bencode = bencode;
        //初始化连接池
        executor = new ThreadPoolExecutor(
                config.getProcessorThreadNum(),
                config.getProcessorThreadNum(),
                5, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(config.getProcessorThreadNum()*2),new ProcessorThreadFactory());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public void process(byte[] bytes, InetSocketAddress sender, int index) {
        executor.execute(() -> {
            try {
                Map<String, Object> krpcMap = bencode.decode(bytes, Type.DICTIONARY);
                //采用fastJson的反序列化，bencode解析之后是map，需要map转string，考虑重写一个bencode直接转string
                KrpcMessage message = JSONObject.parseObject(JSON.toJSONString(krpcMap), KrpcMessage.class);
                // krpc没有y，没有处理的必要。
                if (null == message.getY() || "".equals(message.getY())) {
                    return;
                }
                message.setSender(sender);
                message.setIndex(index);
                beginProcess(message);
            } catch (BencodeException e) {
                // 解析出错了。。。
            } catch (InterruptedException e) {
                // 线程中断。。。
            }
        });

    }

    public void register(AbstractProcessor processor) {
        log.debug("添加Processor:{}", processor.getClass().getName());
        if (null == first) {
            first = last = processor;
            return;
        }
        last.setNext(processor);
        last = processor;
    }

    private void beginProcess(KrpcMessage message) throws InterruptedException {
        first.process(message);
    }
}
