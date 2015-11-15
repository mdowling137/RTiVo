package com.acme.audioplaybackinterface;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Debug;
import android.os.Environment;
import android.os.SystemClock;

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
    public static int DEFAULT_BUFFER_SIZE = 2048;  //should be determined by incoming audio min buffer size method
    private boolean recording;
    private boolean playingLive;
    private AudioTrack loopBack;
    private AudioRecord audioRecord;
    private DataOutputStream dataOutputStream;
    private File recordedRadio;
    short[] buffer;
    private int minBufferSize;
    int bufferSize;
    private long time;
    private MediaRecorder mediaRecorder;
    private String fileName;
    RadioRecord(String fileName) {
        //this.recordedRadio = radioFile;
        this.fileName = fileName;
        this.recording = true;
        this.playingLive = true;
        this.time = 0;
        this.mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(fileName);
        try {
            mediaRecorder.prepare();
        } catch (Exception e){
            e.printStackTrace();
        }
        try {

            //OutputStream outputStream = new FileOutputStream(recordedRadio);
           // BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            //dataOutputStream = new DataOutputStream(bufferedOutputStream);

            minBufferSize = AudioRecord.getMinBufferSize(DEFAULT_SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

            buffer = new short[DEFAULT_BUFFER_SIZE];

           audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,  DEFAULT_SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                    minBufferSize);

            this.loopBack = new AudioTrack(AudioManager.STREAM_MUSIC, RadioRecord.DEFAULT_SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                    minBufferSize, AudioTrack.MODE_STREAM);

        }catch(Exception e){
            e.printStackTrace();;
        }
    }

    public int getBufferSize(){
      return minBufferSize;
    };
    public void run() {
        this.time = SystemClock.elapsedRealtime();
        mediaRecorder.start();
        audioRecord.startRecording();
        loopBack.play();

        while (recording) {
            int numberOfShort = audioRecord.read(buffer, 0, DEFAULT_BUFFER_SIZE);
            if(playingLive) {
                loopBack.write(buffer, 0, DEFAULT_BUFFER_SIZE);
            }


            //for (int i = 0; i < numberOfShort; i++)
            //{
                //try {
                //    dataOutputStream.writeShort(buffer[i]);
                //}catch(Exception e){
               //         e.printStackTrace();
              //          recording  = false;
              //      }
         //   }

        }
        audioRecord.stop();
        audioRecord.release();
        audioRecord = null;
        //try {
            //dataOutputStream.close();
           // recordedRadio.delete();
      //  }
        //catch(Exception e){
            //e.printStackTrace();
       // }

    }

    public void stopRecord(){
        this.recording = false;
    }

    public int stopLiveAudio(){

        loopBack.pause();
        this.playingLive = false;

        return (int) (SystemClock.elapsedRealtime() - time);
    }
    public void startLiveAudio(){
        this.playingLive = true;
        loopBack.play();
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
