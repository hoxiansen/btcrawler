package com.hxs.bt.util;

import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;

import java.nio.charset.Charset;

/**
 * @author HJF
 * @date 2018/11/13 10:26
 */
public class BTUtils {
    //设置为2导致有些服务器判定为invalid transaction id，设置为1看看效果
    private static final int TID_LENGTH = 1;
    private static final int FAKE_NID_LEN = 1;
    private static final int TOKEN_LEN = 2;
    private static Charset charset = CharsetUtil.ISO_8859_1;

    public static String randTidStr() {
        return new String(randTid(), charset);
    }

    public static String randNidStr() {
        return new String(randNid(), charset);
    }

    public static String fakeNidStr(String selfNidStr, String otherNidStr) {
        return new String(fakeNid(selfNidStr, otherNidStr), charset);
    }

    public static String getTokenStr(String otherNid) {
        byte[] bytes = otherNid.getBytes(charset);
        byte[] token = ArrayUtils.subarray(bytes, bytes.length - TOKEN_LEN, bytes.length);
        return new String(token, charset);
    }

    private static byte[] randTid() {
        return RandomUtils.nextBytes(TID_LENGTH);
    }

    private static byte[] randNid() {
        return RandomUtils.nextBytes(20);
    }

    private static byte[] fakeNid(String selfNidStr, String otherNidStr) {
        byte[] selfNid = selfNidStr.getBytes(CharsetUtil.ISO_8859_1);
        byte[] otherNid = otherNidStr.getBytes(CharsetUtil.ISO_8859_1);
        int len = otherNid.length;
        for (int i = 1; i <= FAKE_NID_LEN; i++) {
            otherNid[len - i] = selfNid[len - i];
        }
        return otherNid;
    }
}
