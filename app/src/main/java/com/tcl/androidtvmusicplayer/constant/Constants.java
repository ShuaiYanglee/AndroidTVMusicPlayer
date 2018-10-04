package com.tcl.androidtvmusicplayer.constant;

/**
 * Created by yangshuai on 2018/9/18.
 *
 * @Description: 常量类
 */

public class Constants {
    //音乐服务器IP
    public static final String SERVER_HOST = "http://192.168.0.102:3000/";

    //public static final String SERVER_HOST ="http://10.120.5.25:3000/";


    //华语热门歌单
    public static final String PLAYLIST_URL_CAT = SERVER_HOST + "top/playlist?cat=华语&limit=10&order=hot";

    //排行榜
    public static final String TOP_LIST = SERVER_HOST + "top/list?idx=";

    //歌手榜
    public static final String ARTISTS = SERVER_HOST + "toplist/artist";

    //歌单详情
    public static final String PLAYLIST_DETAIL = SERVER_HOST + "playlist/detail?id=";

    //歌手详情
    public static final String ARTIST_DETAIL = SERVER_HOST + "artists?id=";


    //歌曲URL
    //public static final String SONG_URL = SERVER_HOST+"music/url?id=";

    public static final String SONG_URL = "http://music.163.com/song/media/outer/url?id=";


    public static final String SONG_LYRIC = SERVER_HOST + "lyric?id=";

    //Intent传递的参数
    public static final String PLAYLIST = "playlist";

    //Intent传递的参数
    public static final String ARTIST = "artist";

    //Intent传递的参数
    public static final String SONG = "song";

}
