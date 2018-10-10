package com.tcl.androidtvmusicplayer.activity;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tcl.androidtvmusicplayer.R;
import com.tcl.androidtvmusicplayer.uti.Utils;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.toast(this,"欢迎使用MusicPlayer");
        setContentView(R.layout.activity_splash);
        checkPermission();
    }


    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, 1);
        } else {
            scanMusicFile();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    scanMusicFile();
                }else {
                    Utils.toast(this,"你拒绝了权限，无法访问本地音乐");
                    finish();
                }
                break;
            default:
                break;
        }
    }



    private void scanMusicFile(){
        final ProgressBar progressBar = new ProgressBar(this);
        progressBar.setVisibility(View.VISIBLE);
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
                            TimerTask timerTask = new TimerTask() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            };
                            Timer timer = new Timer();
                            timer.schedule(timerTask,3000);

                        }
                    });
        }
    }

}
