package com.example.videostreamingapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

public class App extends Application {
    private static final String TAG = "App";
    public static final String CHANNEL_ID = "DOWNLOAD_SERVICE_CHANNEL";
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: here");
        createNotificationChannel();
    }

    public void createNotificationChannel(){
        Log.d(TAG, "createNotificationChannel: here");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID,
                    "DownLoad Service Channel",
                    NotificationManager.IMPORTANCE_LOW);

            NotificationManager manager = getSystemService(NotificationManager.class);

            manager.createNotificationChannel(serviceChannel);
        }
    }


}
