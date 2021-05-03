package com.example.videostreamingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;

import com.example.videostreamingapp.VideosAPI.DataSource;
import com.example.videostreamingapp.VideosAPI.Videos;
import com.example.videostreamingapp.ui.AllVideosViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Observer<List<Videos>> observer;
    private static final String TAG = "MainActivity";
    AllVideosViewModel allVideosViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       allVideosViewModel = new ViewModelProvider(this ,
               ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
               .get(AllVideosViewModel.class);

       allVideosViewModel.fetchVideos();
       observer =  new Observer<List<Videos>>() {
           @Override
           public void onChanged(List<Videos> videos) {
               Log.d(TAG, "onChanged: "+videos);
           }
       };

       allVideosViewModel.getListMutableLiveData().observe(this , observer);
    }

}