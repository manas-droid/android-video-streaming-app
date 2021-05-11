package com.example.videostreamingapp.VideosAPI.RetrofitResponse;

import java.util.List;

public class Response {
    private List<listVideosAndName> categories = null;

    public List<listVideosAndName> getCategories() {
        return categories;
    }

    public void setCategories(List<listVideosAndName> categories) {
        this.categories = categories;
    }
}
