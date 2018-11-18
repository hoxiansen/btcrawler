package com.hxs.bt.config;

import com.hxs.bt.pojo.Node;
import com.hxs.bt.util.BTUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
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
     * DHTServer使用的端口列表，多个端口对应多个Server
     */
    private List<Integer> portList = new ArrayList<>(6);
    /**
     * 服务启动的Tracker列表
     */
    private List<String> trackerList = new ArrayList<>(10);
    /**
     * Netty服务器线程组最大数量
     */
    private Integer serverEventLoopThreadNum = 10;
    /**
     * 发送FindNode请求的间隔时间
     */
    private Integer findNodeTaskIntervalMS = 1;
    /**
     * 发送FindNode请求所用的线程数量，总量还要乘以port数量
     */
    private Integer findNodeTaskThreadNum = 20;
    /**
     * Node队列最大长度
     */
    private Integer nodeQueueMaxLength = 102400;

    private Boolean handleGetPeersInfoHash = false;



    /**
     * 每个服务器的Nid
     * 自动生成
     */
    private List<String> selfNidList = new ArrayList<>();
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

    /**
     * 生成NidList
     */
    private void generateSelfNidList() {
        for (int i = 0, len = this.portList.size(); i < len; i++) {
            this.selfNidList.add(BTUtils.randNidStr());
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        generateBootNodeList();
        generateSelfNidList();
    }
}
