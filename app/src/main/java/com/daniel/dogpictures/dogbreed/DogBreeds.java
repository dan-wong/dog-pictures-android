package com.daniel.dogpictures.dogbreed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DogBreeds {
    private static final DogBreeds ourInstance = new DogBreeds();

    private List<Breed> breeds = new ArrayList<>();

    private DogBreeds() {
    }

    public static DogBreeds getInstance() {
        return ourInstance;
    }

    public void addBreeds(Breed breed) {
        breeds.add(breed);
        Collections.sort(breeds, new DogBreedComparator());
    }

    public List<Breed> getBreeds() {
        return Collections.unmodifiableList(breeds);
    }

    public Breed getBreedFromString(String breedString) {
        for (Breed breed : breeds) {
            if (breed.name.equals(breedString)) {
                return breed;
            }
        }
        return null;
    }

    public void clearList() {
        breeds = new ArrayList<>();
    }
}
