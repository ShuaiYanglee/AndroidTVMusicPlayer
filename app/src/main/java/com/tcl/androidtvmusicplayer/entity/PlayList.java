package com.tcl.androidtvmusicplayer.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yangshuai on 2018/9/18.
 *
 * @Description: 歌单实体类
 */

public class PlayList implements Serializable{

    private String name;//歌单名
    private long id;//歌单ID
    private String coverImgUrl;//歌单封面URL
    private String description;//歌单描述
    @SerializedName("tracks")
    private List<Song> songList;//歌单中的所有歌曲

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

    public List<Song> getSongList() {
        return songList;
    }

    public void setSongList(List<Song> songList) {
        this.songList = songList;
    }
}
