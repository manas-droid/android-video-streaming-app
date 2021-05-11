package com.example.videostreamingapp.ui;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.videostreamingapp.VideosAPI.DataSource;
import com.example.videostreamingapp.VideosAPI.RetrofitResponse.Videos;

import java.util.List;

public class AllVideosViewModel extends ViewModel implements DataSource.SetResults {
    private DataSource dataSource;
    private MutableLiveData<List<Videos>> listMutableLiveData;

    public AllVideosViewModel(){
        dataSource = new DataSource();
        listMutableLiveData = new MutableLiveData<>();
        listMutableLiveData.setValue(null);
    }

    public MutableLiveData<List<Videos>> getListMutableLiveData() {
        return listMutableLiveData;
    }

    public void fetchVideos(){
        dataSource.getAllVideos(this);
    }
    @Override
    public void getVideoResponse(List<Videos> videos) {
                listMutableLiveData.setValue(videos);
    }
}
