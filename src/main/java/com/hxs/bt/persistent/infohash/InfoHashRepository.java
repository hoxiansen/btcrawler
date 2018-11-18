package com.hxs.bt.persistent.infohash;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

/**
 * @author HJF
 * @date 2018/11/18 19:51
 */
@Service
public interface InfoHashRepository extends JpaRepository<InfoHash, Integer> {
}
