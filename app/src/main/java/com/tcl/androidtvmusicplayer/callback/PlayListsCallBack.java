package com.tcl.androidtvmusicplayer.callback;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tcl.androidtvmusicplayer.entity.PlayList;
import com.tcl.androidtvmusicplayer.fragment.MainFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by yangshuai on 2018/9/24.
 *
 * @Description: 获取多条的歌单回调接口
 */

public class PlayListsCallBack extends BaseCallBack {

    public PlayListsCallBack(MainFragment mainFragment) {
        super(mainFragment, PlayList.class);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        super.onResponse(call, response);
        JsonArray jsonArray = object.getAsJsonArray("playlists");
        final List<PlayList> list = new ArrayList<>();
        for (JsonElement jsonElement :
                jsonArray) {
            list.add(gson.fromJson(jsonElement, PlayList.class));
        }
        list.size();
        fragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fragment.loadRows(list, 0, "热门歌单");
            }
        });
    }
}
