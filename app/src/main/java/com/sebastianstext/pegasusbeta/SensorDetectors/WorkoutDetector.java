package com.sebastianstext.pegasusbeta.SensorDetectors;


import android.hardware.SensorManager;

import com.sebastianstext.pegasusbeta.Listeners.energyListener;
import com.sebastianstext.pegasusbeta.Listeners.startListener;
import com.sebastianstext.pegasusbeta.Listeners.stepListener;
import com.sebastianstext.pegasusbeta.Listeners.stopListener;

import java.text.ParseException;



public class WorkoutDetector implements stepListener {

    private float oldSignificantEnergy;
    private boolean isSkritting = false;
    private boolean onHorse = false;
    private int numSteps;
    private boolean hasStarted = false;
    float energy;
    startListener startListener;
    stopListener stopListener;
    com.sebastianstext.pegasusbeta.Listeners.energyListener energyListener;
    StepDetector stepDetector = new StepDetector();

    public void registerStartListener(startListener sListener) {
        this.startListener = sListener;
    }
    public void registerStopListener(stopListener xListener) {
        this.stopListener = xListener;
    }

    public void registerEneryListener(energyListener eListener) {
        this.energyListener = eListener;
    }

    public void detectStart(long timeNs, float x, float y, float z) throws ParseException {
        stepDetector.registerListener(this);
        stepDetector.updateAccel(timeNs, x, y, z);


        float mSignificantEnergy = (float) Math.round(y) - SensorManager.GRAVITY_EARTH;


        float velocityFilter = 3.5f;
        if(mSignificantEnergy - oldSignificantEnergy > velocityFilter && !onHorse){
            onHorse = true;
        }

        float stopVelocityFilter = 5f;
        if(mSignificantEnergy - oldSignificantEnergy > stopVelocityFilter && onHorse){
            onHorse = false;
        }

        oldSignificantEnergy = mSignificantEnergy;

        if(onHorse && isSkritting){

            startListener.startSession(true);
        }
        if(!onHorse && !isSkritting){
            stopListener.stopCommand(true);
        }

      

    }


    @Override
    public void tempWorkout(long timeNsSend, long lastStepTimeNs, float velocityEsimate, float oldVelocityEstimate) {

        if(velocityEsimate < 7 && oldVelocityEstimate > -5 && !hasStarted){
            numSteps++;
            if(numSteps > 3){
                isSkritting = true;
                hasStarted = true;
                numSteps = 0;
            }
        }

        if((timeNsSend - lastStepTimeNs) / 1000000 > 2000 && hasStarted){
            isSkritting = false;
            hasStarted = false;
        }


    }
}
