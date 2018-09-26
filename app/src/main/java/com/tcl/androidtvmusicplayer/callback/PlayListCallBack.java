package com.tcl.androidtvmusicplayer.callback;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.tcl.androidtvmusicplayer.entity.PlayList;
import com.tcl.androidtvmusicplayer.fragment.PlayListFragment;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by yangshuai on 2018/9/26.
 *
 * @Description: 获取单条歌单详情的回调接口
 */

public class PlayListCallBack extends BaseCallBack {
    private static final String TAG = "PlayListCallBack";

    PlayListFragment playListFragment;

    public <T> PlayListCallBack(PlayListFragment playListFragment) {
        super(PlayList.class);
        this.playListFragment = playListFragment;
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        super.onResponse(call, response);
        Log.d(TAG, "onResponse: " + jsonStr);
        object = object.getAsJsonObject("playlist");
        final PlayList playList = gson.fromJson(object, PlayList.class);
        playList.getSongList();
        playListFragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playListFragment.loadRows(playList);
            }
        });


    }
}
