package com.example.elderlycareapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Video video);
    }

    private ArrayList<Video> videos;
    private OnItemClickListener listener;


    public VideoAdapter(ArrayList<Video> videos, OnItemClickListener listener) {
        this.videos = videos;
        this.listener = listener;
    }


    public class VideoViewHolder extends RecyclerView.ViewHolder {
        LinearLayout rootLayout;
        ImageView thumbnail;
        TextView title, duration, difficulty, channel;
        ImageButton favoriteIcon;


        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            rootLayout = itemView.findViewById(R.id.rootLayout);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            title = itemView.findViewById(R.id.title);
            duration = itemView.findViewById(R.id.duration);
            difficulty = itemView.findViewById(R.id.difficulty);
            channel = itemView.findViewById(R.id.channel);
            favoriteIcon = itemView.findViewById(R.id.btnFavorite);
        }

        public void bind(Video video, OnItemClickListener listener) {
            thumbnail.setImageResource(video.thumbnailResId);
            title.setText(video.title);
            duration.setText(video.duration);
            difficulty.setText(video.difficulty);
            channel.setText(video.channel);
            favoriteIcon.setImageResource(video.isFavorite() ? R.drawable.favorite_filled : R.drawable.favorite_border);


            // Set background color based on difficulty
            int colorRes;
            switch (video.getDifficulty().toLowerCase()) {
                case "beginner":
                    colorRes = R.color.difficulty_beginner;
                    break;
                case "intermediate":
                    colorRes = R.color.difficulty_intermediate;
                    break;
                case "expert":
                case "advanced":
                    colorRes = R.color.difficulty_expert;
                    break;
                default:
                    colorRes = R.color.difficulty_default;
                    break;
            }
            rootLayout.setBackgroundColor(itemView.getContext().getResources().getColor(colorRes));


            itemView.setOnClickListener(v -> listener.onItemClick(video));


            favoriteIcon.setOnClickListener(v -> {
                Context context = itemView.getContext();
                boolean newState = !video.isFavorite();
                video.setFavorite(newState);

                if (newState) {
                    FavoritesManager.saveFavorite(context, video.getVideoId());
                } else {
                    FavoritesManager.removeFavorite(context, video.getVideoId());
                }

                notifyItemChanged(getAdapterPosition());
            });
        }
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_item, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        holder.bind(videos.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }
}