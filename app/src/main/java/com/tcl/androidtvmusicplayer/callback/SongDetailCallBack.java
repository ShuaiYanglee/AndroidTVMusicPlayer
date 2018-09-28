package com.tcl.androidtvmusicplayer.callback;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.tcl.androidtvmusicplayer.activity.PlayActivity;
import com.tcl.androidtvmusicplayer.entity.Song;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by yangshuai on 2018/9/28.
 *
 * @Description:
 */

public class SongDetailCallBack extends BaseCallBack {



    PlayActivity playActivity;
    public <T> SongDetailCallBack(PlayActivity playActivity) {
        super(Song.class);
        this.playActivity = playActivity;
    }


    @Override
    public void onResponse(Call call, Response response) throws IOException {
        super.onResponse(call, response);
        JsonArray array = object.get("data").getAsJsonArray();
        JsonElement element = array.get(0);
        Song song = gson.fromJson(element,Song.class);
        playActivity.initData(song);
    }
}
