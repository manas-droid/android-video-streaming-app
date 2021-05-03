package com.example.videostreamingapp.VideosAPI;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetDataInstance {
    @GET("master/data.json")
    Call<Response> getAllVideos();
}