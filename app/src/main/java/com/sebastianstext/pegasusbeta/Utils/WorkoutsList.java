package com.sebastianstext.pegasusbeta.Utils;

public class WorkoutsList {
    private String user;
    private int nmbrstops;
    private String avrgspeed;
    private int meters;

    public WorkoutsList(String user, int nmbrstops, String avrgspeed, int meters) {
        this.user = user;
        this.nmbrstops = nmbrstops;
        this.avrgspeed = avrgspeed;
        this.meters = meters;
    }

    public String getUser(){return user;}

    public int getNmbrstops(){return nmbrstops;}

    public String getAvrgspeed(){return avrgspeed;}

    public int getMeters(){return meters;}
}
