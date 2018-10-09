package com.tcl.androidtvmusicplayer.activity;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.tcl.androidtvmusicplayer.R;
import com.tcl.androidtvmusicplayer.uti.Utils;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        checkPermission();
    }


    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, 1);
        } else {
            splash();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    splash();
                }else {
                    Utils.toast(this,"你拒绝了权限，无法访问本地音乐");
                    finish();
                }
                break;
            default:
                break;
        }
    }


    private void splash(){
        File musicDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath());
        final File[] toBeScannedMusicFiles = musicDir.listFiles();
        if(toBeScannedMusicFiles != null) {
            String[] toBeScannedMusicPath = new String[toBeScannedMusicFiles.length];
            for(int i = 0; i < toBeScannedMusicFiles.length; i++){
                toBeScannedMusicPath[i] = toBeScannedMusicFiles[i].getAbsolutePath();
            }
            MediaScannerConnection.scanFile(SplashActivity.this,
                    toBeScannedMusicPath, null, new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            Timer timer = new Timer();
                            TimerTask timerTask = new TimerTask() {
                                @Override
                                public void run() {
                                    leapToPlayActivity();
                                    SplashActivity.this.finish();
                                }
                            };
                            timer.schedule(timerTask, 3000);
                        }
                    });
        }
    }


    private void leapToPlayActivity(){
        Intent intent = new Intent(this, MainActivity.class);

        this.startActivity(intent);
    }
}
