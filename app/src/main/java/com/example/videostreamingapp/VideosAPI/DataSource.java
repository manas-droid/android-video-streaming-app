package com.example.videostreamingapp.VideosAPI;

import android.util.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class DataSource {
    GetDataInstance getDataInstance;
    private static final String TAG = "DataSource";
    public void getAllVideos(SetResults setResults){
        getDataInstance = RetrofitInstance.getInstance().create(GetDataInstance.class);
        Call<Response> responseCall = getDataInstance.getAllVideos();
        responseCall.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if(response.isSuccessful()){
                   listVideosAndName videosAndName =  response.body().getCategories().get(0);
                        setResults.getVideoResponse(videosAndName.getVideos());
                }
                else
                    Log.d(TAG, "onResponse: "+response.errorBody());
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.getMessage());
            }
        });

    }

    public interface SetResults{
        void getVideoResponse(List<Videos> videos);
    }

}
