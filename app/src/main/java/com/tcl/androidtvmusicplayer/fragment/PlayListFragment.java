package com.tcl.androidtvmusicplayer.fragment;

import android.os.Bundle;
import android.support.v17.leanback.app.VerticalGridFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.FocusHighlight;
import android.support.v17.leanback.widget.VerticalGridPresenter;
import android.util.Log;

import com.tcl.androidtvmusicplayer.constant.Constants;
import com.tcl.androidtvmusicplayer.presenter.CardPresenter;

/**
 * Created by yangshuai on 2018/9/25.
 *
 * @Description:
 */

public class PlayListFragment extends VerticalGridFragment {

    private static final int ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_MEDIUM;
    private static final int COLUMNS = 4;

    private ArrayObjectAdapter adapter;

    private static final String TAG = "PlayListFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long playListId = getActivity().getIntent().getLongExtra(Constants.PLAYLIST, 0);
        Log.d(TAG, "onCreate: " + playListId);

        setUpRowAdapter();



    }


    private void setUpRowAdapter(){
        VerticalGridPresenter gridPresenter = new VerticalGridPresenter(ZOOM_FACTOR);
        gridPresenter.setNumberOfColumns(COLUMNS);
        setGridPresenter(gridPresenter);
        adapter = new ArrayObjectAdapter(new CardPresenter());
        setAdapter(adapter);
    }
}
