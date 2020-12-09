package com.sebastianstext.pegasusbeta.SensorDetectors;

import com.sebastianstext.pegasusbeta.Listeners.rotationListener;

import java.text.ParseException;

public class RotationDetector {

    private rotationListener listener;

    private float currentDegree;
    private float oldDegree;
    private float minTurnDrift;
    private float maxTurnDrift;
    private long lastTurnTime = 0;
    private final static float TURN_DELAY_NS = 100000000;


    public void registerListener(rotationListener listener) { this.listener = listener; }

    public void detectTurning(long timeNs, float x, float y, float z) throws ParseException {

        float degree = Math.round(x);

        currentDegree = degree;

        maxTurnDrift = currentDegree + 6.5f;
        minTurnDrift = currentDegree - 6.5f;


        if(oldDegree != currentDegree && (timeNs - lastTurnTime > TURN_DELAY_NS)){
            listener.detectTurn(currentDegree, oldDegree, timeNs, lastTurnTime);
            lastTurnTime = timeNs;
        }

        oldDegree = currentDegree;


        // listener.detectTurn(currentDegree);

    }
}
