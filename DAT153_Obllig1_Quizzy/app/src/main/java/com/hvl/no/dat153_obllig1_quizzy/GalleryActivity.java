package com.hvl.no.dat153_obllig1_quizzy;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.hvl.no.dat153_obllig1_quizzy.features.gallery.model.GalleryItem;
import com.hvl.no.dat153_obllig1_quizzy.features.gallery.repo.GalleryRepository;
import com.hvl.no.dat153_obllig1_quizzy.util.CameraHelper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;


public class GalleryActivity extends AppCompatActivity {
    private List<GalleryItem> galleryItems;
    private String currentPhotoPath;
    private ImageGalleryAdapter adapter;
    private GalleryRepository galleryRepository;
    private Uri currentPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.hvl.no.dat153_obllig1_quizzy.databinding.ActivityGalleryBinding binding = ActivityGalleryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RecyclerView recyclerView = findViewById(R.id.galleryRecyclerView);


        galleryRepository = new GalleryRepository(this);

        galleryItems = galleryRepository.loadGalleryItems();

        adapter = new ImageGalleryAdapter(this, galleryItems);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Buttons
        binding.btnGalleryBack.setOnClickListener(v -> finish());
        binding.btnAddEntry.setOnClickListener(v -> showAddEntryDialog());
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
        Intent takePictureIntent;
        try {
            // Create the image file using the helper.
            File photoFile = CameraHelper.createImageFile(this);
            // Get the content URI from the helper.
            currentPhotoUri = CameraHelper.getPhotoUri(this, photoFile);
            // Store the file path if needed.
            currentPhotoPath = photoFile.getAbsolutePath();
            // Build the camera intent.
            takePictureIntent = CameraHelper.buildCameraIntent(this, currentPhotoUri);
        } catch (IOException e) {
            Toast.makeText(this, "Error creating file!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ensure there is an activity to handle the camera intent.
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
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


    // Add the new entry to RecyclerView
    private void addNewEntry(String name, Uri imageUri) {
        galleryItems.add(new GalleryItem(name, imageUri)); // ✅ Now using Uri
        adapter.notifyItemInserted(galleryItems.size() - 1);
        galleryRepository.saveImageUri(imageUri);
        Toast.makeText(this, "Photo added!", Toast.LENGTH_SHORT).show();;
    }

}


