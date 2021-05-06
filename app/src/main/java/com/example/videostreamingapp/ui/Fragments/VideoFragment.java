package com.example.videostreamingapp.ui.Fragments;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
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
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.videostreamingapp.R;
import com.example.videostreamingapp.VideosAPI.Videos;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;


public class VideoFragment extends Fragment {
    private Videos video;
    private TextView title , subTitle , description;
    private VideoView videoView;
    private ProgressBar progressBar;
    private  final int WRITE_PERMISSION = 1001;
    private  static final String TAG = "VideoFragment";
    private String fileName;
    private String url;
    private final int DOWNLOAD_NOTIFICATION_ID = 1221;

    private AtomicBoolean isCancelled;
    private Handler handler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        video = (Videos) bundle.getSerializable("video");
        isCancelled = new AtomicBoolean(false);
        handler = new Handler(Looper.getMainLooper());
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
        ExecutorService executorService =Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try{
                URL link = new URL(url);
                connection =(HttpURLConnection) link.openConnection();
                connection.connect();

                if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                    return "Server returned HTTP "+connection.getResponseCode()+" "+connection.getResponseMessage();
                }

                int fileLength = connection.getContentLength();
                input = connection.getInputStream();
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) , fileName+".mp4");
                output = new FileOutputStream(file);
                byte data [] = new byte[1024*4];
                long total = 0;
                int count;
                while((count = input.read(data))!= -1){
                    if(isCancelled.get()){
                        input.close();
                        return null;
                    }

                    total+=count;
                    int percentage =(int) total * 100 / fileLength;
                    if(fileLength>0) {
                        publishProgress(percentage);
                    }
                    output.write(data, 0, count);
                }
                return "file length "+fileLength;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    if(output!=null)
                        output.close();
                    if(input!=null)
                        input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(connection!=null)connection.disconnect();
            }
            return null;
        });
        executorService.shutdown();
    }

    public void publishProgress(int progress){
        handler.post(()->{
            Log.d(TAG, "publishProgress: "+progress+"  thread name "+Thread.currentThread().getName());
        });

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
