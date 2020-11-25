package com.sebastianstext.pegasusbeta.Listeners;

public interface rotationListener {
    void detectTurn(float currentDegree, float oldDegree, long timeNs, long lastTurnTime);
}
