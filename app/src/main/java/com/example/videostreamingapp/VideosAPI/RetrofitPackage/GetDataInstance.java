package com.example.videostreamingapp.VideosAPI.RetrofitPackage;

import com.example.videostreamingapp.VideosAPI.RetrofitResponse.Response;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetDataInstance {
    @GET("master/data.json")
    Call<Response> getAllVideos();
}