package com.tcl.androidtvmusicplayer.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.tcl.androidtvmusicplayer.R;
import com.tcl.androidtvmusicplayer.activity.PlayListActivity;
import com.tcl.androidtvmusicplayer.callback.ArtistCallBack;
import com.tcl.androidtvmusicplayer.callback.PlayListsCallBack;
import com.tcl.androidtvmusicplayer.callback.TopListCallBack;
import com.tcl.androidtvmusicplayer.constant.Constants;
import com.tcl.androidtvmusicplayer.entity.Artist;
import com.tcl.androidtvmusicplayer.entity.PlayList;
import com.tcl.androidtvmusicplayer.presenter.CardPresenter;
import com.tcl.androidtvmusicplayer.uti.HttpUtils;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onActivityCreated: ");
        doGetRequest();
        setUIElements();
        prepareBackgroundManager();
        initAdapter();
        setupEventListener();
    }

    private void initAdapter() {
        rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        setAdapter(rowsAdapter);

    }

    private void doGetRequest() {
        HttpUtils.doGetRequest(Constants.PLAYLIST_URL_CAT, new PlayListsCallBack(this));
        TopListCallBack topListCallBack = new TopListCallBack(this);
        for (int i = 1; i <= 23; i++) {
            HttpUtils.doGetRequest(Constants.TOP_LIST + i, topListCallBack);
        }
        HttpUtils.doGetRequest(Constants.ARTIST, new ArtistCallBack(this));
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
        Glide.with(getActivity()).load(uri).centerCrop().error(defaultBackground).into(new SimpleTarget<GlideDrawable>(width, height) {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                backgroundManager.setDrawable(resource);
            }
        });
        backgroundTimer.cancel();
    }


    private final class ItemViewClickedListener implements OnItemViewClickedListener {

        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof PlayList) {
                Intent intent = new Intent(getActivity(), PlayListActivity.class);
                intent.putExtra(Constants.PLAYLIST,((PlayList) item).getId());
                startActivity(intent);
            }
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


}
