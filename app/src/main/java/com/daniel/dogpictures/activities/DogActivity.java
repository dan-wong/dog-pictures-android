package com.daniel.dogpictures.activities;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.daniel.dogpictures.R;
import com.daniel.dogpictures.RedditImage;
import com.daniel.dogpictures.redditscraper.RedditScraper;
import com.daniel.dogpictures.redditscraper.RedditScraperCallback;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

public class DogActivity extends AppCompatActivity implements RedditScraperCallback {
    private List<RedditImage> redditImagesList = new ArrayList<>();

    @BindView(R.id.titleTextView) TextView titleTextView;
    @BindView(R.id.imageView) ImageView imageView;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.moreDogsButton) Button moreDogsButton;

    private int currentImage = 0;
    private String currentDogBreed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog);
        ButterKnife.bind(this);

        currentDogBreed = getIntent().getStringExtra(MainActivity.DOG_BREED);
        setTitle(currentDogBreed);

        moreDogsButton.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        RedditScraper.getImagesFromSubreddit(this, currentDogBreed.toLowerCase(), "", this);
    }

    @OnClick(R.id.moreDogsButton)
    public void moreDogsButton() {
        if (currentImage == redditImagesList.size() - 1) {
            RedditScraper.getImagesFromSubreddit(
                    this,
                    currentDogBreed.toLowerCase(),
                    redditImagesList.get(redditImagesList.size() - 1).id,
                    this);
        } else {
            setImage(redditImagesList.get(currentImage++));
        }
    }

    private void setImage(RedditImage redditImage) {
        titleTextView.setText(StringUtils.abbreviate(redditImage.title, 200));
        progressBar.setVisibility(View.VISIBLE);

        Glide.with(this)
                .load(redditImage.url)
                .apply(new RequestOptions().fitCenter().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toasty.error(getApplicationContext(), "An error occurred :(", Toast.LENGTH_SHORT, true).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.INVISIBLE);
                        return false;
                    }
                })
                .into(imageView);
    }

    @Override
    public void imagesReturned(List<RedditImage> images) {
        if (images == null || images.size() == 0) {
            Toasty.error(getApplicationContext(), "An error occurred :(", Toast.LENGTH_SHORT, true).show();
            return;
        }

        currentImage = 0;
        redditImagesList = images;
        setImage(redditImagesList.get(currentImage++));

        progressBar.setVisibility(View.INVISIBLE);
        moreDogsButton.setEnabled(true);
    }
}
