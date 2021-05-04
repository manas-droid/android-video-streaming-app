package com.example.videostreamingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.example.videostreamingapp.VideosAPI.DataSource;
import com.example.videostreamingapp.VideosAPI.Videos;
import com.example.videostreamingapp.ui.Adapters.VideosAdapter;
import com.example.videostreamingapp.ui.AllVideosViewModel;
import com.example.videostreamingapp.ui.Fragments.MainFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container, new MainFragment()).commit();
    }

}