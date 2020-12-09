package com.sebastianstext.pegasusbeta.DataStorage;

public class Horse {

    private String breed, name;
    private int height;


    public Horse (String name, int height, String breed) {
        this.name = name;
        this.breed = breed;
        this.height = height;
    }

    public String getName() { return name; }
    public String getBreed() { return breed; }
    public int getHeight() { return height; }

}
