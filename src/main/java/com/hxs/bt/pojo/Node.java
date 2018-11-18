package com.hxs.bt.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;

import java.net.InetSocketAddress;

/**
 * @author HJF
 * @date 2018/11/13 17:57
 */
@AllArgsConstructor
@Getter
@Setter
public class Node {
    private String nid;
    private InetSocketAddress address;
}
