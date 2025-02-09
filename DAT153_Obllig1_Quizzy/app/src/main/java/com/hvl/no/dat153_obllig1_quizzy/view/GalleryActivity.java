package com.hvl.no.dat153_obllig1_quizzy.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hvl.no.dat153_obllig1_quizzy.ImageGalleryAdapter;
import com.hvl.no.dat153_obllig1_quizzy.R;
import com.hvl.no.dat153_obllig1_quizzy.databinding.ActivityGalleryBinding;
import com.hvl.no.dat153_obllig1_quizzy.databinding.DialogGalleryOptionsBinding;
import com.hvl.no.dat153_obllig1_quizzy.dialogs.DialogHelper;
import com.hvl.no.dat153_obllig1_quizzy.features.gallery.model.GalleryItem;
import com.hvl.no.dat153_obllig1_quizzy.features.gallery.repo.GalleryRepository;
import com.hvl.no.dat153_obllig1_quizzy.util.CameraHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GalleryActivity extends AppCompatActivity implements ImageGalleryAdapter.OnGalleryItemClickListener {
    private List<GalleryItem> galleryItems;
    private ImageGalleryAdapter adapter;
    private GalleryRepository galleryRepository;
    private Uri currentPhotoUri;
    private boolean sortAscending = true;
    private ActivityGalleryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGalleryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RecyclerView recyclerView = findViewById(R.id.galleryRecyclerView);

        galleryRepository = new GalleryRepository(this);
        galleryItems = galleryRepository.loadGalleryItems();

        adapter = new ImageGalleryAdapter(this, galleryItems, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Buttons
        binding.btnGalleryBack.setOnClickListener(v -> finish());

        binding.btnAddEntry.setOnClickListener(v -> DialogHelper.showAddEntryDialog(this, new DialogHelper.OnAddEntryListener() {
            @Override
            public void onCameraSelected() {
                openCamera();
            }

            @Override
            public void onGallerySelected() {
                // Open gallery
                galleryLauncher.launch("image/*");
            }
        }));

        binding.btnToggleSort.setOnClickListener(v -> toggleSortOrder());

        // Retrieve and use the URI
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String imageUriString = sharedPreferences.getString("image_uri", null);
        Uri imageUri = imageUriString != null ? Uri.parse(imageUriString) : null;

        if (imageUri != null) {
            // Use the URI to load the image
            // Assuming you have a method to load the image using the URI
            loadImage(imageUri);
        }
    }

    private void toggleSortOrder() {
        sortAscending = !sortAscending;

        if (sortAscending) {
            binding.btnToggleSort.setText("Sort: A-Z");
            galleryItems.sort((item1, item2) -> item1.getName().compareToIgnoreCase(item2.getName()));
        } else {
            binding.btnToggleSort.setText("Sort: Z-A");
            galleryItems.sort((item1, item2) -> item2.getName().compareToIgnoreCase(item1.getName()));
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onGalleryItemClicked(GalleryItem item, int position) {
        showOptionsDialog(item, position);
    }

    private void showOptionsDialog(GalleryItem item, int position) {
        // Inflate custom dialog layout
        DialogGalleryOptionsBinding binding = DialogGalleryOptionsBinding.inflate(getLayoutInflater());

        Dialog dialog = new Dialog(this);
        dialog.setContentView(binding.getRoot());

        binding.tvDialogTitle.setText("Choose an option");
        binding.btnEditName.setOnClickListener(v -> {
            dialog.dismiss();
            DialogHelper.showNameDialog(this, name -> {
                // Update the item name and notify the adapter
                item.setName(name);
                adapter.notifyItemChanged(position);
                galleryRepository.saveImageUri(item.getName(), item.getImageUri()); // Save the updated item
            });
        });

        binding.btnDelete.setOnClickListener(v -> {
            dialog.dismiss();
            deleteItem(position);
        });

        dialog.show();
    }

    public void deleteItem(int position) {
        GalleryItem item = galleryItems.get(position);

        galleryItems.remove(position);

        adapter.notifyItemRemoved(position);

        galleryRepository.deleteImageUri(item.getImageUri());

        Toast.makeText(this, "Photo deleted!", Toast.LENGTH_SHORT).show();
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else {
            requestCameraPermissions.launch(Manifest.permission.CAMERA);
        }
    }

    // Handle permission request
    private final ActivityResultLauncher<String> requestCameraPermissions = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            dispatchTakePictureIntent();
        } else {
            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
        }
    });

    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    // When the user selects an image, show the Name Entry dialog.
                    DialogHelper.showNameDialog(this, name -> addNewEntry(name, uri));
                }
            });

    // Launch the camera and store the image
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent;
        try {
            // Create the image file using the helper.
            File photoFile = CameraHelper.createImageFile(this);
            // Get the content URI from the helper.
            currentPhotoUri = CameraHelper.getPhotoUri(this, photoFile);
            // Store the file path if needed.
            String currentPhotoPath = photoFile.getAbsolutePath(); // Build the camera intent.
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

    /**
     * ActivityResultLauncher to handle the camera's result.
     * When a photo is taken successfully, the Name Entry dialog is shown.
     */
    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Show the Name Entry dialog via the helper.
                    DialogHelper.showNameDialog(this, name -> addNewEntry(name, currentPhotoUri));
                }
            });

    // Add the new entry to RecyclerView
    private void addNewEntry(String name, Uri imageUri) {
        galleryItems.add(new GalleryItem(name, imageUri));
        adapter.notifyItemInserted(galleryItems.size() - 1);
        galleryRepository.saveImageUri(name, imageUri);
        Toast.makeText(this, "Photo added!", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri selectedUri = data != null ? data.getData() : null;
            if (selectedUri != null) {
                int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                getContentResolver().takePersistableUriPermission(selectedUri, takeFlags);

                // Save the URI to SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
                sharedPreferences.edit().putString("image_uri", selectedUri.toString()).apply();
            }
        }
    }

    private void loadImage(Uri imageUri) {
        // Implement your image loading logic here
    }
}