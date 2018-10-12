package com.tcl.androidtvmusicplayer.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import android.support.annotation.Nullable;
import android.support.v17.leanback.app.BackgroundManager;

import android.view.KeyEvent;
import android.view.View;

import android.widget.FrameLayout;
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
import com.tcl.androidtvmusicplayer.fragment.SimpleSongListFragment;
import com.tcl.androidtvmusicplayer.service.PlayService;

import com.tcl.androidtvmusicplayer.uti.Utils;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import me.wcy.lrcview.LrcView;


public class PlayActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "PlayActivity";

    ImageButton btnPre;
    ImageButton btnNext;
    ImageButton btnPause;
    ImageButton btnListRepeatMode;
    ImageButton btnSongList;
    TextView tvSongName;
    TextView tvSongArtists;
    TextView tvSongPlayTime;
    TextView tvSongTotalTime;
    ImageView ivSongPic;
    SeekBar songSeekBar;
    LrcView lrcView;

    FrameLayout flFragment;

    SimpleSongListFragment fragment;
    FragmentManager fragmentManager;
    FragmentTransaction transaction;

    BackgroundManager manager;
    PlayService.MyBinder binder;
    Song song;
    List<Song> songList;
    MediaPlayer player;
    DateFormat durationFormat = new SimpleDateFormat("mm:ss");

    //handler处理界面更新操作
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MSG_MUSIC_INIT:
                    Utils.toast(PlayActivity.this, "准备完毕");
                    updateView(binder.getCurrentSong());
                    break;
                case Constants.MSG_MUSIC_START:
                    btnPause.setImageDrawable(getDrawable(R.drawable.ic_pause_selector));
                    handler.post(runnable);
                    break;
                case Constants.MSG_MUSIC_PAUSE:
                    btnPause.setImageDrawable(getDrawable(R.drawable.ic_play_selector));
                    handler.removeCallbacks(runnable);
                    break;
                case Constants.MSG_UPDATE_PLAY_MODE:
                    updatePlayMode(binder.getCurrentMode());
                    break;
                default:
                    break;


            }
        }
    };


    //服务初始化
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (PlayService.MyBinder) service;
            binder.setHandler(handler);
            binder.setSongList(songList);
            binder.initData(song);
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
        btnSongList = findViewById(R.id.image_button_song_list);
        flFragment = findViewById(R.id.fl_fragment);

        songSeekBar = findViewById(R.id.song_seek_bar);
        tvSongName = findViewById(R.id.tv_song_name);
        tvSongArtists = findViewById(R.id.tv_song_artists);
        tvSongPlayTime = findViewById(R.id.tv_song_play_time);
        tvSongTotalTime = findViewById(R.id.tv_song_total_time);

        lrcView = findViewById(R.id.lrc_view);
        ivSongPic = findViewById(R.id.iv_song_pic);

        manager = BackgroundManager.getInstance(this);
        manager.attach(getWindow());

        btnPre.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPause.setOnClickListener(this);
        btnPause.setFocusedByDefault(true);
        btnListRepeatMode.setOnClickListener(this);
        btnSongList.setOnClickListener(this);
    }

    //音乐数据准备完后更新界面
    public void updateView(Song song) {
        tvSongName.setText(song.getName());
        tvSongArtists.setText(song.getArtistsName());
        tvSongTotalTime.setText(durationFormat.format(player.getDuration()));
        GlideApp.with(this).load(song.getAlbum().getPicUrl()).error(getDrawable(R.drawable.ic_app_logo)).circleCrop().into(ivSongPic);

        btnPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_selector));
        songSeekBar.setMax(player.getDuration());
        songSeekBar.setProgress(0);

        //seekBar快进快退
        songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    player.seekTo(progress);
                }


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //加载歌词
        if (song.getSongLyric() != null)
            lrcView.loadLrc(song.getSongLyric());
    }

    //歌曲控制
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_button_pre:
                binder.playPreviousSong();
                break;
            case R.id.image_button_next:
                binder.playNext();
                break;
            case R.id.image_button_pause:
                binder.play();
                break;
            case R.id.image_button_list_repeat_mode:
                binder.setPlayMode();
                break;
            case R.id.image_button_song_list:
                flFragment.setVisibility(View.VISIBLE);
                fragment = new SimpleSongListFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.LOCAL_PLAY_LIST, (Serializable) songList);
                bundle.putSerializable(Constants.CURRENT_SONG_INDEX, binder.getCurrentSongIndex());
                fragment.setArguments(bundle);
                fragmentManager = getFragmentManager();
                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fl_fragment, fragment);
                transaction.commit();
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
                tvSongPlayTime.setText(durationFormat.format(time));
            }
            handler.postDelayed(this, 300);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        GlideApp.with(this).load(song.getAlbum().getPicUrl()).apply(RequestOptions.bitmapTransform(new BlurTransformation(15, 3))).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@Nullable Drawable resource, @Nullable Transition<? super Drawable> transition) {
                manager.setDrawable(resource);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);

    }


    //更新播放模式按钮的背景图片
    private void updatePlayMode(int playMode) {
        switch (playMode) {
            case Constants.MODE_REPEAT_LIST:
                btnListRepeatMode.setImageDrawable(getDrawable(R.drawable.ic_repeat_list_selector));
                break;
            case Constants.MODE_REPEAT_SINGLE:
                btnListRepeatMode.setImageDrawable(getDrawable(R.drawable.ic_repeat_single_selector));
                break;
            case Constants.MODE_REPEAT_RANDOM:
                btnListRepeatMode.setImageDrawable(getDrawable(R.drawable.ic_repeat_random_selector));
                break;
            case Constants.MODE_REPEAT_SEQUENCE:
                btnListRepeatMode.setImageDrawable(getDrawable(R.drawable.ic_repeat_sequence_selector));
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_E:
                transaction.remove(fragment);
                flFragment.setVisibility(View.GONE);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
