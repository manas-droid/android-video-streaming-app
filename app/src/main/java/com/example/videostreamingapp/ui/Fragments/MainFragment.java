package com.example.videostreamingapp.ui.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.videostreamingapp.R;
import com.example.videostreamingapp.VideosAPI.RetrofitResponse.Videos;
import com.example.videostreamingapp.ui.Adapters.VideosAdapter;
import com.example.videostreamingapp.ui.ViewModels.AllVideosViewModel;

import java.util.List;

public class MainFragment extends Fragment {
    private AllVideosViewModel allVideosViewModel;
    private Observer<List<Videos>> observer;
    private  RecyclerView recyclerView;
    private static final String TAG = "MainFragment";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allVideosViewModel = new ViewModelProvider(this ,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()))
                .get(AllVideosViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Log.d(TAG, "MainFragment onCreateView: called");
        observer =  new Observer<List<Videos>>() {
            @Override
            public void onChanged(List<Videos> videos) {
                if(videos!=null) {
                    VideosAdapter videosAdapter = new VideosAdapter(videos);
                    recyclerView.setAdapter(videosAdapter);
                }
            }
        };
        allVideosViewModel.getListMutableLiveData().observe(getActivity() , observer);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
}
