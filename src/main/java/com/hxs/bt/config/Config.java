package com.hxs.bt.config;

import com.hxs.bt.entity.Node;
import com.hxs.bt.util.BTUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author HJF
 * @date 2018/11/13 10:37
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "bt")
public class Config implements InitializingBean {
    /**
     * DHTServer使用的端口号
     */
    private Integer port;
    /**
     * 服务启动的Tracker列表
     */
    private List<String> trackerList;
    /**
     * 发送FindNode请求所用的线程数量
     */
    private Integer findNodeTaskThreadNum = 10;
    /**
     * Node队列最大长度
     */
    private Integer nodeQueueMaxLength = 4096;
    /**
     * 是否处理getPeer消息送来的infoHash
     */
    private Boolean handleGetPeersInfoHash = false;
    /**
     * 是否时调试模式，调试模式下不发送FindNode请求
     */
    private Boolean debug = false;

    //***********************以上是写在配置文件中的****************************

    /**
     * 服务器的Nid
     */
    public static final String SELF_NID = BTUtils.randNidStr();
    /**
     * 根据Tracker生成的NodeList
     * 自动生成
     */
    private List<Node> bootNodeList = new ArrayList<>();

    /**
     * 将trackerString转换成NodeList
     */
    private void generateBootNodeList() {
        this.bootNodeList = this.trackerList.stream().map(item -> {
            String[] split = item.split(":");
            return new Node(BTUtils.randNidStr(), new InetSocketAddress(split[0], Integer.parseInt(split[1])));
        }).collect(Collectors.toList());
    }

    @Override
    public void afterPropertiesSet() {
        generateBootNodeList();
    }
}
