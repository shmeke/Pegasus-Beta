package com.sebastianstext.pegasusbeta.Listeners;

public interface stepListener {
    void tempWorkout(long timeNsSend, long lastStepTimeNs, float velocityEsimate, float oldVelocityEstimate);
}
