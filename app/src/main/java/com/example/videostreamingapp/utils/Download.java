package com.example.videostreamingapp.utils;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.videostreamingapp.OfflineVideoDatabase.VideoRepository;
import com.example.videostreamingapp.R;
import com.example.videostreamingapp.VideosAPI.RetrofitResponse.Videos;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.example.videostreamingapp.App.CHANNEL_ID;

public class Download  extends Service {
    private NotificationCompat.Builder notification;
    private NotificationManager manager;
    private static final String TAG = "Download";
    private int startId = 1;
    private static final String ACTION = "com.example.videostreamingapp.STOP_DOWNLOAD";
    private DownloadResponse response;
    private BroadcastReceiver onDownloadNotification;
    boolean isCancelled = false;
    private Videos video;
    private VideoRepository videoRepository;

    @Override
        public void onCreate() {
            super.onCreate();
            Intent broadcastIntent = new Intent(ACTION);
            PendingIntent actionIntent = PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                    .setContentTitle("Download Service")
                    .setOngoing(true)
                    .setSmallIcon(R.drawable.ic_placeholder)
                    .setContentText("title")
                    .addAction(R.mipmap.ic_launcher, "Cancel", actionIntent)
                    .setProgress(100, 0, true)
                    .setCategory(CHANNEL_ID);

            onDownloadNotification = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d(TAG, "onReceive: ");
                    isCancelled = true;
                }
            };

            IntentFilter intentFilter = new IntentFilter(ACTION);
            intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
            registerReceiver(onDownloadNotification , intentFilter);
    }

    public void getVideoFromURL(String url , String fileName ) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            long tempTotal = 0;
            try{
                URL link = new URL(url);
                connection =(HttpURLConnection) link.openConnection();
                connection.connect();

                if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                    return;
                }

                int fileLength = connection.getContentLength();
                input = connection.getInputStream();
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) , fileName+".mp4");
                output = new FileOutputStream(file);
                byte[] data = new byte[1024*4];
                long total = 0;
                int count;
                while((count = input.read(data))!= -1){
                    total+=count;
                    if(isCancelled){
                        if(file.exists()) file.delete();
                        response = DownloadResponse.CANCELLED;
                        break;
                    }
                    if(tempTotal<total){
                        notification.setProgress(100, (int)((total*100)/fileLength), false);
                        notification.setContentText((int)((total*100)/fileLength)+" %");
                        manager.notify(this.startId, notification.build());
                        tempTotal = total;
                    }
                    output.write(data, 0, count);
                }
                if(response == null) response = DownloadResponse.SUCCESS;

                notification.setCategory("Download Finished").setProgress(0, 0, false);
                manager.notify(this.startId, notification.build());

                stopSelf(this.startId);  // crucial to call it here after the video is downloaded

            } catch (MalformedURLException e) {
                Log.e(TAG, "getVideoFromURL: ",e);
            } catch (IOException e) {
                Log.e(TAG, "getVideoFromURL: ",e);
            }finally {
                try {
                    if(output!=null)
                        output.close();
                    if(input!=null)
                        input.close();
                } catch (IOException e) {
                    Log.e(TAG, "getVideoFromURL: ",e);
                }
                if(connection!=null)connection.disconnect();

                if(response == DownloadResponse.SUCCESS){
                    // save video class to SQLite
                    videoRepository.insert(video);
                }
            }
        });
        executorService.shutdown();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify(startId, notification.build());
        video = (Videos) intent.getSerializableExtra("video");
        videoRepository = new VideoRepository(getApplication());
        this.startId  = startId;
        isCancelled  = false;
        startForeground(this.startId, notification.build());
        getVideoFromURL(video.getSources().get(0), video.getTitle());
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(onDownloadNotification);
        stopSelf(this.startId); // crucial to call this after the video is downloaded
    }

}
