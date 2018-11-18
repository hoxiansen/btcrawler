package com.hxs.bt.persistent.infohash;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author HJF
 * @date 2018/11/18 13:00
 */
@Entity
@Table(name = "infohash")
public class InfoHash {
    private int id;
    private String infoHash;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
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
        InfoHash infoHash1 = (InfoHash) o;
        return id == infoHash1.id &&
                Objects.equals(infoHash, infoHash1.infoHash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, infoHash);
    }
}
