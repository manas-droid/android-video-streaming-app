package com.example.videostreamingapp.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.videostreamingapp.CancelBroadCastReceiver;
import com.example.videostreamingapp.CancelInterface;
import com.example.videostreamingapp.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import static com.example.videostreamingapp.App.CHANNEL_ID;

public class Download  extends Service implements Serializable {
    public AtomicBoolean isCancelled = new AtomicBoolean(false);
    private NotificationCompat.Builder notification;
    private NotificationManager manager;
    private static final String TAG = "Download";
    private int startId = 1;
        @Override
    public void onCreate() {
        Intent broadcastIntent = new Intent(this , CancelBroadCastReceiver.class);
        broadcastIntent.putExtra("CancelInterface", this);
        PendingIntent actionIntent = PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle("Download Service")
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_placeholder)
                .setContentText("title")
                .addAction(R.mipmap.ic_launcher, "Cancel", actionIntent)
                .setProgress(100, 0, true)
                .setCategory(CHANNEL_ID);
        manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
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
                long total = 0;;;;;
                int count;
                while((count = input.read(data))!= -1){
                    if(isCancelled.get()){
                        input.close();
                        break;
                    }
                    total+=count;
                    if(tempTotal<total){
                        notification.setProgress(100, (int)((total*100)/fileLength), false);
                        startForeground(startId, notification.build());
                        tempTotal = total;
                    }
                    output.write(data, 0, count);
                }
                
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
            }
        });
        executorService.shutdown();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        manager.notify(startId, notification.build());

        String url = intent.getStringExtra("url");
        String fileName = intent.getStringExtra("fileName");
        this.startId  = startId;
        Log.d(TAG, "onStartCommand: "+this);

        startForeground(this.startId, notification.build());
        getVideoFromURL(url, fileName);
//        new Thread(() -> {
//            for(int i = 0 ; i<6;i++){
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            isCancelled.set(true);
//            Log.d(TAG, "onStartCommand: called");
//        }).start();
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
        isCancelled.set(true);
        stopSelf(this.startId); // crucial to call this after the video is downloaded
    }
    
}
