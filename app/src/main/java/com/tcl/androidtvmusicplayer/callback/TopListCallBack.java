package com.tcl.androidtvmusicplayer.callback;

import com.google.gson.JsonElement;

import com.tcl.androidtvmusicplayer.entity.PlayList;
import com.tcl.androidtvmusicplayer.fragment.MainFragment;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by yangshuai on 2018/9/24.
 *
 * @Description: 排行榜回调接口
 */

public class TopListCallBack extends BaseCallBack {

    public TopListCallBack(MainFragment fragment) {
        super(fragment, PlayList.class);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        super.onResponse(call, response);
        JsonElement element = object.get("playlist");
        final PlayList topList = gson.fromJson(element, PlayList.class);
        list.add(topList);
        if (list.size() == 23) {
            fragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fragment.loadRows(list, 1, "排行榜");
                }
            });
        }
    }
}
