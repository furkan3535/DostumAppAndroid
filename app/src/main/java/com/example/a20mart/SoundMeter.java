package com.example.a20mart;

import android.media.MediaRecorder;

import java.io.IOException;

public class SoundMeter {
    // This file is used to record voice
    //To be able to record, your app must tell the user that it will access the device's audio input.
    // You must include this permission tag in the app's manifest file:
    static final private double EMA_FILTER = 0.6;

    private MediaRecorder mRecorder = null;
    private double mEMA = 0.0;

    public void start() {

        if (mRecorder == null) {

            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");

            try {
                mRecorder.prepare();
                mRecorder.start();

            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mEMA = 0.0;
        }
    }

    public void stop() {
        if (mRecorder != null) {
            try {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public double getAmplitude() {
        if (mRecorder != null)
            return  (mRecorder.getMaxAmplitude()/2700.0);
        else
            return 0;

    }


}


