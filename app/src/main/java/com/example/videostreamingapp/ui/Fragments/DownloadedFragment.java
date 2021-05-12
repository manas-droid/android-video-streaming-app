package com.example.videostreamingapp.ui.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.videostreamingapp.R;
import com.example.videostreamingapp.ui.Adapters.SavedVideoAdapter;
import com.example.videostreamingapp.ui.ViewModels.SavedVideoViewModel;

public class DownloadedFragment extends Fragment {
    SavedVideoViewModel savedVideoViewModel;
    private static final String TAG = "DownloadedFragment";
    private RecyclerView recyclerView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedVideoViewModel = new ViewModelProvider(getActivity(),
                ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication()))
                .get(SavedVideoViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_downloaded, container , false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        savedVideoViewModel.getVideosLiveData().observe(getViewLifecycleOwner(), videos -> {
            if(videos!=null){
                SavedVideoAdapter videosAdapter = new SavedVideoAdapter(videos);
                recyclerView.setAdapter(videosAdapter);
            }
        });
    }
}
