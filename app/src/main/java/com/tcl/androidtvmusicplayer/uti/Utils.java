package com.tcl.androidtvmusicplayer.uti;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

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


    public static void toast(Context context,String toastMessage){
        Toast.makeText(context,toastMessage,Toast.LENGTH_SHORT).show();
    }
}
