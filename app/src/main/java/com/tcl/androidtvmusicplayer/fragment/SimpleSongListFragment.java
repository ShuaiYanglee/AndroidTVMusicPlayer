package com.tcl.androidtvmusicplayer.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.VerticalGridFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.FocusHighlight;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.VerticalGridPresenter;

import com.tcl.androidtvmusicplayer.activity.PlayActivity;
import com.tcl.androidtvmusicplayer.constant.Constants;
import com.tcl.androidtvmusicplayer.entity.Song;
import com.tcl.androidtvmusicplayer.presenter.CardPresenter;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yangshuai on 2018/10/12.
 *
 * @Description:
 */

public class SimpleSongListFragment extends VerticalGridFragment {

    private static final int ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_MEDIUM;
    private static final int COLUMNS = 4;
    private ArrayObjectAdapter adapter;
    private List<Song> songList;
    private int currentSongIndex;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpRowAdapter();
        Bundle bundle = getArguments();
        songList = (List<Song>) bundle.getSerializable(Constants.LOCAL_PLAY_LIST);
        currentSongIndex = (int) bundle.getSerializable(Constants.CURRENT_SONG_INDEX);
        loadRows(songList);
        setOnItemViewClickedListener(new ItemViewClickedListener());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.getView().requestFocus();
    }

    private void setUpRowAdapter() {
        VerticalGridPresenter gridPresenter = new VerticalGridPresenter(ZOOM_FACTOR);
        gridPresenter.setNumberOfColumns(COLUMNS);
        setGridPresenter(gridPresenter);
        adapter = new ArrayObjectAdapter(new CardPresenter());
        setAdapter(adapter);
    }

    public void loadRows(List<Song> songList) {
        this.setSelectedPosition(currentSongIndex);
        this.songList = songList;
        adapter.addAll(0, songList);
        adapter.notifyItemRangeChanged(0, 1);
        startEntranceTransition();
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
                    getActivity().finish();
                    startActivity(intent);
                }
            }
        }
    }

}
