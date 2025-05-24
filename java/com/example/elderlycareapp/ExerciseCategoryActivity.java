package com.example.elderlycareapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageButton;

public class ExerciseCategoryActivity extends BaseActivity {

    ImageView imgHomeWorkout, imgWeightLoss, imgZumba;
    ImageButton btnBack; // ✅ ADD THIS LINE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        setupBottomNavigation(null);

        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close current activity and go back
            }
        });

        imgHomeWorkout = findViewById(R.id.imgHomeWorkout);
        imgWeightLoss = findViewById(R.id.imgWeightLoss);
        imgZumba = findViewById(R.id.imgZumba);

        imgHomeWorkout.setOnClickListener(v -> openCategory("Home Workout"));
        imgWeightLoss.setOnClickListener(v -> openCategory("Weight Loss"));
        imgZumba.setOnClickListener(v -> openCategory("Zumba"));
    }

    private void openCategory(String category) {
        Intent intent = new Intent(ExerciseCategoryActivity.this, VideoListActivity.class);
        intent.putExtra("CATEGORY", category);
        startActivity(intent);
    }
}