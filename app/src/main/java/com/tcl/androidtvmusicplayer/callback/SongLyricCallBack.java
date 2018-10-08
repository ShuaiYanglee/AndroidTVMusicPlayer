package com.tcl.androidtvmusicplayer.callback;

import android.os.Binder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.tcl.androidtvmusicplayer.activity.PlayActivity;
import com.tcl.androidtvmusicplayer.entity.Song;
import com.tcl.androidtvmusicplayer.service.PlayService;
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

    PlayService.MyBinder binder;
    Song song;
    public <T> SongLyricCallBack(PlayService.MyBinder binder, Song song) {
        super(Song.class);
        this.binder = binder;
        this.song = song;
    }


    @Override
    public void onResponse(Call call, Response response) throws IOException {
        super.onResponse(call, response);
        object = object.getAsJsonObject("lrc");
        String lyric = object.get("lyric").getAsString();
        song.setSongLyric(Utils.parseSongLyric(lyric));
        binder.playMusic();
    }
}
