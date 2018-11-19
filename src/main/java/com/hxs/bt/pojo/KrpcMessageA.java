package com.hxs.bt.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author HJF
 * @date 2018/11/19 15:57
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class KrpcMessageA {
    private String id;
    private String info_hash;
    private String target;
    private String token;
}
