package com.daniel.dogpictures.async.filewriter;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.daniel.dogpictures.R;

import java.io.File;

public class FileWriter {
    private static final String LOG_TAG = "FileWriter";

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static File getPublicAlbumStorageDir(Context context) {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), context.getString(R.string.app_name_folder));
        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }
        return file;
    }
}
