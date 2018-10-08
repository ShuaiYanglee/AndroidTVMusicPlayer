package com.tcl.androidtvmusicplayer.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.VerticalGridFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.FocusHighlight;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.VerticalGridPresenter;
import android.util.DisplayMetrics;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tcl.androidtvmusicplayer.GlideApp;
import com.tcl.androidtvmusicplayer.R;
import com.tcl.androidtvmusicplayer.activity.PlayActivity;
import com.tcl.androidtvmusicplayer.callback.ArtistDetailCallBack;
import com.tcl.androidtvmusicplayer.callback.PlayListCallBack;
import com.tcl.androidtvmusicplayer.constant.Constants;
import com.tcl.androidtvmusicplayer.entity.PlayList;
import com.tcl.androidtvmusicplayer.entity.Song;
import com.tcl.androidtvmusicplayer.presenter.CardPresenter;
import com.tcl.androidtvmusicplayer.uti.HttpUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yangshuai on 2018/9/25.
 *
 * @Description:
 */

public class PlayListFragment extends VerticalGridFragment {

    private static final int ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_MEDIUM;
    private static final int COLUMNS = 4;

    private ArrayObjectAdapter adapter;
    Handler handler = new Handler();
    private String backgroundImageUri;//背景图片URI
    private Drawable defaultBackground;//MainFragment的默认背景图
    private DisplayMetrics displayMetrics;//屏幕信息
    private Timer backgroundTimer;//背景切换计时器
    private BackgroundManager backgroundManager;//背景管理
    private static final int BACKGROUND_UPDATE_DELAY = 300;

    private static final String TAG = "PlayListFragment";
    private List<Song> songList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long playListId = getActivity().getIntent().getLongExtra(Constants.PLAYLIST, 0);
        long artistId = getActivity().getIntent().getLongExtra(Constants.ARTIST, 0);
        if (artistId != 0) {
            HttpUtils.doGetRequest(Constants.ARTIST_DETAIL + artistId, new ArtistDetailCallBack(this));

        }
        if (playListId != 0) {
            HttpUtils.doGetRequest(Constants.PLAYLIST_DETAIL + playListId, new PlayListCallBack(this));
        }
        beforeInitData();
    }

    //获得数据前的准备
    private void beforeInitData() {
        setEventListener();
        prepareBackgroundManager();
        setUpRowAdapter();
        prepareEntranceTransition();

    }

    private void setEventListener() {
        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }

    private void prepareBackgroundManager() {
        backgroundManager = BackgroundManager.getInstance(getActivity());
        backgroundManager.attach(getActivity().getWindow());
        defaultBackground = getResources().getDrawable(R.drawable.default_background);
        displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    }

    private void setUpRowAdapter() {
        VerticalGridPresenter gridPresenter = new VerticalGridPresenter(ZOOM_FACTOR);
        gridPresenter.setNumberOfColumns(COLUMNS);
        setGridPresenter(gridPresenter);
        adapter = new ArrayObjectAdapter(new CardPresenter());
        setAdapter(adapter);

    }

    public void loadRows(PlayList list) {
        setTitle(list.getName());
        loadRows(list.getSongList());

    }

    public void loadRows(List<Song> songList) {
        this.songList = songList;
        adapter.addAll(0, songList);
        adapter.notifyItemRangeChanged(0, 1);
        startEntranceTransition();
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


    private void startBackgroundTimer() {
        if (null != backgroundTimer) {
            backgroundTimer.cancel();
        }
        backgroundTimer = new Timer();
        backgroundTimer.schedule(new UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY);
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


    private class ItemViewSelectedListener implements OnItemViewSelectedListener {

        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof Song) {
                backgroundImageUri = ((Song) item).getAlbum().getPicUrl();
                startBackgroundTimer();
            }
        }
    }


    private class ItemViewClickedListener implements OnItemViewClickedListener {

        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof Song) {
                if (item instanceof Song) {
                    Song song = (Song) item;
                    Intent intent = new Intent(getContext(), PlayActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.SONG_LIST, (Serializable) songList);
                    bundle.putSerializable(Constants.SONG, song);
                    intent.putExtra(Constants.BUNDLE, bundle);
                    startActivity(intent);
                }
            }
        }
    }


}
