package com.sebastianstext.pegasusbeta.Listeners;

import java.text.ParseException;

public interface rotationListener {
    void detectTurn(float currentDegree, float oldDegree, long timeNs, long lastTurnTime) throws ParseException;
}
