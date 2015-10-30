package com.acme.audioplaybackinterface;

import android.media.AsyncPlayer;
import android.media.AudioDeviceCallback;
import android.media.AudioDeviceInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * Created by Matthew on 10/22/2015.
 */
public class RadioPlayer implements Runnable {
    public static int DEFAULT_SAMPLE_RATE = 44100;
    public static int DEFAULT_BUFFER_SIZE = 1024;
    private boolean running;
    private AudioTrack audioTrack;
    private AudioRecord audioRecord;
    short[] buffer;
    int minBufferSize;
    int bufferSize;

    RadioPlayer() {
        this.running = true;



        buffer = new short[DEFAULT_BUFFER_SIZE];
    }

    public void run() {


         audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, DEFAULT_SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, DEFAULT_BUFFER_SIZE * 1);

       audioTrack = new AudioTrack(AudioManager.STREAM_ALARM,
                DEFAULT_SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, DEFAULT_BUFFER_SIZE * 1,
                AudioTrack.MODE_STREAM);

        audioTrack.setPlaybackRate(DEFAULT_SAMPLE_RATE);


        audioRecord.startRecording();
        //Log.i(LOG_TAG,"Audio Recording started");
        audioTrack.play();
        //Log.i(LOG_TAG,"Audio Playing started");
        while (running) {
            audioRecord.read(buffer, 0, DEFAULT_BUFFER_SIZE);
            audioTrack.write(buffer, 0, buffer.length);
        }
        audioRecord.stop();
        audioRecord.release();
        audioRecord = null;
        audioTrack.stop();
        audioTrack.release();
        audioTrack = null;

    }

    public void stopPlayback(){
        this.running = false;
    }

    /**ToDo:
     * Fix Audio Routing - force to speakers/hdmi (remove C-MEDIA sink from usbaudio.conf?
     * Force Input Routing - on device (like Note 4) with internal MIC, force input to USB mic
     * Detect USB Device - auto launch application when USB device with given PID/VID is attached
     * Set Up RTP streaming, create service which continues to run in background even after App closed
     * Setup to record to file for set amount of time
     * Add pause/rewind/delayed time playback controls
     * Add visual audio playback indicator (where in buffered audio are we playing)
     * Check Audio quality / make sure settings for buffer size / sample rate are correct
     * Push buffer size/ sample rate/ etc to GUI for customizability (maybe fetch from device then populate options)
     * Setup GIT repo (integrated with Android Studio?)
     * EXTENDED TIME!!!!
     */

}
