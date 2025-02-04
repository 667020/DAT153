package com.hvl.no.dat153_obllig1_quizzy;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.widget.Toast;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hvl.no.dat153_obllig1_quizzy.databinding.ActivityGalleryBinding;
import com.hvl.no.dat153_obllig1_quizzy.databinding.DialogAddEntryBinding;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class GalleryActivity extends AppCompatActivity {
    private ActivityGalleryBinding binding;
    private List<GalleryItem> galleryItems;
    private String currentPhotoPath;
    private ImageGalleryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGalleryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RecyclerView recyclerView = findViewById(R.id.galleryRecyclerView);

        // Back button
        binding.btnGalleryBack.setOnClickListener(v -> finish());

        // Image listen fra gallery items
        galleryItems = new ArrayList<>();
        galleryItems.add(new GalleryItem("Duck", getUriFromDrawable(R.drawable.duck)));
        galleryItems.add(new GalleryItem("Piggy", getUriFromDrawable(R.drawable.pig)));
        galleryItems.add(new GalleryItem("Super Mario Bro", getUriFromDrawable(R.drawable.mario)));
        galleryItems.add(new GalleryItem("Duck3", getUriFromDrawable(R.drawable.duck)));
        galleryItems.add(new GalleryItem("Duck5", getUriFromDrawable(R.drawable.duck)));
        galleryItems.add(new GalleryItem("Duck6", getUriFromDrawable(R.drawable.duck)));
        galleryItems.add(new GalleryItem("Duck7", getUriFromDrawable(R.drawable.duck)));


        ImageGalleryAdapter adapter = new ImageGalleryAdapter(this,galleryItems);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        binding.btnAddEntry.setOnClickListener(v -> showAddEntryDialog());
    }

    // Hjelpemetode for å hente uri
    private Uri getUriFromDrawable(int drawableID) {
        return Uri.parse("android.resource://" + getPackageName() + "/" + drawableID);
    }

    private void showAddEntryDialog() {
        DialogAddEntryBinding dialogBinding = DialogAddEntryBinding.inflate(LayoutInflater.from(this));

        // Dialog instance
        Dialog dialog =  new Dialog(this);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.btnAddFromCamera.setOnClickListener(v -> {
            dialog.dismiss();
            openCamera();
        });


        dialogBinding.btnAddFromGallery.setOnClickListener(v -> {
            dialog.dismiss();
            // TODO: Fiks denne metoden !!
        });

        dialog.show();

    }

    private void openCamera() {if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            dispatchTakePictureIntent();
        }else {
            requestCameraPermissions.launch(Manifest.permission.CAMERA);
        }
    }

    // Handle permission request
    private final ActivityResultLauncher<String> requestCameraPermissions = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            dispatchTakePictureIntent();
        }else {
            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show(); // HVA BETYR DETTE?  Egentlig sjekk hele metoden
        }
    });

    //Launch the camera and stores the imaage
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile == null) {
                Toast.makeText(this, "Failed to create image file!", Toast.LENGTH_SHORT).show();
                return;
            }
            Uri photoURI = FileProvider.getUriForFile(this, "com.hvl.no.dat153_obllig1_quizzy.fileprovider", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            cameraLauncher.launch(takePictureIntent);
        } else {
            Toast.makeText(this, "No camera app found!", Toast.LENGTH_SHORT).show();
        }
    }


    // Activity result for camera
    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if(result.getResultCode() == RESULT_OK) {
            Uri photoUri = Uri.fromFile(new File(currentPhotoPath));
            addNewEntry("new Photo", photoUri);
        }
    });

    // Create a file to store the captured image
    private File createImageFile() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(imageFileName, ".jpg", storageDir);
            currentPhotoPath = image.getAbsolutePath();
            return image;
        } catch (IOException e) {
            Toast.makeText(this, "Error creating file!", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    // Add the new entry to RecyclerView
    private void addNewEntry(String name, Uri imageUri) {
        galleryItems.add(new GalleryItem(name, imageUri)); // ✅ Now using Uri
        adapter.notifyItemInserted(galleryItems.size() - 1);
        Toast.makeText(this, "Photo added!", Toast.LENGTH_SHORT).show();
    }
}


