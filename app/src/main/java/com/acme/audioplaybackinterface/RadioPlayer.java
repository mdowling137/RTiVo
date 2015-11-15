package com.acme.audioplaybackinterface;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Environment;
import android.util.Log;

/**
 * Created by Matthew on 11/14/2015.
 */
public class RadioPlayer implements Runnable {
    File radioFile;
    int minBufferSize;
    Boolean playing;
    AudioTrack track;
    DataInputStream dataInputStream;
    short[] audioData;
    Boolean paused;

    RadioPlayer(File radioFile, int minBufferSize ){
        this.paused = false;
        this.radioFile = radioFile;
        this.minBufferSize = minBufferSize;
        this.playing = true;
        this.track = new AudioTrack(AudioManager.STREAM_MUSIC, RadioRecord.DEFAULT_SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
               minBufferSize, AudioTrack.MODE_STREAM);
        try {
            InputStream inputStream = new FileInputStream(radioFile);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            dataInputStream = new DataInputStream(bufferedInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        audioData = new short[RadioRecord.DEFAULT_BUFFER_SIZE];

    }

    public void run(){
        try
        {

            track.play();
            while(playing){
                int i = 0;
                while(i < RadioRecord.DEFAULT_BUFFER_SIZE){
                    while(this.paused){
                    }
                    if(dataInputStream.available() <= 0){
                        audioData[i]=0;
                        Log.d("AUDIO_DATA:", "0 inserted");
                    } else {
                        audioData[i] = dataInputStream.readShort();
                    }
                    i++;
                }
                track.write(audioData, 0,RadioRecord.DEFAULT_BUFFER_SIZE);
            }
            track.stop();
            track.release();

            dataInputStream.close();

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopPlayback(){
        this.playing = false;
    }
    public void pausePlayback(){
        this.paused = true;
    }
    public void resumePlayback(){
        this.paused = false;
    }
}
