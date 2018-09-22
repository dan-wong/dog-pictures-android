package com.daniel.dogpictures.async.imagefromurl;

import android.graphics.Bitmap;

interface ImageFromURLCallback {
    void imageRetrieved(Bitmap image);
}
