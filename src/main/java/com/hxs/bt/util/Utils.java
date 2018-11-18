package com.hxs.bt.util;

import java.util.Map;

/**
 * @author HJF
 * @date 2018/11/13 17:33
 */
public class Utils {
    public static int bytesToPort(byte[] bytes) {
        return bytes[1] & 0xFF | (bytes[0] & 0xFF) << 8;
    }
}
