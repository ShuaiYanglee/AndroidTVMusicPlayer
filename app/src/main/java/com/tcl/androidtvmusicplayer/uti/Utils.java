package com.tcl.androidtvmusicplayer.uti;

import android.content.Context;
import android.graphics.Point;
import android.os.Parcelable;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

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
}
