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
 * @Description:
 */

public class PlayListCallBack implements Callback {

    MainFragment mainFragment;


    public PlayListCallBack(MainFragment mainFragment) {
        this.mainFragment = mainFragment;
    }

    @Override
    public void onFailure(Call call, IOException e) {

    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        String jsonString = response.body().string();
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject object = parser.parse(jsonString).getAsJsonObject();
        JsonArray jsonArray = object.getAsJsonArray("playlists");
        final List<PlayList> list = new ArrayList<>();
        for (JsonElement jsonElement:
                jsonArray) {
            list.add(gson.fromJson(jsonElement,PlayList.class));
        }
        list.size();
        mainFragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainFragment.loadRows(list,0,"热门歌单");
            }
        });
        //Log.d(TAG, "onResponse: "+list.size());
    }
}
