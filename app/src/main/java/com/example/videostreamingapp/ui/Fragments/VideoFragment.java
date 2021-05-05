package com.example.videostreamingapp.ui.Fragments;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.videostreamingapp.R;
import com.example.videostreamingapp.VideosAPI.Videos;

import static android.app.DownloadManager.ACTION_NOTIFICATION_CLICKED;

public class VideoFragment extends Fragment {
    Videos video;
    TextView title , subTitle , description;
    VideoView videoView;
    ProgressBar progressBar;
    private static int WRITE_PERMISSION = 1001;
    private static final String TAG = "VideoFragment";
    private String fileName;
    private String URL;
    private DownloadManager manager;
    private final String DOWNLOAD_ID ="DOWNLOAD_ID";
   private SharedPreferences preferences;
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
        URL = video.getSources().get(0);

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
                Toast.makeText(getContext(), "Download Video Clicked", Toast.LENGTH_SHORT).show();
                startDownLoad();
                break;
        }
        return true;
    }

    public void startDownLoad(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                downloadFile(fileName ,URL );
            }
            else{
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION);
            }
        }else{
            downloadFile(fileName , URL);
        }
    }

    public void downloadFile(String fileName , String URL){
        Uri downLoadURI = Uri.parse(URL);
         manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        try {
            if(manager!=null){
                DownloadManager.Request request = new DownloadManager.Request(downLoadURI);
                request.allowScanningByMediaScanner();
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                       .setTitle(fileName)
                        .setDescription("Downloading "+fileName)
                        .setAllowedOverRoaming(true)
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                        .setMimeType(getMimeType(downLoadURI));
               long download_id = manager.enqueue(request);
               preferences  = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor prefEdit = preferences.edit();
                prefEdit.putLong(DOWNLOAD_ID, download_id);
                prefEdit.apply();
                Toast.makeText(getContext(), "Download Started", Toast.LENGTH_SHORT).show();
            }else{
                Intent intent = new Intent(Intent.ACTION_VIEW , downLoadURI);
                startActivity(intent);

            }

        }catch(Exception e){
            Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "downloadFile: "+e.getMessage());
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == WRITE_PERMISSION){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                downloadFile(fileName, URL);
            }else{
                Toast.makeText(getActivity(), "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        getActivity().registerReceiver(downloadReceiver, intentFilter);
    }

    private String getMimeType(Uri uri){
        ContentResolver resolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap =  MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(uri));
    }
    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(preferences.getLong(DOWNLOAD_ID, 0));
            Cursor cursor = manager.query(query);
            if(cursor.moveToFirst()){

            }
        }
    };

}
