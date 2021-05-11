package com.example.videostreamingapp.OfflineVideoDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.videostreamingapp.VideosAPI.RetrofitResponse.Videos;

import java.util.List;


@Dao
public interface VideoDao {
    @Insert
    void insertVideo(Videos video);

    @Query("SELECT * FROM video_database")
    LiveData<List<Videos>> getVideosList();

    @Query("DELETE FROM video_database WHERE id = :id")
    void deleteVideo(int id);
}
