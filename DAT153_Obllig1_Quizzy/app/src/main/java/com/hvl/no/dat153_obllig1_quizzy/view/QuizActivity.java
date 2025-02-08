package com.hvl.no.dat153_obllig1_quizzy.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.hvl.no.dat153_obllig1_quizzy.R;
import com.hvl.no.dat153_obllig1_quizzy.databinding.ActivityQuizBinding;
import com.hvl.no.dat153_obllig1_quizzy.features.gallery.model.GalleryItem;
import com.hvl.no.dat153_obllig1_quizzy.features.gallery.repo.GalleryRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {

    private ActivityQuizBinding binding;
    private GalleryRepository galleryRepository;
    private List<GalleryItem> galleryItems;
    private String currentCorrectAnswer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        galleryRepository = new GalleryRepository(this);
        galleryItems = galleryRepository.loadGalleryItems();

        if(galleryItems.isEmpty()) {
            Toast.makeText(this, "No images in gallery", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        binding.btnAnswer1.setOnClickListener(v -> checkAnswer(binding.btnAnswer1.getText().toString()));
        binding.btnAnswer2.setOnClickListener(v -> checkAnswer(binding.btnAnswer2.getText().toString()));
        binding.btnAnswer3.setOnClickListener(v -> checkAnswer(binding.btnAnswer3.getText().toString()));

        loadQuestion();
    }

    private void checkAnswer(String selectedAnswer) {
        if (selectedAnswer.equals(currentCorrectAnswer)) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Incorrect!", Toast.LENGTH_SHORT).show();
        }
        // Optionally add a short delay to let the user see the toast before loading the next question.
        binding.imgQuizpic.postDelayed(this::loadQuestion, 1000);

    }

    private void loadQuestion() {
        int randomIndex = new Random().nextInt(galleryItems.size());

        GalleryItem selectedGalleryItem = galleryItems.get(randomIndex);

        Glide.with(this)
                .load(selectedGalleryItem.getImageUri())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error_image)
                .into(binding.imgQuizpic);

        List<String> answers = getQuizAnswers(randomIndex);
        currentCorrectAnswer = answers.get(0);

        Collections.shuffle(answers);
        binding.btnAnswer1.setText(answers.get(0));
        binding.btnAnswer2.setText(answers.get(1));
        binding.btnAnswer3.setText(answers.get(2));
    }

    private List<String> getQuizAnswers(int randomIndex) {
        List<String> answers = new ArrayList<>();
        answers.add(galleryItems.get(randomIndex).getName());
        while(answers.size() < 3) {
            int randomAnswerIndex = new Random().nextInt(galleryItems.size());
            String randomAnswer = galleryItems.get(randomAnswerIndex).getName();
            if(!answers.contains(randomAnswer)) {
                answers.add(randomAnswer);
            }
        } return answers;

    }

    }