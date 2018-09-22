package com.daniel.dogpictures;

public enum DogBreed {
    CORGI("Corgi", "corgi"),
    DOGGOS("Doggos", "doggos"),
    DOG_PICTURES("Dog Pictures", "dogpictures"),
    GOLDEN_RETRIEVERS("Golden Retrievers", "goldenretrievers"),
    HUSKY("Husky", "husky"),
    LABRADOR("Labrador", "labrador"),
    PUPPIES("Puppies", "puppies"),
    RARE_PUPPERS("Rare Puppers", "rarepuppers"),
    SAMOYEDS("Samoyeds", "samoyeds"),
    SHIBA_INU("Shiba Inu", "shiba");

    final String name;
    final String subreddit;

    DogBreed(String name, String subreddit) {
        this.name = name;
        this.subreddit = subreddit;
    }

    public static DogBreed fromString(String dogBreed) {
        for (DogBreed current : DogBreed.values()) {
            if (current.name.equals(dogBreed)) return current;
        }
        return CORGI;
    }

    public String getName() {
        return name;
    }

    public String getSubreddit() {
        return subreddit;
    }

    @Override
    public String toString() {
        return name;
    }
}
