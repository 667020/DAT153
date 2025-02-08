package com.hvl.no.dat153_obllig1_quizzy.view;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.hvl.no.dat153_obllig1_quizzy.R;
import com.hvl.no.dat153_obllig1_quizzy.databinding.ActivityQuizBinding;
import com.hvl.no.dat153_obllig1_quizzy.features.gallery.model.GalleryItem;
import com.hvl.no.dat153_obllig1_quizzy.features.gallery.repo.GalleryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {

    private ActivityQuizBinding binding;
    private GalleryRepository galleryRepository;
    private List<GalleryItem> galleryItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        galleryRepository = new GalleryRepository(this);
        List<GalleryItem> galleryItems = galleryRepository.loadGalleryItems();

        if(galleryItems.isEmpty()) {
            Toast.makeText(this, "No images in gallery", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        int randomIndex = new Random().nextInt(galleryItems.size());

        GalleryItem selectedGalleryItem = galleryItems.get(randomIndex);

        Glide.with(this)
                .load(selectedGalleryItem.getImageUri())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error_image)
                .into(binding.imgQuizpic);





    }
}