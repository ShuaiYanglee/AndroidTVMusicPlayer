package com.tcl.androidtvmusicplayer.entity;

/**
 * Created by yangshuai on 2018/9/18.
 *
 * @Description: 歌单实体类
 */

public class PlayList {

    private String name;//歌单名
    private long id;//歌单ID
    private String coverImgUrl;//歌单封面URL
    private String description;//歌单描述

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
