package com.tcl.androidtvmusicplayer.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v17.leanback.widget.GuidedActionAdapter;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.tcl.androidtvmusicplayer.R;
import com.tcl.androidtvmusicplayer.constant.Constants;
import com.tcl.androidtvmusicplayer.entity.Song;
import com.tcl.androidtvmusicplayer.uti.Utils;

import java.io.IOException;
import java.net.URL;


public class PlayActivity extends Activity implements View.OnClickListener{

    private static final String TAG = "PlayActivity";

    ImageButton btnPre;
    ImageButton btnNext;
    ImageButton btnSkipLeft;
    ImageButton btnSkipRight;
    ImageButton btnPause;

    MediaPlayer mediaPlayer = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Song song = (Song) getIntent().getSerializableExtra(Constants.SONG);
        song.getId();

        Log.d(TAG, "onCreate: "+song.getId());
        initView();
        initData();
    }

    private void initData(){
        try {
            mediaPlayer.setDataSource("http://m10.music.126.net/20180928001600/db3e54b474b68ccd0ca2f24fd9f6908a/ymusic/07fa/a2a1/35ea/732937117d6d0a8c13a81bb40184662e.mp3");
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Utils.toast(PlayActivity.this,"准备完毕");
                    mediaPlayer.start();
                }
            });
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initView(){
        btnPre = findViewById(R.id.image_button_pre);
        btnNext = findViewById(R.id.image_button_next);
        btnPause = findViewById(R.id.image_button_pause);
        btnSkipLeft = findViewById(R.id.image_button_skip_left);
        btnSkipRight = findViewById(R.id.image_button_skip_right);
        btnPre.setAlpha(50);
        btnNext.setAlpha(50);
        btnPause.setAlpha(50);
        btnSkipLeft.setAlpha(50);
        btnSkipRight.setAlpha(50);
        btnPre.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPause.setOnClickListener(this);
        btnSkipLeft.setOnClickListener(this);
        btnSkipRight.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_button_pre:
                Utils.toast(this,"上一首");
                Log.d(TAG, "onClick: "+"上一首");
                break;
            case R.id.image_button_next:
                Utils.toast(this,"下一首");
                Log.d(TAG, "onClick: "+"下一首");
                break;
            case R.id.image_button_pause:
                if (!mediaPlayer.isPlaying()){
                    mediaPlayer.start();
                }else {
                    mediaPlayer.pause();
                }
                break;
            default:
                Utils.toast(this,"default message");
                break;
        }
    }
}
