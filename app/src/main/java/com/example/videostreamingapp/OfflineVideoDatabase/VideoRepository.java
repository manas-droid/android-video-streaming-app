package com.example.videostreamingapp.OfflineVideoDatabase;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.videostreamingapp.VideosAPI.RetrofitResponse.Videos;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VideoRepository {
    private VideoDao videoDao;
    private LiveData<List<Videos>> videosLiveData;
    ExecutorService executorService;

    public VideoRepository(Application application){
        VideoDatabase videoDatabase = VideoDatabase.getInstance(application.getApplicationContext());
        videoDao = videoDatabase.videoDao();
        videosLiveData = videoDao.getVideosList();
    }

    public void insert(Videos video){
        executorService = Executors.newSingleThreadExecutor();

        executorService.submit(()->{
                videoDao.insertVideo(video);
        });

        executorService.shutdown();
    }


    public void delete (int id){

        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(()->{
          videoDao.deleteVideo(id);
        });

        executorService.shutdown();
    }



    public LiveData<List<Videos>> getAllSavedVideos(){
        return this.videosLiveData;
    }


}
