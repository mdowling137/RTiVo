package com.acme.audioplaybackinterface;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    RadioPlayer test;
    private boolean playing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playing = false;


    }

    public void startAudio(View view){
        if(!playing) {
            test = new RadioPlayer();
            Thread radioThread = new Thread(test);
            radioThread.start();
            playing = true;
        }


    }
    public void stopAudio(View view){
        if(playing) {
            test.stopPlayback();
            playing = false;
        }

    }

}
