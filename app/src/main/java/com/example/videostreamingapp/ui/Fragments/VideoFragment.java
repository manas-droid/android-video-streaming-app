package com.example.videostreamingapp.ui.Fragments;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.videostreamingapp.R;
import com.example.videostreamingapp.VideosAPI.Videos;
import com.example.videostreamingapp.utils.Download;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class VideoFragment extends Fragment {
    private Videos video;
    private TextView title , subTitle , description;
    private VideoView videoView;
    private ProgressBar progressBar;
    private  final int WRITE_PERMISSION = 1001;
    private  static final String TAG = "VideoFragment";
    private String fileName;
    private String url;
    private final int JOB_CODE = 404;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        video = (Videos) bundle.getSerializable("video");
        setHasOptionsMenu(true);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        title = view.findViewById(R.id.title);
        subTitle = view.findViewById(R.id.subTitle);
        description = view.findViewById(R.id.description);
        videoView = view.findViewById(R.id.videoView);
        progressBar = view.findViewById(R.id.progressBar);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fileName = video.getTitle();

        title.setText(video.getTitle());
        subTitle.setText(video.getSubtitle());
        description.setText(video.getDescription());
        url = video.getSources().get(0);

        videoView.setVideoPath(video.getSources().get(0));
        MediaController mediaController  = new MediaController(getContext());
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setOnPreparedListener(mp -> {
            progressBar.setVisibility(View.GONE);
            videoView.start();
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.kebab_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.downLoad :
                startDownLoad();
                break;
        }
        return true;
    }


    public void startDownLoad(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                downloadFile(fileName ,url );
            }
            else{
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION);
            }
        }else{
            downloadFile(fileName , url);
        }
    }




    public void downloadFile(String fileName , String url){
        Log.d(TAG, "downloadFile: here");
        Intent serviceIntent = new Intent (getContext() , Download.class);
        serviceIntent.putExtra("url", url);
        serviceIntent.putExtra("fileName", fileName);
        ContextCompat.startForegroundService(getActivity(), serviceIntent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == WRITE_PERMISSION){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                downloadFile(fileName, url);
            }else{
                Toast.makeText(getActivity(), "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
