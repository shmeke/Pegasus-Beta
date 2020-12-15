package com.sebastianstext.pegasusbeta.SensorDetectors;

import com.sebastianstext.pegasusbeta.Listeners.startListener;


public class StartDetector {

    private boolean startCommand = false;
    private startListener listener;
    private float oldYval;

    public void registerListener(startListener listener) {
        this.listener = listener;
    }

    public void startWorkout(long timeNs, float x, float y, float z){
        float[] currentAccel = new float[3];
        currentAccel[0] = x;
        currentAccel[1] = y;
        currentAccel[2] = z;


        if(y - oldYval > 5){
            startCommand = true;
            listener.startSession(true, y);
        }
        else{
            startCommand = false;
            listener.startSession(false, y);
        }

        oldYval = y;


    }
}
