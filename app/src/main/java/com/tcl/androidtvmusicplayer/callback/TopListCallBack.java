package com.tcl.androidtvmusicplayer.callback;

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

public class TopListCallBack implements Callback {

    List<PlayList> topList = new ArrayList<>();
    MainFragment fragment;

    public TopListCallBack(MainFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onFailure(Call call, IOException e) {

    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        String jsonString = response.body().string();
        Gson gson = new Gson();
        JsonParser parser =new JsonParser();
        JsonObject object = parser.parse(jsonString).getAsJsonObject();
        JsonElement element = object.get("playlist");
        final PlayList list = gson.fromJson(element,PlayList.class);
        list.getCoverImgUrl();
        topList.add(list);
        if (topList.size() == 23 ){
            fragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fragment.loadRows(topList,1,"排行榜");
                }
            });
        }

    }
}
