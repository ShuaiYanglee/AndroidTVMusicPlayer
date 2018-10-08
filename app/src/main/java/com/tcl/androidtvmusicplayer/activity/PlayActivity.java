package com.tcl.androidtvmusicplayer.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
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

import com.tcl.androidtvmusicplayer.constant.Constants;
import com.tcl.androidtvmusicplayer.entity.Song;
import com.tcl.androidtvmusicplayer.service.PlayService;

import com.tcl.androidtvmusicplayer.uti.Utils;

import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import me.wcy.lrcview.LrcView;


public class PlayActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "PlayActivity";

    ImageButton btnPre;
    ImageButton btnNext;
    ImageButton btnPause;
    ImageButton btnListRepeatMode;
    TextView tvSongName;
    TextView tvSongArtists;
    ImageView ivSongPic;
    SeekBar songSeekBar;
    LrcView lrcView;

    BackgroundManager manager;
    PlayService.MyBinder binder;
    Song song;
    List<Song> songList;
    MediaPlayer player;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MSG_MUSIC_INIT:
                    Utils.toast(PlayActivity.this, "准备完毕");
                    updateView(binder.getCurrentSong());
                    btnPause.performClick();
                    break;
                case Constants.MSG_MUSIC_START:
                    btnPause.setImageDrawable(getDrawable(R.drawable.ic_pause_selector));
                    handler.post(runnable);
                    break;
                case Constants.MSG_MUSIC_PAUSE:
                    btnPause.setImageDrawable(getDrawable(R.drawable.ic_play_selector));
                    handler.removeCallbacks(runnable);
                    break;
                default:
                    break;


            }
        }
    };


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (PlayService.MyBinder) service;
            binder.setHandler(handler);
            binder.initData(song);
            binder.setSongList(songList);
            player = binder.getMediaPlayer();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Intent intent = new Intent(this, PlayService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
        Bundle bundle = getIntent().getBundleExtra(Constants.BUNDLE);
        song = (Song) bundle.getSerializable(Constants.SONG);
        songList = (List<Song>) bundle.getSerializable(Constants.SONG_LIST);
        initView();

    }

    //界面初始化时加载的View
    private void initView() {
        btnPre = findViewById(R.id.image_button_pre);
        btnNext = findViewById(R.id.image_button_next);
        btnPause = findViewById(R.id.image_button_pause);
        btnListRepeatMode = findViewById(R.id.image_button_list_repeat_mode);
        songSeekBar = findViewById(R.id.song_seek_bar);
        tvSongName = findViewById(R.id.tv_song_name);
        tvSongArtists = findViewById(R.id.tv_song_artists);
        lrcView = findViewById(R.id.lrc_view);
        ivSongPic = findViewById(R.id.iv_song_pic);

        manager = BackgroundManager.getInstance(this);
        manager.attach(getWindow());

        btnPre.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPause.setOnClickListener(this);
        btnPause.setFocusedByDefault(true);
        btnListRepeatMode.setOnClickListener(this);
    }

    //音乐数据准备完后更新界面
    public void updateView(Song song) {
        tvSongName.setText(song.getName());
        tvSongArtists.setText(song.getArtistsName());
        GlideApp.with(this).load(song.getAlbum().getPicUrl()).circleCrop().into(ivSongPic);
        GlideApp.with(this).load(song.getAlbum().getPicUrl()).apply(RequestOptions.bitmapTransform(new BlurTransformation(15, 3))).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                manager.setDrawable(resource);
            }
        });

        btnPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_selector));
        songSeekBar.setMax(player.getDuration());
        songSeekBar.setProgress(0);
        lrcView.loadLrc(song.getSongLyric());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_button_pre:
                binder.playNext();
                break;
            case R.id.image_button_next:
                binder.playNext();
                break;
            case R.id.image_button_pause:
                binder.play();
                break;
            default:
                Utils.toast(this, "default message");
                break;
        }
    }


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (player.isPlaying()) {
                long time = player.getCurrentPosition();
                lrcView.updateTime(time);
                songSeekBar.setProgress((int) time);
            }
            handler.postDelayed(this, 300);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }


}
