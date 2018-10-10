package com.tcl.androidtvmusicplayer.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tcl.androidtvmusicplayer.GlideApp;
import com.tcl.androidtvmusicplayer.R;
import com.tcl.androidtvmusicplayer.activity.PlayListActivity;
import com.tcl.androidtvmusicplayer.callback.ArtistCallBack;
import com.tcl.androidtvmusicplayer.callback.PlayListsCallBack;
import com.tcl.androidtvmusicplayer.callback.TopListCallBack;
import com.tcl.androidtvmusicplayer.constant.Constants;
import com.tcl.androidtvmusicplayer.entity.Artist;
import com.tcl.androidtvmusicplayer.entity.PlayList;
import com.tcl.androidtvmusicplayer.presenter.CardPresenter;
import com.tcl.androidtvmusicplayer.service.PlayService;
import com.tcl.androidtvmusicplayer.uti.HttpUtils;
import com.tcl.androidtvmusicplayer.uti.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

    PlayService.MyBinder binder;
    PlayList localPlayList = new PlayList();

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (PlayService.MyBinder) service;
            binder.setHandler(handler);
            binder.setSongList(localPlayList.getSongList());
            binder.initData(localPlayList.getSongList().get(0));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onActivityCreated: ");
        doGetRequest();
        setUIElements();
        prepareBackgroundManager();
        initAdapter();
        setupEventListener();
        Intent intent = new Intent(this.getContext(), PlayService.class);
        getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);

    }

    private void initAdapter() {
        rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        setAdapter(rowsAdapter);
        loadLocalMusic();

    }

    private void doGetRequest() {
        HttpUtils.doGetRequest(Constants.PLAYLIST_URL_CAT, new PlayListsCallBack(this));
        TopListCallBack topListCallBack = new TopListCallBack(this);
        for (int i = 1; i <= 23; i++) {
            HttpUtils.doGetRequest(Constants.TOP_LIST + i, topListCallBack);
        }
        HttpUtils.doGetRequest(Constants.ARTISTS, new ArtistCallBack(this));
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    private void setUIElements() {
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        //设置标题栏的图片
        setBadgeDrawable(getResources().getDrawable(R.drawable.ic_app_logo));

        //head的背景色
        setBrandColor(getResources().getColor(R.color.fastlane_background));

        //搜索的颜色
        setSearchAffordanceColor(getResources().getColor(R.color.search_opaque));
    }

    private void prepareBackgroundManager() {
        backgroundManager = BackgroundManager.getInstance(getActivity());
        backgroundManager.attach(getActivity().getWindow());
        defaultBackground = getResources().getDrawable(R.drawable.default_background);
        displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    }

    public <T> void loadRows(List<T> list, int index, String headerName) {
        CardPresenter cardPresenter = new CardPresenter();
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);
        listRowAdapter.addAll(0, list);
        HeaderItem headerItem = new HeaderItem(index, headerName);
        rowsAdapter.add(new ListRow(headerItem, listRowAdapter));
        rowsAdapter.notifyItemRangeChanged(index, 1);
    }


    private void setupEventListener() {

        setOnItemViewClickedListener(new ItemViewClickedListener());

        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }

    protected void updateBackground(String uri) {
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        GlideApp.with(getActivity()).load(uri).centerCrop().error(defaultBackground).into(new SimpleTarget<Drawable>(width, height) {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                backgroundManager.setDrawable(resource);
            }
        });
        backgroundTimer.cancel();
    }


    private final class ItemViewClickedListener implements OnItemViewClickedListener {

        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            Intent intent = new Intent(getActivity(), PlayListActivity.class);
            if (item instanceof PlayList) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.PLAYLIST_ID,((PlayList) item).getId());
                bundle.putSerializable(Constants.LOCAL_PLAY_LIST, (Serializable) ((PlayList) item).getSongList());
                intent.putExtra(Constants.BUNDLE,bundle);
            }
            if (item instanceof Artist) {
                intent.putExtra(Constants.ARTIST, ((Artist) item).getId());
            }
            startActivity(intent);
        }
    }


    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {

        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof PlayList) {
                backgroundImageUri = ((PlayList) item).getCoverImgUrl();
                startBackgroundTimer();
            }
            if (item instanceof Artist) {
                backgroundImageUri = ((Artist) item).getPicUrl();
                startBackgroundTimer();
            }
        }
    }

    private class UpdateBackgroundTask extends TimerTask {

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


    private void startBackgroundTimer() {
        if (null != backgroundTimer) {
            backgroundTimer.cancel();
        }
        backgroundTimer = new Timer();
        backgroundTimer.schedule(new UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY);
    }




    private void loadLocalMusic(){
        localPlayList.setName("本地音乐");
        localPlayList.setDescription("本地音乐列表");
        localPlayList.setCoverImgUrl(" ");
        List<PlayList> playLists = new ArrayList<>();
        localPlayList.setSongList(Utils.getLocalMusics(this.getContext().getApplicationContext()));
        playLists.add(localPlayList);
        loadRows(playLists,3,"本地音乐");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unbindService(connection);
        binder.getMediaPlayer().stop();
        binder.getMediaPlayer().release();

    }
}
