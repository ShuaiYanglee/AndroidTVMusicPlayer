package com.tcl.androidtvmusicplayer.activity;

import android.app.Activity;
import android.os.Bundle;
import com.tcl.androidtvmusicplayer.R;
import com.tcl.androidtvmusicplayer.constant.Constants;
import com.tcl.androidtvmusicplayer.entity.Song;

public class PlayListActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);
    }
}
