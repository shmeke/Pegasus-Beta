package com.sebastianstext.pegasusbeta.Listeners;

import java.text.ParseException;

public interface stepListener {
    void tempWorkout(long timeNsSend, long lastStepTimeNs, float velocityEsimate, float oldVelocityEstimate) throws ParseException;
}
