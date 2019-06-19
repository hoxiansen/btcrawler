package com.hxs.bt.util.bencode;

import com.hxs.bt.disruptor.event.KrpcEvent;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Decoder {
    private static final Charset CHARSET = StandardCharsets.ISO_8859_1;
    /**
     * Number Marker
     */
    private static final char NUMBER = 'i';

    /**
     * List Marker
     */
    private static final char LIST = 'l';

    /**
     * Dictionary Marker
     */
    private static final char DICTIONARY = 'd';

    /**
     * End of type Marker
     */
    private static final char TERMINATOR = 'e';

    /**
     * Separator between length and string
     */
    private static final char SEPARATOR = ':';

    public static void decode(final ByteBuf byteBuf, final KrpcEvent event) throws IOException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        ByteBufInputStream in = new ByteBufInputStream(byteBuf);
        char token = (char) in.readByte();
        if (token != DICTIONARY) {
            event.setY(StringUtil.EMPTY_STRING);
            log.info("错误的KRPC消息");
            return;
        }
        readDictionary(byteBuf, event);
    }

    private static void readDictionary(final ByteBuf byteBuf, final Object obj) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        String name;
        char token;
        while (byteBuf.isReadable() && (token = (char) byteBuf.readByte()) != TERMINATOR) {
            name = readString(byteBuf, token);
            token = (char) byteBuf.readByte();
            if (token == DICTIONARY) {
                readDictionary(byteBuf, PropertyUtils.getProperty(obj, name));
            } else {
                BeanUtils.setProperty(obj, name, readObject(byteBuf, token));
            }
        }
    }

    private static String readString(final ByteBuf byteBuf, char token) {
        StringBuilder sb = new StringBuilder(2);
        sb.append(token);
        while (byteBuf.isReadable() && (token = (char) byteBuf.readByte()) != SEPARATOR) {
            sb.append(token);
        }
        int length = Integer.parseInt(sb.toString());
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        return new String(bytes, CHARSET);
    }

    private static Object readObject(ByteBuf byteBuf, char token) {
        if (Character.isDigit(token)) {
            return readString(byteBuf, token);
        } else if (token == LIST) {
            return readList(byteBuf);
        } else if (token == NUMBER) {
            return readInteger(byteBuf);
        }
        return null;
    }

    private static String readList(ByteBuf byteBuf) {
        List<Object> list = new ArrayList<>(2);
        char token;
        while ((token = (char) byteBuf.readByte()) != TERMINATOR) {
            list.add(readObject(byteBuf, token));
        }
        return list.toString();
    }

    private static Integer readInteger(ByteBuf byteBuf) {
        StringBuilder sb = new StringBuilder();
        char t;
        while ((t = (char) byteBuf.readByte()) != TERMINATOR) {
            sb.append(t);
        }
        return Integer.valueOf(sb.toString());
    }
}
