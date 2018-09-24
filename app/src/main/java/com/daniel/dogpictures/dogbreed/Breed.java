package com.daniel.dogpictures.dogbreed;

public class Breed {
    public String name;
    public String subreddit;

    public Breed() {
    }

    public Breed(String name, String subreddit) {
        this.name = name;
        this.subreddit = subreddit;
    }

    @Override
    public String toString() {
        return name;
    }
}
