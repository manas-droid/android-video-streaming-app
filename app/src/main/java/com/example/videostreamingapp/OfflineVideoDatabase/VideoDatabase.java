package com.example.videostreamingapp.OfflineVideoDatabase;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

public abstract class VideoDatabase extends RoomDatabase {
    public abstract VideoDao videoDao();

    public  static VideoDatabase instance = null;

    public static VideoDatabase getInstance(Context context){

        if(instance==null){
            instance = Room
                    .databaseBuilder(context.getApplicationContext(), VideoDatabase.class, "video_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
