package com.tcl.androidtvmusicplayer.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tcl.androidtvmusicplayer.R;
import com.tcl.androidtvmusicplayer.constant.Constants;
import com.tcl.androidtvmusicplayer.entity.PlayList;
import com.tcl.androidtvmusicplayer.presenter.CardPresenter;
import com.tcl.androidtvmusicplayer.uti.HttpUtils;

import java.io.IOException;
import java.lang.ref.PhantomReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by yangshuai on 2018/9/23.
 *
 * @Description: 主要的Fragment，呈现歌单等主要内容
 */

public class MainFragment extends BrowseFragment {
    private static final String TAG = "MainFragment";

    private static final int BACKGROUND_UPDATE_DELAY = 300;

    private String backgroundImageUri;//背景图片URI

    private final Handler handler = new Handler();
    private ArrayObjectAdapter rowsAdapter;//存放一组listRow的Adapter
    private Drawable defaultBackground;//MainFragment的默认背景图
    private DisplayMetrics displayMetrics;//屏幕信息
    private Timer backgroundTimer;//背景切换计时器
    private BackgroundManager backgroundManager;//背景管理


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        HttpUtils.doGetRequest(Constants.PLAYLIST_URL_CAT,new PlayListCallBack(this));
        Log.i(TAG, "onActivityCreated: ");
        setUIElements();
        prepareBackgroundManager();
        setupEventListener();
    }


    private void setUIElements(){
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        //设置标题栏的图片
        setBadgeDrawable(getResources().getDrawable(R.drawable.ic_app_logo));

        //head的背景色
        setBrandColor(getResources().getColor(R.color.fastlane_background));

        //搜索的颜色
        setSearchAffordanceColor(getResources().getColor(R.color.search_opaque));
    }

    private void prepareBackgroundManager(){
        backgroundManager = BackgroundManager.getInstance(getActivity());
        backgroundManager.attach(getActivity().getWindow());
        defaultBackground = getResources().getDrawable(R.drawable.default_background);
        displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    }

    private void loadRows(List<PlayList> list){
        CardPresenter cardPresenter = new CardPresenter();
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);
        listRowAdapter.addAll(0,list);
        HeaderItem headerItem = new HeaderItem(0,"热门歌单");
        rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        rowsAdapter.add(new ListRow(headerItem,listRowAdapter));
        setAdapter(rowsAdapter);
    }


    private void setupEventListener(){

        setOnItemViewClickedListener(new ItemViewClickedListener());

        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }

    protected void updateBackground(String uri){
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        Glide.with(getActivity()).load(uri).centerCrop().error(defaultBackground).into(new SimpleTarget<GlideDrawable>(width,height) {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                backgroundManager.setDrawable(resource);
            }
        });
        backgroundTimer.cancel();
    }


    private final class ItemViewClickedListener implements OnItemViewClickedListener{

        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof PlayList){

            }
        }
    }


    private final class ItemViewSelectedListener implements OnItemViewSelectedListener{

        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof PlayList){
                backgroundImageUri = ((PlayList) item).getCoverImgUrl();
                startBackgroundTimer();
            }
        }
    }

    private class UpdateBackgroundTask extends TimerTask{

        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    updateBackground(backgroundImageUri);
                }
            });
        }
    }


    private void startBackgroundTimer(){
        if (null != backgroundTimer){
            backgroundTimer.cancel();
        }
        backgroundTimer = new Timer();
        backgroundTimer.schedule(new UpdateBackgroundTask(),BACKGROUND_UPDATE_DELAY);
    }


    private class PlayListCallBack implements Callback{

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
                    mainFragment.loadRows(list);
                }
            });
            Log.d(TAG, "onResponse: "+list.size());

        }
    }





}
