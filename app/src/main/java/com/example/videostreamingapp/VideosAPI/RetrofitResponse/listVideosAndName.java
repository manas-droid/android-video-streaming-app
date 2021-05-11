package com.example.videostreamingapp.VideosAPI.RetrofitResponse;

import java.util.List;

public class listVideosAndName {
    private List<Videos> videos;
    private String name;

    public List<Videos> getVideos() {
        return videos;
    }

    public void setVideos(List<Videos> videos) {
        this.videos = videos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
