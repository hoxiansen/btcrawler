package com.hxs.bt.util;

import com.hxs.bt.entity.Node;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

/**
 * @author HJF
 * @date 2018/11/13 17:33
 */
public class Utils {
    private final static char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'};

    public static int bytesToPort(byte[] bytes) {
        return bytes[1] & 0xFF | (bytes[0] & 0xFF) << 8;
    }

    public static byte[] portToBytes(int port) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (port >>> 8 & 0xFF);
        bytes[1] = (byte) (port & 0xFF);
        return bytes;
    }

    @Deprecated
    public static String bytesToHexString(byte[] bytes) {
        char[] chars = new char[bytes.length * 2];
        int index = 0;
        for (byte b : bytes) {
            chars[index++] = HEX_CHARS[b >>> 4 & 0x0f];
            chars[index++] = HEX_CHARS[b & 0x0f];
        }
        return new String(chars);
    }

    public static String encodeNode(Node node) {
        byte[] bytes = new byte[26];
        byte[] nid = node.getNid().getBytes(CharsetUtil.ISO_8859_1);
        InetSocketAddress address = node.getAddress();
        byte[] ip = address.getAddress().getAddress();
        byte[] port = portToBytes(address.getPort());
        //防止越界
        System.arraycopy(bytes, 0, nid, 0, nid.length);
        System.arraycopy(bytes, 20, ip, 0, ip.length);
        System.arraycopy(bytes, 24, port, 0, 2);
        return new String(bytes, CharsetUtil.ISO_8859_1);
    }

    public static void main(String[] args) {
    }
}
