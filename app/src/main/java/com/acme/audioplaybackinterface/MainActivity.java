package com.acme.audioplaybackinterface;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private RadioRecord test;
    private boolean radioStarted;
    private File radioFile;
    private RadioPlayer radioPlayer;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.radioStarted = false;

        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            String filePath = this.getFilesDir().getPath().toString() + "/recordedRadio.pcm";
            radioFile = new File(filePath);
            try {
                Log.d("FILE: ", filePath);
                radioFile.createNewFile();

            }  catch(Exception e){
                e.printStackTrace();
            }


        }


    }

    public void startAudio(View view){
        if(!radioStarted){
            radioStarted = true;
            test = new RadioRecord(radioFile);
            Thread recordThread = new Thread(test);
            recordThread.setPriority(Thread.MAX_PRIORITY);
            recordThread.start();
            radioPlayer = new RadioPlayer(radioFile, test.getBufferSize());
            Thread playThread = new Thread(radioPlayer);
            playThread.setPriority(Thread.MAX_PRIORITY);
            playThread.start();
        }
        else {
            radioPlayer.resumePlayback();
        }

    }
    public void stopAudio(View view){
        if(radioStarted) {
            radioPlayer.pausePlayback();

        }

    }

}
