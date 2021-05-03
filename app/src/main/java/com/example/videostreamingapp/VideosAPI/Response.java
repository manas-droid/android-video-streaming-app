package com.example.videostreamingapp.VideosAPI;

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
