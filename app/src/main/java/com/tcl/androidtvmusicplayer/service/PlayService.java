package com.tcl.androidtvmusicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.tcl.androidtvmusicplayer.callback.SongLyricCallBack;
import com.tcl.androidtvmusicplayer.constant.Constants;
import com.tcl.androidtvmusicplayer.entity.Song;
import com.tcl.androidtvmusicplayer.uti.HttpUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by yangshuai on 2018/10/8.
 *
 * @Description:
 */

public class PlayService extends Service {

    public static final MediaPlayer player = new MediaPlayer();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }


    public class MyBinder extends Binder {
        private Handler handler;
        private List<Song> songList;
        private Song currentSong;

        public void initData(Song song){
            setCurrentSong(song);
            String songUrl = Constants.SONG_URL + currentSong.getId() + ".mp3";
            currentSong.setUrl(songUrl);
            HttpUtils.doGetRequest(Constants.SONG_LYRIC + currentSong.getId(), new SongLyricCallBack(this, currentSong));
        }

        public void playMusic() {
            player.reset();
            try {
                player.setDataSource(currentSong.getUrl());
                player.prepareAsync();
                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        handler.sendEmptyMessage(Constants.MSG_MUSIC_INIT);
                    }
                });

                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        initData(songList.iterator().next());
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void playNext(){
            Collections.shuffle(songList);
            Random random = new Random();
            initData(songList.get(random.nextInt(songList.size())));
        }


        public void play(){
            if (!player.isPlaying()) {
                player.start();
                handler.sendEmptyMessage(Constants.MSG_MUSIC_START);
            } else {
                player.pause();
                handler.sendEmptyMessage(Constants.MSG_MUSIC_PAUSE);
            }
        }

        public void setCurrentSong(Song song){
            currentSong = song;
        }

        public Song getCurrentSong(){
            return currentSong;
        }

        public void setSongList(List<Song> songList){
            this.songList =songList;
        }

        public void setHandler(Handler handler){
            this.handler = handler;
        }

        public MediaPlayer getMediaPlayer(){
            return player;
        }


    }
}
