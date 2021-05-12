package com.example.videostreamingapp.ui.Adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.videostreamingapp.R;
import com.example.videostreamingapp.VideosAPI.RetrofitResponse.Videos;
import com.example.videostreamingapp.ui.Fragments.SavedVideoFragment;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class SavedVideoAdapter extends RecyclerView.Adapter<SavedVideoAdapter.VideosViewHolder> {
    private List<Videos> videos;
    private static final String TAG = "VideosAdapter";

    public SavedVideoAdapter(List<Videos> videos) {
        this.videos = videos;
    }

    @NonNull
    @Override
    public VideosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_video, parent, false);
        return new VideosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideosViewHolder holder, int position) {
        Videos video = videos.get(position);
        holder.title.setText(video.getTitle());
        holder.subTitle.setText(video.getSubtitle());
        Glide.with(holder.itemView.getContext()).load(video.getThumb())
                .placeholder(R.drawable.ic_placeholder)
                .into(holder.thumbNail);
        holder.materialCardView.setOnClickListener(v -> {
            SavedVideoFragment videoFragment = new SavedVideoFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("video", video);
            videoFragment.setArguments(bundle);
            AppCompatActivity appCompatActivity = (AppCompatActivity) v.getContext();

            appCompatActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, videoFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }


    public static class VideosViewHolder extends RecyclerView.ViewHolder{
        public TextView title, subTitle;
        public ImageView thumbNail;
        public MaterialCardView materialCardView;
        public VideosViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            subTitle = itemView.findViewById(R.id.subTitle);
            thumbNail = itemView.findViewById(R.id.thumbNail);
            materialCardView = itemView.findViewById(R.id.card);
        }
    }
}
