package com.tcl.androidtvmusicplayer.entity;

import java.io.Serializable;

/**
 * Created by yangshuai on 2018/9/25.
 *
 * @Description: 专辑
 */

public class Album implements Serializable{

    private long id;//专辑ID
    private String name;//专辑名
    private String picUrl;//专辑封面URL

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}
