package com.tcl.androidtvmusicplayer.uti;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yangshuai on 2018/9/18.
 *
 * @Description: 网络工具类
 */

public class HttpUtils {

    static final OkHttpClient client = new OkHttpClient();

    public static <T> void doGetRequest(String url, final Class<T> tClass,Callback callback){
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }
}
