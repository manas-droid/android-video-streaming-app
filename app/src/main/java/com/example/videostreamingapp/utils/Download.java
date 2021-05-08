package com.example.videostreamingapp.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.videostreamingapp.MainActivity;
import com.example.videostreamingapp.R;

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
import java.util.concurrent.atomic.AtomicBoolean;
import static com.example.videostreamingapp.App.CHANNEL_ID;

public class Download  extends Service {
    AtomicBoolean isCancelled = new AtomicBoolean(false);
    private NotificationCompat.Builder notification;
    private static final String TAG = "Download";
    private int startId = 1;
    @Override
    public void onCreate() {
        super.onCreate();
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
                byte data [] = new byte[1024*4];
                long total = 0;
                int count;
                while((count = input.read(data))!= -1){
                    if(isCancelled.get()){
                        input.close();
                        return;
                    }
                    total+=count;
                    if(tempTotal<total){
                        notification.setProgress(100, (int)((total*100)/fileLength), false);
                        startForeground(startId, notification.build());
                        tempTotal = total;
                    }
                    output.write(data, 0, count);
                }
                notification.setCategory("Download Finished").setProgress(0, 0, false).setOngoing(false);
                startForeground(startId, notification.build());
                stopSelf(startId);  // crucial to call it here after the video is downloaded
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
            }
        });
        executorService.shutdown();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: here");
        notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle("Download Service")
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_placeholder)
                .setContentText("title")
                .setProgress(100, 0, true)
                .setCategory(CHANNEL_ID);
        Log.d(TAG, "onStartCommand: "+startId);
        startForeground(startId, notification.build());

        String url = intent.getStringExtra("url");
        String fileName = intent.getStringExtra("fileName");
        this.startId  = startId;
        getVideoFromURL(url, fileName);
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
        stopSelf(this.startId); // crucial to call this after the video is downloaded
    }
}
