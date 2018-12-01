package com.hxs.bt.handler;

import com.hxs.bt.persistent.entity.Btih;
import com.hxs.bt.persistent.repository.BtihRepository;
import com.hxs.bt.util.Utils;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.Semaphore;

/**
 * @author HJF
 * @date 2018/11/17 22:35
 */
@Slf4j
@Component
public class InfoHashHandler {
    @Resource
    private BtihRepository btihRepository;
    private Semaphore semaphore = new Semaphore(100);

    public void handleInfoHash(String bytesInfoHash) {
        String hex = Utils.bytesToHexString(bytesInfoHash.getBytes(CharsetUtil.ISO_8859_1));
        Btih btih = new Btih();
        btih.setInfoHash(hex);
        try {
            semaphore.acquire();
            log.debug("存储InfoHash:{}", btih);
            btihRepository.saveAndFlush(btih);
        } catch (InterruptedException e) {
            //...
        } finally {
            semaphore.release();
        }
    }
}
