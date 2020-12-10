package com.sebastianstext.pegasusbeta.DataStorage;

public class Workout {
    private int meters, metersskritt, meterstrav, metersgalopp, stops, rightvolt, leftvolt, speed;

    public Workout(int meters, int metersskritt, int meterstrav, int metersgalopp, int stops, int rightvolt, int leftvolt, int speed){
        this.meters = meters;
        this.metersskritt = metersskritt;
        this.metersgalopp = metersgalopp;
        this.meterstrav = meterstrav;
        this.speed = speed;
        this.stops = stops;
        this.rightvolt = rightvolt;
        this.leftvolt = leftvolt;

    }



    public int getMeters(){ return meters; }
    public int getMetersskritt(){ return metersskritt; }
    public int getMeterstrav(){ return meterstrav; }
    public int getMetersgalopp(){ return metersgalopp; }
    public int getStops(){ return stops; }
    public int getRightvolt(){ return rightvolt; }
    public int getLeftvolt() { return leftvolt; }
    public int getSpeed() { return speed; }
}
