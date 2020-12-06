package com.sebastianstext.pegasusbeta.Utils;

public class Horse {

    private String name, breed;
    private int height;


    public Horse (String name, String breed, int height) {

        this.name = name;
        this.breed = breed;
        this.height = height;
    }




    public String getName() {
        return name;
    }
    public String getBreed() { return breed; }
    public int getHeight() { return height; }

}
