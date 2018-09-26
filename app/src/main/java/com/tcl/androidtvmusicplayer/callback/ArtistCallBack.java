package com.tcl.androidtvmusicplayer.callback;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tcl.androidtvmusicplayer.entity.Artist;
import com.tcl.androidtvmusicplayer.fragment.MainFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by yangshuai on 2018/9/25.
 *
 * @Description: 歌手榜单回调接口
 */

public class ArtistCallBack extends BaseCallBack {
    MainFragment mainFragment;

    public ArtistCallBack(MainFragment mainFragment) {
        super(Artist.class);
        this.mainFragment = mainFragment;
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        super.onResponse(call, response);
        object = object.get("list").getAsJsonObject();
        JsonArray jsonArray = object.get("artists").getAsJsonArray();
        for (JsonElement element : jsonArray) {
            list.add(gson.fromJson(element, Artist.class));
        }
        mainFragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainFragment.loadRows(list, 2, "歌手榜");
            }
        });
    }
}
