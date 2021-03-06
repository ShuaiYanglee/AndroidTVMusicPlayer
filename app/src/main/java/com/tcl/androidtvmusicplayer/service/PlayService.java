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
        private List<Song> songList;//歌曲列表
        private Song currentSong;//现在播放的歌曲
        private Song previousSong;//前一首播放的歌曲
        private int currentSongIndex;
        private int currentMode = 0;

        //初始化播放的音乐数据
        public void initData(Song song) {
            setCurrentSong(song);
            currentSongIndex = songList.indexOf(currentSong);
            if (currentSong.getId() != 0) {
                String songUrl = Constants.SONG_URL + currentSong.getId() + ".mp3";
                currentSong.setUrl(songUrl);
                HttpUtils.doGetRequest(Constants.SONG_LYRIC + currentSong.getId(), new SongLyricCallBack(this, currentSong));
            } else {
                playMusic(currentSong.getPath());
            }
        }

        //播放音乐，并通过handler更新UI
        public void playMusic(String url) {
            player.reset();
            try {
                player.setDataSource(url);
                player.prepareAsync();
                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        handler.sendEmptyMessage(Constants.MSG_MUSIC_INIT);
                        play();
                    }
                });

                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        playNext();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //播放下一首
        public void playNext() {
            previousSong = currentSong;
            int nextIndex = (currentSongIndex + 1) % songList.size();
            switch (getCurrentMode()) {
                case Constants.MODE_REPEAT_LIST:
                    initData(songList.get(nextIndex));
                    break;
                case Constants.MODE_REPEAT_SINGLE:
                    initData(songList.get(currentSongIndex));
                    break;
                case Constants.MODE_REPEAT_RANDOM:
                    Random random = new Random();
                    initData(songList.get(random.nextInt(songList.size())));
                    break;
                case Constants.MODE_REPEAT_SEQUENCE:
                    if (nextIndex == 0) {
                        player.release();
                    } else {
                        initData(songList.get(nextIndex));
                    }
                    break;
            }
        }

        //播放上一首
        public void playPreviousSong() {
            initData(previousSong);
        }


        //设置播放模式
        public void setPlayMode() {
            currentMode = (currentMode + 1) % 4;
            handler.sendEmptyMessage(Constants.MSG_UPDATE_PLAY_MODE);
        }

        //获得播放模式
        public int getCurrentMode() {
            return currentMode;
        }


        //播放或暂停逻辑控制
        public void play() {
            if (!player.isPlaying()) {
                player.start();
                handler.sendEmptyMessage(Constants.MSG_MUSIC_START);
            } else {
                player.pause();
                handler.sendEmptyMessage(Constants.MSG_MUSIC_PAUSE);
            }
        }

        public void setCurrentSong(Song song) {
            currentSong = song;
        }

        public Song getCurrentSong() {
            return currentSong;
        }

        public void setSongList(List<Song> songList) {
            this.songList = songList;
        }

        public void setHandler(Handler handler) {
            this.handler = handler;
        }

        public MediaPlayer getMediaPlayer() {
            return player;
        }

        public int getCurrentSongIndex(){
            return currentSongIndex;
        }


    }
}
