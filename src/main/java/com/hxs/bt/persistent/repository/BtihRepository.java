package com.hxs.bt.persistent.repository;

import com.hxs.bt.persistent.entity.Btih;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author HJF
 * @date 2018/11/29 14:56
 */
public interface BtihRepository extends JpaRepository<Btih, Integer> {
}
