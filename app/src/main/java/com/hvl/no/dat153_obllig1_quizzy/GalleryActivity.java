package com.hvl.no.dat153_obllig1_quizzy;

import android.media.Image;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hvl.no.dat153_obllig1_quizzy.databinding.ActivityGalleryBinding;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {
    private ActivityGalleryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGalleryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RecyclerView recyclerView = findViewById(R.id.galleryRecyclerView);

        binding.btnGalleryBack.setOnClickListener(v -> finish());

        List<GalleryItem> galleryItems = new ArrayList<>();
        galleryItems.add(new GalleryItem("Duck", R.drawable.duck));
        galleryItems.add(new GalleryItem("Piggy", R.drawable.pig));
        galleryItems.add(new GalleryItem("Super Mario Bro", R.drawable.mario));

        ImageGalleryAdapter adapter = new ImageGalleryAdapter(this,galleryItems);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }
}