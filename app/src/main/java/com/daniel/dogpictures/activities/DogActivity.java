package com.daniel.dogpictures.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.daniel.dogpictures.DogBreed;
import com.daniel.dogpictures.R;
import com.daniel.dogpictures.RedditImage;
import com.daniel.dogpictures.async.filewriter.FileWriter;
import com.daniel.dogpictures.async.filewriter.FileWriterAsyncTask;
import com.daniel.dogpictures.async.filewriter.FileWriterCallback;
import com.daniel.dogpictures.redditscraper.RedditScraper;
import com.daniel.dogpictures.redditscraper.RedditScraperCallback;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import es.dmoral.toasty.Toasty;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class DogActivity extends AppCompatActivity implements RedditScraperCallback, FileWriterCallback {
    private static final String FIRST_LAUNCH = "com.daniel.dogpictures.first.launch";

    private List<RedditImage> redditImagesList = new ArrayList<>();

    @BindView(R.id.titleTextView) TextView titleTextView;
    @BindView(R.id.imageView) ImageView imageView;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.moreDogsButton) Button moreDogsButton;

    private int currentImage = -1;
    private DogBreed dogBreed;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog);
        ButterKnife.bind(this);

        checkInitialLaunch();

        dogBreed = DogBreed.fromString(getIntent().getStringExtra(MainActivity.DOG_BREED));
        setTitle(dogBreed.getName());

        moreDogsButton.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        RedditScraper.getImagesFromSubreddit(this, dogBreed.getSubreddit(), "", this);
    }

    private void checkInitialLaunch() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        boolean firstLaunch = sharedPref.getBoolean(FIRST_LAUNCH, true);

        if (firstLaunch) {
            new AlertDialog.Builder(this)
                    .setTitle("Hot Tip!")
                    .setMessage("You can save the cute dogs by long pressing the image. A dialog will show up asking you if you want to save the image." +
                            "To save the image, click 'Yes'.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    })
                    .show();

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(FIRST_LAUNCH, false);
            editor.apply();
        }
    }

    @OnClick(R.id.moreDogsButton)
    public void moreDogsButton() {
        if (currentImage + 1 >= redditImagesList.size()) {
            RedditScraper.getImagesFromSubreddit(
                    this,
                    dogBreed.getSubreddit(),
                    redditImagesList.get(redditImagesList.size() - 1).id,
                    this);
        } else {
            setImage(redditImagesList.get(++currentImage));
        }
    }

    @OnLongClick(R.id.imageView)
    public boolean imageViewLongClick() {
        DogActivityPermissionsDispatcher.saveImageWithPermissionCheck(this);
        return true;
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void saveImage() {
        if (currentImage <= 0) {
            return;
        }

        final RedditImage image = redditImagesList.get(currentImage);

        if (FileWriter.isExternalStorageWritable()) {
            final File directory = FileWriter.getPublicAlbumStorageDir(this);
            if (image == null) {
                Toasty.warning(this, getString(R.string.no_current_image), Toast.LENGTH_SHORT).show();
            } else { //Try writing to file
                new AlertDialog.Builder(this)
                        .setTitle(R.string.save_image)
                        .setMessage(R.string.save_image_prompt)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                callFileWriterAsyncTask(directory, image);
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        } else {
            Toasty.error(this, getString(R.string.external_storage_not_mounted), Toast.LENGTH_SHORT, true).show();
        }
    }

    private void callFileWriterAsyncTask(File directory, RedditImage image) {
        progressDialog = ProgressDialog.show(this, "Saving Image", "Please wait...");
        new FileWriterAsyncTask(directory, image, this).execute();
    }

    @Override
    public void result(Boolean result) {
        progressDialog.dismiss();
        if (result) {
            Toasty.success(this, getString(R.string.image_saved), Toast.LENGTH_SHORT, true).show();
        } else {
            Toasty.error(this, getString(R.string.an_error_occurred), Toast.LENGTH_SHORT, true).show();
        }
    }

    private void setImage(RedditImage redditImage) {
        titleTextView.setText(StringUtils.abbreviate(redditImage.title, 100));
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
            Toasty.error(getApplicationContext(), getString(R.string.an_error_occurred), Toast.LENGTH_SHORT, true).show();
            finish();
            return;
        }

        currentImage = -1;
        redditImagesList = images;
        moreDogsButton();

        progressBar.setVisibility(View.INVISIBLE);
        moreDogsButton.setEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        DogActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showRationaleForExternalStorage(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.permission_error_save_device))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showDeniedForExternalStorage() {
        showErrorForWriteExternalStorage();
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showErrorForWriteExternalStorage() {
        Toasty.warning(this, getString(R.string.permission_error_save_device), Toast.LENGTH_SHORT, true).show();
    }
}
