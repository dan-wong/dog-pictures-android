package com.daniel.dogpictures.redditscraper;

import com.daniel.dogpictures.RedditImage;

import java.util.List;

public interface RedditScraperCallback {
    void imagesReturned(List<RedditImage> images);
}
