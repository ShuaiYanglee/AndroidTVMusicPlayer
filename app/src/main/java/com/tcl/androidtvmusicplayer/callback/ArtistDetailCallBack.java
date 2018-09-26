package com.tcl.androidtvmusicplayer.callback;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.tcl.androidtvmusicplayer.entity.Song;
import com.tcl.androidtvmusicplayer.fragment.PlayListFragment;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by yangshuai on 2018/9/26.
 *
 * @Description: 歌手详情回调接口，获得歌曲列表
 */

public class ArtistDetailCallBack extends BaseCallBack {

    PlayListFragment playListFragment;

    public <T> ArtistDetailCallBack(PlayListFragment playListFragment) {
        super(Song.class);
        this.playListFragment = playListFragment;
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        super.onResponse(call, response);
        JsonArray array = object.getAsJsonArray("hotSongs");
        for (JsonElement element : array
                ) {
            list.add(gson.fromJson(element, Song.class));
        }
        list.size();
        playListFragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playListFragment.loadRows(list);
            }
        });
    }
}
