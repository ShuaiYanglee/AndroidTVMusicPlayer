package com.tcl.androidtvmusicplayer.entity;

/**
 * Created by yangshuai on 2018/9/25.
 *
 * @Description: 歌手实体类
 */

public class Artist {


    private String name;//歌手名
    private long id;//歌手ID
    private String picUrl;//歌手图片URL
    private int albumSize;//歌手专辑数量
    private int musicSize;//歌手音乐数量


    public Artist() {
    }

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

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public int getAlbumSize() {
        return albumSize;
    }

    public void setAlbumSize(int albumSize) {
        this.albumSize = albumSize;
    }

    public int getMusicSize() {
        return musicSize;
    }

    public void setMusicSize(int musicSize) {
        this.musicSize = musicSize;
    }
}
