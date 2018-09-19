package com.tcl.androidtvmusicplayer.constant;

/**
 * Created by yangshuai on 2018/9/18.
 *
 * @Description: 常量类
 */

public class Constants {
    //音乐服务器IP
    public static final String SERVER_HOST ="http://192.168.0.102:3000/";

    //华语热门歌单
    public static final String PLAYLIST_URL_CAT = SERVER_HOST + "top/playlist?cat=华语&limit=10&order=hot";
}
