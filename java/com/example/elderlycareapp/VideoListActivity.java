package com.example.elderlycareapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Set;


public class VideoListActivity extends BaseActivity {
    private ArrayList<Video> filteredVideos = new ArrayList<>();
    private VideoAdapter adapter;
    private String selectedCategory;
    private TextView tvCategoryTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        setupBottomNavigation(null);

        // Find the title TextView in layout
        tvCategoryTitle = findViewById(R.id.tvCategoryTitle);


        // Get the category passed from the previous activity
        selectedCategory = getIntent().getStringExtra("CATEGORY");


        // Set the dynamic title text, or default if null
        if (selectedCategory != null && !selectedCategory.isEmpty()) {
            tvCategoryTitle.setText(selectedCategory);
        } else {
            tvCategoryTitle.setText("Exercises");
        }


        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        // Initialize the adapter
        adapter = new VideoAdapter(filteredVideos, video -> {
            // When an item is clicked, go to the MainActivity with the videoId
            Intent intent = new Intent(VideoListActivity.this, VideoDisplay.class);
            intent.putExtra("VIDEO_ID", video.getVideoId());
            startActivity(intent);
        });


        recyclerView.setAdapter(adapter);


        // Filter videos based on the selected category
        filterVideosByCategory(selectedCategory);


        // Set up button listeners to filter by difficulty and favorites
        Button btnAll = findViewById(R.id.btnAll);
        Button btnBeginner = findViewById(R.id.btnBeginner);
        Button btnIntermediate = findViewById(R.id.btnIntermediate);
        Button btnAdvanced = findViewById(R.id.btnAdvanced);
        ImageButton btnFavorites = findViewById(R.id.btnFavorites);
        ImageButton btnBack = findViewById(R.id.btnBack);


        btnBack.setOnClickListener(v -> finish());


        btnAll.setOnClickListener(v -> filterVideosByCategory(selectedCategory));


        btnBeginner.setOnClickListener(v -> filterVideosByCategoryAndDifficulty(selectedCategory, "Beginner"));


        btnIntermediate.setOnClickListener(v -> filterVideosByCategoryAndDifficulty(selectedCategory, "Intermediate"));


        btnAdvanced.setOnClickListener(v -> filterVideosByCategoryAndDifficulty(selectedCategory, "Expert"));


        btnFavorites.setOnClickListener(v -> {
            Intent intent = new Intent(VideoListActivity.this, FavoritesActivity.class);
            startActivity(intent);
        });
    }


    private void filterVideosByCategory(String category) {
        ArrayList<Video> allVideos = VideoData.INSTANCE.getAllVideos();


        // Sync favorites
        syncFavorites(allVideos);


        filteredVideos.clear();
        for (Video video : allVideos) {
            if (video.getCategory().equalsIgnoreCase(category)) {
                filteredVideos.add(video);
            }
        }


        // Sort alphabetically by title
        filteredVideos.sort((v1, v2) -> v1.getTitle().compareToIgnoreCase(v2.getTitle()));


        adapter.notifyDataSetChanged();
    }


    private void filterVideosByCategoryAndDifficulty(String category, String difficulty) {
        ArrayList<Video> allVideos = VideoData.INSTANCE.getAllVideos();


        // Sync favorites
        syncFavorites(allVideos);


        filteredVideos.clear();
        for (Video video : allVideos) {
            if (video.getCategory().equalsIgnoreCase(category) && video.getDifficulty().equalsIgnoreCase(difficulty)) {
                filteredVideos.add(video);
            }
        }


        // Sort alphabetically by title
        filteredVideos.sort((v1, v2) -> v1.getTitle().compareToIgnoreCase(v2.getTitle()));


        adapter.notifyDataSetChanged();
    }


    private void syncFavorites(ArrayList<Video> videos) {
        Set<String> favoriteIds = FavoritesManager.getFavoriteVideoIds(this);
        for (Video video : videos) {
            video.setFavorite(favoriteIds.contains(video.getVideoId()));
        }
    }
}