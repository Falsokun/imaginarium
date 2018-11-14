package com.example.olesya.boardgames;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.olesya.boardgames.interfaces.ImageHolderDao;
import com.example.olesya.boardgames.models.ImageHolder;

@Database(entities = {ImageHolder.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ImageHolderDao getImagesDao();

    public static AppDatabase instance;
    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            //TODO: not allow
            instance = Room.databaseBuilder(context,
                    AppDatabase.class, "cards-database")
                    .allowMainThreadQueries()
                    .build();
        }

        return instance;
    }
}