package com.hxs.bt.util;

import io.netty.util.CharsetUtil;

/**
 * @author HJF
 * @date 2018/11/13 17:33
 */
public class Utils {
    private final static char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F'};

    public static int bytesToPort(byte[] bytes) {
        return bytes[1] & 0xFF | (bytes[0] & 0xFF) << 8;
    }

    public static String bytesToHexString(byte[] bytes) {
        char[] chars = new char[bytes.length * 2];
        int index = 0;
        for (byte b : bytes) {
            chars[index++] = HEX_CHARS[b >>> 4 & 0x0f];
            chars[index++] = HEX_CHARS[b & 0x0f];
        }
        return new String(chars);
    }

    public static void main(String[] args) {
        String s = bytesToHexString(BTUtils.randNidStr().getBytes(CharsetUtil.ISO_8859_1));
        System.out.println(s);
    }
}
