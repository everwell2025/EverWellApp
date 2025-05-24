package com.example.elderlycareapp;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Set;
import java.util.ArrayList;


public class FavoritesActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private VideoAdapter adapter;
    private ArrayList<Video> favoriteVideos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        setupBottomNavigation(null);

        ImageButton btnAllBack = findViewById(R.id.btnAllBack);
        btnAllBack.setOnClickListener(v -> finish());


        recyclerView = findViewById(R.id.recyclerViewFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        // Get favorited videos from a list
        favoriteVideos = getFavoritedVideos();


        adapter = new VideoAdapter(favoriteVideos, video -> {
            Intent intent = new Intent(FavoritesActivity.this, VideoDisplay.class);
            intent.putExtra("VIDEO_ID", video.videoId);
            startActivity(intent);
        });


        recyclerView.setAdapter(adapter);
    }

    private ArrayList<Video> getFavoritedVideos() {
        ArrayList<Video> allVideos = VideoData.INSTANCE.getAllVideos();


        // Sync favorites
        syncFavorites(allVideos);


        ArrayList<Video> favs = new ArrayList<>();
        for (Video v : allVideos) {
            if (v.isFavorite()) {
                favs.add(v);
            }
        }

        // Sort favorites alphabetically by title
        favs.sort((v1, v2) -> v1.getTitle().compareToIgnoreCase(v2.getTitle()));

        return favs;
    }


    private void syncFavorites(ArrayList<Video> videos) {
        Set<String> favoriteIds = FavoritesManager.getFavoriteVideoIds(this);
        for (Video video : videos) {
            video.setFavorite(favoriteIds.contains(video.getVideoId()));
        }
    }
}