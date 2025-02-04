package com.hvl.no.dat153_obllig1_quizzy;

import android.net.Uri;

public class GalleryItem {
    private String name;
    private Uri imageUri;

    public GalleryItem(String name, Uri imageUri) {
        this.name = name;
        this.imageUri = imageUri;
    }

    public java.lang.String getName() {
        return name;
    }

    public Uri getImageUri() {
        return imageUri;
    }
}
