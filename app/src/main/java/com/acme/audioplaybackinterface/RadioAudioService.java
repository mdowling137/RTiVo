package com.acme.audioplaybackinterface;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import java.io.File;

public class RadioAudioService extends Service {
    private Boolean radioStarted;
    private RadioRecord radioRecord;
    private File radioFile;
    private RadioPlayer radioPlayer;


    public void onCreate() {
        super.onCreate();
        this.radioStarted = false;
        String filePath = this.getFilesDir().getPath().toString() + "/recordedRadio.pcm";
            radioFile = new File(filePath);
            try {
                Log.d("FILE: ", filePath);
                radioFile.createNewFile();

            }  catch(Exception e){
                e.printStackTrace();
            }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        radioRecord = new RadioRecord(radioFile);
        Thread recordThread = new Thread(radioRecord);
        recordThread.setPriority(Thread.MAX_PRIORITY);
        recordThread.start();
        radioPlayer = new RadioPlayer(radioFile, radioRecord.getBufferSize());
        Thread playThread = new Thread(radioPlayer);
        playThread.setPriority(Thread.MAX_PRIORITY);
        playThread.start();


        return 1;
    }


    public IBinder onUnBind(Intent arg0) {
        // TO DO Auto-generated method
        return null;
    }

    public void onStop() {

    }
    public void onPause() {

    }
    @Override
    public void onDestroy() {

    }

    @Override
    public void onLowMemory() {

    }



    public void startAudio(){


    }
    public void stopAudio(){
        if(radioStarted) {
            radioPlayer.pausePlayback();

        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
