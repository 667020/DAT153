package com.hvl.no.dat153_obllig1_quizzy.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraHelper {

    /**
     * Creates a temporary image file in the external pictures directory.
     *
     * @param context The context for accessing file directories.
     * @return The newly created image file.
     * @throws IOException If an error occurs during file creation.
     */
    public static File createImageFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    /**
     * Returns the content URI for the given image file using FileProvider.
     *
     * @param context   The context needed to access the package name.
     * @param photoFile The file for which to create the content URI.
     * @return The content URI.
     */
    public static Uri getPhotoUri(Context context, File photoFile) {
        // Ensure the authority string matches what you declared in your manifest.
        return FileProvider.getUriForFile(
                context,
                context.getPackageName() + ".fileprovider",
                photoFile);
    }

    /**
     * Builds and returns a camera intent with the proper output URI.
     *
     * @param context  The context used to build the intent.
     * @param photoUri The content URI where the image will be saved.
     * @return The camera intent.
     */
    public static Intent buildCameraIntent(Context context, Uri photoUri) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        return cameraIntent;
    }

}
