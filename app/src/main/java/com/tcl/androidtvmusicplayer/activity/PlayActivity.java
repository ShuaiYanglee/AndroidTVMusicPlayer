package com.tcl.androidtvmusicplayer.activity;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.BackgroundManager;


import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.SeekBar;
import android.widget.TextView;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tcl.androidtvmusicplayer.GlideApp;
import com.tcl.androidtvmusicplayer.R;
import com.tcl.androidtvmusicplayer.callback.SongLyricCallBack;
import com.tcl.androidtvmusicplayer.constant.Constants;
import com.tcl.androidtvmusicplayer.entity.Song;
import com.tcl.androidtvmusicplayer.uti.HttpUtils;
import com.tcl.androidtvmusicplayer.uti.Utils;

import java.io.IOException;

import jp.wasabeef.glide.transformations.BlurTransformation;
import me.wcy.lrcview.LrcView;


public class PlayActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "PlayActivity";
    private static final MediaPlayer mediaPlayer = new MediaPlayer();

    ImageButton btnPre;
    ImageButton btnNext;
    ImageButton btnSkipLeft;
    ImageButton btnSkipRight;
    ImageButton btnPause;
    ImageButton btnListRepeatMode;
    TextView tvSongName;
    TextView tvSongArtists;
    ImageView ivSongPic;
    SeekBar songSeekBar;
    LrcView lrcView;

    BackgroundManager manager;
    String songUrl;
    Song song;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        song = (Song) getIntent().getSerializableExtra(Constants.SONG);
        songUrl = Constants.SONG_URL + song.getId() + ".mp3";
        song.setUrl(songUrl);
        //HttpUtils.doGetRequest(Constants.SONG_URL+song.getId(),new SongLyricCallBack(this));
        HttpUtils.doGetRequest(Constants.SONG_LYRIC+song.getId(),new SongLyricCallBack(this,song));
        initView();

    }

    public void initData(final Song song) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(song.getUrl());
            song.getSongLyric();
            lrcView.loadLrc(song.getSongLyric());
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Utils.toast(PlayActivity.this, "准备完毕");
                    btnPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_selector));
                    songSeekBar.setMax(mediaPlayer.getDuration());
                    songSeekBar.setProgress(0);
                    btnPause.performClick();
                }
            });
            
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        btnPre = findViewById(R.id.image_button_pre);
        btnNext = findViewById(R.id.image_button_next);
        btnPause = findViewById(R.id.image_button_pause);
        btnSkipLeft = findViewById(R.id.image_button_skip_left);
        btnSkipRight = findViewById(R.id.image_button_skip_right);
        btnListRepeatMode = findViewById(R.id.image_button_list_repeat_mode);
        songSeekBar = findViewById(R.id.song_seek_bar);
        tvSongName = findViewById(R.id.tv_song_name);
        tvSongName.setText(song.getName());
        tvSongArtists = findViewById(R.id.tv_song_artists);
        tvSongArtists.setText(song.getArtistsName());
        lrcView = findViewById(R.id.lrc_view);
        manager = BackgroundManager.getInstance(this);
        manager.attach(getWindow());
        ivSongPic = findViewById(R.id.iv_song_pic);

        GlideApp.with(this).load(song.getAlbum().getPicUrl()).circleCrop().into(ivSongPic);
        GlideApp.with(this).load(song.getAlbum().getPicUrl()).apply(RequestOptions.bitmapTransform(new BlurTransformation(15,3))).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                manager.setDrawable(resource);
            }
        });
        btnPre.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPause.setOnClickListener(this);
        btnPause.setFocusedByDefault(true);
        btnSkipLeft.setOnClickListener(this);
        btnSkipRight.setOnClickListener(this);
        btnListRepeatMode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_button_pre:
                Utils.toast(this, "上一首");
                Log.d(TAG, "onClick: " + "上一首");
                break;
            case R.id.image_button_next:
                Utils.toast(this, "下一首");
                Log.d(TAG, "onClick: " + "下一首");
                break;
            case R.id.image_button_pause:
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    btnPause.setImageDrawable(getDrawable(R.drawable.ic_pause_selector));
                    handler.post(runnable);
                } else {
                    mediaPlayer.pause();
                    btnPause.setImageDrawable(getDrawable(R.drawable.ic_play_selector));
                    handler.removeCallbacks(runnable);
                }
                break;
            default:
                Utils.toast(this, "default message");
                break;
        }
    }


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer.isPlaying()) {
                long time = mediaPlayer.getCurrentPosition();
                lrcView.updateTime(time);
                songSeekBar.setProgress((int) time);
            }

            handler.postDelayed(this, 300);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
