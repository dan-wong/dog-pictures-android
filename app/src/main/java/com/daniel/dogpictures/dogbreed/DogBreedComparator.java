package com.daniel.dogpictures.dogbreed;

import java.util.Comparator;

public class DogBreedComparator implements Comparator<Breed> {
    @Override
    public int compare(Breed breed, Breed other) {
        if (breed == null || other == null) {
            return 0;
        }
        return breed.name.compareTo(other.name);
    }
}
