package com.hxs.bt.disruptor.event;

import lombok.Data;

import java.net.InetSocketAddress;

@Data
public class KrpcEvent {
    private transient InetSocketAddress sender;

    public String t;
    public String y;

    public String e;
    public String q;

    public KrpcA a = new KrpcA();
    public KrpcR r = new KrpcR();
}