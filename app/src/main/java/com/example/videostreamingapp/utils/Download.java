package com.example.videostreamingapp.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Download  extends Service {
    AtomicBoolean isCancelled = new AtomicBoolean(false);
    private static final String TAG = "Download";

    @Override
    public void onCreate() {
        Intent notificationIntent = new Intent(this , MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Download Service")
                .setSmallIcon(R.drawable.ic_placeholder)
                .setContentText("title")
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
    }

    public void getVideoFromURL(String url , String fileName) {
        Log.d(TAG, "getVideoFromURL: here");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
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
                    Log.d(TAG, "getVideoFromURL: "+((total*100)/fileLength));
                    output.write(data, 0, count);
                }
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
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: here");
        
        String url = intent.getStringExtra("url");
        String fileName = intent.getStringExtra("fileName");

        getVideoFromURL(url, fileName);
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
