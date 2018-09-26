package com.tcl.androidtvmusicplayer.callback;

import android.app.Fragment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
 * @Description: 回调接口基类
 */

public class BaseCallBack implements Callback {

    private static final String TAG = "BaseCallBack";
    protected Gson gson;
    protected String jsonStr;
    protected JsonParser parser;
    protected JsonObject object;
    protected List list;

    public <T> BaseCallBack(Class<T> tClass) {
        list = new ArrayList<T>();
    }


    @Override
    public void onFailure(Call call, IOException e) {
        Log.e(TAG, "onFailure: " + e.toString());
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        jsonStr = response.body().string();
        gson = new Gson();
        parser = new JsonParser();
        object = parser.parse(jsonStr).getAsJsonObject();
    }
}
