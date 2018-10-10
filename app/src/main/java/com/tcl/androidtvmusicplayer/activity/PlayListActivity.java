package com.tcl.androidtvmusicplayer.activity;

import android.app.Activity;
import android.os.Bundle;
import com.tcl.androidtvmusicplayer.R;
import com.tcl.androidtvmusicplayer.constant.Constants;
import com.tcl.androidtvmusicplayer.entity.Song;
import com.tcl.androidtvmusicplayer.uti.ActivityCollector;

public class PlayListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);
        ActivityCollector.addActivity(this);
    }
}
