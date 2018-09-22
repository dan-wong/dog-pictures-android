package com.daniel.dogpictures.async.imagefromurl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.URL;

public class ImageFromURLAsyncTask extends AsyncTask<String, Void, Bitmap> {
    private ImageFromURLCallback callback;

    public ImageFromURLAsyncTask(ImageFromURLCallback callback) {
        this.callback = callback;
    }

    @Override
    protected Bitmap doInBackground(String[] urls) {
        String imageUrl = urls[0];
        Bitmap image = null;
        try {
            InputStream in = new URL(imageUrl).openStream();
            image = BitmapFactory.decodeStream(in);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        callback.imageRetrieved(result);
    }
}
