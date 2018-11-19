package com.hxs.bt.pojo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dampcake.bencode.Bencode;
import com.dampcake.bencode.Type;
import io.netty.util.CharsetUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.jackson.JsonObjectDeserializer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author HJF
 * @date 2018/11/19 15:52
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class KrpcMessage {
    //这两个手动添加
    private transient InetSocketAddress sender;
    private transient int index;

    private String t;
    private String y;

    //暂时将e解析为String
    private String e;
    private String q;
    private KrpcMessageA a;
    private KrpcMessageR r;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
