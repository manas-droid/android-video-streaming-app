package com.example.videostreamingapp.ui.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.videostreamingapp.OfflineVideoDatabase.VideoRepository;
import com.example.videostreamingapp.VideosAPI.RetrofitResponse.Videos;

import java.util.List;

public class SavedVideoViewModel extends AndroidViewModel {
    VideoRepository videoRepository;
    LiveData<List<Videos>> videosLiveData;

    public SavedVideoViewModel(@NonNull Application application) {
        super(application);
        videoRepository = new VideoRepository(application);
        videosLiveData = videoRepository.getAllSavedVideos();
    }

    public LiveData<List<Videos>> getVideosLiveData(){
        return this.videosLiveData;
    }

}
