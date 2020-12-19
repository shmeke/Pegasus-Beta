package com.sebastianstext.pegasusbeta.DataStorage;

public class Horse {

    private String breed;
    private int height;


    public Horse (int height, String breed) {
        this.breed = breed;
        this.height = height;
    }

    public String getBreed() { return breed; }
    public int getHeight() { return height; }

}
