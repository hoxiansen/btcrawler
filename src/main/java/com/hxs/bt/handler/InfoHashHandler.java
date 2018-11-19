package com.hxs.bt.handler;

import com.hxs.bt.persistent.infohash.InfoHash;
import com.hxs.bt.persistent.infohash.InfoHashRepository;
import com.hxs.bt.util.Utils;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author HJF
 * @date 2018/11/17 22:35
 */
@Slf4j
@Component
public class InfoHashHandler {
    @Resource
    private InfoHashRepository infoHashRepository;

    public void handleInfoHash(String bytesInfoHash) {
        String hex = Utils.bytesToHexString(bytesInfoHash.getBytes(CharsetUtil.ISO_8859_1));
        InfoHash infoHash = new InfoHash();
        infoHash.setInfoHash(hex);
        infoHashRepository.saveAndFlush(infoHash);
    }
}
