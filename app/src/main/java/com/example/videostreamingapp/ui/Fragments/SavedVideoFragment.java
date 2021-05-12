package com.example.videostreamingapp.ui.Fragments;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.example.videostreamingapp.VideosAPI.RetrofitResponse.Videos;

import java.io.File;
import java.io.IOException;

public class SavedVideoFragment extends Fragment {
    private Videos video;
    private TextView title , subTitle , description;
    private VideoView videoView;
    private ProgressBar progressBar;
    private  static final String TAG = "VideoFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        video = (Videos) bundle.getSerializable("video");
        setHasOptionsMenu(true);
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
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) , video.getTitle()+".mp4");

        try {
            Log.d(TAG, "onActivityCreated: "+file.getCanonicalPath());
            videoView.setVideoPath(file.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        MediaController mediaController  = new MediaController(getContext());
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setOnPreparedListener(mp -> {
            progressBar.setVisibility(View.GONE);
            videoView.start();
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.kebab_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.downLoad :
                Log.d(TAG, "onOptionsItemSelected: here");
                break;
        }
        return true;
    }



}
