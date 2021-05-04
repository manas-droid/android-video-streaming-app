package com.example.videostreamingapp.ui.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.videostreamingapp.R;
import com.example.videostreamingapp.VideosAPI.Videos;

public class VideoFragment extends Fragment {
    Videos video;
    TextView title , subTitle , description;
    VideoView videoView;
    ProgressBar progressBar;
    private static final String TAG = "VideoFragment";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        video = (Videos) bundle.getSerializable("video");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        title = view.findViewById(R.id.title);
        subTitle = view.findViewById(R.id.subTitle);
        description = view.findViewById(R.id.description);
        videoView = view.findViewById(R.id.videoView);
        progressBar = view.findViewById(R.id.progressBar);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        title.setText(video.getTitle());
        subTitle.setText(video.getSubtitle());
        description.setText(video.getDescription());

        videoView.setVideoPath(video.getSources().get(0));
        MediaController mediaController  = new MediaController(getContext());
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setOnPreparedListener(mp -> {
            progressBar.setVisibility(View.GONE);
            videoView.start();
        });
    }

}
