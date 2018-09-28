package com.tcl.androidtvmusicplayer.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v17.leanback.widget.GuidedActionAdapter;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.tcl.androidtvmusicplayer.R;
import com.tcl.androidtvmusicplayer.callback.SongDetailCallBack;
import com.tcl.androidtvmusicplayer.constant.Constants;
import com.tcl.androidtvmusicplayer.entity.Song;
import com.tcl.androidtvmusicplayer.uti.HttpUtils;
import com.tcl.androidtvmusicplayer.uti.Utils;

import java.io.IOException;


public class PlayActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "PlayActivity";

    ImageButton btnPre;
    ImageButton btnNext;
    ImageButton btnSkipLeft;
    ImageButton btnSkipRight;
    ImageButton btnPause;
    ImageButton btnListRepeatMode;
    TextView tvSongName;
    TextView tvSongArtists;
    ImageView ivSongPic;


    MediaPlayer mediaPlayer = new MediaPlayer();
    String songUrl;
    Song song;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        song = (Song) getIntent().getSerializableExtra(Constants.SONG);
        songUrl = Constants.SONG_URL + song.getId() + ".mp3";
        song.setUrl(songUrl);
        //HttpUtils.doGetRequest(Constants.SONG_URL+song.getId(),new SongDetailCallBack(this));
        initView();
        initData(song);

    }

    public void initData(Song song) {
        try {
            mediaPlayer.setDataSource(song.getUrl());
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Utils.toast(PlayActivity.this, "准备完毕");
                    btnPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_selector));
                    mediaPlayer.start();
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
        tvSongName = findViewById(R.id.tv_song_name);
        tvSongName.setText(song.getName());
        tvSongArtists = findViewById(R.id.tv_song_artists);
        tvSongArtists.setText(song.getArtistsName());

        ivSongPic = findViewById(R.id.iv_song_pic);
        Glide.with(this).load(song.getAlbum().getPicUrl()).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(),resource);
                roundedBitmapDrawable.setCornerRadius(resource.getWidth()/2);
                roundedBitmapDrawable.setAntiAlias(true);
                ivSongPic.setImageDrawable(roundedBitmapDrawable);
            }
        });

        /*btnPre.setAlpha(50);
        btnNext.setAlpha(50);
        btnPause.setAlpha(50);
        btnSkipLeft.setAlpha(50);
        btnSkipRight.setAlpha(50);*/
        btnPre.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPause.setOnClickListener(this);
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
                } else {
                    mediaPlayer.pause();
                    btnPause.setImageDrawable(getDrawable(R.drawable.ic_play_selector));
                }
                break;
            default:
                Utils.toast(this, "default message");
                break;
        }
    }


}
