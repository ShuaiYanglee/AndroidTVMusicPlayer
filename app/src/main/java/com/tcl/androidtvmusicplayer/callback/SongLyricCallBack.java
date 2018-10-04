package com.tcl.androidtvmusicplayer.callback;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.tcl.androidtvmusicplayer.activity.PlayActivity;
import com.tcl.androidtvmusicplayer.entity.Song;
import com.tcl.androidtvmusicplayer.uti.Utils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by yangshuai on 2018/9/28.
 *
 * @Description:
 */

public class SongLyricCallBack extends BaseCallBack {

    PlayActivity playActivity;
    Song song;
    public <T> SongLyricCallBack(PlayActivity playActivity,Song song) {
        super(Song.class);
        this.playActivity = playActivity;
        this.song = song;
    }


    @Override
    public void onResponse(Call call, Response response) throws IOException {
        super.onResponse(call, response);
        object = object.getAsJsonObject("lrc");
        String lyric = object.get("lyric").getAsString();
        song.setSongLyric(Utils.parseSongLyric(lyric));
        playActivity.initData(song);
    }
}
