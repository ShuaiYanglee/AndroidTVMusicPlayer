package com.tcl.androidtvmusicplayer.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yangshuai on 2018/9/25.
 *
 * @Description: 歌曲
 */

public class Song implements Serializable{

    private String name;//歌曲名
    private long id;//歌曲ID

    @SerializedName("ar")
    private List<Artist> artists;//歌曲演唱者
    @SerializedName("al")
    private Album album;//歌曲所属专辑


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

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }
}
