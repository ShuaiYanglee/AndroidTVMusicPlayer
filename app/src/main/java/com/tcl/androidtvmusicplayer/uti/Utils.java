package com.tcl.androidtvmusicplayer.uti;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.tcl.androidtvmusicplayer.entity.Album;
import com.tcl.androidtvmusicplayer.entity.Artist;
import com.tcl.androidtvmusicplayer.entity.Song;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yangshuai on 2018/9/23.
 *
 * @Description:
 */

public class Utils {

    //获得屏幕大小
    public static Point getDisplaySize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }


    //Dp转为Pixel
    public static int convertDpToPixel(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }


    public static void toast(Context context, String toastMessage) {
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
    }

    public static String parseSongLyric(String songLrc) {
        String[] array = songLrc.split("\n");
        Matcher lineMatcher;
        StringBuffer buffer = new StringBuffer();
        Pattern pattern = Pattern.compile("((\\[\\d\\d:\\d\\d\\.\\d\\d\\d])+)(.+)");
        Pattern timePattern = Pattern.compile("\\[(\\d\\d):(\\d\\d)\\.(\\d\\d\\d)]");
        for (String str : array) {
            lineMatcher = pattern.matcher(str);
            if (lineMatcher.matches()) {
                String times = lineMatcher.group(1);
                Matcher timeMatcher = timePattern.matcher(times);
                if (timeMatcher.matches()) {
                    String min = timeMatcher.group(1).substring(0, 2);
                    String sec = timeMatcher.group(2).substring(0, 2);
                    String mil = timeMatcher.group(3).substring(0, 2);
                    String replacement = "[" + min + ":" + sec + "." + mil + "]";
                    str = str.replaceAll("\\[(.*?)]", replacement);
                }
            }
            buffer.append(str + "\n");
        }

        return buffer.toString();
    }



    public static List<Song> getLocalMusics(Context context){
        List<Song> songList = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor != null){
            int count = cursor.getCount();
            while (cursor.moveToNext()){
                Song song = new Song();
                song.setName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
                String artistName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                Artist artist = new Artist();
                ArrayList<Artist> artists = new ArrayList<>();
                artist.setName(artistName);
                artists.add(artist);
                song.setArtists(artists);
                Album album = new Album();
                album.setPicUrl(" ");
                song.setAlbum(album);
                song.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                song.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                songList.add(song);
            }
            cursor.close();
        }
        return songList;
    }
}
