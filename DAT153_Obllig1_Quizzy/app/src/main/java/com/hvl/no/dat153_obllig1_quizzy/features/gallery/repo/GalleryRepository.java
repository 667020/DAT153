package com.hvl.no.dat153_obllig1_quizzy.features.gallery.repo;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.hvl.no.dat153_obllig1_quizzy.R;
import com.hvl.no.dat153_obllig1_quizzy.features.gallery.model.GalleryItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GalleryRepository {

    private static final String PREFS_NAME = "GalleryPrefs";
    private static final String KEY_IMAGE_URIS = "imageUris";

    private final SharedPreferences sharedPreferences;
    private final Context context;

    public GalleryRepository(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Helper method to get URI from drawable resource ID
    private Uri getUriFromDrawable(int drawableID) {
        return Uri.parse("android.resource://" + context.getPackageName() + "/" + drawableID);
    }

    // Loads default images and saved image URIs.
    public List<GalleryItem> loadGalleryItems() {
        List<GalleryItem> galleryItems = new ArrayList<>();

        // Add default images.
        galleryItems.add(new GalleryItem("Duck", getUriFromDrawable(R.drawable.duck)));
        galleryItems.add(new GalleryItem("Piggy", getUriFromDrawable(R.drawable.pig)));
        galleryItems.add(new GalleryItem("Super Mario Bro", getUriFromDrawable(R.drawable.mario)));

        // Add any saved images from SharedPreferences.
        Set<String> savedUris = sharedPreferences.getStringSet(KEY_IMAGE_URIS, new HashSet<>());
        for (String uriString : savedUris) {
            galleryItems.add(new GalleryItem("Saved Photo", Uri.parse(uriString)));
        }
        return galleryItems;
    }

    // Saves a new image URI to SharedPreferences.
    public void saveImageUri(Uri imageUri) {
        Set<String> savedUris = sharedPreferences.getStringSet(KEY_IMAGE_URIS, new HashSet<>());
        Set<String> updatedUris = new HashSet<>(savedUris);
        updatedUris.add(imageUri.toString());
        sharedPreferences.edit().putStringSet(KEY_IMAGE_URIS, updatedUris).apply();
    }

    public void deleteImageUri(Uri imageUri) {
        // Get the set of saved URIs.
        Set<String> savedUris = sharedPreferences.getStringSet(KEY_IMAGE_URIS, new HashSet<>());
        // create new set to avoid modifying the original set.
        Set<String> updatedUris = new HashSet<>(savedUris);
        // Remove the URI to delete.
        updatedUris.remove(imageUri.toString());
        // Save the updated set of URIs.
        sharedPreferences.edit().putStringSet(KEY_IMAGE_URIS, updatedUris).apply();
    }

}