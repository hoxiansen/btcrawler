package com.hxs.bt.persistent.entity;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author HJF
 * @date 2018/11/29 14:54
 */
@Entity
@Table
public class Btih {
    private Integer id;
    private String infoHash;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "info_hash")
    public String getInfoHash() {
        return infoHash;
    }

    public void setInfoHash(String infoHash) {
        this.infoHash = infoHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Btih btih = (Btih) o;
        return id.equals(btih.id) &&
                Objects.equals(infoHash, btih.infoHash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, infoHash);
    }
}
