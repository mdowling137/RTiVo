package com.acme.audioplaybackinterface;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Debug;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by Matthew on 10/22/2015.
 */
public class RadioRecord implements Runnable {
    public static int DEFAULT_SAMPLE_RATE = 44100; //should be determined from incoming audio
    public static int DEFAULT_BUFFER_SIZE = 1024;  //should be determined by incoming audio min buffer size method
    private boolean recording;
    //private AudioTrack audioTrack;
    private AudioRecord audioRecord;
    private DataOutputStream dataOutputStream;
    private File recordedRadio;
    short[] buffer;
    private int minBufferSize;
    int bufferSize;

    RadioRecord(File radioFile) {
        this.recordedRadio = radioFile;
        this.recording = true;

        try {

            OutputStream outputStream = new FileOutputStream(recordedRadio);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            dataOutputStream = new DataOutputStream(bufferedOutputStream);

            minBufferSize = AudioRecord.getMinBufferSize(DEFAULT_SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

            buffer = new short[minBufferSize];

            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,  DEFAULT_SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                    minBufferSize);



        }catch(Exception e){
            e.printStackTrace();;
        }
    }

    public int getBufferSize(){
      return minBufferSize;
    };
    public void run() {

        audioRecord.startRecording();

        while (recording) {
            int numberOfShort = audioRecord.read(buffer, 0, minBufferSize);
            for (int i = 0; i < numberOfShort; i++)
            {
                try {
                    dataOutputStream.writeShort(buffer[i]);
                }catch(Exception e){
                        e.printStackTrace();
                        recording  = false;
                    }
            }

        }
        audioRecord.stop();
        audioRecord.release();
        audioRecord = null;
        try {
            dataOutputStream.close();
            recordedRadio.delete();
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    public void stopRecord(){
        this.recording = false;
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
